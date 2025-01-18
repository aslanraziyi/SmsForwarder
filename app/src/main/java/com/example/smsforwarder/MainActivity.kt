package com.example.smsforwarder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smsforwarder.databinding.ActivityMainBinding
import com.example.smsforwarder.adapters.PhoneNumberAdapter
import com.example.smsforwarder.adapters.SmsLogAdapter
import com.example.smsforwarder.models.SmsLog
import com.example.smsforwarder.utils.PreferencesManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var phoneNumberAdapter: PhoneNumberAdapter
    private lateinit var smsLogAdapter: SmsLogAdapter
    private val phoneNumbers = mutableListOf<String>()
    private val smsLogs = mutableListOf<SmsLog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)
        
        // Add default number
        if (preferencesManager.getPhoneNumbers().isEmpty()) {
            preferencesManager.savePhoneNumbers(listOf("+989125177906"))
        }

        setupUI()
        checkPermissions()
        startSmsService()
    }

    private fun setupUI() {
        phoneNumbers.addAll(preferencesManager.getPhoneNumbers())
        
        phoneNumberAdapter = PhoneNumberAdapter(phoneNumbers) { position ->
            phoneNumbers.removeAt(position)
            preferencesManager.savePhoneNumbers(phoneNumbers)
            phoneNumberAdapter.notifyDataSetChanged()
        }

        binding.rvPhoneNumbers.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = phoneNumberAdapter
        }

        binding.btnAddPhone.setOnClickListener {
            val number = binding.etPhoneNumber.text.toString()
            if (number.isNotEmpty() && phoneNumbers.size < 5) {
                phoneNumbers.add(number)
                preferencesManager.savePhoneNumbers(phoneNumbers)
                phoneNumberAdapter.notifyDataSetChanged()
                binding.etPhoneNumber.text.clear()
            }
        }

        // Setup SMS logs RecyclerView
        smsLogAdapter = SmsLogAdapter(smsLogs)
        binding.rvSmsLogs.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = smsLogAdapter
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS
        )
        
        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startSmsService() {
        val serviceIntent = Intent(this, SmsForwardService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 123
    }
}
