package uqac.dim.clockapp.ui.timer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimerViewModel extends ViewModel {

    private MutableLiveData<String> text;

    public TimerViewModel() {
        text = new MutableLiveData<>();
        text.setValue("00:00");
    }

    public LiveData<String> getText() {
        return text;
    }

    public void setText(String timerText) {
        text.setValue(timerText);
    }
}
