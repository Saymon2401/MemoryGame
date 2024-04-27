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

    var card1 = true
    var img2 = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        animationstart = AnimationUtils.loadAnimation(this,R.anim.mainanimstart)
        animationend = AnimationUtils.loadAnimation(this,R.anim.mainanimend)

        animationstart?.setAnimationListener(this)
        binding.card1.startAnimation(animationstart)


        binding.nextBtn.setOnClickListener {
            var intent = Intent(this,SecondActivity::class.java)
            startActivity(intent)
        }


        animationend?.setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if (!img2){
                    if (card1){
                        binding.card1.startAnimation(animationstart)
                        card1 = false
                    }else if (!card1){
                        binding.card1.startAnimation(animationstart)
                        card1 = true
                        img2 = true
                    }
                }else{
                    if (card1){
                        binding.card1.startAnimation(animationstart)
                        card1 = false
                    }else if (!card1){
                        binding.card1.startAnimation(animationstart)
                        card1 = true
                        img2 = false
                    }
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }

    override fun onAnimationStart(animation: Animation?) {

    }

    override fun onAnimationEnd(animation: Animation?) {
        if (!img2){
            if (card1){
                binding.img1.visibility = View.VISIBLE
                binding.card1.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.purplend))
                binding.card1.startAnimation(animationend)
            }else {
                binding.img1.visibility = View.INVISIBLE
                binding.card1.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.purplestart
                    )
                )
                binding.card1.startAnimation(animationend)
            }
        }else{
            if (card1){
                binding.img2.visibility = View.VISIBLE
                binding.card1.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.purplend))
                binding.card1.startAnimation(animationend)
            }else {
                binding.img2.visibility = View.INVISIBLE
                binding.card1.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.purplestart
                    )
                )
                binding.card1.startAnimation(animationend)
            }
        }
    }

    override fun onAnimationRepeat(animation: Animation?) {

    }
}