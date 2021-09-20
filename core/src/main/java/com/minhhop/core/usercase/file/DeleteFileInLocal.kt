package com.minhhop.core.usercase.file

import com.minhhop.core.data.repository.FileHelperRepository

class DeleteFileInLocal(private val fileHelperRepository: FileHelperRepository) {
    operator fun invoke(path:String) = fileHelperRepository.deleteFile(path)
}