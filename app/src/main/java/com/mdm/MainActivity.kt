package com.fimar.mdm

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    // SMS'in gönderileceği hedef telefon numarası (Kendi numaranı yaz)
    private val HEDEF_NUMARA = "05424776915" 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGonder = findViewById<Button>(R.id.btnGonder)
        
        btnGonder.setOnClickListener {
            checkPermissionsAndSend()
        }
    }

    private fun checkPermissionsAndSend() {
        val permissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE
        )
        
        // İzinler verilmiş mi kontrol et
        if (permissions.all { ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            sendSerialSMS()
        } else {
            // İzinleri iste
            ActivityCompat.requestPermissions(this, permissions, 101)
        }
    }

    private fun sendSerialSMS() {
        try {
            // Seri numarasını al (Android 10+ için Device Owner yetkisi gerekir, aksi halde unknown döner)
            val serialNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Build.getSerial()
                } catch (e: SecurityException) {
                    "YETKI_YOK_VEYA_UNKNOWN"
                }
            } else {
                Build.SERIAL
            }

            val mesaj = "CIHAZ_KAYIT_SERI_NO: $serialNumber"
            
            // Modern Android (API 31+) ve eski sürümler için SmsManager uyumluluğu
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            // SMS Gönderimi
            smsManager.sendTextMessage(HEDEF_NUMARA, null, mesaj, null, null)
            
            Toast.makeText(this, "SMS Başarıyla Gönderildi!", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // İzin isteği sonucunu yakala
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendSerialSMS()
        } else {
            Toast.makeText(this, "SMS gönderimi için izin vermeniz gerekiyor.", Toast.LENGTH_SHORT).show()
        }
    }
}
