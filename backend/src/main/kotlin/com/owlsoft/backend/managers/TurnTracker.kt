package com.owlsoft.backend.managers

import com.owlsoft.backend.data.TrackerData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
class TurnTracker(
    turnsPerRound: Int,
    timePerTurn: Int,
    private var isPaused: Boolean = true
) {

    companion object {
        const val TIMER_TICK = 1000L
    }

    private val turnsPerRoundChannel = ConflatedBroadcastChannel(turnsPerRound)
    private val timePerTurnChannel = ConflatedBroadcastChannel(timePerTurn)

    private var _turnIndex = 0
    private var _roundIndex = 1

    private val ticker = ticker(TIMER_TICK)

    fun track(): Flow<TrackerData> =
        combine(
            turnsPerRoundChannel.asFlow(),
            timePerTurnChannel.asFlow()
        ) { _, turnTime -> turnTime }
            .flatMapLatest { turnTime ->
                var timerTick = turnTime

                ticker
                    .consumeAsFlow()
                    .map {
                        val newTimerValue = if (isPaused) timerTick else timerTick--

                        if (timerTick == 0) {
                            nextTurn()
                        }

                        TrackerData(
                            newTimerValue,
                            _turnIndex,
                            _roundIndex,
                            isPaused
                        )
                    }
            }

    suspend fun nextTurn() {

        timePerTurnChannel.send(timePerTurnChannel.value)

        if (_turnIndex == turnsPerRoundChannel.value - 1) {
            _turnIndex = 0
            _roundIndex++
        } else {
            _turnIndex++
        }
    }

    suspend fun updateConfig(turnsPerRound: Int, singleTurnSeconds: Int) {
        timePerTurnChannel.send(singleTurnSeconds)
        turnsPerRoundChannel.send(turnsPerRound)
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    fun currentTurn() = _turnIndex

    fun complete() {
        ticker.cancel()
    }

}