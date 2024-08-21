package com.plogging.app.application

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.plogging.app.R
import com.plogging.app.view.login.LoginActivity

abstract class BaseMainFragment: Fragment() {

    private var backKeyPressedTime:Long=0
    abstract var isBackAvailable : Boolean
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            if(System.currentTimeMillis()>backKeyPressedTime + 2000){
                Toast.makeText(context, R.string.txt_pressed_back_logout, Toast.LENGTH_SHORT).show()
                backKeyPressedTime = System.currentTimeMillis()
                return@addCallback
            }else{
                if(isBackAvailable){
                    findNavController().popBackStack()
                }else{
                    logout()
                    activity?.finish()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        callback.isEnabled=true

    }
    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

}