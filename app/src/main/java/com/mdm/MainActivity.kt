package com.mdm.smssender48 // Yeni paket adınızla uyarlandı

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    // SMS'in gönderileceği hedef telefon numarası
    private val HEDEF_NUMARA = "05424776915" 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // XML DOSYASI OLMADAN ARAYÜZ OLUŞTURMA
        // layout/activity_main.xml dosyanız olmadığı için butonu kodla oluşturuyoruz
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
        }

        val btnGonder = Button(this).apply {
            text = "Cihaz Kaydını SMS ile Gönder"
        }
        
        layout.addView(btnGonder)
        setContentView(layout)
        
        btnGonder.setOnClickListener {
            checkPermissionsAndSend()
        }
    }

    private fun checkPermissionsAndSend() {
        val permissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE
        )
        
        if (permissions.all { ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            sendSerialSMS()
        } else {
            ActivityCompat.requestPermissions(this, permissions, 101)
        }
    }

    private fun sendSerialSMS() {
        try {
            val serialNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Build.getSerial()
                } catch (e: SecurityException) {
                    "YETKI_YOK_VEYA_UNKNOWN"
                }
            } else {
                @Suppress("DEPRECATION")
                Build.SERIAL
            }

            val mesaj = "CIHAZ_KAYIT_SERI_NO: $serialNumber"
            
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this.getSystemService(SmsManager::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            smsManager.sendTextMessage(HEDEF_NUMARA, null, mesaj, null, null)
            Toast.makeText(this, "SMS Başarıyla Gönderildi!", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendSerialSMS()
        } else {
            Toast.makeText(this, "İzin reddedildi.", Toast.LENGTH_SHORT).show()
        }
    }
}
