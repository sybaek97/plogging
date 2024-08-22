package com.plogging.app.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.plogging.app.common.Const.SID
import com.plogging.app.common.Const.USER_NAME
import com.plogging.app.common.FirebaseAuthErrorHelper

class UserRepository(private val db: FirebaseFirestore) {
    private val _nickname= MutableLiveData<String?>()
    private val _studentId=MutableLiveData<String?>()
    private val _points = MutableLiveData<Int?>()
    val nickname: MutableLiveData<String?>
        get()=_nickname
    val studentId: MutableLiveData<String?>
        get()=_studentId
    val points: MutableLiveData<Int?>
        get() = _points
    fun getNickname(uid:String){
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document->
                if(document!=null){
                    val nickName=document.getString(USER_NAME)
                    _nickname.postValue(nickName)
                }else{
                    _nickname.postValue(null)
                }
            }
            .addOnFailureListener{
                _nickname.postValue(null)
            }
    }
    fun getStudentId(uid:String){
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document->
                if(document!=null){
                    val studentId=document.getString(SID)
                    _studentId.postValue(studentId)
                }else{
                    _studentId.postValue(null)
                }
            }
            .addOnFailureListener{
                _studentId.postValue(null)
            }
    }
    fun getPoints(email: String) {
        db.collection("points").document(email).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val points = document.getLong("points")?.toInt()
                    _points.postValue(points)
                } else {
                    _points.postValue(null)
                }
            }
            .addOnFailureListener {
                _points.postValue(null)
            }
    }
    fun updatePoints(uid: String, newPoint: Int) {
        db.collection("points").document(uid)
            .update("points", newPoint)
            .addOnSuccessListener {
                _points.value = newPoint
                Log.d("UserViewModel", "Points successfully updated to $newPoint")
            }
            .addOnFailureListener { exception ->
                Log.e("UserViewModel", "Error updating points", exception)
            }
    }
    fun signUpUser(email:String,password:String,nickName:String,studentId:String, onSuccess: () -> Unit, onFailure: (String) -> Unit){
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userData = hashMapOf(
                            USER_NAME to nickName,
                            SID to studentId
                        )

                        db.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                // 회원가입 성공 시 포인트 테이블에 초기 데이터 추가
                                val pointsData = hashMapOf(
                                    "points" to 0 // 초기 포인트 값은 0으로 설정
                                )

                                db.collection("points").document(user.uid)
                                    .set(pointsData)
                                    .addOnSuccessListener {
                                        onSuccess()
                                    }
                                    .addOnFailureListener { exception ->
                                        onFailure(exception.message ?: "포인트 초기화 실패")
                                    }
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception.message ?: "Firestore 저장 실패")
                            }
                    }
                } else {
                    val exception = task.exception as? FirebaseAuthException
                    val errorMessage = FirebaseAuthErrorHelper.getErrorMessage(exception)
                    onFailure(errorMessage ?: "회원가입 실패")
                }
            }
    }

}