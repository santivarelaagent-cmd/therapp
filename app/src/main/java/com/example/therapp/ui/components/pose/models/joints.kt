package com.example.therapp.ui.components.pose.models

import com.google.mlkit.vision.pose.PoseLandmark

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 10/3/2025
 * @version 1.0
 */
enum class Joint(val id: Int, val jointName: String, val minAngle: Double, val maxAngle: Double) {
    LEFT_ELBOW(PoseLandmark.LEFT_ELBOW, minAngle = 0.0, maxAngle = 180.0 , jointName ="Codo Izquierdo"),
    RIGHT_ELBOW(PoseLandmark.RIGHT_ELBOW, minAngle = 0.0, maxAngle = 180.0, jointName = "Codo Derecho"),
    LEFT_KNEE(PoseLandmark.LEFT_KNEE, minAngle = 0.0, maxAngle = 180.0, jointName = "Rodilla Izquierda"),
    RIGHT_KNEE(PoseLandmark.RIGHT_KNEE, minAngle = 0.0, maxAngle = 180.0, jointName = "Rodilla Derecha"),
    LEFT_SHOULDER(PoseLandmark.LEFT_SHOULDER, minAngle = 0.0, maxAngle = 180.0, jointName = "Hombro Izquierdo"),
    RIGHT_SHOULDER(PoseLandmark.RIGHT_SHOULDER, minAngle = 0.0, maxAngle = 180.0, jointName = "Hombro Derecho"),
    LEFT_HIP(PoseLandmark.LEFT_HIP, minAngle = 0.0, maxAngle = 180.0, jointName = "Cadera Izquierda"),
    RIGHT_HIP(PoseLandmark.RIGHT_HIP, minAngle = 0.0, maxAngle = 180.0, jointName = "Cadera Derecha")
}

// Definimos qué puntos forman cada ángulo
val jointDependencies = mapOf(
    Joint.LEFT_ELBOW to Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_WRIST),
    Joint.RIGHT_ELBOW to Pair(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_WRIST),
    Joint.LEFT_KNEE to Pair(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_ANKLE),
    Joint.RIGHT_KNEE to Pair(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_ANKLE),
    Joint.LEFT_SHOULDER to Pair(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_ELBOW),
    Joint.RIGHT_SHOULDER to Pair(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_ELBOW),
    Joint.LEFT_HIP to Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_KNEE),
    Joint.RIGHT_HIP to Pair(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_KNEE)
)