package com.plogging.app.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.plogging.app.R
import com.plogging.app.adapter.RankAdapter
import com.plogging.app.application.BaseMainFragment
import com.plogging.app.databinding.FragmentRankBinding
import com.plogging.app.repository.UserRepository
import com.plogging.app.viewModel.UserViewModel
import com.plogging.app.viewModel.UserViewModelFactory

class RankFragment: BaseMainFragment() {
    override var isBackAvailable: Boolean = false

    private lateinit var binding: FragmentRankBinding
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
            R.layout.fragment_rank, container,
            false
        )
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewRanking.layoutManager = LinearLayoutManager(requireContext())

        // ViewModel에서 랭킹 데이터 가져오기
        userViewModel.fetchRankingData()

        // LiveData 관찰하여 RecyclerView 업데이트
        userViewModel.rankingList.observe(viewLifecycleOwner) { rankingList ->
            binding.recyclerViewRanking.adapter = RankAdapter(rankingList)
        }
    }
}