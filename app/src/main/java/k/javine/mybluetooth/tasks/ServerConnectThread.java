package k.javine.mybluetooth.tasks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import k.javine.mybluetooth.utils.KeyUtils;

/**
 * Created by KuangYu on 2016/6/18 0018.
 */
public class ServerConnectThread extends Thread {

    public static final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket clientSocket;
    private Handler mHandler;
    private ReadDataThread readDataThread;
    private WriteDataThread writeDataThread;
    private boolean isCancel = false;

    public ServerConnectThread(BluetoothAdapter mAdapter,Handler handler){
        BluetoothServerSocket tmp = null;
        try{
            tmp = mAdapter.listenUsingRfcommWithServiceRecord("Server", UUID.fromString(MY_UUID));
        }catch (IOException e){
            e.printStackTrace();
        }
        serverSocket = tmp;
        mHandler = handler;
    }

    public void setHandler(Handler handler){
        mHandler = handler;
        if (readDataThread != null)
            readDataThread.setmHandler(handler);
        if (writeDataThread != null)
            writeDataThread.setmHandler(handler);
    }

    @Override
    public void run() {
        BluetoothSocket socket_tmp = null;
        while (!isCancel){
            try{
                socket_tmp = serverSocket.accept();
            }catch (IOException e){
                mHandler.sendEmptyMessage(KeyUtils.MSG_CONNECT_FAIL);
                e.printStackTrace();
            }

            if (socket_tmp != null){
                clientSocket = socket_tmp;
                Log.d("Javine", "Server is connected!!!");
                mHandler.sendEmptyMessage(KeyUtils.MSG_CONNECT_SUCCESS);
                readDataThread = new ReadDataThread(clientSocket,mHandler);
                readDataThread.start();
                writeDataThread = new WriteDataThread(clientSocket,mHandler);
                writeDataThread.start();
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

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
        isCancel = true;
    }
}
