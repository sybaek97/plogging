package com.plogging.app.application

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plogging.app.R
import com.plogging.app.databinding.DialogFinishLayoutBinding
import com.plogging.app.repository.UserRepository
import com.plogging.app.view.main.MainActivity
import com.plogging.app.view.main.MapActivity
import com.plogging.app.viewModel.PermissionViewModel
import com.plogging.app.viewModel.UserViewModel
import com.plogging.app.viewModel.UserViewModelFactory

class DialogFinishFragment:BottomSheetDialogFragment() {

    private lateinit var binding : DialogFinishLayoutBinding
    private lateinit var permissionViewModel: PermissionViewModel
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var score: Int = 0
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(db))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.TransparentDialog)
        arguments?.let {
            score = it.getInt("SCORE")
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_finish_layout, container,
            false
        )
        permissionViewModel = ViewModelProvider(this)[PermissionViewModel::class.java]

        return binding.root
    }
    companion object {
        fun newInstance(score: Int): DialogFinishFragment {
            val fragment = DialogFinishFragment()
            val args = Bundle()
            args.putInt("SCORE", score)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        userViewModel.getPoints(uid)


       binding.btnAction.setOnClickListener {
           userViewModel.updatePoints(uid, score)
           activity?.finish()
           val intent = Intent(requireContext(), MainActivity::class.java)
           startActivity(intent)

       }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

    }



}