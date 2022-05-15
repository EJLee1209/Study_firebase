package com.example.study

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.study.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
//    lateinit var storage: FirebaseStorage
//    lateinit var storageRef: StorageReference
//    lateinit var pathReference: StorageReference
    var mbinding : ActivityMainBinding? = null
    val binding get() = mbinding!!
    lateinit var glide: RequestManager
    lateinit var sharedPreferences: SharedPreferences
    var userList = mutableListOf<User>()
    var usersUID = mutableListOf<String>()

    fun getProfileList(){
        // 리사이클러뷰를 찾아서
        val recyclerView = binding.userRecyclerview
        // 어답터 연결
        recyclerView.adapter = ProfileRecyclerViewAdapter(
            userList,
            usersUID,
            layoutInflater,
            this,
            glide,
            sharedPreferences
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FireStore 객체 얻기
        db = FirebaseFirestore.getInstance()

        // FirebaseAuth 객체 얻기
        auth = FirebaseAuth.getInstance()

        Log.d("testt", "uid: " + auth.currentUser!!.uid)

        glide = Glide.with(this)

        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val testuid = sharedPreferences.getString("user_uid", "") // 현재 저장되어 있는 uid를 가져옴

        // 파이어베이스 스토리지는 앱의 파일을 저장하는 기능을 제공
        // 스토리지를 이용하면 사용자가 앱에서 사진을 선택하고 서버에 올린 후
        // 다시 특정 시점에 내려 받을 수 있도록 할 수 있다.

        // FirebaseStorage 객체 얻기
//        storage = Firebase.storage
//        // 파일을 올리거나 내려받으려면 파일을 가리키는 StorageReference 를 만들어야 한다
//        storageRef = storage.reference
//        // child() 함수로 파일의 경로가 담긴 StorageReference 객체를 만든다
//        val imgRef: StorageReference = storageRef.child("images/me.png")
//
//        // putBytes() 함수로 바이트값 저장하기
//        // putBytes() 함수는 바이트 배열을 스토리에 저장할 때 사용
//        // 대표적인 예 - 뷰의 화면을 바이트로 읽어서 저장하는 경우
//        fun getBitmapFromView(view: View) : Bitmap? {
//            // Bitmap.createBitmap() 함수로 뷰의 크기와 같은 빈 Bitmap 객체를 만들고
//            var bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
//            // Canvas 객체로 뷰의 내용을 그린 후 리턴
//            var canvas = Canvas(bitmap)
//            view.draw(canvas)
//            return bitmap
//        }
//
//        val bitmap = getBitmapFromView(findViewById(R.id.profile_image))
//        val baos = ByteArrayOutputStream()
//        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()
//
//        var uploadTask = imgRef.putBytes(data)
//        uploadTask.addOnFailureListener{
//            Log.d("testt", "upload fail.......")
//        }.addOnCompleteListener{
//                Log.d("testt", "upload success.......")
//            }


        binding.btnLogout.setOnClickListener {
            //로그아웃
            auth.signOut()
            val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("current_user").commit()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.btnRefresh.setOnClickListener {
            getProfileList()
        }


        // 데이터베이스에서 데이터를 한번에 가져와서 list 로 어떻게 저장할까?
        db.collection("users")
            .get() // "users" 컬렉션의 모든 문서를 가져와서
            .addOnSuccessListener {
                it.forEach {
                    //각각의 필드값들을 변수에 저장
                    val name: String = it.data["name"].toString()
                    val job: String = it.data["job"].toString()
                    val age: Int = it.data["age"].toString().toInt()
                    // User 객체 생성
                    val user = User(name, job, age)
                    // 리스트에 추가
                    userList.add(user)
                }
                getProfileList()
            }
            .addOnFailureListener {

            }

        db.collection("users")
            .get()
            .addOnSuccessListener {
                it.forEach {
                    usersUID.add(it.id)
                }
            }

        // Map 형태로 저장하기 (key - value)
//        val user = mapOf(
//            "name" to "EunJae",
//            "email" to auth.currentUser!!.email,
//            "avg" to 10
//        )

        // 객체 형태로 저장하기
//        class User(
//            val name: String,
//            val email: String,
//            val avg: Int,
//            @JvmField val isAdmin: Boolean,
//            val isTop: Boolean
//        ) // 프로퍼티명이 'is'로 시작한다면 파이어스토어의 필드의 키에서는 is가 제거된다.
          // @JvmField를 사용하면 is를 유지할 수 있다.

//        val user = User("kim", auth.currentUser!!.email.toString(), 24, true, true)

        //users라는 컬렉션에 user를 추가한다 (add()함수 이용)
//        db.collection("users").add(user)
//            .addOnSuccessListener { documentReference -> // 저장 성공
//                Log.d("testt", "DocumentSnapshot added with ID : ${documentReference.id}")
//            }
//            .addOnFailureListener { e -> // 저장 실패
//                Log.w("testt", "Error adding document", e)
//            }

        // set() 함수로 데이터 저장하기
        // set() 함수는 신규 데이터 뿐만 아니라 기존의 데이터를 변경할 때도 사용된다.
//        val user_modify = User("lee", "lee@a.com", 31, true, true)
//        db.collection("users")
//            .document("ID01") // users 컬렉션에 ID01이라는 문서가 없으면 새로 추가하고
//                                          // 이미 있으면 해당 문서 전체를 덮어쓴다.(수정)
//            .set(user_modify)

        // 데이터 업데이트
        // set() 함수를 이용해 업데이트 할 수 있지만, 문서 전체를 덮어쓰므로
        // 기존 문서의 특정 필드값만 업데이트 하려면 update() 함수를 이용한다
//        db.collection("users")
//            .document("ID01")
//            .update("email", "lee@b.com") // email 필드 값만 업데이트

        // 데이터 삭제하기
        // delete()
        // 문서의 필드값을 삭제하는 경우
//        db.collection("users")
//            .document("ID01")
//            .update(mapOf(
//                "avg" to FieldValue.delete() // avg라는 필드값을 삭제
//            ))
        // 문서 전체를 삭제하는 경우
//        db.collection("users")
//            .document("ID01")
//            .delete()

        //get() 함수로 컬렉션의 전체 문서 가져오기
//        db.collection("users")
//            .get()
//            .addOnSuccessListener { result ->
//                for(document in result){
//                    Log.d("testt", "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception->
//                Log.d("testt", "Error getting documents: ", exception)
//            }

        //get() 함수로 단일 문서 가져오기
//        db.collection("users")
//            .document("ID01")
//            .get()
//            .addOnSuccessListener {
//                if(it != null) Log.d("testt", "Document data: ${it.data}")
//                else Log.d("testt", "no such document")
//
//            }
//            .addOnFailureListener {
//                Log.d("testt", "error", it)
//            }

        // 문서를 객체에 담기
        // get() 함수로 가져온 문서를 객체에 담아서 사용할 때는 콜백 매개변수의
        // toObject() 함수를 이용한다. 이 함수에 클래스를 지정하면 문서의 데이터를
        // 자동으로 객체에 담아준다.
        // toObject() 함수에 지정하는 클래스는 매개변수가 없는 생성자가 있어야 하며,
        // 클래스의 프로퍼티가 public 게터 함수를 가져야 한다.

//        class User2{
//            var name: String? = null
//            var email: String? = null
//            var avg: Int = 0
//        }
//        db.collection("users").document("ID01")
//            .get().addOnSuccessListener {
//                val selectUser = it.toObject(User2::class.java)
//                Log.d("testt", "name: ${selectUser?.name}")
//            }

        // whereXXX() 함수로 조건 설정
        // 조건에 맞는 문서만 가져오는 방법
//        db.collection("users")
//            .whereEqualTo("name","lee")
//            .get()
//            .addOnSuccessListener {
//                for(document in it){
//                    Log.d("testt", "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener {
//                Log.w("testt", "error", it)
//            }
    }

    class ProfileRecyclerViewAdapter(
        val userList: MutableList<User>,
        val usersUID: MutableList<String>,
        val inflater: LayoutInflater,
        val context: Context,
        val glide: RequestManager,
        val sharedPreferences: SharedPreferences
    ): RecyclerView.Adapter<ProfileRecyclerViewAdapter.ViewHolder>(){
        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val name: TextView
            val job: TextView
            val profileImage: ImageView

            init{
                name = itemView.findViewById(R.id.name)
                job = itemView.findViewById(R.id.job)
                profileImage = itemView.findViewById(R.id.profile_image)

                itemView.setOnClickListener {
                    val user = userList[adapterPosition]
                    val name = user.name
                    val job = user.job

                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra("name", name)
                    intent.putExtra("job", job)
                    intent.putExtra("uid", usersUID[adapterPosition])
                    context.startActivity(intent)
                }


            }

        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ProfileRecyclerViewAdapter.ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.user_item, parent, false))
        }

        override fun onBindViewHolder(holder: ProfileRecyclerViewAdapter.ViewHolder, position: Int) {
            val user = userList[position]
            val userUID = usersUID[position]
            val storage = FirebaseStorage.getInstance()
            val storageReference = storage.reference
            val pathReference = storageReference.child("images")

            holder.name.text = user.name
            holder.job.text = user.job
            if(pathReference==null){

            }else{
                // 파이어 스토리지로 부터 이미지를 가져오는 과정
                val submitProfile = storageReference.child("images/PROFILE_${userUID}_.png")
                if(submitProfile == null){
                }
                else{
                    submitProfile.downloadUrl.addOnSuccessListener {
                        glide.load(it).circleCrop().into(holder.profileImage)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return userList.size
        }
    }
}
class User (
    var name: String,
    var job: String,
    var age: Int
)

