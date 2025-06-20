package com.example.hellosudoku.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.hellosudoku.ui.theme.HelloSudokuTheme

class GameRulesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloSudokuTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    CheckerboardBackground()
                    GameRulesScreen()
                }
            }
        }
    }
}

@Composable
fun GameRulesScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(50.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Some rules, strategies, and tips for solving Sudoku",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = """
                1. The Core Rules:
                
                -Each row must contain the numbers 1-9, without repetition.
                
                -Each column must contain the numbers 1-9, without repetition.
                
                -Each 3x3 block must contain the numbers 1-9, without repetition.
                
                
                2. Strategies for Solving:
                
                - Single Candidate: If a cell can only contain one number, fill it in.
                
                - Scanning: Regularly scan rows, columns, and blocks to identify missing numbers.
                
                - Naked Pairs/Triples: If two or three cells in a row, column, or block contain the same numbers, eliminate them elsewhere.
                
                - X-Wing: A more advanced technique to eliminate candidates in a row or column.
                
                - Focus on the Bigger Picture: Don't get stuck in one area; re-evaluate the puzzle.
                
                - Keep Moving: As you place numbers, look for new possibilities that arise.
                
                - Be Patient: Sudoku can be challenging, so don't get discouraged.
                
                
                3. Tips for Beginners:
                
                - Start with Easy Puzzles: Build your skills with simpler puzzles.
                
                - Don't Guess: Use logic and deduction.
                
                - Take Breaks: Return with fresh eyes if stuck.
                
                - Practice Regularly: Improves pattern recognition.
                
            """.trimIndent(),
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 0.dp)
        )
    }
}