package net.jeikobu.uplewd.service

import org.springframework.web.multipart.MultipartFile

interface UploadFileService {
    fun saveFile(file: MultipartFile): Pair<String, String>
}