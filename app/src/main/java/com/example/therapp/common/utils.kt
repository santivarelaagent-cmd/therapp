package com.example.therapp.common

import com.google.mlkit.vision.pose.PoseLandmark

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 10/3/2025
 * @version 1.0
 */
enum class Joint(val id: Int) {
    LEFT_ELBOW(PoseLandmark.LEFT_ELBOW),
    RIGHT_ELBOW(PoseLandmark.RIGHT_ELBOW),
    LEFT_KNEE(PoseLandmark.LEFT_KNEE),
    RIGHT_KNEE(PoseLandmark.RIGHT_KNEE),
    LEFT_SHOULDER(PoseLandmark.LEFT_SHOULDER),
    RIGHT_SHOULDER(PoseLandmark.RIGHT_SHOULDER),
    LEFT_HIP(PoseLandmark.LEFT_HIP),
    RIGHT_HIP(PoseLandmark.RIGHT_HIP)
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