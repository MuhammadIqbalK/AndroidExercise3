package com.eunoiagroup.retrofitjson

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SendAjaxRequestThread(private val handler: Handler) : Thread() {
    override fun run() {

        val url = URL("http://example.com/api/endpoint")
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")

        val data = """
      {
        "name": "John Smith",
        "age": 30
      }
    """

        val outputStream = connection.outputStream
        outputStream.write(data.toByteArray())
        outputStream.close()

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = reader.readLine()
            val message = Message.obtain()
            message.obj = response
            handler.sendMessage(message)}
        else {
            val message = Message.obtain()
            message.obj = "Error: $responseCode"
            handler.sendMessage(message)
        }
    }
}

