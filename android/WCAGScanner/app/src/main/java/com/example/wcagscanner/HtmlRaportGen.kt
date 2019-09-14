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


class HtmlRaportGen {
    companion object {
        fun generateReport(image: Bitmap, brokenRules: List<BrokenRule>): File {
            val baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()
            val fileName = "raport.html"
            val f = File(baseDir + File.separator + fileName)
            val outputStreamWriter =
                OutputStreamWriter(f.outputStream())

// Not sure if the / is on the path or not
            outputStreamWriter.write("<h1>WCAG raport</h1>")
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
                Log.v("ASD", item.left.toString())
                Log.v("ASD", item.right.toString())
                Log.v("ASD", item.top.toString())
                Log.v("ASD", item.bottom.toString())
                val rect = Rect(item.left, item.top, item.right, item.bottom)

                val height = item.top - item.bottom
                val width = item.right - item.left
                val x = item.left
                val y = item.top - height
                Log.v("ASDASD", height.toString())
                Log.v("ASDASD", width.toString())
                Log.v("ASDASD", x.toString())
                Log.v("ASDASD", y.toString())

                var bmp = Bitmap.createBitmap(image, item.left, item.top, rect.width(), rect.height())
                outputStreamWriter.write("<h2> Element with id " + item.elementName + "</h2>")
                val byteArrayOutputStream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)

                outputStreamWriter.write("<img src=\"data:image/png;base64, " + encoded + "\"</img>")

                rulesPerView[it]?.forEach {
                    rule ->
                    outputStreamWriter.write("<h3>- !! " + rule.message + "</h3>" )
                }

            }
            outputStreamWriter.close()

            return f
        }
    }
}