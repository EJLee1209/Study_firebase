package com.example.study

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.study.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class LoginActivity : AppCompatActivity() {
    var mBinding: ActivityLoginBinding? = null
    val binding get() = mBinding!!
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseAuth 객체 얻기
        auth = FirebaseAuth.getInstance()


        binding.register.setOnClickListener {
            //회원가입 버튼 클릭 시
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.btnLogin.setOnClickListener {
            var email = binding.inputEmail.text.toString()
            var pw = binding.inputPw.text.toString()
            login(email,pw)
        }

    }
    fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { result ->
                if(result.isSuccessful){
                    // 로그인 성공 시 메인 화면으로
                    startActivity(Intent(this, MainActivity::class.java))
                    // 로그인 정보 저장
                    val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("current_user", auth.currentUser.toString())
                    editor.putString("user_uid", auth.currentUser!!.uid)
                    editor.commit()

                }
                else{
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}