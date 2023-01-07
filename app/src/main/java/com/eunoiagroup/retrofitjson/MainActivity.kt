package com.eunoiagroup.retrofitjson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import com.google.gson.Gson


class SendAjaxRequestThreads(private val handler: Handler) : Thread() {
    override fun run() {

        val baseUrl = "https://reqres.in/api/users/2"



        val url = URL(baseUrl)
        val connection = url.openConnection() as HttpsURLConnection

        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        val gson = Gson()
        val data = """
     {
  "data": {
    "id": 2,
    "email": "janet.weaver@reqres.in",
    "first_name": "Janet",
    "last_name": "Weaver",
    "avatar": "https://reqres.in/img/faces/2-image.jpg"
  },
  "support": {
    "url": "https://reqres.in/#support-heading",
    "text": "To keep ReqRes free, contributions towards server costs are appreciated!"
  }
}
    """
        val outputStream = connection.outputStream
        outputStream.write(data.toByteArray())
        outputStream.close()


        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_CREATED) {
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



class MainActivity : AppCompatActivity() {

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val textView: TextView = findViewById(R.id.response_text)
                textView.text = msg.obj as String
            }
        }

        sendAjaxRequest()
    }

    private fun sendAjaxRequest() {

        val thread = SendAjaxRequestThreads(handler)
        thread.start()
    }


}