package k.javine.mybluetooth.tasks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by KuangYu on 2016/6/17 0017.
 */
public class ClientConnectThread extends Thread {
    public static final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothDevice mDevice;
    private BluetoothSocket clientSocket;
    private boolean isConnected;

    public ClientConnectThread(BluetoothDevice device){
        BluetoothSocket socket = null;
        mDevice = device;
        try {
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            clientSocket.connect();
        }catch (IOException e){
            e.printStackTrace();
            try {
                clientSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        Log.d("Javine","Client is connected!!!");
        manageSocket();
    }

    private void manageSocket(){

    }

    public void cancel(){
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
