package com.plogging.app.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plogging.app.R
import com.plogging.app.application.BaseMainFragment
import com.plogging.app.common.Const.SID
import com.plogging.app.common.Const.USER_NAME
import com.plogging.app.common.SharedPreferenceHelper
import com.plogging.app.databinding.FragmentMypageBinding
import com.plogging.app.repository.UserRepository
import com.plogging.app.view.login.LoginActivity
import com.plogging.app.viewModel.UserViewModel
import com.plogging.app.viewModel.UserViewModelFactory

class MypageFragment:BaseMainFragment() {
    override var isBackAvailable: Boolean = false

    private lateinit var binding: FragmentMypageBinding
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(db))
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_mypage, container,
            false
        )
    return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val uid = user.uid

            userViewModel.getNickname(uid)
            userViewModel.getStudentId(uid)

            userViewModel.nickname.observe(viewLifecycleOwner) { nickName ->
                binding.txtNick.text = nickName ?: "닉네임 없음"
            }

            userViewModel.studentId.observe(viewLifecycleOwner) { studentId ->
                binding.txtStudentid.text = studentId ?: "학번 없음"
            }
        }
        binding.btnLogout.setOnClickListener {
            logout()
            activity?.finish()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

    }

}