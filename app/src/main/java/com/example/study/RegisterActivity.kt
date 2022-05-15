package com.example.study

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.study.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var registerEmail: EditText
    lateinit var registerPw1: EditText
    lateinit var registerPw2: EditText
    lateinit var db: FirebaseFirestore
    var mBinding: ActivityRegisterBinding? = null
    val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance()

        registerEmail = findViewById(R.id.register_email)
        registerPw1 = findViewById(R.id.register_pw1)
        registerPw2 = findViewById(R.id.register_pw2)

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            var email = registerEmail.text.toString()
            var pw1 = registerPw1.text.toString()
            var pw2 = registerPw2.text.toString()
            var name = binding.registerName.text.toString()
            var job = binding.registerJob.text.toString()
            var age = binding.registerAge.text.toString().toInt()
            val newUser = User(name, job, age)

            if(pw1 != pw2){
                Toast.makeText(this, "비밀번호를 확인 해주세요", Toast.LENGTH_SHORT).show()
            }
            auth.createUserWithEmailAndPassword(email,pw1)
                .addOnCompleteListener { result->
                    if(result.isSuccessful){
                        Toast.makeText(this, "계정 생성 완료", Toast.LENGTH_SHORT).show() // 토스트 메시지 띄우고
                        finish() // 액티비티 종료
                        db.collection("users")
                            .document(auth.currentUser!!.uid)
                            .set(newUser)
                            .addOnFailureListener {
                                Log.d("testt", "데이터 베이스 저장 실패")
                            }
                            .addOnSuccessListener {
                                Log.d("testt", "데이터 베이스 저장 성공")
                            }
                    }
                    else{
                        Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}