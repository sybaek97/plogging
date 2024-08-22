package com.plogging.app.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plogging.app.R
import com.plogging.app.application.BaseMainFragment
import com.plogging.app.application.DialogPermissionFragment
import com.plogging.app.databinding.FragmentHomeBinding
import com.plogging.app.repository.UserRepository
import com.plogging.app.viewModel.PermissionViewModel
import com.plogging.app.viewModel.UserViewModel
import com.plogging.app.viewModel.UserViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : BaseMainFragment() {
    override var isBackAvailable: Boolean = false

    private lateinit var binding: FragmentHomeBinding
    private lateinit var permissionViewModel: PermissionViewModel
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
        permissionViewModel = ViewModelProvider(this)[PermissionViewModel::class.java]
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val currentDate = Date()
        val formattedDate = formatDate(currentDate)

        binding.txtCalendar.text=formattedDate

        userViewModel.getPoints(uid)

        userViewModel.points.observe(viewLifecycleOwner) { point ->

            binding.txtPoint.text = "POINT : ${point}점"

        }


        permissionViewModel.permissionGranted.observe(viewLifecycleOwner) { granted ->
            if (granted) {
                startMapActivity()
            } else {
                showPermissionBottomSheet()
            }
        }
        binding.btnStart.setOnClickListener {
            permissionViewModel.checkLocationPermission()

        }


    }

    private fun showPermissionBottomSheet() {
        val bottomSheet = DialogPermissionFragment()
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun startMapActivity() {
        val intent = Intent(requireContext(), MapActivity::class.java)
        startActivity(intent)
    }
    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        return dateFormat.format(date)
    }
}