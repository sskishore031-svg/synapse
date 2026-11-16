package com.siamts.synapse.data.local

interface LearningTaskDao {
    suspend fun getAllTasks(): List<LearningTaskEntity>
    suspend fun insertTask(task: LearningTaskEntity): Long
    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean)
    suspend fun deleteTask(taskId: Long)
}

