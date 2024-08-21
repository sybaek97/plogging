package com.plogging.app.view.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plogging.app.R
import com.plogging.app.application.BaseFragment
import com.plogging.app.databinding.FragmentSignupBinding
import com.plogging.app.repository.UserRepository
import com.plogging.app.viewModel.UserViewModel
import com.plogging.app.viewModel.UserViewModelFactory

class SignupFragment: BaseFragment() {
    private lateinit var binding : FragmentSignupBinding
    override var isBackAvailable: Boolean = false
    private val TAG = javaClass.simpleName
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var passwordRecheck: String
    private lateinit var nickName: String
    private lateinit var studentId: String
    private lateinit var db: FirebaseFirestore
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
            R.layout.fragment_signup, container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try{
            setupBackPressHandler(this)
            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()

            binding.btnSignup.setOnClickListener {

                btnUnable()

                email = binding.editEmail.text.toString().trim()
                password = binding.passwordEdit.text.toString().trim()
                passwordRecheck = binding.passwordRecheckEdit.text.toString().trim()
                nickName = binding.nicknameEdit.text.toString().trim()
                studentId = binding.studentidEdit.text.toString().trim()

                if (email.isEmpty() || password.isEmpty() || passwordRecheck.isEmpty() || nickName.isEmpty() || studentId.isEmpty()) {
                    btnEnable()
                    Toast.makeText(requireContext(), "모든 항목를 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else if (password != passwordRecheck) {
                    btnEnable()
                    Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    userViewModel.signUpUser(email, password, nickName,studentId, onSuccess = {
                        btnEnable()
                        Toast.makeText(requireContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_signUpFragment_to_LoginFragment)
                    }, onFailure = { errorMessage ->
                        btnEnable()
                        Toast.makeText(requireContext(), "회원가입 실패: $errorMessage", Toast.LENGTH_SHORT).show()
                    })
                }
            }



        }catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        binding.btnLogin.setOnClickListener{
            findNavController().navigate(R.id.action_signUpFragment_to_LoginFragment)
        }

    }
    private fun btnEnable(){
        binding.editEmail.isEnabled=true
        binding.passwordEdit.isEnabled=true
        binding.passwordRecheckEdit.isEnabled=true
        binding.btnSignup.isEnabled=true
    }

    private fun btnUnable(){
        binding.editEmail.isEnabled=false
        binding.passwordEdit.isEnabled=false
        binding.passwordRecheckEdit.isEnabled=false
        binding.btnSignup.isEnabled=false
    }
    private fun setupBackPressHandler(owner: LifecycleOwner) {
        requireActivity().onBackPressedDispatcher.addCallback(
            owner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }
        )
    }
}