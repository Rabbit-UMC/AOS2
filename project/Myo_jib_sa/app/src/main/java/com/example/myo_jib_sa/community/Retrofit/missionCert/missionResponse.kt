package com.example.myo_jib_sa.community.Retrofit.missionCert

data class MissionResponse(

    val isSuccess:String,
    val code:Int,
    val message:String,
    val result: MissionResult
)
data class MissionResult(
    val mainMissionId:Long,
    val mainMissionName:String,
    val dday:String,
    val mainMissionContent:String,
    val rank:List<Rank>,
    val missionProofImages:List<MissionProofImages>
)
data class Rank(
    val userId:Long,
    val userName:String
)
data class MissionProofImages(
    val imageId:Int,
    val userId:Int,
    val filePath:String
)

data class MCrecyclrImg(
    var data1:MissionProofImages,
    val data2:MissionProofImages,
    val data3:MissionProofImages
)
