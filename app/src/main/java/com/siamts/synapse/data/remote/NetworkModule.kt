package com.siamts.synapse.data.remote

/**
 * NetworkModule object providing access to Retrofit network clients and APIs.
 */
object NetworkModule {
    const val BASE_URL = RetrofitClient.BASE_URL

    val client: RetrofitClient get() = RetrofitClient
    val authApi: AuthApi get() = RetrofitClient.authApi
    val chaptersApi: ChaptersApi get() = RetrofitClient.chaptersApi
    val quizApi: QuizApi get() = RetrofitClient.quizApi
    val notesApi: NotesApi get() = RetrofitClient.notesApi
    val profileApi: ProfileApi get() = RetrofitClient.profileApi
    val homeApi: HomeApi get() = RetrofitClient.homeApi
    val subjectsApi: SubjectsApi get() = RetrofitClient.subjectsApi
    val learningPlanApi: LearningPlanApi get() = RetrofitClient.learningPlanApi
    val achievementsApi: AchievementsApi get() = RetrofitClient.achievementsApi
}

