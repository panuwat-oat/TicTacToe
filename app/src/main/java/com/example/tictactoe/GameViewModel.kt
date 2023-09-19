package com.example.tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    var state by mutableStateOf(GameState(startingPlayer = BoardCellValue.CIRCLE))

    private var isComputerPlaying = false


    val boardItems: MutableMap<Int, BoardCellValue> = mutableMapOf(
        1 to BoardCellValue.NONE,
        2 to BoardCellValue.NONE,
        3 to BoardCellValue.NONE,
        4 to BoardCellValue.NONE,
        5 to BoardCellValue.NONE,
        6 to BoardCellValue.NONE,
        7 to BoardCellValue.NONE,
        8 to BoardCellValue.NONE,
        9 to BoardCellValue.NONE,
    )

    fun onAction(action: UserAction) {
        if (isComputerPlaying) return
        when (action) {
            is UserAction.BoardTapped -> {
                addValueToBoard(action.cellNo)
                if (state.currentTurn == BoardCellValue.CROSS) {
                    isComputerPlaying = true
                    computerPlay()
                    isComputerPlaying = false
                }
            }

            UserAction.PlayAgainButtonClicked -> {
                if (state.hasWon || hasBoardFull()) {
                    gameReset()
                }
            }
        }
    }

    private fun gameReset() {
        boardItems.forEach { (i, _) ->
            boardItems[i] = BoardCellValue.NONE
        }

        val newStartingPlayer =
            if (state.startingPlayer == BoardCellValue.CIRCLE) BoardCellValue.CROSS else BoardCellValue.CIRCLE

        state = state.copy(
            startingPlayer = newStartingPlayer,
            currentTurn = newStartingPlayer,
            hintText = "Player '0' turn",
            victoryType = VictoryType.NONE,
            hasWon = false
        )

        if (state.currentTurn == BoardCellValue.CROSS) {
            isComputerPlaying = true
            computerPlay()
            isComputerPlaying = false
        }
    }


    private fun computerPlay() {
        val winningMove = findWinningMove(BoardCellValue.CROSS)
        val blockingMove = findWinningMove(BoardCellValue.CIRCLE)

        when {
            winningMove != null -> addValueToBoard(winningMove)
            blockingMove != null -> addValueToBoard(blockingMove)
            boardItems[5] == BoardCellValue.NONE -> addValueToBoard(5)
            else -> addValueToBoard(boardItems.filter { it.value == BoardCellValue.NONE }.keys.random())
        }
    }

    private fun findWinningMove(player: BoardCellValue): Int? {
        for (i in 1..9) {
            if (boardItems[i] == BoardCellValue.NONE) {
                boardItems[i] = player
                if (checkForVictory(player)) {
                    boardItems[i] = BoardCellValue.NONE
                    return i
                }
                boardItems[i] = BoardCellValue.NONE
            }
        }
        return null
    }

    private fun addValueToBoard(cellNo: Int) {
        if (boardItems[cellNo] != BoardCellValue.NONE) {
            return
        }
        if (state.currentTurn == BoardCellValue.CIRCLE) {
            boardItems[cellNo] = BoardCellValue.CIRCLE
            state = if (checkForVictory(BoardCellValue.CIRCLE)) {
                state.copy(
                    hintText = "Player 'O' Won",
                    playerCircleCount = state.playerCircleCount + 1,
                    currentTurn = BoardCellValue.NONE,
                    hasWon = true
                )
            } else if (hasBoardFull()) {
                state.copy(
                    hintText = "Game Draw",
                    drawCount = state.drawCount + 1
                )
            } else {
                state.copy(
                    hintText = "Player 'X' turn",
                    currentTurn = BoardCellValue.CROSS
                )
            }
        } else if (state.currentTurn == BoardCellValue.CROSS) {
            boardItems[cellNo] = BoardCellValue.CROSS
            state = if (checkForVictory(BoardCellValue.CROSS)) {
                state.copy(
                    hintText = "Player 'X' Won",
                    playerCrossCount = state.playerCrossCount + 1,
                    currentTurn = BoardCellValue.NONE,
                    hasWon = true
                )
            } else if (hasBoardFull()) {
                state.copy(
                    hintText = "Game Draw",
                    drawCount = state.drawCount + 1
                )
            } else {
                state.copy(
                    hintText = "Player 'O' turn",
                    currentTurn = BoardCellValue.CIRCLE
                )
            }
        }
    }

    private fun checkForVictory(boardValue: BoardCellValue): Boolean {
        when {
            boardItems[1] == boardValue && boardItems[2] == boardValue && boardItems[3] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL1)
                return true
            }

            boardItems[4] == boardValue && boardItems[5] == boardValue && boardItems[6] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL2)
                return true
            }

            boardItems[7] == boardValue && boardItems[8] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.HORIZONTAL3)
                return true
            }

            boardItems[1] == boardValue && boardItems[4] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL1)
                return true
            }

            boardItems[2] == boardValue && boardItems[5] == boardValue && boardItems[8] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL2)
                return true
            }

            boardItems[3] == boardValue && boardItems[6] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.VERTICAL3)
                return true
            }

            boardItems[1] == boardValue && boardItems[5] == boardValue && boardItems[9] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL1)
                return true
            }

            boardItems[3] == boardValue && boardItems[5] == boardValue && boardItems[7] == boardValue -> {
                state = state.copy(victoryType = VictoryType.DIAGONAL2)
                return true
            }

            else -> return false
        }
    }

    private fun hasBoardFull(): Boolean {
        return !boardItems.containsValue(BoardCellValue.NONE)
    }
}