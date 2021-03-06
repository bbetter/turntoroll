package com.owlsoft.backend.managers

import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.Serializable

@Serializable
data class TrackerData(
    val timerTick: Int = 0,
    val turnIndex: Int = 0,
    val roundIndex: Int = 0,
    val isPaused: Boolean = false
)

class TurnTracker(
    private val timePerTurn: Int,
    private var turnsPerRound: Int,
    private var isPaused: Boolean
) {

    companion object {
        const val TIMER_TICK = 1000L
    }

    private var _turnIndex = 0
    private var _roundIndex = 1
    private var _tick = timePerTurn

    private val ticker = ticker(TIMER_TICK, 0)

    var state = TrackerData()
        private set

    fun track(): Flow<TrackerData> {

        return ticker
            .consumeAsFlow()
            .transform {
                val newTimerValue = if (isPaused) _tick else _tick--

                state = state.copy(
                    timerTick = newTimerValue,
                    turnIndex = _turnIndex,
                    roundIndex = _roundIndex,
                    isPaused = isPaused
                )

                emit(state)

                if (_tick == 0) {
                    nextTurn()
                }
            }
    }

    fun nextTurn() {
        _tick = timePerTurn

        if (_turnIndex == turnsPerRound - 1) {
            _turnIndex = 0
            _roundIndex++
        } else {
            _turnIndex++
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    fun updateTurnCount(turnsPerRound: Int) {
        this.turnsPerRound = turnsPerRound
    }

    fun cancel() {
        ticker.cancel()
    }
}