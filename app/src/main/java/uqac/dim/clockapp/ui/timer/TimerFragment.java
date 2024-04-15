package uqac.dim.clockapp.ui.timer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import uqac.dim.clockapp.R;

public class TimerFragment extends Fragment {

    private TimerViewModel timerViewModel;
    private TextView textViewTimer;
    private Button leftButton;
    private Button rightButton;
    private NumberPicker hoursPicker;
    private NumberPicker minutesPicker;
    private NumberPicker secondsPicker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(uqac.dim.clockapp.R.layout.fragment_timer, container, false);

        textViewTimer = root.findViewById(R.id.textViewTimer);
        leftButton = root.findViewById(R.id.leftButton);
        rightButton = root.findViewById(R.id.rightButton);
        hoursPicker = root.findViewById(R.id.hoursPicker);
        minutesPicker = root.findViewById(R.id.minutesPicker);
        secondsPicker = root.findViewById(R.id.secondsPicker);

        // Set up the number pickers
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(24);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);


        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        // Observe timer state changes
        timerViewModel.getTimerState().observe(getViewLifecycleOwner(), timerState -> {
            updateButtonStates(timerState);
        });

        // Observe elapsed time updates
        timerViewModel.getElapsedTime().observe(getViewLifecycleOwner(), elapsedTime -> {
            String formattedTime = formatTime(elapsedTime);
            textViewTimer.setText(formattedTime);

            // Check if timer has reached 0
            if (elapsedTime <= 0 && timerViewModel.getTimerState().getValue() == TimerState.RUNNING) {

                // Send notification to the user
                sendNotification();

                // Reset the timer
                resetTimer();
            }
        });

        // Set listeners on NumberPicker widgets
        hoursPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            updateTimerFromPickers();
        });

        minutesPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            updateTimerFromPickers();
        });

        secondsPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            updateTimerFromPickers();
        });

        leftButton.setOnClickListener(view -> {
            // Reset the timer and number pickers
            resetTimer();
        });

        rightButton.setOnClickListener(view -> {
            // Start, pause, or resume the timer based on current state
            if (timerViewModel.getTimerState().getValue() == TimerState.INACTIVE) {
                // Start the timer
                int hours = hoursPicker.getValue();
                int minutes = minutesPicker.getValue();
                int seconds = secondsPicker.getValue();
                long totalMillis = (hours * 3600 + minutes * 60 + seconds) * 1000;
                timerViewModel.setTimer(totalMillis);
                timerViewModel.startTimer();
            } else if (timerViewModel.getTimerState().getValue() == TimerState.RUNNING) {
                // Pause the timer
                timerViewModel.pauseTimer();
            } else if (timerViewModel.getTimerState().getValue() == TimerState.PAUSED) {
                // Resume the timer
                timerViewModel.resumeTimer();
            }
        });

        resetTimer();

        return root;
    }

    private void updateTimerFromPickers() {
        int hours = hoursPicker.getValue();
        int minutes = minutesPicker.getValue();
        int seconds = secondsPicker.getValue();
        long totalMillis = (hours * 3600 + minutes * 60 + seconds) * 1000;
        timerViewModel.resetTimer(totalMillis);
        Log.d("TimerFragment", "Updated timer to " + totalMillis + " milliseconds");
    }

    private void updateButtonStates(TimerState timerState) {
        if (timerState == TimerState.INACTIVE) {
            enableNumberPickers(true);
            rightButton.setText("Start");
        } else if (timerState == TimerState.RUNNING) {
            enableNumberPickers(false);
            rightButton.setText("Pause");
        } else if (timerState == TimerState.PAUSED) {
            enableNumberPickers(true);
            rightButton.setText("Resume");
        }
    }

    public void enableNumberPickers(boolean enabled) {
        hoursPicker.setEnabled(enabled);
        minutesPicker.setEnabled(enabled);
        secondsPicker.setEnabled(enabled);
    }

    private String formatTime(long elapsedTime) {
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void resetTimer() {
        updateTimerFromPickers();
    }

    private void sendNotification() {
        Context context = requireContext();

        // Notification channel ID is required for Android Oreo and higher
        String channelId = "timer_channel";
        String channelName = "Timer Notification Channel";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel if necessary (for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_timer_notification)
                .setContentTitle("Timer Expired")
                .setContentText("Your timer has reached zero.")
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set priority to HIGH for heads-up notification
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE) // Add sound and vibration
                .setAutoCancel(true); // Auto cancel the notification when tapped

        // Show the notification
        notificationManager.notify(1, builder.build());
    }


}
