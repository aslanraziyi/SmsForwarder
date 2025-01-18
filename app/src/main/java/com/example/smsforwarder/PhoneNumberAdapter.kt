package com.example.smsforwarder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smsforwarder.R
import com.example.smsforwarder.databinding.ItemPhoneNumberBinding

class PhoneNumberAdapter(
    private val phoneNumbers: List<String>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<PhoneNumberAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemPhoneNumberBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(phoneNumber: String, onDeleteClick: (Int) -> Unit) {
            binding.tvPhoneNumber.text = phoneNumber
            binding.btnDelete.setOnClickListener {
                onDeleteClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhoneNumberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(phoneNumbers[position], onDeleteClick)
    }

    override fun getItemCount() = phoneNumbers.size
}
