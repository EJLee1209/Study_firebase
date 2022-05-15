package com.example.study

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.study.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class ProfileActivity : AppCompatActivity() {
    lateinit var mbinding: ActivityProfileBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var auth: FirebaseAuth
    val binding get() = mbinding
    var glide: RequestManager? = null
    var fbStorage: FirebaseStorage? = null
    var uriPhoto: Uri? = null
    var name: String = ""
    var job: String = ""
    var uid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()

        glide = Glide.with(this)
        fbStorage = FirebaseStorage.getInstance()


        val intent = intent
        name = intent.getStringExtra("name").toString()
        job = intent.getStringExtra("job").toString()
        uid = intent.getStringExtra("uid").toString()

        binding.profileName.text = name
        binding.profileJob.text = job

        binding.profileImage.setOnClickListener {
            // 프로필 이미지를 클릭 했을 때
            // 사진첩으로 이동해서 프로필 이미지를 설정하게 끔 만들고 싶음
            if(auth.currentUser!!.uid != uid){
                Toast.makeText(this, "다른 사람의 프로필 사진은 바꿀 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                ActivityCompat.requestPermissions(
                    this, // 앨범에 접근하는 것을 허용할지 선택을 하는 메시지
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1
                ) // 한번 허용하면 앱이 설치 되어 있는 동안 다시 뜨지 않

                var photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, 0)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                uriPhoto = data?.data
                glide!!.load(uriPhoto).centerCrop().circleCrop().into(binding.profileImage)
                ImageUpload()
            }
        }
        else{
            Log.d("testt", "fail")
        }
    }
    private fun ImageUpload(){
        // 유저마다 다른 프로필 사진을 가질 수 있도록 유저의 고유한 번호인 uid를 이용해서
        // 이미지 이름을 유저마다 다르게 함
        val userUID = sharedPreferences.getString("user_uid","")
        var imgFileName = "PROFILE_" + userUID + "_.png"
        var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

        storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
            Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
        }
    }

}