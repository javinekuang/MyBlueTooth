package k.javine.mybluetooth.tasks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public void sendData(BluetoothSocket socket, int data) throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream(4);
        output.write(data);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(output.toByteArray());
    }

    public int receiveData(BluetoothSocket socket) throws IOException{
        byte[] buffer = new byte[4];
        ByteArrayInputStream input = new ByteArrayInputStream(buffer);
        InputStream inputStream = socket.getInputStream();
        inputStream.read(buffer);
        return input.read();
    }

    public void cancel(){
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
