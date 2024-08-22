package com.plogging.app.viewModel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PermissionViewModel(application: Application) : AndroidViewModel(application) {

    private val _permissionGranted = MutableLiveData<Boolean>()
    val permissionGranted: LiveData<Boolean> get() = _permissionGranted

    var deniedCount = 0

    fun checkLocationPermission() {
        _permissionGranted.value = ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun onRequestPermissionResult(
        requestCode : Int,
        grantResults:IntArray
    ){
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (granted) {
                _permissionGranted.value = true
                deniedCount = 0  // 권한이 허용되면 카운터 초기화
            } else {
                deniedCount++
                if (deniedCount >= 2) {
                    // 두 번 이상 거부되었을 때 처리
                    _permissionGranted.value = false
                }
            }
        }
    }
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}