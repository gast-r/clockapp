package uqac.dim.clockapp.ui.stopwatch;

import android.os.Handler;
import android.os.SystemClock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StopwatchViewModel extends ViewModel {

    private MutableLiveData<Long> elapsedTimeMillis = new MutableLiveData<>();
    private Handler timerHandler;
    private Runnable timerRunnable;

    public StopwatchViewModel() {
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = SystemClock.elapsedRealtime();
                long elapsedTime = currentTime - elapsedTimeMillis.getValue();
                elapsedTimeMillis.setValue(elapsedTime);
                timerHandler.postDelayed(this, 100); // Update every 100 milliseconds
            }
        };
    }

    public LiveData<Long> getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    public void startTimer() {
        if (elapsedTimeMillis.getValue() == null) {
            // Start a new timer
            elapsedTimeMillis.setValue(SystemClock.elapsedRealtime());
            timerHandler.postDelayed(timerRunnable, 100); // Start the timer
        }
    }

    public void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void resetTimer() {
        elapsedTimeMillis.setValue(null);
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        timerHandler.removeCallbacks(timerRunnable);
    }
}
