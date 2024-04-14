package uqac.dim.clockapp.ui.stopwatch;

import android.os.Handler;
import android.os.SystemClock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class StopwatchViewModel extends ViewModel {


    // General Timer
    private long startTimeMillis;
    private MutableLiveData<Long> elapsedTime = new MutableLiveData<>(0L);
    private MutableLiveData<StopwatchState> stopwatchState = new MutableLiveData<>(StopwatchState.INACTIVE);
    private Handler timerHandler;
    private Runnable timerRunnable;

    // Lap Timer
    private long startLapMillis;
    private MutableLiveData<Long> lapElapsedTime = new MutableLiveData<>(0L);
    private Handler lapTimerHandler;
    private Runnable lapTimerRunnable;
    private MutableLiveData<List<Long>> lapTimes = new MutableLiveData<>(new ArrayList<>());




    public StopwatchViewModel() {
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTimeMillis = SystemClock.elapsedRealtime();
                long updatedElapsedTime = currentTimeMillis - startTimeMillis;
                elapsedTime.setValue(updatedElapsedTime);
                timerHandler.postDelayed(this, 10); // Update every 100 milliseconds
            }
        };

        lapTimerHandler = new Handler();
        lapTimerRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTimeMillis = SystemClock.elapsedRealtime();
                long updatedElapsedTime = currentTimeMillis - startLapMillis;
                lapElapsedTime.setValue(updatedElapsedTime);
                lapTimerHandler.postDelayed(this, 10); // Update every 100 milliseconds
            }
        };

        // Reset the stopwatch when the ViewModel is created
        resetStopwatch();
    }

    public LiveData<Long> getElapsedTime() {
        return elapsedTime;
    }

    public LiveData<Long> getLapElapsedTime() {
        return lapElapsedTime;
    }

    public LiveData<List<Long>> getLapTimes() {
        return lapTimes;
    }
    public LiveData<StopwatchState> getStopwatchState() {
        return stopwatchState;
    }

    // RIGHT BUTTON
    public void startStopwatch() {
        if (stopwatchState.getValue() == StopwatchState.INACTIVE) {
            startTimeMillis = SystemClock.elapsedRealtime();
            startLapMillis = SystemClock.elapsedRealtime();
            timerHandler.postDelayed(timerRunnable, 0); // Start the timer
            lapTimerHandler.postDelayed(lapTimerRunnable, 0); // Start the lap timer
            stopwatchState.setValue(StopwatchState.RUNNING);
        }
    }
    public void pauseStopwatch() {
        if (stopwatchState.getValue() == StopwatchState.RUNNING) {
            timerHandler.removeCallbacks(timerRunnable);
            lapTimerHandler.removeCallbacks(lapTimerRunnable);
            stopwatchState.setValue(StopwatchState.STOPPED);
        }
    }

    public void resumeStopwatch() {
        if (stopwatchState.getValue() == StopwatchState.STOPPED) {
            // Resume the stopwatch
            startTimeMillis = SystemClock.elapsedRealtime() - elapsedTime.getValue();
            startLapMillis = SystemClock.elapsedRealtime() - lapElapsedTime.getValue();
            timerHandler.postDelayed(timerRunnable, 0); // Resume the timer
            lapTimerHandler.postDelayed(lapTimerRunnable, 0); // Resume the lap timer
            stopwatchState.setValue(StopwatchState.RUNNING);
        }
    }

    // LEFT BUTTON
    public void resetStopwatch() {
        timerHandler.removeCallbacks(timerRunnable);
        lapTimerHandler.removeCallbacks(lapTimerRunnable);
        elapsedTime.setValue(0L);
        lapElapsedTime.setValue(0L);
        lapTimes.setValue(new ArrayList<>());
        stopwatchState.setValue(StopwatchState.INACTIVE);
    }

    public void saveLap() {
        if (stopwatchState.getValue() == StopwatchState.RUNNING) {
            lapTimerHandler.removeCallbacks(lapTimerRunnable);
            lapTimes.getValue().add(lapElapsedTime.getValue());
            startLapMillis = SystemClock.elapsedRealtime();
            lapElapsedTime.setValue(0L);
            lapTimerHandler.postDelayed(lapTimerRunnable, 0); // Resume the lap timer
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        timerHandler.removeCallbacks(timerRunnable);
        lapTimerHandler.removeCallbacks(lapTimerRunnable);
    }
}
