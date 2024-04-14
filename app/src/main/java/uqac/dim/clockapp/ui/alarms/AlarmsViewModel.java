package uqac.dim.clockapp.ui.alarms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlarmsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AlarmsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Alarms fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}