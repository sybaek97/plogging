package com.plogging.app.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plogging.app.R
import com.plogging.app.adapter.BoardAdapter
import com.plogging.app.application.BaseMainFragment
import com.plogging.app.databinding.FragmentBoardBinding
import com.plogging.app.repository.BoardRepository
import com.plogging.app.repository.UserRepository
import com.plogging.app.viewModel.BoardViewModel
import com.plogging.app.viewModel.BoardViewModelFactory
import com.plogging.app.viewModel.UserViewModel
import com.plogging.app.viewModel.UserViewModelFactory

class BoardFragment: BaseMainFragment() {
    override var isBackAvailable: Boolean = true

    private lateinit var binding: FragmentBoardBinding
    private val db = FirebaseFirestore.getInstance()
    private val boardViewModel: BoardViewModel by viewModels {
        BoardViewModelFactory(BoardRepository(db))
    }

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
            R.layout.fragment_board, container,
            false
        )
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewBoard.layoutManager = LinearLayoutManager(requireContext())


        // ViewModel에서 데이터를 가져와 RecyclerView에 설정
        boardViewModel.boardData.observe(viewLifecycleOwner) { entries ->
            binding.recyclerViewBoard.adapter = BoardAdapter(entries,userViewModel,this)

            binding.recyclerViewBoard.scrollToPosition(entries.size - 1)
        }

        boardViewModel.fetchBoard()

        // 메시지 전송 버튼 클릭 시
        binding.btnBoard.setOnClickListener {
            val message = binding.editBoard.text.toString()
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            if (message.isNotEmpty()) {
                boardViewModel.addGuestbookEntry(message, currentUserUid)
                binding.editBoard.text.clear()

                binding.recyclerViewBoard.scrollToPosition(
                    binding.recyclerViewBoard.adapter?.itemCount ?: (0 - 1)
                )

            }
        }
    }
}