package com.example.therapp.ui.components.pose.strategies.renderer


import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.example.therapp.ui.components.pose.models.Joint
import com.example.therapp.ui.components.pose.models.jointDependencies
import com.example.therapp.ui.components.pose.processors.PoseDataProcessor
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark


/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 10/17/2025
 * @version 1.0
 */
class AngleRenderer(
    private val landmarks: List<NormalizedLandmark>,
    private val trackedPoints: List<Joint>,
    private val processor: PoseDataProcessor,
    private val paint: Paint
) : PoseRenderer {
    override fun DrawScope.render() {
        trackedPoints.forEach { joint ->
            val deps = jointDependencies[joint] ?: return@forEach
            val angleData = processor.processAngle(
                landmarks[deps.first],
                landmarks[joint.id],
                landmarks[deps.second]
            )

            // Dibujar texto del ángulo
            drawContext.canvas.nativeCanvas.drawText(
                "${angleData.degrees.toInt()}°",
                angleData.position.x,
                angleData.position.y,
                paint
            )

            // Dibujar arco
            drawArc(
                color = Color.Yellow,
                startAngle = angleData.arcData.startAngle,
                sweepAngle = angleData.arcData.sweepAngle,
                useCenter = false,
                style = Stroke(width = 6f),
                topLeft = Offset(
                    angleData.arcData.centerX - angleData.arcData.radius,
                    angleData.arcData.centerY - angleData.arcData.radius
                ),
                size = Size(
                    angleData.arcData.radius * 2,
                    angleData.arcData.radius * 2
                )
            )
        }
    }
}