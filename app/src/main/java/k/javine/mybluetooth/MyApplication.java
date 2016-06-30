package k.javine.mybluetooth;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;

import com.squareup.leakcanary.LeakCanary;

import k.javine.mybluetooth.tasks.ServerConnectThread;

/**
 * Created by KuangYu on 2016/6/30 0030.
 */
public class MyApplication extends Application {

    public static ServerConnectThread serverConnectThread;
    public static BluetoothAdapter bluetoothAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
