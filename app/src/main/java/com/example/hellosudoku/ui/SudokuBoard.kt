package com.example.hellosudoku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SudokuBoard(sudokuGrid: Array<IntArray>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ensure the grid is a 9x9 matrix
        if (sudokuGrid.size != 9 || sudokuGrid.any { it.size != 9 }) {
            Text("Error: Invalid Sudoku Grid", color = Color.Red)
            return
        }

        for (i in 0 until 9) { // Ensuring 9 rows
            Row {
                for (j in 0 until 9) { // Ensuring 9 columns
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .border(1.dp, Color.Black)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (sudokuGrid[i][j] == 0) "" else sudokuGrid[i][j].toString(),
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}


