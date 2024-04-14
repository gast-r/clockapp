package uqac.dim.clockapp.ui.alarms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import uqac.dim.clockapp.databinding.FragmentAlarmsBinding;

public class AlarmsFragment extends Fragment {

    private FragmentAlarmsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AlarmsViewModel AlarmsViewModel =
                new ViewModelProvider(this).get(AlarmsViewModel.class);

        binding = FragmentAlarmsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAlarms;
        AlarmsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}