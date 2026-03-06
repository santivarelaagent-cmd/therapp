package com.example.therapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.therapp.data.routines.local.*

/**
 * @author Santiago Varela Daza
 * @email svarela03@uan.edu.co
 * @github https://github.com/sanvarela03
 * @since 3/5/2026
 * @version 1.0
 */
@Database(
    entities = [
        ScheduledTrainingEntity::class,
        TherapyEntity::class,
        RoutineEntity::class,
        ExerciseEntity::class,
        DifficultyEntity::class,
        RangeEntity::class,
        SkeletonPointEntity::class,
        PointTrackedEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val routineDao: RoutineDao
}
