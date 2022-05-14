package com.example.study

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.study.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    var mbinding : ActivityMainBinding? = null
    val binding get() = mbinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 파이어스토어 객체 얻기
        db = FirebaseFirestore.getInstance()

        // 파이어베이스 객체 얻기
        auth = FirebaseAuth.getInstance()

        binding.btnLogout.setOnClickListener {
            //로그아웃
            auth.signOut()
            val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("current_user").commit()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        val userList = mutableListOf<User>()

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
                // 리사이클러뷰를 찾아서
                val recyclerView = binding.userRecyclerview
                // 어답터 연결
                recyclerView.adapter = ProfileRecyclerViewAdapter(
                    userList,
                    layoutInflater,
                    this
                )
            }
            .addOnFailureListener {

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
}
class User (
    var name: String,
    var job: String,
    var age: Int
)

class ProfileRecyclerViewAdapter(
    var userList: MutableList<User>,
    var inflater: LayoutInflater,
    var context: Context
): RecyclerView.Adapter<ProfileRecyclerViewAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView
        val job: TextView

        init{
            name = itemView.findViewById(R.id.name)
            job = itemView.findViewById(R.id.job)
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
        holder.name.text = user.name
        holder.job.text = user.job
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
