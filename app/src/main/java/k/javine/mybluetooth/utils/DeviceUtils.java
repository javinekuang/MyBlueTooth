package k.javine.mybluetooth.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import k.javine.mybluetooth.model.DeviceDetail;

/**
 * Created by KuangYu on 2016/6/30 0030.
 */
public class DeviceUtils{

    //BluetoothDevice can not be serializable,can not transfer by intent.
    public static List<DeviceDetail> bluetoothDeviceList = new ArrayList<>();

}
