package uqac.dim.clockapp.ui.timer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import uqac.dim.clockapp.databinding.FragmentTimerBinding;

public class TimerFragment extends Fragment {

    private FragmentTimerBinding binding;
    private TimerViewModel timerViewModel;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long startTimeInMillis = 600000; // Initial timer duration in milliseconds (10 minutes)

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTimerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textViewTimer = binding.textViewTimer;
        final Button leftButton = binding.leftButton;
        final Button rightButton = binding.rightButton;

        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        // Observe timer text updates
        timerViewModel.getText().observe(getViewLifecycleOwner(), timerText -> {
            textViewTimer.setText(timerText);
        });

        leftButton.setOnClickListener(view -> {
            resetTimer();
        });

        rightButton.setOnClickListener(view -> {
            if (timerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        updateButtons();

        return root;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(startTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                startTimeInMillis = millisUntilFinished;
                updateTimerText(startTimeInMillis);
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                updateButtons();
            }
        }.start();

        timerRunning = true;
        updateButtons();
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        updateButtons();
    }

    private void resetTimer() {
        startTimeInMillis = 600000; // Reset timer to 10 minutes
        updateTimerText(startTimeInMillis);
        timerRunning = false;
        updateButtons();
    }

    private void updateTimerText(long milliseconds) {
        int minutes = (int) (milliseconds / 1000) / 60;
        int seconds = (int) (milliseconds / 1000) % 60;
        String timerText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerViewModel.setText(timerText);
    }

    private void updateButtons() {
        final Button leftButton = binding.leftButton;
        final Button rightButton = binding.rightButton;

        if (timerRunning) {
            leftButton.setText(R.string.reset);
            rightButton.setText(R.string.pause);
        } else {
            leftButton.setText(R.string.reset);
            rightButton.setText(R.string.start);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
