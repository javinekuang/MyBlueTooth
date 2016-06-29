package k.javine.mybluetooth.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import k.javine.mybluetooth.R;
import k.javine.mybluetooth.model.DeviceDetail;

/**
 * Created by KuangYu on 2016/6/29 0029.
 */
public class DeviceAdapter extends BaseAdapter {

    private List<DeviceDetail> mDatas;
    private LayoutInflater inflater;

    public DeviceAdapter(Context context,List<DeviceDetail> mDatas){
        this.mDatas = mDatas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_device_list,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.iv_device_icon = (ImageView) convertView.findViewById(R.id.iv_device_icon);
            viewHolder.tv_device_address = (TextView) convertView.findViewById(R.id.tv_device_address);
            viewHolder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_device_name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DeviceDetail deviceDetail = mDatas.get(position);
        if (deviceDetail.getDeviceType() == DeviceDetail.DEVICE_PHONE){
            viewHolder.iv_device_icon.setImageResource(R.drawable.phone);
        }else{
            viewHolder.iv_device_icon.setImageResource(R.drawable.unknow);
        }
        viewHolder.tv_device_name.setText(deviceDetail.getBluetoothDevice().getName());
        viewHolder.tv_device_address.setText(deviceDetail.getAddress());
        return convertView;
    }

    class ViewHolder{
        ImageView iv_device_icon;
        TextView tv_device_name,tv_device_address;
    }
}
