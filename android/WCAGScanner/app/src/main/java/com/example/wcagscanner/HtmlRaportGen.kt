package com.example.wcagscanner

import android.graphics.Bitmap
import android.os.Environment
import java.io.File.separator
import android.os.Environment.getExternalStorageDirectory
import java.io.File
import android.content.Context.MODE_PRIVATE
import android.util.Log
import java.io.OutputStreamWriter
import android.R.attr.bitmap
import android.graphics.Rect
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class HtmlRaportGen {
    companion object {
        fun generateReport(image: Bitmap, brokenRules: List<BrokenRule>): File {
            val baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()
            val fileName = "raport.html"
            val f = File(baseDir + File.separator + fileName)
            val outputStreamWriter =
                OutputStreamWriter(f.outputStream())
            val c = Calendar.getInstance().getTime()
            println("Current time => $c")

            val df = SimpleDateFormat("dd-MMM-yyyy")
            val formattedDate = df.format(c)
            outputStreamWriter.write("""
<html lang="en">
  <head>
    <title>WCAG Report</title>
    <style>
      table {
        border-collapse: collapse;
      }
      
      table, th, td {
        border: 1px solid black;
      }
    </style>
  </head>
  <body>
    <h1>WCAG Report</h1>
    <p>Date: """+ formattedDate + """</p>
    <table>
      <tr>
        <th>Screenshot</th>
        <th>Violation</th>
      </tr>
      """)
            var rulesPerView = mutableMapOf<String, MutableList<BrokenRule>>()
            brokenRules.forEach {
                if (!rulesPerView.containsKey(it.elementId)) {
                    rulesPerView[it.elementId] = mutableListOf<BrokenRule>()
                }
                Log.v("RULEEE", it.message)
                rulesPerView[it.elementId]?.add(it)
            }

            rulesPerView.keys.forEach {
                var item = rulesPerView[it]!!.first()
                val rect = Rect(item.left, item.top, item.right, item.bottom)

                val height = item.top - item.bottom
                val width = item.right - item.left
                val x = item.left
                val y = item.top - height
                Log.v("ASDASD", height.toString())
                Log.v("ASDASD", width.toString())
                Log.v("ASDASD", x.toString())
                Log.v("ASDASD", y.toString())
                var html: String = ""
try {
    var bmp = Bitmap.createBitmap(image, item.left, item.top, rect.width(), rect.height())

    val byteArrayOutputStream = ByteArrayOutputStream()
    bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)

    html = """<tr>
          <td>
            <img src="data:image/png;base64, """ + encoded + """"></img>
                 </td>
          <td>
            """
} catch (e: Throwable) {

    val byteArrayOutputStream = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
    html = """<tr>
          <td>
            <img src="data:image/png;base64, """ + encoded + """"></img>
                 </td>
          <td>
            """
}
                rulesPerView[it]?.forEach {
                        rule ->
                    html += ("<p>" + rule.message + "</p>" )
                }
                html += """"
          </td>
        </tr>
"""

                outputStreamWriter.write(html)



            }
            outputStreamWriter.close()

            return f
        }
    }
}