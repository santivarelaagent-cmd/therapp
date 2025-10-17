package com.example.therapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.therapp.ui.presenter.pose_camera.UploadState

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 10/15/2025
 * @version 1.0
 */
@Composable
fun UploadProgressOverlay(
    uploadState: UploadState,
    uploadProgress: Float,
    onDismiss: () -> Unit
) {
    if (uploadState !is UploadState.Idle) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(enabled = false) { },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(32.dp)
                    .width(300.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (uploadState) {
                        is UploadState.Uploading -> {
                            CircularProgressIndicator()
                            Text(
                                text = "Subiendo video...",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${uploadProgress.toInt()}%",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            LinearProgressIndicator(
                                progress = uploadProgress / 100f,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        is UploadState.Success -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color.Green,
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = "¡Video subido exitosamente!",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "URL: ${uploadState.downloadUrl.take(50)}...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(onClick = onDismiss) {
                                Text("Cerrar")
                            }
                        }
                        is UploadState.Error -> {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = "Error al subir video",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = uploadState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = onDismiss) {
                                Text("Cerrar")
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}