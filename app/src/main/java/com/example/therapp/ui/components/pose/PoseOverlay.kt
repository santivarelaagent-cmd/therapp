package com.example.therapp.ui.components.pose

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
import com.example.therapp.ui.components.pose.models.Joint
import com.example.therapp.ui.components.pose.models.jointDependencies
import com.example.therapp.ui.components.pose.processors.PoseDataProcessor
import com.example.therapp.ui.components.pose.strategies.renderer.PoseRendererFactory
import com.example.therapp.ui.components.pose.strategies.scale.ScaleStrategyFactory
import com.example.therapp.ui.components.pose.transformers.CoordinateTransformer
import com.google.mediapipe.tasks.components.containers.Connection
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.lang.Math.toDegrees
import kotlin.math.abs
import kotlin.math.atan2


private const val TAG = "PoseOverlay"
private const val LANDMARK_STROKE_DP = 3f
private val DEFAULT_LINE_COLOR = Color(0xFF00FF00)

@Composable
fun PoseOverlay(
    modifier: Modifier = Modifier,
    runningMode: RunningMode = RunningMode.LIVE_STREAM,
    imageWidth: Int,
    imageHeight: Int,
    pointColor: Color = Color.Cyan,
    lineColor: Color = Color.LightGray,
    results: PoseLandmarkerResult?,
    trackedPoints: List<Joint> = emptyList()
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

        // Escalado automático según el modo
        val scaleStrategy = ScaleStrategyFactory.create(runningMode)
        val scaleFactor = scaleStrategy.calculate(size.width, size.height, imageWidth, imageHeight)

        val transformer = CoordinateTransformer(imageWidth, imageHeight, scaleFactor)
        val processor = PoseDataProcessor(transformer)

        val rendererFactory = PoseRendererFactory(
            transformer,
            pointColor,
            lineColor,
            strokePx,
            radiusPx,
            paint
        )
        results.landmarks().forEach { personLandmarks ->
            val renderer = rendererFactory.createRenderer(
                personLandmarks,
                trackedPoints,
                processor
            )
            with(renderer) { render() }
        }


        // Dibujar landmarks
//        results.landmarks()
//            .forEachIndexed { personIndex, personLandmarks: List<NormalizedLandmark> ->
//                drawTrackedPoints(
//                    personLandmarks,
//                    trackedPoints,
//                    imageWidth,
//                    scaleFactor,
//                    imageHeight,
//                    paint
//                )
//                PoseLandmarker.POSE_LANDMARKS.forEach { connection ->
//                    drawConnectionLines(
//                        personLandmarks,
//                        connection,
//                        imageWidth,
//                        scaleFactor,
//                        imageHeight,
//                        lineColor,
//                        strokePx
//                    )
//                }
//                personLandmarks.forEachIndexed { index, lm: NormalizedLandmark ->
//                    drawJoints(
//                        lm,
//                        trackedPoints,
//                        imageWidth,
//                        scaleFactor,
//                        imageHeight,
//                        personIndex,
//                        index,
//                        pointColor,
//                        radiusPx
//                    )
//                }
//            }
    }
}

private fun DrawScope.drawTrackedPoints(
    personLandmarks: List<NormalizedLandmark>,
    trackedPoints: List<Joint>,
    imageWidth: Int,
    scaleFactor: Float,
    imageHeight: Int,
    paint: Paint
) {
    trackedPoints.forEach { joint: Joint ->
        drawAngle(
            personLandmarks[jointDependencies[joint]!!.first],
            personLandmarks[joint.id],
            personLandmarks[jointDependencies[joint]!!.second],
            imageWidth,
            scaleFactor,
            imageHeight,
            paint
        )
    }
}

private fun DrawScope.drawJoints(
    lm: NormalizedLandmark,
    trackedPoints: List<Joint>,
    imageWidth: Int,
    scaleFactor: Float,
    imageHeight: Int,
    personIndex: Int,
    index: Int,
    pointColor: Color,
    radiusPx: Float
) {
    val x = lm.x() * imageWidth * scaleFactor
    val y = lm.y() * imageHeight * scaleFactor

    Log.v(TAG, "person=$personIndex norm=(${lm.x()},${lm.y()}) view=($x,$y)")

    val isTracked = trackedPoints.any { it.id == index }

    val circleColor = if (isTracked) Color.Red else pointColor


    drawCircle(
        color = circleColor,
        radius = radiusPx * 2,
        center = Offset(x, y)
    )
}

private fun getScaleFactor(
    runningMode: RunningMode,
    canvasW: Float,
    imageWidth: Int,
    canvasH: Float,
    imageHeight: Int
) = when (runningMode) {
    RunningMode.IMAGE, RunningMode.VIDEO -> {
        minOf(canvasW / imageWidth, canvasH / imageHeight)
    }

    RunningMode.LIVE_STREAM -> {
        maxOf(canvasW / imageWidth, canvasH / imageHeight)
    }
}

private fun DrawScope.drawConnectionLines(
    personLandmarks: List<NormalizedLandmark>,
    connection: Connection,
    imageWidth: Int,
    scaleFactor: Float,
    imageHeight: Int,
    lineColor: Color,
    strokePx: Float
) {
    val start = personLandmarks[connection.start()]
    val end = personLandmarks[connection.end()]

    val sView =
        Offset(
            start.x() * imageWidth * scaleFactor,
            start.y() * imageHeight * scaleFactor
        )
    val eView =
        Offset(
            end.x() * imageWidth * scaleFactor,
            end.y() * imageHeight * scaleFactor
        )

    drawLine(
        color = lineColor,
        start = sView,
        end = eView,
        strokeWidth = strokePx
    )
}

private fun DrawScope.drawAngle(
    p1: NormalizedLandmark,
    center: NormalizedLandmark,
    p2: NormalizedLandmark,
    imageWidth: Int,
    scaleFactor: Float,
    imageHeight: Int,
    paint: Paint
) {
    val angle = angleBetweenPoints(
        p1.x(),
        p1.y(),
        center.x(),
        center.y(),
        p2.x(),
        p2.y()
    )

    drawContext.canvas.nativeCanvas.drawText(
        "${angle.toInt()}°",
        center.x() * imageWidth * scaleFactor + 20f, // un poco a la derecha
        center.y() * imageHeight * scaleFactor - 20f, // un poco arriba
        paint
    )

    drawArchAngle(
        p1,
        center,
        p2,
        imageWidth,
        imageHeight,
        scaleFactor
    )
}

private fun DrawScope.drawArchAngle(
    p1: NormalizedLandmark,
    center: NormalizedLandmark,
    p2: NormalizedLandmark,
    imageWidth: Int,
    imageHeight: Int,
    scaleFactor: Float,
) {
    // Coordenadas escaladas
    val p1x = p1.x() * imageWidth * scaleFactor
    val p1y = p1.y() * imageHeight * scaleFactor
    val cx = center.x() * imageWidth * scaleFactor
    val cy = center.y() * imageHeight * scaleFactor
    val p2x = p2.x() * imageWidth * scaleFactor
    val p2y = p2.y() * imageHeight * scaleFactor

// Ángulos absolutos en grados (ojo: Compose usa 0° = eje X, sentido horario)
    val p1Angle = toDegrees(atan2(p1y - cy, p1x - cx).toDouble()).toFloat()
    val p2Angle = toDegrees(atan2(p2y - cy, p2x - cx).toDouble()).toFloat()

// Barrido (sweep) = diferencia mínima entre ambos ángulos
    var sweepAngle = p2Angle - p1Angle
    if (sweepAngle > 180f) sweepAngle -= 360f
    if (sweepAngle < -180f) sweepAngle += 360f - sweepAngle  // opcional: ángulo agudo

// Rect centrado en el codo
    val radius = 25f
    val rectTopLeft = Offset(cx - radius, cy - radius)
    val rectSize = Size(radius * 2, radius * 2)

// Dibujar arco desde la línea hombro–codo hasta muñeca–codo
    drawArc(
        color = Color.Yellow,
        startAngle = p1Angle,
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
