package com.plogging.app.view.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.plogging.app.R
import com.plogging.app.application.DialogFinishFragment
import com.plogging.app.databinding.ActivityMapBinding
import com.plogging.app.repository.UserRepository
import com.plogging.app.viewModel.PermissionViewModel.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.plogging.app.viewModel.UserViewModel
import com.plogging.app.viewModel.UserViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapBinding
    private val TAG = javaClass.simpleName
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var startTime = 0L
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(db))
    }

    private fun initMapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.view_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.view_map, it).commit()
            }

        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        initMapView()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        startTimer()
        userViewModel.getPoints(uid)

        userViewModel.points.observe(this) { point ->

            binding.txtPoint.text = point.toString()

        }

        binding.apply {
            txtCalendar.text

            btnPlus.setOnClickListener {
                val currentPoint = binding.txtPoint.text.toString().toIntOrNull() ?: 0
                val newPoint = currentPoint + 1
                binding.txtPoint.text = newPoint.toString()
                userViewModel.updatePoints(uid, newPoint)
            }

            btnMinus.setOnClickListener {
                val currentPoint = binding.txtPoint.text.toString().toIntOrNull() ?: 0
                val newPoint = if (currentPoint > 0) currentPoint - 1 else 0
                binding.txtPoint.text = newPoint.toString()
                userViewModel.updatePoints(uid, newPoint)
            }
        }
        binding.btnFinish.setOnClickListener {
            dialogFinish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dialogFinish()
            }
        })


    }


    private fun dialogFinish() {
        val bottomSheet = DialogFinishFragment()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    private fun startTimer() {
        // 시작 시간을 현재 시간으로 설정
        startTime = System.currentTimeMillis()

        // Coroutine을 사용하여 비동기적으로 타이머 시작
        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                val elapsedTime = System.currentTimeMillis() - startTime

                val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60

                binding.txtCalendar.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                // 1초 대기 후 다시 실행
                delay(1000)
            }
        }
    }


    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
    }
}