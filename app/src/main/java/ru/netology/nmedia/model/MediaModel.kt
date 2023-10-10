package ru.netology.nmedia.model

import android.net.Uri
import ru.netology.nmedia.enumeration.AttachmentType
import java.io.File

data class MediaModel(
    val uri: Uri,
    val file: File,
    val type: AttachmentType,
)