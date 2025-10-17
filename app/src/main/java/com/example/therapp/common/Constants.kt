package com.example.therapp.common

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 7/30/2025
 * @version 1.0
 */
//private val IP = "172.27.80.1"
//private val IP = "172.31.16.1"s
private val IP = "192.168.2.102"
private val PORT = "8000"
private val SCHEME = "http"

//http://192.168.2.105:8000/
//val HOST_URL = "http://$IP:$PORT"
//val HOST_URL = "$SCHEME://$IP:$PORT"
val HOST_URL = "https://telerehabilitacion-be.onrender.com"


@RequiresApi(Build.VERSION_CODES.O)
fun String.toReadableDate(): String {
    return try {
        val dateTime = OffsetDateTime.parse(this) // parsea el ISO 8601 con zona horaria
        val formatter = DateTimeFormatter.ofPattern(
            "EEEE, d 'de' MMMM 'de' yyyy 'a las' h:mm a",
            Locale("es", "CO")
        )
        dateTime.format(formatter)
    } catch (e: Exception) {
        this // si falla el parseo, devuelve el string original
    }
}