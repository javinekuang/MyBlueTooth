package k.javine.mybluetooth.tasks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.UUID;

import k.javine.mybluetooth.utils.KeyUtils;

/**
 * Created by KuangYu on 2016/6/17 0017.
 */
public class ClientConnectThread extends Thread {
    public static final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothDevice mDevice;
    private BluetoothSocket clientSocket;
    private Handler mHandler;
    private ReadDataThread readDataThread;
    private WriteDataThread writeDataThread;

    public ClientConnectThread(BluetoothDevice device,Handler handler){
        BluetoothSocket socket = null;
        mDevice = device;
        try {
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientSocket = socket;
        mHandler = handler;
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
            mHandler.sendEmptyMessage(KeyUtils.MSG_CONNECT_FAIL);
            return;
        }
        Log.d("Javine", "Client is connected!!!");
        manageSocket();
    }

    private void manageSocket(){
        mHandler.sendEmptyMessage(KeyUtils.MSG_CONNECT_SUCCESS);
        readDataThread = new ReadDataThread(clientSocket,mHandler);
        readDataThread.start();
        writeDataThread = new WriteDataThread(clientSocket,mHandler);
        writeDataThread.start();
    }

    public void sendData(byte[] data){
        writeDataThread.setSendData(data);
    }

    public void cancel(){
        if (readDataThread != null){
            readDataThread.cancel();
            readDataThread = null;
        }
        if (writeDataThread != null){
            writeDataThread.cancel();
            writeDataThread = null;
        }
    }
}
