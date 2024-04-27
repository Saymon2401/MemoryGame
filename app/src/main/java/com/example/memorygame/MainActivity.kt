package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.memorygame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),Animation.AnimationListener {
    lateinit var binding: ActivityMainBinding
    var animationstart:Animation? = null
    var animationend:Animation? = null
    var images:List<Int>? = null
    var image:List<Int>? = null

    var card1 = true
    var count:Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        images = listOf(
            R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6
        )
        image = images!!.shuffled()
        animationstart = AnimationUtils.loadAnimation(this,R.anim.mainanimstart)
        animationend = AnimationUtils.loadAnimation(this,R.anim.mainanimend)


        binding.brain.visibility = View.VISIBLE
        animationstart?.setAnimationListener(this)
        binding.card1.startAnimation(animationstart)


        binding.nextBtn.setOnClickListener {
            card1 = true
            count = 0
            var intent = Intent(this,SecondActivity::class.java)
            startActivity(intent)
        }


        animationend?.setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if (card1){
                    binding.card1.startAnimation(animationstart)
                    card1 = false
                }else if (!card1){
                    binding.card1.startAnimation(animationstart)
                    card1 = true
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }

    override fun onAnimationStart(animation: Animation?) {

    }

    override fun onAnimationEnd(animation: Animation?) {
        if (card1){
            binding.img1.visibility = View.VISIBLE
            binding.brain.visibility = View.INVISIBLE
            if (count == 0){
                binding.img1.setImageResource(image!![count!!])
                count = count!! + 1
            }else if (count!! >= 5 ){
                binding.img1.setImageResource(image!![count!!])
                count = 0
            }else{
                binding.img1.setImageResource(image!![count!!])
                count = count!! + 1
            }
            binding.card1.setBackgroundDrawable(
                ContextCompat.getDrawable(this@MainActivity,R.drawable.endcolor)
            )
            binding.card1.startAnimation(animationend)
        }else {
            binding.img1.visibility = View.INVISIBLE
            binding.brain.visibility = View.VISIBLE
            binding.card1.setBackgroundDrawable(
                ContextCompat.getDrawable(this@MainActivity,R.drawable.startcolor)
            )
            binding.card1.startAnimation(animationend)
        }
    }

    override fun onAnimationRepeat(animation: Animation?) {

    }
}