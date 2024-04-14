package uqac.dim.clockapp.ui.timer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Locale;

import uqac.dim.clockapp.R;

public class TimerViewModel extends ViewModel {

    private MutableLiveData<String> timerText = new MutableLiveData<>();
    private CountDownTimer countDownTimer;
    private TimerState timerState = TimerState.INACTIVE;
    private long remainingTime = 0;
    private static final String CHANNEL_ID = "TimerChannel";
    private static final int NOTIFICATION_ID = 1;

    public TimerViewModel() {
        timerText.setValue("00:00:00");
    }

    public LiveData<String> getText() {
        return timerText;
    }

    public TimerState getTimerState() {
        return timerState;
    }

    public void startTimer(long millisInFuture, Context context) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                updateTimerText(millisUntilFinished);
                timerState = TimerState.RUNNING;
            }

            @Override
            public void onFinish() {
                updateTimerText(0);
                timerState = TimerState.INACTIVE;
                remainingTime = 0;
                showNotification(context);
            }
        };

        countDownTimer.start();
        timerState = TimerState.RUNNING;
    }

    public void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            timerState = TimerState.PAUSED;
        }
    }

    public void resumeTimer(Context context) {
        if (timerState == TimerState.PAUSED && remainingTime > 0) {
            startTimer(remainingTime, context);
        }
    }

    public void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerText.setValue("00:00:00");
        timerState = TimerState.INACTIVE;
        remainingTime = 0;
    }

    private void updateTimerText(long millisUntilFinished) {
        int hours = (int) (millisUntilFinished / 3600000);
        int minutes = (int) (millisUntilFinished % 3600000) / 60000;
        int seconds = (int) (millisUntilFinished % 60000) / 1000;

        String timerString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        timerText.postValue(timerString);
    }

    private void showNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Create a notification channel (required for Android Oreo and higher)
        createNotificationChannel(context);

        // Create a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.navbar_timer)
                .setContentTitle("Timer Finished!")
                .setContentText("Your timer has finished.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(soundUri)
                .build();

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Timer Notifications";
            String description = "Notification channel for timer";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
