package com.minhhop.core.usercase.file

import com.minhhop.core.data.repository.FileHelperRepository
import java.io.File
import java.io.IOException
import java.io.InputStream

class CreateImageFileWithInputStream(private val fileHelperRepository: FileHelperRepository) {
    @Throws(IOException::class)
    operator fun invoke(externalFilesDir: File, data: InputStream) : File? = fileHelperRepository.createImageFile(externalFilesDir, data)
}