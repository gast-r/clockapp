package uqac.dim.clockapp.ui.stopwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

import uqac.dim.clockapp.R;
import uqac.dim.clockapp.databinding.FragmentStopwatchBinding;

public class StopwatchFragment extends Fragment {

    private FragmentStopwatchBinding binding;
    private StopwatchViewModel stopwatchViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStopwatchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textViewTimer = binding.textViewTimer;
        final Button leftButton = binding.leftButton;
        final Button rightButton = binding.rightButton;
        final LinearLayout linearLayoutLaps = binding.linearLayoutLapsList;

        stopwatchViewModel = new ViewModelProvider(this).get(StopwatchViewModel.class);

        // Observe stopwatch state changes
        stopwatchViewModel.getStopwatchState().observe(getViewLifecycleOwner(), stopwatchState -> {
            updateButtonStates(stopwatchState);
        });

        // Observe elapsed time updates
        stopwatchViewModel.getElapsedTime().observe(getViewLifecycleOwner(), elapsedTime -> {
            String formattedTime = formatTime(elapsedTime);
            textViewTimer.setText(formattedTime);
        });

        // Observe elapsed lap time updates
        stopwatchViewModel.getLapElapsedTime().observe(getViewLifecycleOwner(), lapElapsedTime -> {
            if (stopwatchViewModel.getStopwatchState().getValue() != StopwatchState.RUNNING) {
                return;
            }

            String formattedTime = formatTime(lapElapsedTime);
            List<Long> lapTimes = stopwatchViewModel.getLapTimes().getValue();
            long lapTimesSize = !lapTimes.isEmpty() ? lapTimes.size() : 0;

            linearLayoutLaps.removeAllViews();
            for (int i = 0; i < lapTimes.size(); i++) {
                TextView lapTimeTextView = new TextView(getContext());
                lapTimeTextView.setText(formatLapTime(lapTimes.get(i), i+1));
                linearLayoutLaps.addView(lapTimeTextView, 0);
            }

            TextView lastLapTimeTextView = new TextView(getContext());
            lastLapTimeTextView.setText(formatLapTime(lapElapsedTime, lapTimesSize + 1));
            linearLayoutLaps.addView(lastLapTimeTextView, 0);
        });

        // Observe lap times updates
        stopwatchViewModel.getLapTimes().observe(getViewLifecycleOwner(), lapTimes -> {

        });

        return root;
    }

    private void updateButtonStates(StopwatchState stopwatchState) {
        final TextView textViewTimer = binding.textViewTimer;
        final Button leftButton = binding.leftButton;
        final Button rightButton = binding.rightButton;

        switch (stopwatchState) {
            case RUNNING:
                // left button
                leftButton.setText(R.string.Lap);
                leftButton.setEnabled(true);
                // listener for left button
                leftButton.setOnClickListener(view -> {
                    stopwatchViewModel.saveLap();
                });
                // right button
                rightButton.setText(R.string.Stop);
                rightButton.setEnabled(true);
                // listener for right button
                rightButton.setOnClickListener(view -> {
                    stopwatchViewModel.pauseStopwatch();
                });



                break;
            case STOPPED:
                leftButton.setText(R.string.Reset);
                leftButton.setEnabled(true);
                leftButton.setOnClickListener(view -> {
                    stopwatchViewModel.resetStopwatch();
                });
                rightButton.setText(R.string.Start);
                rightButton.setEnabled(true);
                rightButton.setOnClickListener(view -> {
                    stopwatchViewModel.resumeStopwatch();
                });
                break;
            case INACTIVE:
                leftButton.setText(R.string.Lap);
                leftButton.setEnabled(false);
                leftButton.setOnClickListener(view -> {
                    stopwatchViewModel.saveLap();
                });

                rightButton.setText(R.string.Start);
                rightButton.setEnabled(true);
                rightButton.setOnClickListener(view -> {
                    stopwatchViewModel.startStopwatch();
                });

                // hide lap times
                LinearLayout linearLayoutLaps = binding.linearLayoutLapsList;
                linearLayoutLaps.removeAllViews();
                break;
        }
    }

private String formatTime(long elapsedTime) {
    long milliseconds = (elapsedTime % 1000) / 10;
    long seconds = (elapsedTime / 1000) % 60;
    long minutes = (elapsedTime / (1000 * 60)) % 60;
    long hours = elapsedTime / (1000 * 60 * 60);

    return String.format(Locale.getDefault(), "%02d:%02d:%02d,%02d", hours, minutes, seconds, milliseconds);
}

    private String formatLapTime(long lapTime, long lapNumber) {
        String formattedTime = formatTime(lapTime);

        return String.format(Locale.getDefault(), "Lap %d: %s", lapNumber, formattedTime);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
