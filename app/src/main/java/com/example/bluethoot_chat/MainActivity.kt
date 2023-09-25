package com.example.bluethoot_chat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import java.util.UUID

private var myBluetoothAdapter: BluetoothAdapter? = null
var socketAux: BluetoothServerSocket? = null
val APP_NAME = "BTChat"
var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
val REQUEST_ENABLE_BLUETOOTH = 1
var servidor: Boolean = false
lateinit var n_pairedDevice: Set<BluetoothDevice>
lateinit var btnListaDispositivos: Button
lateinit var lsView: ListView
var socketCliente: BluetoothSocket? = null
var conectarCliente: SocketClienteBluetooth? = null
var escuchar: SocketServidorBluetooth? = null
lateinit var txtStatus: TextView
lateinit var txtMensaje: TextView
lateinit var txtMsg: EditText
lateinit var btnEscuchar: Button
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        verificarPermisos()

        habilitarBluetooth()

        implementarListeners()
    }

    @SuppressLint("MissingPermission")
    private fun implementarListeners() {
        lsView = findViewById(R.id.lsView)
        txtStatus = findViewById(R.id.txtStatus)
        btnListaDispositivos = findViewById(R.id.btnListaDispositivos)
        txtMensaje = findViewById(R.id.txtMensaje)
        txtMsg = findViewById(R.id.txtMsg)
        btnEscuchar = findViewById(R.id.btnEscuchar)

        btnListaDispositivos.setOnClickListener {
            servidor = false
            n_pairedDevice = myBluetoothAdapter!!.bondedDevices
            val list: ArrayList<BluetoothDevice> = ArrayList()
            val listaNombre: ArrayList<String> = ArrayList()
            if (!n_pairedDevice.isEmpty()){
                for (device: BluetoothDevice in n_pairedDevice){
                    var nombre: String = device.name
                    if (nombre == null){
                        nombre = device.address
                    }
                    listaNombre.add(nombre.toString())
                    list.add(device)
                }
            } else{
                Toast.makeText(this, "No se encontraron dispositivos bluetooth", Toast.LENGTH_SHORT).show()
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaNombre)
            lsView!!.adapter = adapter

            lsView!!.onItemClickListener = AdapterView.OnItemClickListener{_, _, position, _ ->
                val device: BluetoothDevice = list[position]
                socketCliente = device.createRfcommSocketToServiceRecord(myUUID)

                conectarCliente =
                    SocketClienteBluetooth(this, txtStatus!!, device, socketCliente!!, txtMensaje!!)
                Toast.makeText(this, "aqui", Toast.LENGTH_SHORT).show()
                conectarCliente!!.IniciarConexion()
                if (conectarCliente!!.r!!){
                    txtStatus!!.text = "Conectado"
                } else{
                    txtStatus!!.text = "No se logro conectar..."
                }

            }



        }
        escuchar = SocketServidorBluetooth(this, txtStatus!!, myBluetoothAdapter!!, socketAux!!, txtMsg!!, txtMensaje!!)

        btnEscuchar!!.setOnClickListener {
            txtStatus.text = escuchar!!.conexionStatus
        }
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

    @SuppressLint("MissingPermission")
    private fun habilitarBluetooth(){
        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.getAdapter()

        myBluetoothAdapter = bluetoothManager.adapter
        socketAux = myBluetoothAdapter!!.listenUsingRfcommWithServiceRecord(APP_NAME, myUUID)

        //Habilitar bluetooth
        if (!myBluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
            Toast.makeText(this, "Bluetooth Hhabilitado", Toast.LENGTH_SHORT).show()
        }
    }
}