package com.focus617.myopengldemo.utils.helper

import android.content.Context
import android.content.res.Resources
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object TextResourceReader {
    /**
     * Reads in text from a resource file and returns a String containing the
     * text.
     */
    fun loadFromResourceFile(context: Context, resourceId: Int): String {

        Timber.d("loadFromResourceFile($resourceId)")

        val body = StringBuilder()

        try {
            val inputStream = context.resources.openRawResource(resourceId)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var nextLine: String?

            while (bufferedReader.readLine().also { nextLine = it } != null) {
                body.append(nextLine)
                body.append('\n')
            }
        } catch (e: IOException) {
            throw RuntimeException("Could not open resource: $resourceId $ e")
        } catch (nfe: Resources.NotFoundException) {
            throw RuntimeException("Resource not found: $resourceId$nfe")
        }
        return body.toString()
    }

    /**
     * Reads in text from a assets file and returns a String containing the
     * text.
     */
    fun loadFromAssetsFile(context: Context, filePath: String): String {

        Timber.d("loadFromAssetsFile($filePath)")

        val body = StringBuilder()

        try {
            val inputStream = context.resources.assets.open(filePath)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var nextLine: String?

            while (bufferedReader.readLine().also { nextLine = it } != null) {
                body.append(nextLine)
                body.append('\n')
            }
        } catch (e: IOException) {
            throw RuntimeException("Could not open shader file: $filePath $ e")
        }
        return body.toString()
    }
}