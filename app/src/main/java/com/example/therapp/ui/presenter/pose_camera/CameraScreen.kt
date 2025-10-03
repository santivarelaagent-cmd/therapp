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
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.therapp.ui.components.CircularBtn
import com.example.therapp.ui.components.PoseOverlay
import com.example.therapp.ui.navigation.routes.CameraRoutes
import com.example.therapp.ui.presenter.pose_camera.helper.PoseLandmarkerHelper
import com.google.mediapipe.tasks.vision.core.RunningMode

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
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            navController.navigate(CameraRoutes.Permissions) {
                popUpTo(CameraRoutes.Camera) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!checkCameraPermission(context)) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
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

        try {
            cameraProvider?.unbindAll()
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.Builder().requireLensFacing(cameraFacing).build(),
                preview,
                imageAnalyzer
            )
        } catch (e: Exception) {
            Log.e("CameraScreen", "Error configurando cámara: ${e.message}")
        }
    }

    // Limpiar recursos al salir
    DisposableEffect(lifecycleOwner) {
        onDispose {
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
                            onClick = {}
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
            LeftBottomMenu()
            BottomCenterMenu(navController)
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
        }
    }
}

@Composable
private fun BottomCenterMenu(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        IconButton(
            modifier = Modifier.size(85.dp),
            onClick = { navController.navigate(CameraRoutes.Gallery) }
        ) {
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = "Configuración",
                modifier = Modifier.size(85.dp),
                tint = Color.White
            )
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
private fun LeftBottomMenu() {
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
                onClick = {}
            )
        }
    }
}

private fun checkCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}