package com.plogging.app.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.plogging.app.R
import com.plogging.app.application.BaseFragment
import com.plogging.app.common.Const
import com.plogging.app.common.FirebaseAuthErrorHelper
import com.plogging.app.common.SharedPreferenceHelper
import com.plogging.app.databinding.FragmentLoginBinding
import com.plogging.app.repository.UserRepository
import com.plogging.app.view.main.MainActivity
import com.plogging.app.viewModel.UserViewModel
import com.plogging.app.viewModel.UserViewModelFactory


class LoginFragment:BaseFragment() {
    private lateinit var binding : FragmentLoginBinding
    override var isBackAvailable: Boolean = false
    private val TAG = javaClass.simpleName
    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(db))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login, container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        auth = FirebaseAuth.getInstance()
        try {
            binding.btnLogin.setOnClickListener {
                btnUnable()
                val email = binding.editEmail.text.toString().trim()
                val password = binding.editPw.text.toString().trim()
                if(email.isNotEmpty()&&password.isNotEmpty()) {
                    loginUser(email, password)
                } else {
                    btnEnable()
                    Toast.makeText(
                        requireContext(),
                        "로그인 실패: 아이디 또는 비밀번호를 확인해주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }


        } catch (e: Exception) {
            Log.e(TAG, "onViewCreated: ${e.message}")
        }
        binding.btnSignup.setOnClickListener{
            findNavController().navigate(R.id.action_LoginFragment_to_signUpFragment)
        }

    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "로그인 성공", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                } else {
                    btnEnable()

                    val exception = task.exception as? FirebaseAuthException
                    val errorMessage = FirebaseAuthErrorHelper.getErrorMessage(exception)
                    Toast.makeText(
                        requireContext(),
                        "로그인 실패: $errorMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    private fun navigateToMainActivity() {
        binding.editEmail.isEnabled=true
        binding.editPw.isEnabled=true
        binding.boxLogin.isEnabled=true
        activity?.finish()
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    private fun btnEnable(){
        binding.editEmail.isEnabled=true
        binding.editPw.isEnabled=true
        binding.boxLogin.isEnabled=true
    }

    private fun btnUnable(){
        binding.editEmail.isEnabled=false
        binding.editPw.isEnabled=false
        binding.boxLogin.isEnabled=false
    }
}