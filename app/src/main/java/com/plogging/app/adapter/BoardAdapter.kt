package com.plogging.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.plogging.app.data.BoardData
import com.plogging.app.databinding.ItemBoardMyBinding
import com.plogging.app.databinding.ItemBoardOtherBinding
import com.plogging.app.viewModel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BoardAdapter (private val guestbookList: List<BoardData>,  private val viewModel: UserViewModel,private val lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
    private val anonymousMap = mutableMapOf<Int, String>()
    private val anonymousCounter: MutableList<Int>
    init {
        // 전체 항목의 개수만큼 번호 리스트를 생성, 내림차순으로 정렬
        anonymousCounter = (1..guestbookList.size).toMutableList().asReversed()
    }


    inner class MyBoardViewHolder(private val binding: ItemBoardMyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: BoardData) {
            binding.txtMessage.text = entry.message
            val formattedTime = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault()).format(Date(entry.timestamp))
            binding.txtTime.text = formattedTime


            binding.txtMessage.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            binding.txtNickname.text = "나"
        }
    }
    inner class OtherBoardViewHolder(private val binding: ItemBoardOtherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: BoardData, anonymousName: String) {
            binding.txtMessage.text = entry.message
            val formattedTime = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault()).format(Date(entry.timestamp))
            binding.txtTime.text = formattedTime

            binding.txtMessage.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            binding.txtNickname.text = anonymousName
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (guestbookList[position].uid == currentUserUid) {
            MY_BOARD
        } else {
            OTHER_BOARD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MY_BOARD) {
            val binding = ItemBoardMyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MyBoardViewHolder(binding)
        } else {
            val binding = ItemBoardOtherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            OtherBoardViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentEntry = guestbookList[position]

        if (holder is MyBoardViewHolder) {
            // "나"인 경우
            holder.bind(currentEntry)
            // 닉네임은 "나"로 표시되지만, 번호를 소비해야 함
            if (anonymousCounter.isNotEmpty()) {
                anonymousCounter.removeAt(0)
            }
        } else if (holder is OtherBoardViewHolder) {
            // "나"가 아닌 경우
            val anonymousName = anonymousMap[position] ?: run {
                val name = if (anonymousCounter.isNotEmpty()) {
                    "익명 ${anonymousCounter.removeAt(0)}"
                } else {
                    "익명"
                }
                anonymousMap[position] = name
                name
            }
            holder.bind(currentEntry, anonymousName)
        }
    }

    override fun getItemCount(): Int = guestbookList.size

    companion object {
        private const val MY_BOARD = 1
        private const val OTHER_BOARD = 2
    }
}