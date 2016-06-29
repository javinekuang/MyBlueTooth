package k.javine.mybluetooth.model;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by KuangYu on 2016/6/29 0029.
 */
public class DeviceDetail implements Serializable{

    public static final int DEVICE_PHONE = BluetoothClass.Device.PHONE_SMART;

    private String address;
    private int deviceType;
    private BluetoothDevice bluetoothDevice;

    public DeviceDetail(){

    }

    public DeviceDetail(BluetoothDevice bluetoothDevice, int deviceType){
        address = bluetoothDevice.getAddress();
        this.deviceType = deviceType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceDetail that = (DeviceDetail) o;

        return address.equals(that.address);

    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }
}
