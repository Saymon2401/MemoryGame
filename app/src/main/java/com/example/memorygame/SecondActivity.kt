package com.example.memorygame

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.RelativeLayout
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
    private lateinit var cardViews: List<RelativeLayout> // для карт
    private lateinit var imgList: List<ImageView>       //массив для изображений открытых карт
    private lateinit var brainClose: List<ImageView>    //массив для картинки закрытых карт
    private lateinit var images: List<Int>    //массив для картинки закрытых карт
    private val openedCards = mutableListOf<Int?>()     //Открытые карты
    private val levelList = mutableListOf<Int?>()     //Уровень
    private val timeList = mutableListOf<String?>()     //Время секундомера
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var click: MediaPlayer
    private lateinit var gamewon: MediaPlayer
    private lateinit var rightchoose: MediaPlayer
    private lateinit var wrongtchoose: MediaPlayer

    var soundlike = true
    var wrongCard = false            // Когда выбран два неправильных карт
    var cardIndex:Int? = null        // последняя нажатая карта
    var lastIndex:Int? = null        // первая нажатая карта
    var change = false               // Для открытия начало карт
    var change2 = false              // Для закрытия начало карт
    var falseCard = 3               // Считается сколько сделали неправильных карт
    var elapsedTime:Long = 0         // Elapsed
    var min:Long? = null             // минута
    var sec:Long? = null             // секунд
    var recordTime:String? = null    // Присваивается время остановки когда все карты жёлтые
    var isTimerStarted = false       // Чтоб секундомер стартанул при нажатий на CardView
    var isAnimationRunning = true    // Чтоб не смог нажать подряд три раза
    var clickEnabled = true          // Вначале чтоб не смог нажать когда все желтые
    var levelCount = 1
    var hurt = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        mediaPlayer = MediaPlayer.create(this,R.raw.backsound)
        click = MediaPlayer.create(this,R.raw.click)
        gamewon = MediaPlayer.create(this,R.raw.gamewon)
        rightchoose = MediaPlayer.create(this,R.raw.rightchoose)
        wrongtchoose = MediaPlayer.create(this,R.raw.wrongchoose)

        mediaPlayer.isLooping = true
        click.setVolume(0.3f,0.3f)
//        mediaPlayer.start()


        //Добавлякм карты и картинки в массив
        cardViews = listOf(
            binding.card1,binding.card2,binding.card3,binding.card4,binding.card5,binding.card6,binding.card7,binding.card8,binding.card9, binding.card10, binding.card11, binding.card12
        )
        imgList = listOf(
            binding.img1, binding.img2, binding.img3, binding.img4,binding.img5, binding.img6, binding.img7, binding.img8, binding.img9, binding.img10, binding.img11, binding.img12
        )
        brainClose = listOf(
            binding.brain1, binding.brain2, binding.brain3, binding.brain4, binding.brain5, binding.brain6, binding.brain7, binding.brain8, binding.brain9, binding.brain10, binding.brain11, binding.brain12
        )
        images = listOf(
            R.drawable.img1, R.drawable.img1, R.drawable.img2, R.drawable.img2, R.drawable.img3, R.drawable.img3, R.drawable.img4, R.drawable.img4, R.drawable.img5, R.drawable.img5, R.drawable.img6, R.drawable.img6
        )

        // запускаем первоначальный показ карт
        var shuffledImg = images.shuffled() // разбрасывает
        cardViews.forEachIndexed{index, cardView ->
            imgList[index].setImageResource(shuffledImg[index])
            cardView.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.startcolor))
        }
        GlobalScope.launch {
            changeColor()
        }

        //Анимаций
        animationstart = AnimationUtils.loadAnimation(this,R.anim.animstart)
        animationend = AnimationUtils.loadAnimation(this,R.anim.animend)
        animationstart?.setAnimationListener(this)

        cardViews.forEachIndexed{ i , card ->
            card.setOnClickListener {
                click.start()
                // Если карта уже нажата не дает второй раз нажать , Невозможно сразу три раза открыть карту
                if (!clickEnabled || openedCards.contains(i) && isAnimationRunning) return@setOnClickListener
                cardIndex = i
                if (!isTimerStarted) { //Секундомер запускается при первом клике на карты
                    binding.secund.base = SystemClock.elapsedRealtime()
                    binding.secund.start()
                    isTimerStarted = true
                }
                card.startAnimation(animationstart)
            }
        }
        //кнопка назад
        binding.cancelBtn.setOnClickListener {
            click.start()
            wrongCard = false
            cardIndex = null
            lastIndex = null
            change = false
            change2 = false
            falseCard = 0
            elapsedTime = 0
            min = null
            sec = null
            recordTime = null
            isTimerStarted = false
            isAnimationRunning = true
            clickEnabled = true
            finish()
        }
        //кнопка перезагрузки карт
        binding.restartBtn.setOnClickListener {
            restartBtn()
        }
        //Кнопка звука
        binding.sound.setOnClickListener{
            click.start()
            if (mediaPlayer.isPlaying){
                mediaPlayer.pause()
                binding.soundon.visibility = View.INVISIBLE
                binding.soundoff.visibility = View.VISIBLE
            }else{
                mediaPlayer.start()
                binding.soundon.visibility = View.VISIBLE
                binding.soundoff.visibility = View.INVISIBLE
            }
        }


        animationend?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }
            override fun onAnimationEnd(animation: Animation?) {
                if(cardIndex!=null){
                    if (openedCards.size % 2 == 0){ // Принимает когда две карты открыты
                        lastIndex = openedCards[openedCards.size-2]
                        if (!wrongCard){
                            //если карты совпадают то карточка становится желтым
                            if (imgList[cardIndex!!].drawable.constantState == imgList[lastIndex!!].drawable.constantState){
                                cardViews[cardIndex!!].setBackgroundDrawable(ContextCompat.getDrawable(this@SecondActivity,R.drawable.startyellow))
                                cardViews[lastIndex!!].setBackgroundDrawable(ContextCompat.getDrawable(this@SecondActivity,R.drawable.startyellow))
                                if (soundlike) rightchoose.start() else soundlike = true
                            }
                            //если карты не совподают то становится красным и закрываются обратно
                            if(imgList[cardIndex!!].drawable.constantState != imgList[lastIndex!!].drawable.constantState){
                                cardViews[cardIndex!!].setBackgroundDrawable(ContextCompat.getDrawable(this@SecondActivity,R.drawable.startred))
                                cardViews[lastIndex!!].setBackgroundDrawable(ContextCompat.getDrawable(this@SecondActivity,R.drawable.startred))
                                clickEnabled = false //блокировка обработчик нажатий
                                wrongtchoose.start()
                                vibrate(vibrator,200)
                                Handler().postDelayed({ // Не дает нажать когда карточки красные
                                    clickEnabled = true // Разблокировка обработчики нажатий
                                }, 500)
                                cardViews[cardIndex!!].startAnimation(animationstart)
                                cardViews[lastIndex!!].startAnimation(animationstart)
                                wrongCard = true
                                hurt = true
                                falseCard--
                                binding.wrongBack.startAnimation(animationstart)
                                binding.wrong.setText(falseCard.toString())
                                soundlike = false
                            }
                        }
                    }
                }
                if (falseCard==0){
                    restartBtn()
                }
                if (allCardTrue()){ //Когда все карты открыты
                    gamewon.start()
                    binding.secund.stop() //остановка секундомера
                    elapsedTime = binding.secund.base - SystemClock.elapsedRealtime()
                    min = (elapsedTime / 60000)*-1 // минута
                    sec = (((elapsedTime / 1000) % 60)*-1) //секунд
                    timeList.add("$min:$sec") //добавляем время остановки секундомера
                    var timeeList = timeList
                    //Обрезка времени Activity
                    var timeRec = binding.time.text
                    var doubleDotRec = timeRec.indexOf(":")
                    var minRec = timeRec.substring(1,doubleDotRec)
                    var secRec = timeRec.substring(doubleDotRec+1,timeRec.length)

                    for (i in timeList.indices) {
                        var doubleDot = timeList[i]?.indexOf(":")
                        var minSub = timeList[i]?.substring(0,doubleDot!!)
                        var secSub = timeList[i]?.substring(doubleDot!!+1,timeList[i]!!.length)
                        if (timeeList.size == 1)
                        { //если Рекорд пустая то присваиваем нынешную
                            recordTime = "0$minSub:$secSub"
                            binding.time.text = recordTime
                        }
                        else if ((minRec == minSub) && (secRec.toInt() >= secSub!!.toInt()))
                        { // если минута нулевая то проверяется секунда
                            recordTime = "0$minSub:$secSub"
                            binding.time.text = recordTime
                        }
                        else if (minRec.toInt() > minSub!!.toInt())
                        {//в противном случае проверяется минута
                            recordTime = "0$minSub:$secSub"
                            binding.time.text = recordTime
                        }
                    }
                    levelCount++
                    levelList.add(levelCount)
                    if (levelCount>=10){
                        var level = "$levelCount"
                        binding.level.setText(level)
                    }else{
                        var level = "0$levelCount"
                        binding.level.setText(level)
                    }
                    if (binding.levelRec.text == "0"){
                        binding.levelRec.setText("2")
                    }else{
                        var numInd = 2
                        var num = ((binding.levelRec.text).toString()).toInt()
                        for (i in levelList){
                            if (i!!>=num) numInd = i
                        }
                        binding.levelRec.setText(numInd.toString())
                    }
                    continueFunc()
                }
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
    }
    override fun onResume() {
        super.onResume()
        mediaPlayer = MediaPlayer.create(this, R.raw.backsound) // Инициализируем MediaPlayer заново
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.5f, 0.5f)
        mediaPlayer.start() // Запускаем проигрывание музыки
    }
    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
    private fun vibrate(vibrator: Vibrator, duration: Long) {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(duration)
        }
    }
    override fun onAnimationStart(animation: Animation?) {
        isAnimationRunning = false
    }
    override fun onAnimationEnd(animation: Animation?) {
        if (hurt){
            binding.wrongBack.startAnimation(animationend)
            hurt = false
        }
            isAnimationRunning = true
            if (cardIndex!=null){
                for (i in cardViews.indices){
                    if (i == cardIndex ){
                        //Добавляем открытые карты в массив
                        if (!openedCards.contains(cardIndex)) openedCards.add(i)
                        if (!wrongCard)
                        {
                            imgList[i].visibility = View.VISIBLE
                            brainClose[i].visibility = View.INVISIBLE
                            cardViews[i].setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.endcolor))
                            cardViews[i].startAnimation(animationend)
                        }
                    }
                }
            }
        // если карты не совпали
        if (wrongCard){
            wrongCard = false
            openedCards.remove(lastIndex) //удаляем последние красных карт из Массива открытых карт
            openedCards.remove(cardIndex)
            imgList[cardIndex!!].visibility = View.INVISIBLE //Закрываем картинки
            imgList[lastIndex!!].visibility = View.INVISIBLE
            brainClose[cardIndex!!].visibility = View.VISIBLE
            brainClose[lastIndex!!].visibility = View.VISIBLE
            //Меняем цвет на начальный
            cardViews[cardIndex!!].setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.startcolor))
            cardViews[lastIndex!!].setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.startcolor))
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
        //Для первого показа карт открытым на три секунды
        if (change){
            for ( i in cardViews){
                for (i in brainClose) {
                    i.visibility = View.INVISIBLE
                }
                for (img in imgList){
                    img.visibility = View.VISIBLE
                }
                i.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.startyellow))
                i.startAnimation(animationend)
            }
            change = false
        }
        if (change2){
            for ( i in cardViews){
                for (i in brainClose) {
                    i.visibility = View.VISIBLE
                }
                for (img in imgList){
                    img.visibility = View.INVISIBLE
                }
                i.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.startcolor))
                i.startAnimation(animationend)
            }
            rightchoose.start()
            change2 = false
        }
    }

    override fun onAnimationRepeat(animation: Animation?) {

    }
    fun continueFunc(){
        lastIndex = null
        cardIndex = null
        wrongCard = false
        isTimerStarted = false
        openedCards.clear()
        binding.secund.stop()
        binding.secund.base = SystemClock.elapsedRealtime()
        isAnimationRunning = true
        falseCard = 3
        binding.wrong.text = "3"
        var shuffledImg = images.shuffled() // разбрасывает
        cardViews.forEachIndexed{index, cardView ->
            imgList[index].setImageResource(shuffledImg[index])
            cardView.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.startcolor))
        }

        GlobalScope.launch {
            changeColor()
        }
    }
    fun restartBtn(){
        click.start()
        levelCount = 1
        lastIndex = null
        cardIndex = null
        wrongCard = false
        isTimerStarted = false
        falseCard = 3

        openedCards.clear()
        binding.secund.stop()
        binding.secund.base = SystemClock.elapsedRealtime()
        binding.wrong.text = "3"
        binding.level.text = "01"
        binding.time.text = "00:00"
        isAnimationRunning = true
        timeList.clear()
        var shuffledImg = images.shuffled() // разбрасывает
        cardViews.forEachIndexed{index, cardView ->
            imgList[index].setImageResource(shuffledImg[index])
            cardView.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.startcolor))
        }
        GlobalScope.launch {
            changeColor()
        }
    }
    //Функция для первого показа карт открытым
    suspend fun changeColor() {
        clickEnabled = false
        for (i in cardViews){
            change = true
            i.startAnimation(animationstart)
        }
        delay(3000)
        for (i in cardViews){
            change2 = true
            i.startAnimation(animationstart)
        }
        delay(300)
        clickEnabled = true
    }
    //Проверяет все карты ли открыты и желтые
    fun allCardTrue():Boolean{
        val allCardsOpened = openedCards.size == 12
        return allCardsOpened
    }
}
