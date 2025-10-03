package com.example.therapp.ui.components

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 8/15/2025
 * @version 1.0
 */

import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.google.mlkit.vision.pose.PoseLandmark
import java.lang.Math.toDegrees
import kotlin.math.abs
import kotlin.math.atan2

private const val TAG = "PoseOverlay"
private const val LANDMARK_STROKE_DP = 3f
private val DEFAULT_LINE_COLOR = Color(0xFF00FF00)

@Composable
fun PoseOverlay(
    results: PoseLandmarkerResult?,
    runningMode: RunningMode = RunningMode.LIVE_STREAM,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier,
    pointColor: Color = Color.Cyan,
    lineColor: Color = Color.LightGray
) {
    val density = LocalDensity.current
    val strokePx = remember { with(density) { LANDMARK_STROKE_DP.dp.toPx() } }
    val radiusPx = strokePx / 2f

    Canvas(modifier = modifier) {

        val paint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 40f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        if (results == null) return@Canvas

        val canvasW = size.width
        val canvasH = size.height
        Log.d(
            "PoseOverlay",
            "[ imageWidth: $imageWidth , imageHeight: $imageHeight ] , [ canvasW: $canvasW , canvasH: $canvasH ]"
        )


        // Escalado automático según el modo
        val scaleFactor = when (runningMode) {
            RunningMode.IMAGE, RunningMode.VIDEO -> {
                minOf(canvasW / imageWidth, canvasH / imageHeight)
            }

            RunningMode.LIVE_STREAM -> {
                maxOf(canvasW / imageWidth, canvasH / imageHeight)
            }
        }
        Log.d("runningMode: ", "$runningMode")
        Log.d("scaleFactor: ", "$scaleFactor")


        // Dibujar landmarks
        results.landmarks().forEachIndexed { personIndex, personLandmarks ->

            val rightShoulder = personLandmarks[PoseLandmark.RIGHT_SHOULDER]
            val rightElbow = personLandmarks[PoseLandmark.RIGHT_ELBOW]
            val rightWrist = personLandmarks[PoseLandmark.RIGHT_WRIST]

            drawAngleText(
                rightShoulder,
                rightElbow,
                rightWrist,
                imageWidth,
                scaleFactor,
                imageHeight,
                paint
            )

            val leftShoulder = personLandmarks[PoseLandmark.LEFT_SHOULDER]
            val leftElbow = personLandmarks[PoseLandmark.LEFT_ELBOW]
            val leftWrist = personLandmarks[PoseLandmark.LEFT_WRIST]

            drawAngleText(
                leftShoulder,
                leftElbow,
                leftWrist,
                imageWidth,
                scaleFactor,
                imageHeight,
                paint
            )


            // Dibujar conexiones
            PoseLandmarker.POSE_LANDMARKS.forEach { connection ->
                val start = personLandmarks[connection.start()]
                val end = personLandmarks[connection.end()]

                val sView =
                    Offset(
                        start.x() * imageWidth * scaleFactor,
                        start.y() * imageHeight * scaleFactor
                    )
                val eView =
                    Offset(end.x() * imageWidth * scaleFactor, end.y() * imageHeight * scaleFactor)

                drawLine(
                    color = lineColor,
                    start = sView,
                    end = eView,
                    strokeWidth = strokePx
                )
            }

            personLandmarks.forEachIndexed { index, lm ->
                val x = lm.x() * imageWidth * scaleFactor
                val y = lm.y() * imageHeight * scaleFactor

                Log.v(TAG, "person=$personIndex norm=(${lm.x()},${lm.y()}) view=($x,$y)")

                val isElbow =
                    (index == PoseLandmark.LEFT_ELBOW || index == PoseLandmark.RIGHT_ELBOW)
                val circleColor = if (isElbow) Color.Red else pointColor


                drawCircle(
                    color = circleColor,
                    radius = radiusPx * 2,
                    center = Offset(x, y)
                )

            }
        }
    }
}

private fun DrawScope.drawAngleText(
    rightShoulder: NormalizedLandmark,
    rightElbow: NormalizedLandmark,
    rightWrist: NormalizedLandmark,
    imageWidth: Int,
    scaleFactor: Float,
    imageHeight: Int,
    paint: Paint
) {
    val angle = angleBetweenPoints(
        rightShoulder.x(),
        rightShoulder.y(),
        rightElbow.x(),
        rightElbow.y(),
        rightWrist.x(),
        rightWrist.y()
    )

    drawContext.canvas.nativeCanvas.drawText(
        "${angle.toInt()}°",
        rightElbow.x() * imageWidth * scaleFactor + 20f, // un poco a la derecha
        rightElbow.y() * imageHeight * scaleFactor - 20f, // un poco arriba
        paint
    )

    drawArchAngle(
        rightShoulder,
        rightElbow,
        rightWrist,
        imageWidth,
        imageHeight,
        scaleFactor
    )
}

private fun DrawScope.drawArchAngle(
    rightShoulder: NormalizedLandmark,
    rightElbow: NormalizedLandmark,
    rightWrist: NormalizedLandmark,
    imageWidth: Int,
    imageHeight: Int,
    scaleFactor: Float,
) {
    // Coordenadas escaladas
    val sx = rightShoulder.x() * imageWidth * scaleFactor
    val sy = rightShoulder.y() * imageHeight * scaleFactor
    val ex = rightElbow.x() * imageWidth * scaleFactor
    val ey = rightElbow.y() * imageHeight * scaleFactor
    val wx = rightWrist.x() * imageWidth * scaleFactor
    val wy = rightWrist.y() * imageHeight * scaleFactor

// Ángulos absolutos en grados (ojo: Compose usa 0° = eje X, sentido horario)
    val shoulderAngle = toDegrees(atan2(sy - ey, sx - ex).toDouble()).toFloat()
    val wristAngle = toDegrees(atan2(wy - ey, wx - ex).toDouble()).toFloat()

// Barrido (sweep) = diferencia mínima entre ambos ángulos
    var sweepAngle = wristAngle - shoulderAngle
    if (sweepAngle > 180f) sweepAngle -= 360f
    if (sweepAngle < -180f) sweepAngle += 360f - sweepAngle  // opcional: ángulo agudo

// Rect centrado en el codo
    val radius = 25f
    val rectTopLeft = Offset(ex - radius, ey - radius)
    val rectSize = Size(radius * 2, radius * 2)

// Dibujar arco desde la línea hombro–codo hasta muñeca–codo
    drawArc(
        color = Color.Yellow,
        startAngle = shoulderAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(width = 6f),
        topLeft = rectTopLeft,
        size = rectSize
    )
}


fun angleBetweenPoints(ax: Float, ay: Float, bx: Float, by: Float, cx: Float, cy: Float): Float {
    // Calculamos los vectores BA y BC
    val abX = ax - bx
    val abY = ay - by
    val cbX = cx - bx
    val cbY = cy - by

    val angle1 = atan2(abY, abX)
    val angle2 = atan2(cbY, cbX)

    var angle = Math.toDegrees(abs(angle1 - angle2).toDouble()).toFloat()
    if (angle > 180) angle = 360 - angle
    return angle
}