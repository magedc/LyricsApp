package com.magstore.lyricsapp.presentation.helpers.utils

import android.content.Context
import android.content.res.AssetManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun readAssetFile(context: Context, fileName: String): String {
    val assetManager: AssetManager = context.assets
    val bufferedReader: BufferedReader

    try {
        bufferedReader = BufferedReader(InputStreamReader(assetManager.open(fileName)))
        var line: String?
        var Result = ""
        while (bufferedReader.readLine().also { line = it } != null) {
            if(line?.trim() == "")
                continue
            Result += line + "\r\n"
        }
        return Result
    } catch (e: IOException) {
        e.printStackTrace()
        return ""
    }


}

