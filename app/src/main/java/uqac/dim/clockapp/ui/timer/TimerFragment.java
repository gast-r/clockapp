package uqac.dim.clockapp.ui.timer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import uqac.dim.clockapp.R;
import uqac.dim.clockapp.databinding.FragmentTimerBinding;

public class TimerFragment extends Fragment {

    private FragmentTimerBinding binding;
    private TimerViewModel timerViewModel;
    private NumberPicker hoursPicker;
    private NumberPicker minutesPicker;
    private NumberPicker secondsPicker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTimerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textViewTimer = binding.textViewTimer;
        final Button leftButton = binding.leftButton;
        final Button rightButton = binding.rightButton;

        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        // Initialize NumberPickers
        hoursPicker = binding.timePickerLayout.findViewById(R.id.hoursPicker);
        minutesPicker = binding.timePickerLayout.findViewById(R.id.minutesPicker);
        secondsPicker = binding.timePickerLayout.findViewById(R.id.secondsPicker);

        // Set initial values and wrap selector wheels
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(24);
        hoursPicker.setWrapSelectorWheel(true);

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.setWrapSelectorWheel(true);

        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);
        secondsPicker.setWrapSelectorWheel(true);

        leftButton.setOnClickListener(view -> {
            resetTimer();
        });

        rightButton.setOnClickListener(view -> {
            if (timerViewModel.getTimerState() == TimerState.INACTIVE) {
                int hours = hoursPicker.getValue();
                int minutes = minutesPicker.getValue();
                int seconds = secondsPicker.getValue();
                long totalMilliseconds = (hours * 3600 + minutes * 60 + seconds) * 1000L;
                timerViewModel.startTimer(totalMilliseconds, requireContext());
                rightButton.setText("Pause");
            } else if (timerViewModel.getTimerState() == TimerState.RUNNING) {
                timerViewModel.pauseTimer();
                rightButton.setText("Resume");
            } else if (timerViewModel.getTimerState() == TimerState.PAUSED) {
                timerViewModel.resumeTimer(requireContext());
                rightButton.setText("Pause");
            }
        });

        // Observe timer text updates
        timerViewModel.getText().observe(getViewLifecycleOwner(), timerText -> {
            textViewTimer.setText(timerText);
        });

        // Observe timer state to enable/disable input fields
        timerViewModel.getText().observe(getViewLifecycleOwner(), timerText -> {
            boolean active = timerViewModel.getTimerState() != TimerState.INACTIVE;
            hoursPicker.setEnabled(!active);
            minutesPicker.setEnabled(!active);
            secondsPicker.setEnabled(!active);
        });

        return root;
    }

    private void resetTimer() {
        Button rightButton = binding.rightButton;
        timerViewModel.resetTimer();
        hoursPicker.setValue(0);
        minutesPicker.setValue(0);
        secondsPicker.setValue(0);
        rightButton.setText("Start");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
