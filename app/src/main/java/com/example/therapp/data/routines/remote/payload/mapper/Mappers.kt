package com.example.therapp.data.routines.remote.payload.mapper

import com.example.therapp.data.routines.local.DifficultyEntity
import com.example.therapp.data.routines.local.DifficultyWithRanges
import com.example.therapp.data.routines.local.ExerciseEntity
import com.example.therapp.data.routines.local.ExerciseWithDifficulties
import com.example.therapp.data.routines.local.RangeEntity
import com.example.therapp.data.routines.local.RoutineEntity
import com.example.therapp.data.routines.local.RoutineWithExercises
import com.example.therapp.data.routines.local.ScheduledTrainingEntity
import com.example.therapp.data.routines.local.ScheduledTrainingWithRoutine
import com.example.therapp.data.routines.local.TherapyEntity
import com.example.therapp.data.routines.remote.payload.res.DifficultyRes
import com.example.therapp.data.routines.remote.payload.res.ExercisesRes
import com.example.therapp.data.routines.remote.payload.res.RangeRes
import com.example.therapp.data.routines.remote.payload.res.RoutineRes
import com.example.therapp.data.routines.remote.payload.res.ScheduledTrainingRes
import com.example.therapp.data.routines.remote.payload.res.TherapyRes
import com.example.therapp.domain.model.Difficulty
import com.example.therapp.domain.model.Exercise
import com.example.therapp.domain.model.Range
import com.example.therapp.domain.model.Routine
import com.example.therapp.domain.model.ScheduledTraining
import com.example.therapp.domain.model.Therapy

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 3/5/2026
 * @version 1.0
 */
fun ScheduledTrainingRes.toEntity() = ScheduledTrainingEntity(
    id = id,
    routineId = routine.id,
    startTime = startTime,
    status = status
)

fun TherapyRes.toEntity() = TherapyEntity(
    id = id,
    name = name,
    description = description,
    isModel = isModel,
    isActive = isActive
)

fun RoutineRes.toEntity() = RoutineEntity(
    id = id,
    therapyId = therapy.id,
    name = name,
    description = description,
    isModeled = isModeled,
    isActive = isActive
)

fun ExercisesRes.toEntity() = ExerciseEntity(
    id = id,
    routineId = routineId,
    name = name,
    description = description,
    order = order,
    status = status,
    video = video,
    isModel = isModel,
    isActive = isActive
)

fun DifficultyRes.toEntity() = DifficultyEntity(
    id = id,
    exerciseId = exerciseId,
    name = name,
    description = description
)

fun RangeRes.toEntity(difficultyId: Int) = RangeEntity(
    difficultyId = difficultyId,
    pointTrackedId = pointTrackedId,
    maxAngle = maxAngle,
    minAngle = minAngle
)

// Domain Mappers
fun ScheduledTrainingWithRoutine.toDomain() = ScheduledTraining(
    id = scheduledTraining.id,
    routine = routine.toDomain(),
    startTime = scheduledTraining.startTime,
    status = scheduledTraining.status
)

fun RoutineWithExercises.toDomain() = Routine(
    id = routine.id,
    therapyId = routine.therapyId,
    therapy = therapy.toDomain(),
    name = routine.name,
    description = routine.description,
    exercises = exercises.map { it.toDomain() },
    isModeled = routine.isModeled,
    isActive = routine.isActive
)

fun TherapyEntity.toDomain() = Therapy(
    id = id,
    name = name,
    description = description,
    isModel = isModel,
    isActive = isActive
)

fun ExerciseWithDifficulties.toDomain() = Exercise(
    id = exercise.id,
    name = exercise.name,
    description = exercise.description,
    order = exercise.order,
    routineId = exercise.routineId,
    status = exercise.status,
    video = exercise.video,
    isModel = exercise.isModel,
    isActive = exercise.isActive,
    difficulties = difficulties.map { it.toDomain() }
)

fun DifficultyWithRanges.toDomain() = Difficulty(
    id = difficulty.id,
    exerciseId = difficulty.exerciseId,
    name = difficulty.name,
    description = difficulty.description,
    ranges = ranges.map { it.toDomain() }
)

fun RangeEntity.toDomain() = Range(
    id = id,
    pointTrackedId = pointTrackedId,
    pointTracked = null, // Can be fetched if needed
    difficultyId = difficultyId,
    maxAngle = maxAngle,
    minAngle = minAngle
)
