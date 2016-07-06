package k.javine.mybluetooth.model;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by KuangYu on 2016/6/29 0029.
 */
public class DeviceDetail implements Parcelable{

    public static final int DEVICE_PHONE = BluetoothClass.Device.PHONE_SMART;

    private String address;
    private int deviceType;
    private BluetoothDevice bluetoothDevice;

    public DeviceDetail(){

    }

    public DeviceDetail(BluetoothDevice bluetoothDevice, int deviceType){
        address = bluetoothDevice.getAddress();
        this.deviceType = deviceType;
        this.bluetoothDevice = bluetoothDevice;
    }

    protected DeviceDetail(Parcel in) {
        address = in.readString();
        deviceType = in.readInt();
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    public static final Creator<DeviceDetail> CREATOR = new Creator<DeviceDetail>() {
        @Override
        public DeviceDetail createFromParcel(Parcel in) {
            return new DeviceDetail(in);
        }

        @Override
        public DeviceDetail[] newArray(int size) {
            return new DeviceDetail[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeInt(deviceType);
        dest.writeParcelable(bluetoothDevice, flags);
    }
}
