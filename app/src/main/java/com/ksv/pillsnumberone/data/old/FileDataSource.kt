package com.ksv.pillsnumberone.data.old

import android.content.Context
import android.widget.Toast
import com.ksv.pillsnumberone.entity.old.MedicineItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

class FileDataSource(private val context: Context) {

    fun saveData(medicineMap: Map<String, List<MedicineItem>>) {
        val text = Json.encodeToString(medicineMap)

        var fos: FileOutputStream? = null
        try {
            fos = context.openFileOutput(PILLS_FILE_NAME, Context.MODE_PRIVATE)
            fos.write(text.toByteArray())
        } catch (ex: IOException) {
            Toast.makeText(context, FILE_WRITE_ERROR_MSG, Toast.LENGTH_SHORT).show()
        } finally {
            fos?.close()
        }

    }

    fun loadData(): Map<String, List<MedicineItem>> {

        val file = File(context.filesDir.absolutePath, PILLS_FILE_NAME)

        if (!file.exists()) {
            return emptyMap()
        }

        var fis: FileInputStream? = null
        try {
            fis = context.openFileInput(PILLS_FILE_NAME)
            val inputStreamReader = InputStreamReader(fis)
            val text = inputStreamReader.readText()
            val map = Json.decodeFromString<Map<String, List<MedicineItem>>>(text)
            return map

        } catch (ex: IOException) {
            Toast.makeText(context, FILE_READ_ERROR_MSG, Toast.LENGTH_SHORT).show()
            return emptyMap()
        } finally {
            fis?.close()
        }
    }

    companion object {
        private const val PILLS_FILE_NAME = "pills.txt"
        private const val FILE_WRITE_ERROR_MSG = "Ошибка записи файла"
        private const val FILE_READ_ERROR_MSG = "Ошибка чтения файла"
    }
}