package com.example.hellosudoku

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.hellosudoku.ui.theme.HelloSudokuTheme
import com.example.hellosudoku.ui.GameActivity
import com.example.hellosudoku.ui.SolverActivity
import com.example.hellosudoku.ui.GameRulesActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloSudokuTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    //Applying CheckerBoard background
                    CheckerboardBackground()
                    MainMenuScreen(
                        onPlayGameClick = {
                            startActivity(Intent(this@MainActivity, GameActivity::class.java))
                        },
                        onSolverClick = {
                            startActivity(Intent(this@MainActivity, SolverActivity::class.java))
                        },
                        onGameRulesClick = {
                            startActivity(Intent(this@MainActivity, GameRulesActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(onPlayGameClick: () -> Unit, onSolverClick: () -> Unit, onGameRulesClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val logo = painterResource(id = R.drawable.hellosudoku_logo)
        Image(
            painter = logo,
            contentDescription = "HelloSudoku Logo",
            modifier = Modifier.size(200.dp).padding(bottom = 80.dp)
        )
        AnimatedButton("Play Sudoku", onPlayGameClick)
        AnimatedButton("Sudoku Solver", onSolverClick)
        AnimatedButton("Game Rules", onGameRulesClick)
    }
}

//Animation function
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

// ✅ Checkerboard Background
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
