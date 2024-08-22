package com.plogging.app.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.plogging.app.data.RankData
import com.plogging.app.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository): ViewModel() {
    private val _rankingList = MutableLiveData<List<RankData>>()
    val rankingList: LiveData<List<RankData>> get() = _rankingList

    private val db = FirebaseFirestore.getInstance()

    val nickname: LiveData<String?>
        get()=repository.nickname
    val studentId: LiveData<String?>
        get()=repository.studentId
    val points: LiveData<Int?>
        get()=repository.points
    fun getNickname(uid:String){
        viewModelScope.launch {
            repository.getNickname(uid)
        }
    }
    fun getStudentId(uid:String){
        viewModelScope.launch {
            repository.getStudentId(uid)
        }
    }

    fun getPoints(uid:String){
        viewModelScope.launch {
            repository.getPoints(uid)
        }
    }
    fun updatePoints(uid:String, newPoint : Int){
        viewModelScope.launch {
            repository.updatePoints(uid,newPoint)
        }
    }

    fun signUpUser(email: String, password: String, nickName: String,studentId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            repository.signUpUser(email, password, nickName,studentId, onSuccess, onFailure)
        }
    }
    fun  fetchRankingData() {
        viewModelScope.launch {
            val rankings = mutableListOf<RankData>()

            // 1. 포인트 테이블에서 상위 10명의 UID와 포인트를 가져옴
            db.collection("points")
                .orderBy("points", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener { documents ->
                    val uidPointPairs = documents.map { doc ->
                        Pair(doc.id, doc.getLong("points")?.toInt() ?: 0)
                    }

                    // 2. 공동 순위를 계산하기 위한 정렬 및 순위 계산
                    val rankedPairs = mutableListOf<Triple<String, Int, Int>>()
                    var currentRank = 1

                    uidPointPairs.forEachIndexed { index, (uid, points) ->
                        if (index > 0 && points < uidPointPairs[index - 1].second) {
                            currentRank = index + 1
                        }
                        rankedPairs.add(Triple(uid, points, currentRank))
                    }

                    // 3. UID를 사용해 유저 테이블에서 닉네임을 가져옴
                    rankedPairs.forEach { (uid, points, rank) ->
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { userDoc ->
                                val nickname = userDoc.getString("nickname") ?: "Unknown"
                                rankings.add(RankData(nickname, points, rank))

                                // 4. 상위 10명 모두 처리했으면 LiveData 업데이트
                                if (rankings.size == rankedPairs.size) {
                                    _rankingList.postValue(rankings.sortedBy { it.rank })
                                }
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    _rankingList.postValue(emptyList())
                }
        }
    }
}