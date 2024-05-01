package com.kotlin.xoxo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.xoxo.databinding.ActivityGameBinding
import android.content.Intent

class GameActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityGameBinding

    private var gameModel: GameModel? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("highScores", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // onclick listeners for game buttons
        setupButtonListeners()

        binding.startGameBtn.setOnClickListener {
            startGame()
        }

        GameData.gameModel.observe(this) { gameModel ->
            this.gameModel = gameModel
            setUI()
        }

        binding.goToMainMenuBtn.setOnClickListener {
            goToMainMenu()
        }
    }

    private fun goToMainMenu() {
        // intents
        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)

        finish()
    }

    private fun setupButtonListeners() {
        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4, binding.btn5, binding.btn6, binding.btn7, binding.btn8
        )
        buttons.forEach { button ->
            button.setOnClickListener(this)
        }
    }

    private fun setUI() {
        gameModel?.apply {
            // Display round information and scores
            binding.roundInfoTextView.text = "Round $roundsPlayed - Player X: $scoreX, Player O: $scoreO"

            binding.gameStatusText.text = when (gameStatus) {
                GameStatus.CREATED -> {
                    binding.startGameBtn.visibility = View.INVISIBLE
                    "Game ID: $gameId"
                }
                GameStatus.JOINED -> {
                    binding.startGameBtn.visibility = View.VISIBLE
                    "Click on start game"
                }
                GameStatus.INPROGRESS -> {
                    binding.startGameBtn.visibility = View.INVISIBLE
                    "Current Player: $currentPlayer"
                }
                GameStatus.FINISHED -> {
                    binding.startGameBtn.visibility = View.VISIBLE
                    if (winner.isNotEmpty()) "$winner Won"
                    else "It's a draw"
                }
            }

            // Update the buttons' text based on the current game state
            binding.btn0.text = filledPos[0]
            binding.btn1.text = filledPos[1]
            binding.btn2.text = filledPos[2]
            binding.btn3.text = filledPos[3]
            binding.btn4.text = filledPos[4]
            binding.btn5.text = filledPos[5]
            binding.btn6.text = filledPos[6]
            binding.btn7.text = filledPos[7]
            binding.btn8.text = filledPos[8]
        }
    }


    private fun startGame() {
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS,
                    filledPos = filledPos,
                    currentPlayer = currentPlayer,
                    roundsPlayed = roundsPlayed,
                    scoreX = scoreX,
                    scoreO = scoreO
                )
            )
        }
    }

    private fun updateGameData(model: GameModel) {
        GameData.saveGameModel(model)
    }

    private fun checkForWinner() {
        val winningPos = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6),
        )

        gameModel?.apply {
            var hasWinner = false
            for (i in winningPos) {
                // Check if there's a winner in each possible winning position
                if (filledPos[i[0]] == filledPos[i[1]] && filledPos[i[1]] == filledPos[i[2]] && filledPos[i[0]].isNotEmpty()) {
                    winner = filledPos[i[0]]
                    hasWinner = true
                    gameStatus = GameStatus.FINISHED

                    // Update scores
                    if (winner == "X") {
                        scoreX++
                    } else if (winner == "O") {
                        scoreO++
                    }



                    //  toast message for the winning player
                    Toast.makeText(this@GameActivity, "$winner won this round!", Toast.LENGTH_SHORT).show()

                    break
                }
            }

            // Check for a draw
            if (!hasWinner && filledPos.none { it.isEmpty() }) {
                gameStatus = GameStatus.FINISHED
            }

            if (gameStatus == GameStatus.FINISHED) {
                roundsPlayed++
                //change       // rounds
                if (roundsPlayed >= 5) {
                    endGameAndSaveHighScores()
                } else {

                    resetGame()
                }
            }

            updateGameData(this)
        }
    }


    private fun resetGame() {
        gameModel?.apply {
            filledPos = mutableListOf("", "", "", "", "", "", "", "", "")
            currentPlayer = if (currentPlayer == "X") "O" else "X"
            gameStatus = GameStatus.INPROGRESS
            winner = ""
        }
        updateGameData(gameModel!!)
    }

    private fun endGameAndSaveHighScores() {
        gameModel?.apply {

//usage of shared preferences

            // Retrieve previous total scores from SharedPreferences
            val prevTotalScoreX = sharedPreferences.getInt("totalScoreX", 0)
            val prevTotalScoreO = sharedPreferences.getInt("totalScoreO", 0)

            // Add new scores to previous total scores
            val newTotalScoreX = prevTotalScoreX + scoreX
            val newTotalScoreO = prevTotalScoreO + scoreO



            // Save the new total scores
            editor.putInt("totalScoreX", newTotalScoreX)
            editor.putInt("totalScoreO", newTotalScoreO)

            // Apply the changes to SharedPreferences
            editor.apply()

            // Show a toast message indicating the game is over and high scores have been saved
            Toast.makeText(this@GameActivity, "Game over. High scores and total scores saved!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onClick(v: View?) {
        gameModel?.apply {
            if (gameStatus != GameStatus.INPROGRESS) {
                Toast.makeText(applicationContext, "Game not started", Toast.LENGTH_SHORT).show()
                return
            }

            val clickedPos = (v?.tag as String).toInt()
            if (filledPos[clickedPos].isEmpty()) {
                filledPos[clickedPos] = currentPlayer
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                checkForWinner()
                updateGameData(this)
            }
        }
    }
}
