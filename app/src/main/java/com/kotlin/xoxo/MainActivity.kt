package com.kotlin.xoxo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.xoxo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("highScores", MODE_PRIVATE)
        editor = sharedPreferences.edit()
//display
        displayHighScores()
//play button
        binding.play.setOnClickListener {
            play()
        }
//clear score button
        binding.clearHighScoresBtn.setOnClickListener {
            clearHighScores()
        }
    }
//play
    private fun play() {
        // Initialize a new game with scores set to 0
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.JOINED,
                roundsPlayed = 0,
                scoreX = 0,
                scoreO = 0
            )
        )
        startGame()
    }
//start
    private fun startGame() {
        startActivity(Intent(this, GameActivity::class.java))
    }
//clear scores
    private fun clearHighScores() {
        // Clear the high scores from SharedPreferences
        editor.remove("totalScoreX")
        editor.remove("totalScoreO")
        editor.apply()


        Toast.makeText(this, "High scores cleared!", Toast.LENGTH_SHORT).show()

        // Update the UI
        binding.highScoreTextView.text = "High Scores:\nPlayer X: 0\nPlayer O: 0"
    }

    private fun displayHighScores() {
//usage of shared preference
        // Get high scores
        val highScoreX = sharedPreferences.getInt("totalScoreX", 0)
        val highScoreO = sharedPreferences.getInt("totalScoreO", 0)
        binding.highScoreTextView.text = "High Scores:\nPlayer X: $highScoreX\nPlayer O: $highScoreO"
    }
}
