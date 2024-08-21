package com.plogging.app.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plogging.app.R
import com.plogging.app.application.BaseMainFragment
import com.plogging.app.common.Const
import com.plogging.app.common.SharedPreferenceHelper
import com.plogging.app.databinding.FragmentHomeBinding
import com.plogging.app.repository.UserRepository
import com.plogging.app.viewModel.UserViewModel
import com.plogging.app.viewModel.UserViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : BaseMainFragment() {
    override var isBackAvailable: Boolean = false

    private lateinit var binding: FragmentHomeBinding
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(db))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home, container,
            false
        )
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        userViewModel.getPoints(uid)
        val currentDate = Date()
        val formattedDate = formatDate(currentDate)

        binding.txtCalendar.text=formattedDate


        userViewModel.points.observe(viewLifecycleOwner) { point ->

            binding.txtPoint.text = "POINT : ${point}점"

        }
        binding.btnStart.setOnClickListener {
            activity?.finish()
            val intent = Intent(requireContext(), MapActivity::class.java)
            startActivity(intent)
        }


    }
    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        return dateFormat.format(date)
    }
}