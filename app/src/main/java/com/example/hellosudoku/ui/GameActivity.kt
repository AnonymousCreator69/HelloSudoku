package com.example.hellosudoku.ui

import android.os.Bundle
import androidx.compose.foundation.clickable
import com.example.hellosudoku.R
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.text.BasicTextField
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hellosudoku.logic.SudokuGenerator
import com.example.hellosudoku.logic.SudokuSolver
import com.example.hellosudoku.ui.theme.HelloSudokuTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloSudokuTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // ✅ Apply Checkerboard Background
                    CheckerboardBackground()

                    // ✅ Sudoku Game Content
                    SudokuGameScreen()
                }
            }
        }
    }
}

//Function for generating the CheckerBoard background
@Composable
fun CheckerboardBackground() {
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

@Composable
fun SudokuGameScreen() {
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    var sudokuBoard by remember { mutableStateOf(emptyArray<IntArray>()) }
    var correctSolution by remember { mutableStateOf(emptyArray<IntArray>()) }
    var userInput by remember { mutableStateOf(emptyArray<IntArray>()) }
    var isSolved by remember { mutableStateOf<Boolean?>(null) }
    var originalEmptyCells by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
    var showSolution by remember { mutableStateOf(false) } // ✅ State variable

    if (selectedDifficulty == null) {
        DifficultySelectionScreen { difficulty ->
            selectedDifficulty = difficulty
            sudokuBoard = SudokuGenerator.generateSudoku(difficulty)
            correctSolution = sudokuBoard.map { it.clone() }.toTypedArray() // ✅ Save correct solution
            SudokuSolver.solveSudoku(correctSolution) // ✅ Solve and store the solution
            userInput = sudokuBoard.map { it.clone() }.toTypedArray()
            originalEmptyCells = userInput.indices.flatMap { x ->
                userInput[x].indices.mapNotNull { y -> if (userInput[x][y] == 0) x to y else null }
            }.toSet()
            isSolved = null
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sudoku ($selectedDifficulty)",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 30.sp,
                modifier = Modifier.padding(vertical = 30.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            if(showSolution==false) {
                SudokuInputBoard1(
                    generatedGrid = sudokuBoard,
                    userInputGrid = userInput,
                    originalEmptyCells = originalEmptyCells,
                    solved = isSolved,
                    onGridChange = { updatedGrid ->
                        userInput =
                            updatedGrid.map { it.clone() }.toTypedArray() // ✅ Force recomposition
                    }
                )
            }else if(showSolution==true){
                SudokuInputBoard1(
                    generatedGrid = correctSolution, // ✅ Use solved board
                    userInputGrid = correctSolution.map { it.copyOf() }.toTypedArray(),
                    originalEmptyCells = originalEmptyCells, // ✅ No editable cells
                    solved = true,
                    onGridChange = {}
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                AnimatedButton(text = "Difficulty") {
                    selectedDifficulty = null
                    showSolution = false
                }
                Spacer(modifier = Modifier.width(0.dp))
                AnimatedButton(text = "Evaluate") {
                    if(showSolution==false) {
                        isSolved = userInput.contentDeepEquals(correctSolution) // ✅ Compare user input with correct solution
                    }else if(showSolution==true){
                        isSolved = correctSolution.contentDeepEquals(correctSolution)
                    }
                }
            }

            Spacer(modifier = Modifier.height(0.dp))

            // ✅ NEW ROW for "Show Solution" and "Hint" buttons
            Row {
                AnimatedButton("Show Solution") {
                    showSolution = true // ✅ Triggers recomposition
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ✅ Show success or failure message
            if (isSolved == true) {
                if (showSolution == false) {
                    Text(
                        text = "SUCCESS! Correct solution 🎉",
                        color = Color.Green,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }else if(showSolution == true){
                    Text(
                        text = "Generated Solution!",
                        color = Color.Cyan,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
            } else if(isSolved == false){
                Text(
                    text = "SORRY! Wrong solution ❌",
                    color = Color.Red,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 5.dp)
                )
            } else{ }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "ⓘRe-select the difficulty level to generate a new board",
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}

@Composable
fun DifficultySelectionScreen(onDifficultySelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Difficulty",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 28.sp,
            modifier = Modifier.padding(vertical = 60.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
        val difficulties = listOf("easy", "medium", "hard")
        for (difficulty in difficulties) {
            AnimatedButton(text = difficulty.replaceFirstChar { it.uppercase() }) {
                onDifficultySelected(difficulty)
            }
        }
    }
}

//Animation generator function
@Composable
fun AnimatedButton(text: String, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "Button Press Animation"
    )

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

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100) // Allow animation to complete
            isPressed = false
            onClick() // Call the actual action after animation completes
        }
    }
}

@Composable
fun SudokuInputBoard1(
    generatedGrid: Array<IntArray>, // ✅ Grid from SudokuGenerator
    userInputGrid: Array<IntArray>, // ✅ Grid updated as user solves
    originalEmptyCells: Set<Pair<Int, Int>>, // ✅ Tracks editable cells
    solved: Boolean?,
    onGridChange: (Array<IntArray>) -> Unit // ✅ Only updates userInputGrid
) {
    val boardSize = 9
    val cellSize = 35.dp // Increased cell size for better interaction

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(cellSize * boardSize)
    ) {
        // ✅ Background Sudoku Board Image
        Image(
            painter = painterResource(id = R.drawable.sudoku_board),
            contentDescription = "Sudoku Board",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // ✅ Foreground Grid for Input
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in 0 until boardSize) {
                Row {
                    for (j in 0 until boardSize) {
                        var textValue by remember {
                            mutableStateOf(if (userInputGrid[i][j] == 0) "" else userInputGrid[i][j].toString())
                        }

                        val isEditable = originalEmptyCells.contains(i to j) // ✅ Only allow editing empty cells
                        val textColor = if (!isEditable) Color.Black else if (solved == true) Color.Blue else Color.Red

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .border(1.dp, Color.Transparent)
                                .background(Color.Transparent) // White background for cells
                                .clickable {  }, // Allows interaction but no effect if non-editable
                            contentAlignment = Alignment.Center
                        ) {
                            if (isEditable) {
                                BasicTextField(
                                    value = textValue,
                                    onValueChange = { newValue ->
                                        if (newValue.isEmpty() || (newValue.length == 1 && newValue[0] in '1'..'9')) {
                                            textValue = newValue
                                            val updatedGrid = userInputGrid.map { it.copyOf() }.toTypedArray()
                                            updatedGrid[i][j] = newValue.toIntOrNull() ?: 0
                                            onGridChange(updatedGrid) // ✅ Updates only user input grid
                                        }
                                    },
                                    textStyle = TextStyle(fontSize = 22.sp, color = textColor),
                                    singleLine = true,
                                    //Specifying keyboard type
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxSize().padding(4.dp)
                                )
                            } else {
                                Text(
                                    text = textValue,
                                    fontSize = 22.sp,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

