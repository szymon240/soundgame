package pl.soundgame.playerutils

import pl.soundgame.modes.InstrumentalMode

data class UserData(
    var nickname: String = "player",
    var achievements: MutableList<String> = mutableListOf(),
    var gamesPlayed: Int = 0,
    var totalScore: Double  = 0.0,
    var instrumental: Double  = 0.0,
    var rhythm: Double = 0.0,
    var pitch: Double = 0.0
)
//
//MutableMap<String, Any> = mutableMapOf(
//"nickname" to "",
//"achievements" to mutableListOf<String>(),
//"gameStats" to mutableMapOf<String, Any>(
//"gamesPlayed" to 0,
//"totalScore" to 0.0,
//"highScores" to mutableMapOf(
//"rhythm" to 0.0,
//"instrumental" to 0.0
//)
//)
//)