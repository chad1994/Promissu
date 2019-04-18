package com.simsimhan.promissu.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.databinding.ItemDetailUserStatusBinding
import com.simsimhan.promissu.detail.DetailEventListener
import com.simsimhan.promissu.detail.DetailViewModel
import com.simsimhan.promissu.network.model.Participant

class DetailUserStatusAdapter(
        private val lifecycleOwner: LifecycleOwner,
        private val listener: DetailEventListener,
        private val viewModel: DetailViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        const val TYPE_USER = 1
//        const val TYPE_INVITE = 2
    }

    private var list = listOf<Participant.Response>()

    fun setData(list: List<Participant.Response>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        when (viewType) {
//            TYPE_INVITE -> {
//                val binding = ItemDetailInviteBinding
//                        .inflate(LayoutInflater.from(parent.context), parent, false)
//                return DetailInviteStatusViewHolder(binding)
//            }
//            else -> {
//                val binding = ItemDetailUserStatusBinding
//                        .inflate(LayoutInflater.from(parent.context), parent, false)
//                return DetailUserStatusViewHolder(binding)
//            }
//        }
        val binding = ItemDetailUserStatusBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailUserStatusViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (holder) {
//            is DetailUserStatusViewHolder -> {
//                holder.bind(lifecycleOwner, list[position])
//            }
//            is DetailInviteStatusViewHolder -> {
//                holder.bind(lifecycleOwner, listener)
//            }
//        }
        (holder as DetailUserStatusViewHolder).bind(lifecycleOwner, list[position], listener, viewModel)
    }

    override fun getItemViewType(position: Int): Int {
//        return when (position) {
//            list.size - 1 -> TYPE_INVITE
//            else -> TYPE_USER
//        }
        return TYPE_USER
    }

    class DetailUserStatusViewHolder(private val itemBinding: ItemDetailUserStatusBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(_lifecycleOwner: LifecycleOwner, _participant: Participant.Response, _listener: DetailEventListener, _viewModel: DetailViewModel) {
            itemBinding.apply {
                lifecycleOwner = _lifecycleOwner
                participants = _participant
                listener = _listener
                viewModel = _viewModel
                executePendingBindings()
            }
        }
    }

//    class DetailInviteStatusViewHolder(private val itemBinding: ItemDetailInviteBinding
//    ) : RecyclerView.ViewHolder(itemBinding.root) {
//        fun bind(_lifecycleOwner: LifecycleOwner, _listener: DetailEventListener) {
//            itemBinding.apply {
//                lifecycleOwner = _lifecycleOwner
//                listener = _listener
//                executePendingBindings()
//            }
//        }
//    }
}