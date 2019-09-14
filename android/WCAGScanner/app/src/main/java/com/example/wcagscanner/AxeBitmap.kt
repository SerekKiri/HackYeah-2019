package com.example.wcagscanner

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.util.Base64
import android.util.Log
import com.deque.axe.android.colorcontrast.AxeColor
import com.deque.axe.android.colorcontrast.AxeImage
import com.deque.axe.android.wrappers.AxePoint
import com.deque.axe.android.wrappers.AxeRect
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

class AxeBitmap : AxeImage {
    private val base64png: String?
    @Transient
    private val bitmap: Bitmap?
    private val calculatedTextColor: Int = 0
    private var frame: AxeRect? = null
    private val row: Int = 0

    inner class BitmapCreationException internal constructor() : RuntimeException("Tried to create a null bitmap.")

    internal constructor() {
        this.bitmap = null
        this.base64png = null
        this.frame = AxeRect(0, 0, 0, 0)
    }

    override fun frame(): AxeRect? {
        return this.frame
    }

    fun isInBitmap(axePoint: AxePoint): Boolean {
        return axePoint.valueX >= this.frame!!.left && axePoint.valueX <= this.frame!!.right && axePoint.valueY >= this.frame!!.top && axePoint.valueY <= this.frame!!.bottom
    }

    override fun pixel(i: Int, i2: Int): AxeColor {
        return AxeColor(this.bitmap!!.getPixel(i, i2))
    }

    override fun toBase64Png(): String {
        return toBase64(CompressFormat.PNG)
    }

    constructor(bitmap2: Bitmap?) {
        if (bitmap2 != null) {
            this.bitmap = bitmap2.copy(bitmap2.config, true)
            this.base64png = toBase64(CompressFormat.PNG)
            this.frame = AxeRect(0, bitmap2.width, 0, bitmap2.height)
            return
        }
        throw BitmapCreationException()
    }

    fun toBase64(compressFormat: CompressFormat): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        this.bitmap!!.compress(compressFormat, 100, byteArrayOutputStream)
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0)
    }

    fun saveToFile(str: String) {
        try {
            val fileOutputStream = FileOutputStream(str)
            this.bitmap!!.compress(CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    fun setPixel(axePoint: AxePoint, i: Int) {
        if (isInBitmap(axePoint)) {
            setPixel(axePoint.valueX, axePoint.valueY, i)
        }
    }

    fun setPixel(i: Int, i2: Int, i3: Int) {
        setPixel(AxePoint(i, i2), i3)
    }
}