package com.example.hellosudoku.logic

object SudokuSolver {
    fun solveSudoku(board: Array<IntArray>): Boolean {
        if (countGivenNumbers(board) < 17) return false // ✅ Ensure at least 17 clues
        if (hasDuplicateInRowsOrColumns(board)) return false // ✅ Ensure no duplicates
        return solve(board) // ✅ Solve if valid
    }

    private fun countGivenNumbers(board: Array<IntArray>): Int {
        return board.sumOf { row -> row.count { it != 0 } }
    }

    private fun hasDuplicateInRowsOrColumns(board: Array<IntArray>): Boolean {
        for (i in 0..8) {
            val rowSet = mutableSetOf<Int>()
            val colSet = mutableSetOf<Int>()
            for (j in 0..8) {
                if (board[i][j] != 0 && !rowSet.add(board[i][j])) return true // Duplicate in row
                if (board[j][i] != 0 && !colSet.add(board[j][i])) return true // Duplicate in column
            }
        }
        return false
    }

    private fun solve(board: Array<IntArray>): Boolean {
        for (row in board.indices) {
            for (col in board[row].indices) {
                if (board[row][col] == 0) {  // Empty cell
                    for (num in 1..9) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num
                            if (solve(board)) {
                                return true
                            }
                            board[row][col] = 0 // Backtrack
                        }
                    }
                    return false // No valid number found
                }
            }
        }
        return true // Solved
    }

    private fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (i in 0..8) {
            if (board[row][i] == num || board[i][col] == num ||
                board[row / 3 * 3 + i / 3][col / 3 * 3 + i % 3] == num) {
                return false
            }
        }
        return true
    }
}



