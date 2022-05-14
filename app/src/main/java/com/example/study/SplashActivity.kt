package com.example.study

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        // 자동 로그인 기능 구현
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val currentUser = sharedPreferences.getString("current_user", "")
        Log.d("testt", ""+currentUser)

        when(currentUser){
            "" ->{ // 로그인이 안되어 있으면
                startActivity(Intent(this, LoginActivity::class.java))
            }
            else ->{ // 로그인이 되어 있으면
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}