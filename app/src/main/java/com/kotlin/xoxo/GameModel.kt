package com.kotlin.xoxo

import kotlin.random.Random

data class GameModel(
    var gameId: String = "-1",
    var filledPos: MutableList<String> = mutableListOf("", "", "", "", "", "", "", "", ""),
    var winner: String = "",
    var gameStatus: GameStatus = GameStatus.CREATED,
    var currentPlayer: String = arrayOf("X", "O")[Random.nextInt(2)],
    var roundsPlayed: Int = 0, //  rounds played
    var scoreX: Int = 0, // score for player X
    var scoreO: Int = 0 //  score for player O
)
