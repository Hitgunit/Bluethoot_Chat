package com.example.bluethoot_chat

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Build
import android.os.Message
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class SocketServidorBluetooth(var context: Context, var txtStatus: TextView, var bluetoothAdapter: BluetoothAdapter, var socketSer: BluetoothServerSocket,
    var txtMSG: TextView, var txtMensaje: TextView) {

    val STATE_LISTENING = 1
    val STATE_CONNECTING = 2
    val STATE_CONECTED = 3
    val STATE_CONNECTION_FAILED = 4
    val STATE_MESSAGE_RECEIVED = 5
    var conexionStatus: String="Esperar conexion"
    var btServerSocket: BluetoothServerSocket? = null
    public var socket: BluetoothSocket? = null
    var buffer: String?= null

    init {
        try {
            btServerSocket = socketSer
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun iniciarConexion(){
        Thread.sleep(1000)
        GlobalScope.launch (Dispatchers.IO) {
            conectar()
        }
    }

    suspend fun conectar(){
        var mensaje: Message

        while (socket==null){
            try {
                socket=btServerSocket!!.accept()
                mensaje= Message.obtain()
                mensaje.what = STATE_CONNECTING
                conexionStatus = "Conectado"
                //Iniciar enviar y recibir datos
                while (socket != null){
                    try {
                        var a: Char? = null
                        println("1####################################")
                        a = socket!!.inputStream.read().toChar()
                        println("2 $a ####################################")
                        if (a == '!'){
                            buffer = ""
                        } else{
                            Thread.sleep(3)
                            buffer = "$buffer$a"
                            txtMensaje.text=buffer
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                }
            }catch (e: IOException){
                e.printStackTrace()

                mensaje= Message.obtain()
                mensaje.what=STATE_CONNECTION_FAILED
                conexionStatus="Conexio fallo"
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            txtStatus.text = conexionStatus
        }
    }

    //Metodo enviar datos
    public fun enviar(m:String){
        var r = "!$m"
        socket!!.outputStream.write(r.toByteArray())
    }
}