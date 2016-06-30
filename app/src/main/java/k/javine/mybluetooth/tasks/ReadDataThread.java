package k.javine.mybluetooth.tasks;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import k.javine.mybluetooth.utils.KeyUtils;

/**
 * Created by KuangYu on 2016/6/18 0018.
 * 蓝牙接收数据线程
 */
public class ReadDataThread extends Thread {
    private Handler mHandler;
    private BluetoothSocket bluetoothSocket;
    private boolean isCancel = false;
    private byte[] receiveBuffer;
    public ReadDataThread(BluetoothSocket socket,Handler handler){
        bluetoothSocket = socket;
        mHandler = handler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public int receiveData(BluetoothSocket socket) throws IOException {
        if (!socket.isConnected()){ //如果socket close, isConnected会返回false
            return -1;
        }
        InputStream inputStream = socket.getInputStream();
        receiveBuffer = new byte[256];
        return inputStream.read(receiveBuffer);
    }

    @Override
    public void run() {
        while(!isCancel){
            try {
                int len = receiveData(bluetoothSocket);
                Log.d("Javine","readDataThread:"+len);
                if (len == -1){
                    mHandler.sendEmptyMessage(KeyUtils.MSG_CONNECT_FAIL);
                    isCancel = true;
                }else{
                    Message msg = mHandler.obtainMessage();
                    msg.what = KeyUtils.MSG_READ_DATA;
                    msg.arg1 = len;
                    msg.obj = receiveBuffer;
                    mHandler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(KeyUtils.MSG_CONNECT_FAIL);
                isCancel = true;
            }
        }
    }

    public void cancel(){
        try {
            bluetoothSocket.close();
            isCancel = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
