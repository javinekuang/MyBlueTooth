package k.javine.mybluetooth.tasks;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import k.javine.mybluetooth.utils.KeyUtils;

/**
 * Created by KuangYu on 2016/6/18 0018.
 * 蓝牙发送数据线程
 */
public class WriteDataThread extends Thread {
    private BluetoothSocket bluetoothSocket;
    private Handler mHandler;
    private boolean isCancel;
    private int sendData = -1;
    private byte[] sendBuffer;

    public WriteDataThread(BluetoothSocket socket,Handler handler){
        bluetoothSocket = socket;
        mHandler = handler;
        isCancel = false;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void cancel(){
        try {
            bluetoothSocket.close();
            isCancel = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(BluetoothSocket socket, byte[] data) throws IOException{
        if (!socket.isConnected()){
            mHandler.sendEmptyMessage(KeyUtils.MSG_CONNECT_FAIL);
            isCancel = true;
            return;
        }
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data);
    }

    public void setSendData(byte[] data){
        sendBuffer = data;
        sendData = 1;
    }

    @Override
    public void run() {
        while (!isCancel){
            if (sendData != -1){
                try {
                    sendData(bluetoothSocket, sendBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(KeyUtils.MSG_SEND_DATA_FAIL);
                }
                sendData = -1;
                mHandler.sendEmptyMessage(KeyUtils.MSG_SEND_DATA_SUCCESS);
            }
        }
    }
}
