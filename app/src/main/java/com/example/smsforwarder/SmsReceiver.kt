package com.example.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SmsMessage
import com.example.smsforwarder.models.SmsLog
import com.example.smsforwarder.utils.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            messages?.let {
                val senderNumber = messages[0].originatingAddress ?: "Unknown"
                val fullMessage = messages.joinToString("") { it.messageBody }
                
                val formattedMessage = "از: $senderNumber\nپیام: $fullMessage"
                
                val preferencesManager = PreferencesManager(context)
                val phoneNumbers = preferencesManager.getPhoneNumbers()
                
                for (number in phoneNumbers) {
                    sendSms(context, number, formattedMessage)
                }
            }
        }
    }

    private fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            val parts = smsManager.divideMessage(message)
            
            smsManager.sendMultipartTextMessage(
                phoneNumber,
                null,
                parts,
                null,
                null
            )

            // ذخیره لاگ موفق
            saveSmsLog(context, SmsLog(
                phoneNumber = phoneNumber,
                message = message,
                isDelivered = true
            ))
        } catch (e: Exception) {
            // ذخیره لاگ ناموفق
            saveSmsLog(context, SmsLog(
                phoneNumber = phoneNumber,
                message = message,
                isDelivered = false
            ))
        }
    }

    private fun saveSmsLog(context: Context, smsLog: SmsLog) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(context)
            database.smsLogDao().insert(smsLog)
            database.smsLogDao().deleteOldLogs()
        }
    }
}
