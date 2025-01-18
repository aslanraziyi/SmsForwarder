package com.example.smsforwarder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smsforwarder.R
import com.example.smsforwarder.databinding.ItemSmsLogBinding
import com.example.smsforwarder.models.SmsLog
import java.text.SimpleDateFormat
import java.util.*

class SmsLogAdapter(
    private val smsLogs: List<SmsLog>
) : RecyclerView.Adapter<SmsLogAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemSmsLogBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(smsLog: SmsLog) {
            binding.apply {
                tvMessage.text = smsLog.message
                tvTimestamp.text = formatTimestamp(smsLog.timestamp)
                ivStatus.setImageResource(
                    if (smsLog.isDelivered) R.drawable.ic_delivered
                    else R.drawable.ic_failed
                )
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            return SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                .format(Date(timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSmsLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(smsLogs[position])
    }

    override fun getItemCount() = smsLogs.size
}
