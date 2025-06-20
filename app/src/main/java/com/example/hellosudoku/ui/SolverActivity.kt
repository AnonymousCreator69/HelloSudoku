package com.example.hellosudoku.ui

import android.os.Bundle
import com.example.hellosudoku.R
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import kotlinx.coroutines.delay
import com.example.hellosudoku.logic.SudokuSolver
import com.example.hellosudoku.ui.theme.HelloSudokuTheme

class SolverActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloSudokuTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    //Applying CheckerBoard background
                    CheckerboardBackground1()
                    SolverScreen()
                }
            }
        }
    }
}

@Composable
fun SolverScreen() {
    var sudokuGrid by remember { mutableStateOf(Array(9) { IntArray(9) }) }
    var solved by remember { mutableStateOf<Boolean?>(null) }
    var originalEmptyCells by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(31.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sudoku Solver", style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 10.dp))

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "WARNING: This solver may not work for all Sudoku puzzles.",
            color = Color.Red,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        SudokuInputBoard(sudokuGrid, originalEmptyCells, solved) { newGrid, emptyCells ->
            sudokuGrid = newGrid
            originalEmptyCells = emptyCells
        }

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedButton1(text = "Solve Sudoku") {
            try {
                val copiedGrid = sudokuGrid.map { it.copyOf() }.toTypedArray()
                if (SudokuSolver.solveSudoku(copiedGrid)) {
                    sudokuGrid = copiedGrid.map { it.copyOf() }.toTypedArray()
                    solved = true
                } else {
                    solved = false
                }
            } catch (e: Exception) {
                solved = false
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedButton1(text = "Reset Grid") {
            sudokuGrid = Array(9) { IntArray(9) }
            originalEmptyCells = setOf()
            solved = null
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (solved == false) {
            Text(text = "Invalid Sudoku Puzzle!", color = Color.Red)
        } else if (solved == true) {
            Text(text = "Solved!", color = Color.Green)
        } else {
            Text(text = "ⓘEnter your puzzle in the above grid.", color = Color.Black)
        }
    }
}

@Composable
fun SudokuInputBoard(
    sudokuGrid: Array<IntArray>,
    originalEmptyCells: Set<Pair<Int, Int>>,
    solved: Boolean?,
    onGridChange: (Array<IntArray>, Set<Pair<Int, Int>>) -> Unit
) {
    val boardSize = 9
    var tempEmptyCells by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
    val cellSize = 30.dp // Size of each cell

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(cellSize * boardSize) // Ensure board scales with cell size
    ) {
        // ✅ Background Image (Sudoku Board)
        Image(
            painter = painterResource(id = R.drawable.sudoku_board),
            contentDescription = "Sudoku Board",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // ✅ Foreground Grid (User Input)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in 0 until boardSize) {
                Row {
                    for (j in 0 until boardSize) {
                        var textValue by remember(sudokuGrid[i][j]) {
                            mutableStateOf(if (sudokuGrid[i][j] == 0) "" else sudokuGrid[i][j].toString())
                        }

                        val textColor = if (solved == true && originalEmptyCells.contains(i to j)) Color.Blue else Color.Black
                        val borderWidth = 1.dp
                        val thickBorderWidth = 2.dp

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(width = borderWidth, color = Color.Transparent)
                                .border(width = if (j % 3 == 0) thickBorderWidth else 0.dp, color = Color.Transparent)
                                .border(width = if (i % 3 == 0) thickBorderWidth else 0.dp, color = Color.Transparent)
                                .background(Color.Transparent), // Make grid transparent
                            contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(
                                value = textValue,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || (newValue.length == 1 && newValue[0] in '1'..'9')) {
                                        textValue = newValue
                                        val newGrid = sudokuGrid.map { it.copyOf() }.toTypedArray()
                                        newGrid[i][j] = newValue.toIntOrNull() ?: 0
                                        tempEmptyCells = newGrid.indices.flatMap { x ->
                                            newGrid[x].indices.mapNotNull { y -> if (newGrid[x][y] == 0) x to y else null }
                                        }.toSet()
                                        onGridChange(newGrid, tempEmptyCells)
                                    }
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, color = textColor),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), // 🔹 Restrict to numeric input
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

//Function for generating the button animations
@Composable
fun AnimatedButton1(text: String, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "Button Press Animation"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
            onClick()
        }
    }

    Button(
        onClick = {
            isPressed = true
        },
        modifier = Modifier
            .scale(scale)
            .padding(16.dp)
    ) {
        Text(text)
    }
}

//Function for generating the CheckBoard background
@Composable
fun CheckerboardBackground1() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val tileSize = 50.dp.toPx()
        for (i in 0..(size.width / tileSize).toInt()) {
            for (j in 0..(size.height / tileSize).toInt()) {
                drawRect(
                    color = if ((i + j) % 2 == 0) Color.LightGray else Color.White,
                    topLeft = androidx.compose.ui.geometry.Offset(i * tileSize, j * tileSize),
                    size = androidx.compose.ui.geometry.Size(tileSize, tileSize)
                )
            }
        }
    }
}

