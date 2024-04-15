package uqac.dim.clockapp.ui.timer;

import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimerViewModel extends ViewModel {

    private MutableLiveData<Long> elapsedTime = new MutableLiveData<>(0L);
    private MutableLiveData<TimerState> timerState = new MutableLiveData<>(TimerState.INACTIVE);
    private CountDownTimer countDownTimer;

    public LiveData<Long> getElapsedTime() {
        return elapsedTime;
    }

    public LiveData<TimerState> getTimerState() {
        return timerState;
    }

    public void setTimer(long millisInFuture) {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(millisInFuture, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                elapsedTime.setValue(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                elapsedTime.setValue(0L);
            }
        };
    }

    public void startTimer() {

        if (elapsedTime.getValue() == 0) {
            return;
        }

        countDownTimer.start();
        timerState.setValue(TimerState.RUNNING);
    }

    public void pauseTimer() {
        countDownTimer.cancel();
        timerState.setValue(TimerState.PAUSED);
    }

    public void resumeTimer() {
        countDownTimer.cancel();
        setTimer(elapsedTime.getValue());
        countDownTimer.start();
        timerState.setValue(TimerState.RUNNING);
    }

    public void resetTimer(long millisInFuture) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        elapsedTime.setValue(millisInFuture);
        timerState.setValue(TimerState.INACTIVE);
    }
}
