package com.example.study

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var registerEmail: EditText
    lateinit var registerPw1: EditText
    lateinit var registerPw2: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        registerEmail = findViewById(R.id.register_email)
        registerPw1 = findViewById(R.id.register_pw1)
        registerPw2 = findViewById(R.id.register_pw2)

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            var email = registerEmail.text.toString()
            var pw1 = registerPw1.text.toString()
            var pw2 = registerPw2.text.toString()

            if(pw1 != pw2){
                Toast.makeText(this, "비밀번호를 확인 해주세요", Toast.LENGTH_SHORT).show()
            }
            auth.createUserWithEmailAndPassword(email,pw1)
                .addOnCompleteListener { result->
                    if(result.isSuccessful){
                        Toast.makeText(this, "계정 생성 완료", Toast.LENGTH_SHORT).show() // 토스트 메시지 띄우고
                        finish() // 액티비티 종료
                    }
                    else{
                        Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}