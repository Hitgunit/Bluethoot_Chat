package com.example.bluethoot_chat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Message
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class SocketClienteBluetooth(var context: Context, var tvStatus: TextView, var dispositivo: BluetoothDevice, var socketCliente: BluetoothSocket, var mMensaje: TextView) {
    var buffer: String? = null
    var btSocketCliente: BluetoothSocket? = null
    var r: Boolean? = null
    val STATE_LISTENING = 1
    val STATE_CONNECTING = 2
    val STATE_CONECTED = 3
    val STATE_CONNECTION_FAILED = 4
    val STATE_MESSAGE_RECEIVED = 5

    init {
        try {

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    public fun IniciarConexion() {
        conectar()
    }

    @SuppressLint("MissingPermission")
    fun conectar() {
        var mensaje: Message? = null
        try {
            socketCliente!!.connect()
            btSocketCliente = socketCliente
            mensaje = Message.obtain()
            mensaje.what = STATE_CONECTED
            r = true
            Thread.sleep(1000)
            GlobalScope.launch (Dispatchers.IO) {
                leer()
            }

        } catch (e: IOException) {
            e.printStackTrace()
            mensaje = Message.obtain()
            mensaje.what = STATE_CONNECTION_FAILED
            r = false
        }
    }

    //Metodo enviar datos
    public fun enviar(m: String){
        var r = "!$m"
        btSocketCliente!!.outputStream.write(r.toByteArray())
    }

    suspend fun leer(){
        //Iniciar enviar y recibir datos
        while (btSocketCliente != null){
            try {
                var a: Char? = null
                println("1####################################")
                a = btSocketCliente!!.inputStream.read().toChar()
                println("2 $a ####################################")
                if (a == '!'){
                    buffer = ""
                } else{
                    Thread.sleep(3)
                    buffer = "$buffer$a"
                    mMensaje.text=buffer
                }
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}