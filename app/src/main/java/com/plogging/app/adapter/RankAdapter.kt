package com.plogging.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.plogging.app.data.RankData
import com.plogging.app.databinding.ItemRankingBinding

class RankAdapter (private val rankingList: List<RankData>) : RecyclerView.Adapter<RankAdapter.RankingViewHolder>() {

    inner class RankingViewHolder(private val binding: ItemRankingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userRank: RankData) {
            binding.txtNickname.text = "닉네임 : ${userRank.nickname}"
            binding.txtPoints.text = "${userRank.point.toString()}점"
            binding.txtRank.text="${userRank.rank.toString()}위"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = ItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        holder.bind(rankingList[position])
    }

    override fun getItemCount(): Int = rankingList.size
}