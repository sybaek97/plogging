package com.plogging.app.application

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.plogging.app.R
import com.plogging.app.databinding.DialogPermissionLayoutBinding
import com.plogging.app.view.main.MapActivity
import com.plogging.app.viewModel.PermissionViewModel

class DialogPermissionFragment:BottomSheetDialogFragment() {

    private lateinit var binding: DialogPermissionLayoutBinding
    private lateinit var permissionViewModel: PermissionViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.TransparentDialog)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_permission_layout, container,
            false
        )
        permissionViewModel = ViewModelProvider(this)[PermissionViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        permissionViewModel.permissionGranted.observe(viewLifecycleOwner) { granted ->
            if (granted) {
                startMapActivity()
            } else if (permissionViewModel.deniedCount >= 2) {
                showPermissionDeniedDialog()
            }
        }
        binding.btnAction.setOnClickListener {
            permissionViewModel.checkLocationPermission()
            if (permissionViewModel.permissionGranted.value == true) {
                startMapActivity()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PermissionViewModel.LOCATION_PERMISSION_REQUEST_CODE
                )
            }

        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionViewModel.onRequestPermissionResult(requestCode, grantResults)
    }

    private fun startMapActivity() {
        activity?.finish()
        val intent = Intent(requireContext(), MapActivity::class.java)
        startActivity(intent)
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("위치 권한 필요")
            .setMessage("지도를 사용하기 위해 위치 권한이 필요합니다. 설정에서 권한을 허용해주세요.")
            .setPositiveButton("설정으로 이동") { _, _ ->
                // 설정 화면으로 이동
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + requireContext().packageName)
                startActivity(intent)
            }
            .setNegativeButton("취소", null)
            .show()
    }

}