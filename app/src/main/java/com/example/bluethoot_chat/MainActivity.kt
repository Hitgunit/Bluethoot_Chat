package com.example.bluethoot_chat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verificarPermisos()
    }

    private fun verificarPermisos() {
        val permsRequestCode = 100
        val perms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
        )

        val accessFinePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            val accesCoarsePermission =
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            val BluetoothPermission = checkSelfPermission(Manifest.permission.BLUETOOTH)
            if (BluetoothPermission == PackageManager.PERMISSION_GRANTED && accesCoarsePermission == PackageManager.PERMISSION_GRANTED){
                //Se realiza lo necesario
            } else{
                requestPermissions(perms,permsRequestCode)
            }
            } else{
            val bluetoothPermission =
                registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
                }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
                //El permiso no esta aceptado. Solicitar permisos de ubicacion
                bluetoothPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            } else{
                
            }
        }
    }
}