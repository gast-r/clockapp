package uqac.dim.clockapp.ui.worldwideclock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WorldwideclockViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public WorldwideclockViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is WorldwideClock fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}