package com.example.therapp.ui.components.pose.processors

import com.example.therapp.ui.components.pose.models.Joint
import com.example.therapp.ui.components.pose.builders.ArcDataBuilder
import com.example.therapp.ui.components.pose.models.AngleData
import com.example.therapp.ui.components.pose.models.ScaledCoordinates
import com.example.therapp.ui.components.pose.strategies.angle.AngleCalculator
import com.example.therapp.ui.components.pose.strategies.angle.VectorAngleCalculator
import com.example.therapp.ui.components.pose.transformers.CoordinateTransformer
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark


/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 10/17/2025
 * @version 1.0
 */
class PoseDataProcessor(
    private val transformer: CoordinateTransformer,
    private val angleCalculator: AngleCalculator = VectorAngleCalculator(),
    private val arcBuilder: ArcDataBuilder = ArcDataBuilder(transformer)
) {
    fun processAngle(
        p1: NormalizedLandmark,
        center: NormalizedLandmark,
        p2: NormalizedLandmark
    ): AngleData {
        val angle = angleCalculator.calculate(p1, center, p2)
        val centerCoords = transformer.transform(center)
        val arcData = arcBuilder.build(p1, center, p2)

        return AngleData(
            degrees = angle,
            position = ScaledCoordinates(
                x = centerCoords.x + 20f,
                y = centerCoords.y - 20f
            ),
            arcData = arcData
        )
    }

    fun isTrackedJoint(jointId: Int, trackedPoints: List<Joint>): Boolean {
        return trackedPoints.any { it.id == jointId }
    }
}