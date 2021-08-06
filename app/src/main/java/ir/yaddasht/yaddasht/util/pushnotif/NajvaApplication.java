package ir.yaddasht.yaddasht.util.pushnotif;

import android.app.Application;

import com.najva.sdk.NajvaClient;
import com.najva.sdk.NajvaConfiguration;

public class NajvaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NajvaConfiguration config = new NajvaConfiguration();
        registerActivityLifecycleCallbacks(NajvaClient.getInstance(this,config));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NajvaClient.getInstance().onAppTerminated();
    }
}
