package com.modulotech.utilities

import com.modulotech.models.RecordFile
import java.io.File

fun getRecordingCallOnXiaoMi() : List<RecordFile> {
    return getPlayList("/storage/emulated/0/MIUI/sound_recorder/call_rec/")
}
fun getPlayList(rootPath: String): List<RecordFile> {
    val fileList = ArrayList<RecordFile>()
    val rootFolder = File(rootPath)
    val files: Array<File>? = rootFolder.listFiles()
    if (files != null) {
        for (file in files) {
            if (file.isFile && file.name.endsWith(".mp3")) {
                file.lastModified()
                fileList.add(
                    RecordFile(
                        fileName = file.name,
                        lastModified = file.lastModified()
                    )
                )
            }
        }
    }
    Logger.i(fileList.toString())
    return fileList
}