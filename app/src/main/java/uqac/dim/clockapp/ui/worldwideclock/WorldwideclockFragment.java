package uqac.dim.clockapp.ui.worldwideclock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import uqac.dim.clockapp.databinding.FragmentWorldwideclockBinding;

public class WorldwideclockFragment extends Fragment {

    private FragmentWorldwideclockBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WorldwideclockViewModel WorldwideclockViewModel =
                new ViewModelProvider(this).get(WorldwideclockViewModel.class);

        binding = FragmentWorldwideclockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textWorldwideclock;
        WorldwideclockViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}