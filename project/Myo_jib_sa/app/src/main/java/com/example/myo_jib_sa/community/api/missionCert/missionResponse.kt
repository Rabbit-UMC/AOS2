package com.example.myo_jib_sa.community.api.missionCert

import com.example.myo_jib_sa.base.BaseResponse

data class MissionResponse(
    val result: MissionResult
): BaseResponse()
data class MissionResult(
    val mainMissionId:Long,
    val mainMissionName:String,
    val startDay:String,
    val mainMissionContent:String,
    val rank:List<Rank>,
    val missionProofImages:List<MissionProofImages>,
    val dday:String,
)
data class Rank(
    val userId:Long,
    val userName:String
)
data class MissionProofImages(
    val imageId:Long,
    val userId:Long,
    val filePath:String,
    val isLike:Boolean
)

data class MCrecyclrImg(
    var data1:MissionProofImages,
    val data2:MissionProofImages,
    val data3:MissionProofImages
)
