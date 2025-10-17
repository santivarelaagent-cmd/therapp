/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.therapp.ui.presenter.pose_camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.therapp.ui.components.pose.models.Joint
import com.example.therapp.ui.components.CircularBtn
import com.example.therapp.ui.components.pose.PoseOverlay
import com.example.therapp.ui.components.UploadProgressOverlay
import com.example.therapp.ui.navigation.routes.CameraRoutes
import com.example.therapp.ui.presenter.pose_camera.helper.PoseLandmarkerHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    val previewView = remember { PreviewView(context) }
    var imageAnalyzer by remember { mutableStateOf<ImageAnalysis?>(null) }
    var cameraFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

    var imageWidth by remember { mutableStateOf(1) }
    var imageHeight by remember { mutableStateOf(1) }


    // Observar los valores del ViewModel
    val currentDelegate by viewModel.currentDelegate.collectAsState()
    val currentModel by viewModel.currentModel.collectAsState()
    val minDetectionConfidence by viewModel.currentMinPoseDetectionConfidence.collectAsState()
    val minTrackingConfidence by viewModel.currentMinPoseTrackingConfidence.collectAsState()
    val minPresenceConfidence by viewModel.currentMinPosePresenceConfidence.collectAsState()

    // PoseLandmarkerHelper con valores actuales
    val poseLandmarkerHelper = remember(
        currentDelegate,
        currentModel,
        minDetectionConfidence,
        minTrackingConfidence,
        minPresenceConfidence
    ) {
        PoseLandmarkerHelper(
            context = context,
            runningMode = RunningMode.LIVE_STREAM,
            minPoseDetectionConfidence = minDetectionConfidence,
            minPoseTrackingConfidence = minTrackingConfidence,
            minPosePresenceConfidence = minPresenceConfidence,
            currentDelegate = currentDelegate,
            currentModel = currentModel
        )
    }

    // Observar resultados de MediaPipe
    val poseResults by poseLandmarkerHelper.poseResults.collectAsState()
    val inferenceTime by poseLandmarkerHelper.inferenceTime.collectAsState()
    val error by poseLandmarkerHelper.error.collectAsState()
    val inputImageWidth by poseLandmarkerHelper.inputImageWidth.collectAsState()
    val inputImageHeight by poseLandmarkerHelper.inputImageHeight.collectAsState()


    var isTorchOn by remember { mutableStateOf(false) }

    var zoomRatio by remember { mutableStateOf(1f) }

    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    var isRecording by remember { mutableStateOf(false) }


    var selectedJoints by remember {
        mutableStateOf(
            setOf<Joint>(
                Joint.LEFT_ELBOW,
                Joint.RIGHT_ELBOW,
                Joint.RIGHT_KNEE,
                Joint.LEFT_HIP,
                Joint.LEFT_SHOULDER
            )
        )
    }
    var showJointSelector by remember { mutableStateOf(false) }

    val uploadState by viewModel.uploadState.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()

    LaunchedEffect(camera) {
        // Observar zoom inicial al enlazar cámara
        val zoomState = camera?.cameraInfo?.zoomState?.value
        zoomRatio = zoomState?.zoomRatio ?: 1f
    }


    Log.d("CameraScreen", "PoseResults: $poseResults")
    // Mostrar errores
    LaunchedEffect(error) {
        error?.let { errorMessage ->
            Log.e("CameraScreen", errorMessage)
        }
    }

    // Verificar permisos de cámara
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false

        if (!cameraGranted) {
            navController.navigate(CameraRoutes.Permissions) {
                popUpTo(CameraRoutes.Camera) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (!checkCameraPermission(context)) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (!checkAudioPermission(context)) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    // Configurar cámara
    LaunchedEffect(lifecycleOwner, cameraFacing) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProvider = cameraProviderFuture.get()


        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(
                AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY // o RATIO_16_9 si prefieres
            )
            .build()


        preview = Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .setTargetRotation(previewView.display.rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setResolutionSelector(resolutionSelector)
            .setTargetRotation(previewView.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also { analyzer ->
                analyzer.setAnalyzer(
                    ContextCompat.getMainExecutor(context)
                ) { imageProxy ->
                    // Procesar con MediaPipe
                    imageWidth = imageProxy.width
                    imageHeight = imageProxy.height

                    poseLandmarkerHelper
                        .detectLiveStream(
                            imageProxy = imageProxy,
                            isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
                        )
                }
            }
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HD))
            .build()

        videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider?.unbindAll()
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.Builder().requireLensFacing(cameraFacing).build(),
                preview,
                imageAnalyzer,
                videoCapture
            )
        } catch (e: Exception) {
            Log.e("CameraScreen", "Error configurando cámara: ${e.message}")
        }
    }

    // Limpiar recursos al salir
    DisposableEffect(lifecycleOwner) {
        onDispose {
            recording?.stop()
            cameraProvider?.unbindAll()
            poseLandmarkerHelper.clearPoseLandmarker()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Vista previa de la cámara
            AndroidView(
                factory = { context ->
                    previewView.apply {
                        this.scaleType = PreviewView.ScaleType.FILL_START
                    }
                },
                modifier = Modifier
//                    .padding(16.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
//                    .border(2.dp, Color.Red)
                update = { previewView ->
                    preview?.setSurfaceProvider(previewView.surfaceProvider)
                },
            )


            // Overlay de poses con resultados reales de MediaPipe
            PoseOverlay(
                results = poseResults,
                imageWidth = inputImageWidth,
                imageHeight = inputImageHeight,
                trackedPoints = selectedJoints.toList(),
                modifier = Modifier
//                    .padding(50.dp)
                    .fillMaxSize()
//                    .border(2.dp, Color.Green)
            )

            // Controles de la cámara
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp, start = 5.dp, end = 5.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.Top)

                    ) {
                        if (inferenceTime > 0) {
                            Card(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .width(115.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Black.copy(alpha = 0.2f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                ) {
                                    Text(
                                        text = "Inferencia: ${inferenceTime}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "ms",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }
                        if (isRecording) {
                            Card(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .width(115.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Red.copy(alpha = 0.7f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()  // Agregar esto
                                        .padding(
                                            horizontal = 5.dp,
                                            vertical = 4.dp
                                        ),  // Agregar padding vertical
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center  // Agregar esto para centrar
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Circle,
                                        contentDescription = "Recording",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "GRABANDO",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.Top)

                    ) {
                        CircularBtn(
                            icon = Icons.Default.Settings,
                            size = 40,
                            onClick = { showJointSelector = true }
                        )

                        CircularBtn(
                            size = 40,
                            icon = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            onClick = {
                                isTorchOn = !isTorchOn
                                camera?.cameraControl?.enableTorch(isTorchOn)
                            }
                        )
                        CircularBtn(
                            size = 40,
                            icon = Icons.Default.Add, // puedes poner otro ícono como Add
                            onClick = {
                                val maxZoom =
                                    camera?.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 5f
                                val newZoom = (zoomRatio + 0.1f).coerceAtMost(maxZoom)
                                camera?.cameraControl?.setZoomRatio(newZoom)
                                zoomRatio = newZoom
                            }
                        )
                        CircularBtn(
                            size = 40,
                            icon = Icons.Default.Remove, // puedes poner otro ícono como Remove
                            onClick = {
                                val newZoom = (zoomRatio - 0.1f).coerceAtLeast(
                                    camera?.cameraInfo?.zoomState?.value?.minZoomRatio ?: 1f
                                )
                                camera?.cameraControl?.setZoomRatio(newZoom)
                                zoomRatio = newZoom
                            }
                        )


                    }

                }
            }
            // Botón de galería
            LeftBottomMenu(
                viewModel = viewModel,
                onVideoSelected = { uri ->
                    viewModel.uploadVideo(uri)
                }
            )
            BottomCenterMenu(
                isRecording = isRecording,
                onRecordClick = {
                    if (isRecording) {
                        // Detener grabación
                        recording?.stop()
                        recording = null
                        isRecording = false
                    } else {
                        // Iniciar grabación
                        startRecording(
                            context = context,
                            videoCapture = videoCapture,
                            onRecordingStarted = { activeRecording ->
                                recording = activeRecording
                                isRecording = true
                            },
                            onError = { errorMsg ->
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            )
            RightBottomMenu(
                cameraFacing,
                onCameraFacingChange = { newFacing ->
                    cameraFacing =
                        if (newFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                }
            )
            if (showJointSelector) {
                JointSelectorDialog(
                    selectedJoints = selectedJoints,
                    onJointsChanged = { selectedJoints = it },
                    onDismiss = { showJointSelector = false }
                )
            }
            UploadProgressOverlay(
                uploadState = uploadState,
                uploadProgress = uploadProgress,
                onDismiss = { viewModel.resetUploadState() }
            )
        }
    }
}

@Composable
private fun BottomCenterMenu(
    isRecording: Boolean,
    onRecordClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isRecording) {
            // Botón cuando está grabando: círculo rojo con icono Stop blanco
            IconButton(
                modifier = Modifier.size(85.dp),
                onClick = onRecordClick
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Círculo rojo de fondo
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = "Stop recording background",
                        modifier = Modifier.size(85.dp),
                        tint = Color.Red
                    )
                    // Icono Stop blanco en el centro
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop recording",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
            }
        } else {
            // Botón cuando no está grabando: círculo blanco
            IconButton(
                modifier = Modifier.size(85.dp),
                onClick = onRecordClick
            ) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "Start recording",
                    modifier = Modifier.size(85.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun RightBottomMenu(
    cameraFacing: Int,
    onCameraFacingChange: (Int) -> Unit = {}
) {
    var cameraFacing1 = cameraFacing
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            CircularBtn(
                icon = Icons.Default.Cameraswitch,
                size = 40,
                onClick = {
                    onCameraFacingChange(cameraFacing1)
                }
            )
        }
    }
}

@Composable
private fun LeftBottomMenu(
    viewModel: MainViewModel,
    onVideoSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val mimeType = context.contentResolver.getType(it)

                when {
                    mimeType?.startsWith("video/") == true -> {
                        Toast.makeText(
                            context,
                            "Video seleccionado, subiendo...",
                            Toast.LENGTH_SHORT
                        ).show()
                        onVideoSelected(it)
                    }

                    mimeType?.startsWith("image/") == true -> {
                        Toast.makeText(context, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        Toast.makeText(context, "Archivo no compatible", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                Toast.makeText(context, "No se seleccionó ningún archivo", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    )
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            CircularBtn(
                size = 40,
                icon = Icons.Default.PermMedia,
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                    )
                }
            )
        }
    }
}

@Composable
fun JointSelectorDialog(
    selectedJoints: Set<Joint>,
    onJointsChanged: (Set<Joint>) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Seleccionar Articulaciones",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Selecciona las articulaciones a rastrear:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(Joint.values()) { joint ->
                    JointCheckboxItem(
                        joint = joint,
                        isSelected = selectedJoints.contains(joint),
                        onToggle = {
                            val newSet = selectedJoints.toMutableSet()
                            if (newSet.contains(joint)) {
                                newSet.remove(joint)
                            } else {
                                newSet.add(joint)
                            }
                            onJointsChanged(newSet)
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onJointsChanged(setOf())
                }
            ) {
                Text("Limpiar todo")
            }
        }
    )
}

@Composable
private fun JointCheckboxItem(
    joint: Joint,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = joint.jointName,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


private fun checkCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

private fun checkAudioPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}

private fun startRecording(
    context: Context,
    videoCapture: VideoCapture<Recorder>?,
    onRecordingStarted: (Recording) -> Unit,
    onError: (String) -> Unit
) {
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
        .format(System.currentTimeMillis())

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/TherApp")
        }
    }

    val mediaStoreOutputOptions = MediaStoreOutputOptions
        .Builder(context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        .setContentValues(contentValues)
        .build()

    try {
        val recording = videoCapture?.output
            ?.prepareRecording(context, mediaStoreOutputOptions)
            ?.apply {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            ?.start(ContextCompat.getMainExecutor(context), Consumer { videoRecordEvent ->
                when (videoRecordEvent) {
                    is VideoRecordEvent.Start -> {
                        Log.d("CameraScreen", "Recording started")
                    }

                    is VideoRecordEvent.Finalize -> {
                        if (!videoRecordEvent.hasError()) {
                            val msg = "Video guardado: ${videoRecordEvent.outputResults.outputUri}"
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            Log.d("CameraScreen", msg)
                        } else {
                            val errorMsg = "Error al grabar: ${videoRecordEvent.error}"
                            onError(errorMsg)
                            Log.e("CameraScreen", errorMsg)
                        }
                    }
                }
            })

        if (recording != null) {
            onRecordingStarted(recording)
        } else {
            onError("Error al iniciar la grabación")
        }
    } catch (e: Exception) {
        onError("Error: ${e.message}")
        Log.e("CameraScreen", "Error al iniciar grabación", e)
    }
}