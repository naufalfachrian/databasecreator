package id.bungamungil.databasecreator

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import java.io.File
import java.io.FileOutputStream

class DatabaseCreator {

    fun createFromAssets(assetName: String, withContext: Context, callback: DatabaseCreatorCallback) {
        try {
            if (databaseHasBeenCreated(determineFilePath(assetName, withContext))) {
                callback.databaseHasBeenExisted()
                return
            }
            copyFileFromAssets(assetName, withContext)
            callback.databaseHasBeenCreated()
        } catch (reason: Throwable) {
            callback.failedToCreateDatabase(reason)
        }
    }

    private fun databaseHasBeenCreated(onPath: String): Boolean {
        var db: SQLiteDatabase? = null
        try {
            val file = File(onPath)
            if (file.exists() && !file.isDirectory) {
                db = SQLiteDatabase.openDatabase(onPath, null, SQLiteDatabase.OPEN_READONLY)
            }
        } catch (reason: SQLiteException) {
            throw RuntimeException("Failed to check database exists because ${reason.message}.")
        }
        db?.close()
        return db != null
    }

    private fun copyFileFromAssets(assetName: String, withContext: Context) {
        try {
            val inputStream = withContext.assets.open(assetName)
            val outputFilePath = determineFilePath(assetName, withContext)
            val outputStream = FileOutputStream(outputFilePath)
            val buffer = ByteArray(1024)
            var length = inputStream.read(buffer)
            while (length > 0) {
                outputStream.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (reason: Throwable) {
            throw RuntimeException("Failed to copy file from assets because ${reason.message}")
        }
    }

    private fun determineFilePath(fromAssetName: String, withContext: Context): String {
        try {
            val outputDirPath = withContext.getExternalFilesDir(null)?.absolutePath
                    ?: throw RuntimeException("Output directory is null")
            return "$outputDirPath/$fromAssetName"
        } catch (reason: Throwable) {
            throw RuntimeException("Failed to determine file path because ${reason.message}")
        }
    }

}