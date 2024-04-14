package uqac.dim.clockapp.ui.stopwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import uqac.dim.clockapp.databinding.FragmentStopwatchBinding;

public class StopwatchFragment extends Fragment {

    private FragmentStopwatchBinding binding;
    private StopwatchViewModel stopwatchViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStopwatchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textViewTimer;
        final Button startStopButton = binding.buttonStart;
        final Button clearButton = binding.buttonStop;

        stopwatchViewModel = new ViewModelProvider(this).get(StopwatchViewModel.class);

        stopwatchViewModel.getElapsedTimeMillis().observe(getViewLifecycleOwner(), elapsedTime -> {
            long seconds = elapsedTime / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            String timeString = String.format("%02d:%02d:%02d",
                    hours % 24, minutes % 60, seconds % 60);

            textView.setText(timeString);
        });

        startStopButton.setOnClickListener(view -> {
            if (stopwatchViewModel.getElapsedTimeMillis().getValue() == null) {
                // Start the stopwatch
                stopwatchViewModel.startTimer();
                startStopButton.setText("Stop");
                clearButton.setEnabled(true);
            } else {
                // Stop the stopwatch
                stopwatchViewModel.stopTimer();
                startStopButton.setText("Start");
            }
        });

        clearButton.setOnClickListener(view -> {
            // Clear the stopwatch
            stopwatchViewModel.resetTimer();
            startStopButton.setText("Start");
            clearButton.setEnabled(false);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
