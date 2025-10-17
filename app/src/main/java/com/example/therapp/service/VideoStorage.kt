package com.example.therapp.service

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 10/15/2025
 * @version 1.0
 */
class VideoStorageRepository {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    suspend fun uploadVideo(
        videoUri: Uri,
        onProgress: (Float) -> Unit = {}
    ): Result<String> {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
            val fileName = "video_$timestamp.mp4"
            val videoRef = storageRef.child("videos/$fileName")

            // Subir el archivo
            val uploadTask = videoRef.putFile(videoUri)

            // Monitorear progreso
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress =
                    (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                onProgress(progress)
            }

            // Esperar que se complete
            uploadTask.await()

            // Obtener URL de descarga
            val downloadUrl = videoRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteVideo(videoUrl: String): Result<Unit> {
        return try {
            val videoRef = storage.getReferenceFromUrl(videoUrl)
            videoRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVideoMetadata(videoUrl: String): Result<Map<String, Any>> {
        return try {
            val videoRef = storage.getReferenceFromUrl(videoUrl)
            val metadata = videoRef.metadata.await()

            val metadataMap = mapOf(
                "name" to (metadata.name ?: ""),
                "size" to metadata.sizeBytes,
                "contentType" to (metadata.contentType ?: ""),
                "createdTime" to (metadata.creationTimeMillis)
            )

            Result.success(metadataMap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}