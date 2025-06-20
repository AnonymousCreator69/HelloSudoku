package com.example.hellosudoku.logic

import kotlin.random.Random

object SudokuGenerator {
    private const val SIZE = 9
    private const val SUBGRID_SIZE = 3

    // Generate a completely random Sudoku puzzle
    fun generateSudoku(difficulty: String): Array<IntArray> {
        val board = Array(SIZE) { IntArray(SIZE) { 0 } }

        // Fill the board randomly with a valid Sudoku solution
        generateFullBoard(board)

        // Remove numbers to create the puzzle
        removeNumbers(board, difficulty)

        return board
    }

    // Recursive backtracking algorithm to generate a valid complete Sudoku board
    private fun generateFullBoard(board: Array<IntArray>): Boolean {
        for (row in 0 until SIZE) {
            for (col in 0 until SIZE) {
                if (board[row][col] == 0) {
                    val numbers = (1..SIZE).shuffled(Random)
                    for (num in numbers) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num
                            if (generateFullBoard(board)) return true
                            board[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    // Check if placing a number at (row, col) is valid
    private fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (i in 0 until SIZE) {
            if (board[row][i] == num || board[i][col] == num) return false
        }

        val startRow = row / SUBGRID_SIZE * SUBGRID_SIZE
        val startCol = col / SUBGRID_SIZE * SUBGRID_SIZE
        for (i in 0 until SUBGRID_SIZE) {
            for (j in 0 until SUBGRID_SIZE) {
                if (board[startRow + i][startCol + j] == num) return false
            }
        }
        return true
    }

    // Remove numbers based on difficulty level
    private fun removeNumbers(board: Array<IntArray>, difficulty: String) {
        val clues = when (difficulty.lowercase()) {
            "easy" -> 40
            "medium" -> 32
            "hard" -> 24
            else -> 40 // Default to easy
        }

        var cellsToRemove = SIZE * SIZE - clues
        while (cellsToRemove > 0) {
            val row = Random.nextInt(SIZE)
            val col = Random.nextInt(SIZE)

            if (board[row][col] != 0) {
                board[row][col] = 0
                cellsToRemove--
            }
        }
    }
}

