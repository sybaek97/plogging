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
import com.plogging.app.databinding.DialogFinishLayoutBinding
import com.plogging.app.view.main.MainActivity
import com.plogging.app.view.main.MapActivity
import com.plogging.app.viewModel.PermissionViewModel

class DialogFinishFragment:BottomSheetDialogFragment() {

    private lateinit var binding : DialogFinishLayoutBinding
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
            R.layout.dialog_finish_layout, container,
            false
        )
        permissionViewModel = ViewModelProvider(this)[PermissionViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



       binding.btnAction.setOnClickListener {
           activity?.finish()
           val intent = Intent(requireContext(), MainActivity::class.java)
           startActivity(intent)
       }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

    }



}