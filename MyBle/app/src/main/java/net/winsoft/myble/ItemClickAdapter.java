package net.winsoft.myble;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;


public class ItemClickAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    public ItemClickAdapter(List<BluetoothDevice> data) {
        super(R.layout.dialog_device_item, data);
    }

    @Override
    protected void convert(@NonNull final BaseViewHolder helper, BluetoothDevice item) {
        helper.setText(R.id.tv_device_name, TextUtils.isEmpty(item.getName()) ? "未知" : item.getName());
        helper.setText(R.id.tv_device_address, TextUtils.isEmpty(item.getAddress()) ? "未知" : item.getAddress());
        helper.addOnClickListener(R.id.bt_lj);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
    }
}
