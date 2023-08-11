package com.example.myo_jib_sa.community.missionCert

import androidx.lifecycle.ViewModel
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionResponse

class MissionCertViewModel : ViewModel() {
    var missionResponse: MissionResponse? = null
    var day: Int = 0
    var lastDay: Int = 0
}