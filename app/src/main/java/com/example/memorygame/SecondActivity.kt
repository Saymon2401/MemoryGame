package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.memorygame.databinding.ActivitySecondBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity(),Animation.AnimationListener {
    lateinit var binding: ActivitySecondBinding
    var animationstart:Animation? = null
    var animationend:Animation? = null
    private lateinit var cardViews: List<CardView>
    private lateinit var imgList: List<ImageView>
    private val openedCards = mutableListOf<Int?>()
    private val timeList = mutableListOf<String?>()
    private val wrongList = mutableListOf<Int?>()

    var wrongCard = false
    var cardIndex:Int? = null
    var lastIndex:Int? = null
    var change = false
    var change2 = false
    var falseCard = 0
    var time:String? = null
    var elapsedTime:Long = 0
    var min:Long? = null
    var sec:Long? = null
    var recordTime:String? = null
    var isTimerStarted = false
    var restart = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cardViews = listOf(
            binding.card1,
            binding.card2,
            binding.card3,
            binding.card4,
            binding.card5,
            binding.card6,
            binding.card7,
            binding.card8,
            binding.card9,
            binding.card10,
            binding.card11,
            binding.card12
        )
        imgList = listOf(
            binding.img1,
            binding.img2,
            binding.img3,
            binding.img4,
            binding.img5,
            binding.img6,
            binding.img7,
            binding.img8,
            binding.img9,
            binding.img10,
            binding.img11,
            binding.img12
        )
        GlobalScope.launch {
            changeColor()
        }


        animationstart = AnimationUtils.loadAnimation(this,R.anim.animstart)
        animationend = AnimationUtils.loadAnimation(this,R.anim.animend)
        animationstart?.setAnimationListener(this)

        cardViews.forEachIndexed{ i , card ->
            card.setOnClickListener {
                if (openedCards.contains(i)) return@setOnClickListener
                cardIndex = i
                if (!isTimerStarted) {
                    binding.secund.base = SystemClock.elapsedRealtime()
                    binding.secund.start()
                    isTimerStarted = true
                }
                card.startAnimation(animationstart)
            }
        }

        binding.cancelBtn.setOnClickListener {
            restart = true
            lastIndex = null
            cardIndex = null
            wrongCard = false
            isTimerStarted = false
            falseCard = 0
            openedCards.clear()
            binding.secund.stop()
            binding.secund.base = SystemClock.elapsedRealtime()
            binding.wrong.text = "0"
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        binding.restartBtn.setOnClickListener {
            restart = true
            lastIndex = null
            cardIndex = null
            wrongCard = false
            isTimerStarted = false
            falseCard = 0
            openedCards.clear()
            binding.secund.stop()
            binding.secund.base = SystemClock.elapsedRealtime()
            binding.wrong.text = "0"
            GlobalScope.launch {
                changeColor()
            }
        }


        animationend?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if(cardIndex!=null){
                    if (openedCards.size % 2 == 0){
                        lastIndex = openedCards[openedCards.size-2]
                        if (!wrongCard){
                            if (imgList[cardIndex!!].drawable.constantState == imgList[lastIndex!!].drawable.constantState){
                                cardViews[cardIndex!!].setCardBackgroundColor(ContextCompat.getColor(this@SecondActivity,R.color.yellow))
                                cardViews[lastIndex!!].setCardBackgroundColor(ContextCompat.getColor(this@SecondActivity,R.color.yellow))
                            }
                            if(imgList[cardIndex!!].drawable.constantState != imgList[lastIndex!!].drawable.constantState){
                                cardViews[cardIndex!!].setCardBackgroundColor(ContextCompat.getColor(this@SecondActivity,R.color.red))
                                cardViews[lastIndex!!].setCardBackgroundColor(ContextCompat.getColor(this@SecondActivity,R.color.red))
                                cardViews[cardIndex!!].startAnimation(animationstart)
                                cardViews[lastIndex!!].startAnimation(animationstart)
                                wrongCard = true
                                falseCard++
                            }
                        }
                    }
                }
                if (allCardTrue()){
                    binding.secund.stop()
                    elapsedTime = binding.secund.base - SystemClock.elapsedRealtime()
                    min = (elapsedTime / 60000)*-1
                    sec = (((elapsedTime / 1000) % 60)*-1)
                    time = "$min:$sec"
                    wrongList.add(falseCard)
                    timeList.add(time)
                    var timeeList = timeList
                    var wronggList = wrongList
                    Log.d("time","TimeList: $timeeList")
                    Log.d("wrong","WrongList: $wronggList")

                    var timeRec = binding.time.text
                    var doubleDotRec = timeRec.indexOf(":")
                    var minRec = timeRec.substring(1,doubleDotRec)
                    var secRec = timeRec.substring(doubleDotRec+1,timeRec.length)

                    for (i in timeList.indices) {
                        var doubleDot = timeList[i]?.indexOf(":")
                        var minSub = timeList[i]?.substring(0,doubleDot!!)
                        var secSub = timeList[i]?.substring(doubleDot!!+1,timeList[i]!!.length)
                        if (timeeList.size == 1){
                            recordTime = "0$minSub:$secSub"
                            binding.time.text = recordTime
                            binding.wrongRectime.text = wrongList[i].toString()
                        }else if ((minRec == minSub) && (secRec.toInt() >= secSub!!.toInt())){
                            recordTime = "0$minSub:$secSub"
                            binding.time.text = recordTime
                            binding.wrongRectime.text = wrongList[i].toString()
                        }else if (minRec.toInt() > minSub!!.toInt()){
                            recordTime = "0$minSub:$secSub"
                            binding.time.text = recordTime
                            binding.wrongRectime.text = wrongList[i].toString()
                        }
                        Log.d("Record","Time: $minSub:$secSub")
                    }



//                    Log.d("first", "Время: 0${elapsedTime / 60000}:${((elapsedTime / 1000) % 60)*-1}")
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }


    override fun onAnimationStart(animation: Animation?) {

    }

    override fun onAnimationEnd(animation: Animation?) {

            if (cardIndex!=null){
                for (i in cardViews.indices){
                    if (i == cardIndex ){
                        if (!openedCards.contains(cardIndex)){
                            openedCards.add(i)
                        }
                        if (!wrongCard){
                            imgList[i].visibility = View.VISIBLE
                            cardViews[i].setCardBackgroundColor(ContextCompat.getColor(this,R.color.purplend))
                            cardViews[i].startAnimation(animationend)
                        }
                    }
                }
            }

        if (wrongCard){
            val wrongcard = falseCard.toString()
            binding.wrong.text = wrongcard
            wrongCard = false
            openedCards.remove(lastIndex)
            openedCards.remove(cardIndex)
            imgList[cardIndex!!].visibility = View.INVISIBLE
            imgList[lastIndex!!].visibility = View.INVISIBLE
            cardViews[cardIndex!!].setCardBackgroundColor(ContextCompat.getColor(this@SecondActivity,R.color.purplestart))
            cardViews[lastIndex!!].setCardBackgroundColor(ContextCompat.getColor(this@SecondActivity,R.color.purplestart))
            cardViews[cardIndex!!].startAnimation(animationend)
            cardViews[lastIndex!!].startAnimation(animationend)
            if (openedCards.size == 0){
                lastIndex = null
                cardIndex = null
            }else{
                lastIndex = openedCards[openedCards.size-2]
                cardIndex = openedCards[openedCards.size-1]
            }
        }
        if (restart){

        }
        if (change){
            for ( i in cardViews){
                for (img in imgList){
                    img.visibility = View.VISIBLE
                }
                i.setCardBackgroundColor(ContextCompat.getColor(this,R.color.yellow))
                i.startAnimation(animationend)
            }
            change = false
        }
        if (change2){
            for ( i in cardViews){
                for (img in imgList){
                    img.visibility = View.INVISIBLE
                }
                i.setCardBackgroundColor(ContextCompat.getColor(this,R.color.purplestart))
                i.startAnimation(animationend)
            }
            change2 = false
        }
    }

    override fun onAnimationRepeat(animation: Animation?) {

    }

    suspend fun changeColor(){
        for (i in cardViews){
            change = true
            i.startAnimation(animationstart)
        }
        delay(3000)
        for (i in cardViews){
            change2 = true
            i.startAnimation(animationstart)
        }
    }
    fun allCardTrue():Boolean{
        val allCardsOpened = openedCards.size == cardViews.size
        val allCardsYellow = cardViews.all { cardView ->
            cardView.cardBackgroundColor == ContextCompat.getColorStateList(this@SecondActivity, R.color.yellow)
        }
        return allCardsOpened && allCardsYellow
    }
}
