package com.example.myo_jib_sa.schedule.historyActivity.api.profile_nickName

import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMission.ProfileNickNameResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ProfileNickNameService {
    companion object {
        private const val authKey = "" //Authorization쓰기!!
    }

    @GET("app/users/profile")//?Authorization=$authKey
    fun profileNickNameMission(
        @Header("X-ACCESS-TOKEN")
        accessToken: String?
    ) : Call<ProfileNickNameResponse>
}
