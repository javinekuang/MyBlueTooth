package k.javine.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import k.javine.mybluetooth.tasks.ClientConnectThread;
import k.javine.mybluetooth.tasks.ServerConnectThread;
import k.javine.mybluetooth.utils.KeyUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_BT_ENABLE = 0x01;
    public static final int REQUEST_BT_SCAN = 0x02;

    @Bind(R.id.device_list)
    ListView deviceList;
    @Bind(R.id.btn_scan)
    RelativeLayout btn_scan;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.btn_sw)
    Button btn_sw;
    @Bind(R.id.text)
    TextView scan_text;
    @Bind(R.id.ll_receive)
    LinearLayout ll_receive;
    @Bind(R.id.tv_receive)
    TextView tv_receive;
    @Bind(R.id.ed_input)
    EditText ed_input;
    @Bind(R.id.btn_send)
    Button btn_send;

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> mArrayAdapter;
    private List<BluetoothDevice> mDevices = new ArrayList<>();
    private ClientConnectThread clientConnectThread;
    private ServerConnectThread serverConnectThread;
    private boolean isClientMode = true;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case KeyUtils.MSG_CONNECT_SUCCESS:
                    ll_receive.setVisibility(View.VISIBLE);
                    deviceList.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this,"Device Connected!",Toast.LENGTH_SHORT).show();
                    break;
                case KeyUtils.MSG_CONNECT_FAIL:
                    ll_receive.setVisibility(View.GONE);
                    mDevices.clear();
                    deviceList.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this,"Device Disconnected!",Toast.LENGTH_SHORT).show();
                    cancelThread();
                    break;
                case KeyUtils.MSG_READ_DATA:
                    byte[] data = (byte[]) msg.obj;
                    String s = "";
                    try {
                        s = new String(data, "GB2312");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tv_receive.append(s.trim() + "\n");
                    break;
                case KeyUtils.MSG_SEND_DATA_FAIL:
                    break;
                case KeyUtils.MSG_SEND_DATA_SUCCESS:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btn_scan.setOnClickListener(this);
        btn_sw.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(discoverReceiver, filter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        deviceList.setAdapter(mArrayAdapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //连接device时，先确保discovery已经关闭
                if (bluetoothAdapter.isDiscovering()){
                    bluetoothAdapter.cancelDiscovery();
                    progressBar.setVisibility(View.GONE);
                }
                //connect a device
                if (isClientMode){
                    clientConnectThread = new ClientConnectThread(mDevices.get(position),mHandler);
                    clientConnectThread.start();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_scan:
                if (isClientMode)
                    scanBt();
                else
                    discoverableBt();
                break;
            case R.id.btn_sw:
                checkBtIsOn();
                break;
            case R.id.btn_send:
                if (isClientMode){
                    String clientData = ed_input.getText().toString();
                    try {
                        clientConnectThread.sendData(clientData.getBytes("GB2312"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else{
                    String serverData = ed_input.getText().toString();
                    try {
                        serverConnectThread.sendData(serverData.getBytes("GB2312"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                ed_input.setText("");
                break;
        }
    }

    /**
     * Client or Server
     */
    private void checkBtIsOn(){
        if (bluetoothAdapter == null){
            Toast.makeText(this,"your device does not have BT.",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
            return;
        }else{
            Toast.makeText(MainActivity.this,"Bluetooth is open.",Toast.LENGTH_SHORT).show();
        }
        if (isClientMode){
            isClientMode = false;
            btn_sw.setText("Server");
            scan_text.setText("Discoverable");
        }else{
            isClientMode = true;
            btn_sw.setText("Client");
            scan_text.setText("Scan");
        }
    }

    private void scanBt(){
        if (bluetoothAdapter == null){
            Toast.makeText(this,"your device does not have BT.",Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        bluetoothAdapter.startDiscovery();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        bluetoothAdapter.cancelDiscovery();
                    }
                });
            }
        },10000);
    }

    private void discoverableBt(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivity(discoverableIntent);
        serverConnectThread = new ServerConnectThread(bluetoothAdapter,mHandler);
        serverConnectThread.start();
    }

    private final BroadcastReceiver discoverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mDevices.add(device);
            }else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,0);
                Log.d("Javine","BOUND_STATE:"+state);
                if (state == BluetoothDevice.BOND_BONDED){
                    Toast.makeText(MainActivity.this,"Device Paired!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_BT_ENABLE:
                    Toast.makeText(MainActivity.this,"Bluetooth is open.",Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_BT_SCAN:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(discoverReceiver);
        cancelThread();
    }

    private void cancelThread() {
        if (clientConnectThread != null)
            clientConnectThread.cancel();
        if (serverConnectThread != null)
            serverConnectThread.cancel();
    }
}
