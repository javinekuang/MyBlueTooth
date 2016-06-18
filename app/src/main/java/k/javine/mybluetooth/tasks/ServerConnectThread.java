package k.javine.mybluetooth.tasks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by KuangYu on 2016/6/18 0018.
 */
public class ServerConnectThread extends Thread {

    public static final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothDevice mDevice;
    private BluetoothServerSocket serverSocket;

    public ServerConnectThread(BluetoothAdapter mAdapter){
        BluetoothServerSocket tmp = null;
        try{
            tmp = mAdapter.listenUsingRfcommWithServiceRecord("Server", UUID.fromString(MY_UUID));
        }catch (IOException e){
            e.printStackTrace();
        }
        serverSocket = tmp;
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;

        while (true){
            try{
                socket = serverSocket.accept();
            }catch (IOException e){
                e.printStackTrace();
            }

            if (socket != null){
                Log.d("Javine","Server is connected!!!");
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }
}
