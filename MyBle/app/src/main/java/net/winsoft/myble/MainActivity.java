package net.winsoft.myble;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import net.winsoft.myble.decoration.SpacesItemDecoration;
import net.winsoft.myble.util.DialogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_PERMISSION = 0; /*动态权限申请*/
    private final int REQUEST_ENABLE_BT = 1;
    private final int ACTION_LOCATION_SOURCE_SETTINGS = 2; /*GPS 设置*/

    private BluetoothAdapter mBtAdapter;
    private BleService mBleService;
    private BroadcastReceiver mBleReceiver;
    private ItemClickAdapter mDevicesListAdapter;
    private List<BluetoothDevice> mBluetoothDeviceList;
    private DialogUtil dialogUtil;
    private RecyclerView wd_rv;
    private BaseQuickAdapter<String, BaseViewHolder> wdAdapter;
    private Button bt_ly_lj, bt_1, bt_2, bt_3, bt_4, bt_5, bt_6;
    private TextView tv_s_l, tv_s_r, tv_x_l, tv_x_r, tv_s_z, ly_name_state, ly_kg_state,
            tv_1_1,
            tv_2_1,
            tv_3_1,
            tv_4_1,
            tv_1_2,
            tv_2_2,
            tv_3_2,
            tv_4_2,
            wd_tv,
            settingBt,
            tv_1_3,
            tv_2_3,
            tv_3_3,
            tv_4_3;


    private SharedPreferences sp;
    private SimpleDateFormat sdf;
    private List<String> wdList;

    private String selectKg = "1";

    private int kg_1 = 0;
    private int kg_2 = 0;
    private int kg_3 = 0;
    private int kg_4 = 0;
    private int kg_5 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();

        initPermission(); /*请求地理位置权限*/
        /*是否开启位置*/
        if (!isLocServiceEnable()) {
            dialogUtil.showTwoBtDialog("请开启GPS", false, "开启", "取消", new DialogUtil.IBtOnClickListen() {
                @Override
                public void positiveClick(DialogInterface dialogInterface) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, ACTION_LOCATION_SOURCE_SETTINGS);
                    dialogInterface.dismiss();
                }

                @Override
                public void negativeClick(DialogInterface dialogInterface) {
                    exit();
                }
            });
        }

        initBle();
        initData();
        registerBleReceiver();


    }

    /**
     * 自动连接蓝牙
     */
    private void autoConLy() {
        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);

        String mac = sp.getString("mac", "");
        Log.d("mac", "autoConLy: " + mac);

//        clickedWd = sp.getInt("wd_index", -1);
//
//        if (wdList != null && wdList.size() > 0) {
//            if (clickedWd > 0 && clickedWd < wdList.size()) {
//                wd_tv.setText(wdList.get(clickedWd) + " ℃");
//
//                // TODO 温度设置
//                Log.d("wg", "温度设置: " + "1" + wdList.get(clickedWd));
//                sendData("1" + wdList.get(clickedWd));
//            }
//        }
//
//        if (wdAdapter != null) {
//            wdAdapter.notifyDataSetChanged();
//        }


        if (!"".equals(mac)) {
            mBleService.disconnect();
            mBleService.release();

            dialogUtil.showLoading("正在连接中……", false);

            mBtAdapter.stopLeScan(mLeScanCallback);
            boolean connect = mBleService.connect(mBtAdapter, mac);
            if (connect) {
                dialogUtil.missDeviceListDialog();
                try {
                    int wd_index_1 = sp.getInt("wd_index_1", 30);
                    int wd_index_2 = sp.getInt("wd_index_2", 30);
                    int wd_index_3 = sp.getInt("wd_index_3", 30);
                    int wd_index_4 = sp.getInt("wd_index_4", 30);

//                    sendData("01" + wd_index_1 + "01");
//                    sendData("02" + wd_index_2 + "01");
//                    sendData("03" + wd_index_3 + "01");
//                    sendData("04" + wd_index_4 + "01");

                    sendData("000001" + wd_index_1 + wd_index_2 + wd_index_3 + wd_index_4);

                    wd_tv.setText(
                            "前左胸温度：" + wd_index_1 + " ℃" + "\n" +
                                    "前右胸温度：" + wd_index_2 + " ℃" + "\n" +
                                    "后左胸温度：" + wd_index_3 + " ℃" + "\n" +
                                    "后右胸温度：" + wd_index_4 + " ℃" + "\n"
                    );

                    kg_1 = sp.getInt("kg_1", 0);
                    kg_2 = sp.getInt("kg_2", 0);
                    kg_3 = sp.getInt("kg_3", 0);
                    kg_4 = sp.getInt("kg_4", 0);
                    kg_5 = sp.getInt("kg_5", 0);


                    Log.d("wg", "autoConLy: kg_1 " + kg_1);
                    Log.d("wg", "autoConLy: kg_2 " + kg_2);
                    Log.d("wg", "autoConLy: kg_3 " + kg_3);
                    Log.d("wg", "autoConLy: kg_4 " + kg_4);
                    Log.d("wg", "autoConLy: kg_5 " + kg_5);

                    if (kg_1 == 1) {
                        sendData("010100");
                        bt_1.setText("前左胸 开");
                        tv_1_1.setText("开启");
//                        kg_1 = 0;
                    } else if (kg_1 == 0) {
                        sendData("010000");
                        bt_1.setText("前左胸 关");
                        tv_1_1.setText("关闭");
//                        kg_1 = 1;
                    }

                    if (kg_2 == 1) {
                        sendData("020100");
                        bt_2.setText("前右胸 开");
                        tv_2_1.setText("开启");
//                        kg_2 = 0;
                    } else if (kg_2 == 0) {
                        sendData("020000");
                        bt_2.setText("前右胸 关");
                        tv_2_1.setText("关闭");
//                        kg_2 = 1;
                    }
                    if (kg_3 == 1) {
                        sendData("030100");
                        bt_3.setText("后左背 开");
                        tv_3_1.setText("开启");
//                        kg_3 = 0;
                    } else if (kg_3 == 0) {
                        sendData("030000");
                        bt_3.setText("后左背 关");
                        tv_3_1.setText("关闭");
//                        kg_3 = 1;
                    }
                    if (kg_4 == 1) {
                        sendData("040100");
                        bt_4.setText("后右背 开");
                        tv_4_1.setText("开启");
//                        kg_4 = 0;
                    } else if (kg_4 == 0) {
                        sendData("040000");
                        bt_4.setText("后右背 关");
                        tv_4_1.setText("关闭");
//                        kg_4 = 1;
                    }
                    if (
                            kg_1 == 1 &&
                                    kg_2 == 1 &&
                                    kg_3 == 1 &&
                                    kg_4 == 1
                    ) {
                        sendData("050100");
                        bt_5.setText("全开");
                        setDataState("开启", "开启", "开启", "开启");
//                        kg_5 = 0;
                    } else if (kg_1 == 0 &&
                            kg_2 == 0 &&
                            kg_3 == 0 &&
                            kg_4 == 0) {
                        sendData("050000");
                        bt_5.setText("全关");
                        setDataState("关闭", "关闭", "关闭", "关闭");
//                        kg_5 = 1;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private int clickedWd = -1;

    private void initUi() {
        dialogUtil = new DialogUtil(this); /*Dialog 工具类*/

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        wd_rv = findViewById(R.id.wd_rv); // 最高温度设置
        wd_tv = findViewById(R.id.wd_tv); // 最高温度设置

        wdList = new ArrayList<>();
        wdList.add("30");
        wdList.add("40");
        wdList.add("50");
        wdList.add("60");
        wdList.add("70");

        wdAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.main_wd_item, wdList) {

            @Override
            protected void convert(@NonNull BaseViewHolder helper, String item) {
                if (clickedWd == helper.getAdapterPosition()) {
                    helper.setBackgroundRes(R.id.bg_item, R.drawable.shape_je_bg);
                    helper.setTextColor(R.id.tv, getResources().getColor(R.color.md_white_1000));
                } else {
                    helper.setBackgroundRes(R.id.bg_item, R.drawable.shape_je_bg_1);
                    helper.setTextColor(R.id.tv, getResources().getColor(R.color.colorPrimary));
                }
                helper.setText(R.id.tv, item + " ℃");

            }
        };

        wdAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                clickedWd = position;
                if (sp != null) {
                    sp.edit().putInt("wd_index", clickedWd).apply();
                }
                wdAdapter.notifyDataSetChanged();
                wd_tv.setText(wdList.get(position) + " ℃");
                // TODO 发送温度设置
                Log.d("wg", "温度设置: " + "TE1," + wdList.get(clickedWd));
                sendData(selectKg + wdList.get(clickedWd));
            }
        });

        wd_rv.setLayoutManager(new GridLayoutManager(this, 5));
        wd_rv.addItemDecoration(
                new SpacesItemDecoration(
                        ConvertUtils.dp2px(6.0f),
                        ConvertUtils.dp2px(6.0f)
                )
        );
        wd_rv.setAdapter(wdAdapter);

        bt_ly_lj = findViewById(R.id.bt_ly_lj); // 蓝牙连接
        bt_ly_lj.setOnClickListener(this);

        bt_1 = findViewById(R.id.bt_1); // 蓝牙连接
        bt_1.setOnClickListener(this);

        bt_2 = findViewById(R.id.bt_2); // 蓝牙连接
        bt_2.setOnClickListener(this);

        bt_3 = findViewById(R.id.bt_3); // 蓝牙连接
        bt_3.setOnClickListener(this);

        bt_4 = findViewById(R.id.bt_4); // 蓝牙连接
        bt_4.setOnClickListener(this);

        bt_5 = findViewById(R.id.bt_5); // 蓝牙连接
        bt_5.setOnClickListener(this);

        bt_6 = findViewById(R.id.bt_6); // 蓝牙连接
        bt_6.setOnClickListener(this);

        settingBt = findViewById(R.id.settingBt); // 蓝牙连接
        settingBt.setOnClickListener(this);

        tv_s_l = findViewById(R.id.tv_s_l);
        tv_s_r = findViewById(R.id.tv_s_r);
        tv_x_l = findViewById(R.id.tv_x_l);
        tv_x_r = findViewById(R.id.tv_x_r);
        tv_s_z = findViewById(R.id.tv_s_z);
        ly_name_state = findViewById(R.id.ly_name_state);
        ly_kg_state = findViewById(R.id.ly_kg_state);

        tv_1_1 = findViewById(R.id.tv_1_1);
        tv_2_1 = findViewById(R.id.tv_2_1);
        tv_3_1 = findViewById(R.id.tv_3_1);
        tv_4_1 = findViewById(R.id.tv_4_1);

        tv_1_2 = findViewById(R.id.tv_1_2);
        tv_2_2 = findViewById(R.id.tv_2_2);
        tv_3_2 = findViewById(R.id.tv_3_2);
        tv_4_2 = findViewById(R.id.tv_4_2);

        tv_1_3 = findViewById(R.id.tv_1_3);
        tv_2_3 = findViewById(R.id.tv_2_3);
        tv_3_3 = findViewById(R.id.tv_3_3);
        tv_4_3 = findViewById(R.id.tv_4_3);
    }


    /**
     * 获取地理位置权限
     */
    private void initPermission() {
        PackageManager pkgManager = getPackageManager();

        // 地理位置权限
        boolean coarseLocationPermission =
                pkgManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        // 地理位置权限
        boolean accessFineLocationPermission =
                pkgManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= 23
                && !coarseLocationPermission ||
                !accessFineLocationPermission) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    },
                    REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 初始化蓝牙
     */
    private void initBle() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mBtAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
            return;
        }

        // 搜索蓝牙设备
        scanBleDevice();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 蓝牙设备列表
        mBluetoothDeviceList = new ArrayList<>();
        mDevicesListAdapter = new ItemClickAdapter(mBluetoothDeviceList);

        mDevicesListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.bt_lj:

                        mBleService.disconnect();
                        mBleService.release();

                        dialogUtil.showLoading("正在连接中……", false);

                        mBtAdapter.stopLeScan(mLeScanCallback);
                        boolean connect = mBleService.connect(mBtAdapter, mBluetoothDeviceList.get(position).getAddress());
                        if (connect) {
                            dialogUtil.missDeviceListDialog();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 注册蓝牙信息接收器
     */
    private void registerBleReceiver() {
        // 绑定服务
        Intent intent = new Intent(this, BleService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(intent));
        } else {
            startService(intent);
        }

        // 注册蓝牙信息广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BleService.ACTION_GATT_CONNECTED);
        filter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        filter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BleService.ACTION_DATA_AVAILABLE);
        filter.addAction(BleService.ACTION_READ_DATA_AVAILABLE);
        filter.addAction(BleService.ACTION_CONNECTING_FAIL);

        if (mBleReceiver == null) {
            mBleReceiver = new BleReceiver();
        }

        registerReceiver(mBleReceiver, filter);
    }

    /**
     * 搜索蓝牙设备
     */
    private void scanBleDevice() {
        mBtAdapter.stopLeScan(mLeScanCallback);
        mBtAdapter.startLeScan(mLeScanCallback);
        // 搜索10s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtAdapter.stopLeScan(mLeScanCallback);
            }
        }, 10000);
    }

    /**
     * 搜索蓝牙设备回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
//            if (!mBluetoothDeviceList.contains(bluetoothDevice)) {
//                mBluetoothDeviceList.add(bluetoothDevice);
//                mRssiList.add(String.valueOf(i));
//                mDeviceListAdapter.notifyDataSetChanged();
//            }
//
            if (!mBluetoothDeviceList.contains(bluetoothDevice)) {
                mBluetoothDeviceList.add(bluetoothDevice);
                mDevicesListAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * 服务
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mBleService = ((BleService.LocalBinder) rawBinder).getService();
            Log.d("wg", "-----------------------------------------------");
            autoConLy();
        }

        public void onServiceDisconnected(ComponentName classname) {
            mBleService = null;
        }
    };


    private void pdQkQgState() {
        if (
                kg_1 == 1 &&
                        kg_2 == 1 &&
                        kg_3 == 1 &&
                        kg_4 == 1
        ) {
            sendData("050100");
            bt_5.setText("全开");
            setDataState("开启", "开启", "开启", "开启");
            kg_5 = 0;
            sp.edit().putInt("kg_5", kg_5).apply();
        } else if (kg_1 == 0 &&
                kg_2 == 0 &&
                kg_3 == 0 &&
                kg_4 == 0) {
            sendData("050000");
            bt_5.setText("全关");
            setDataState("关闭", "关闭", "关闭", "关闭");
            kg_5 = 1;
            sp.edit().putInt("kg_5", kg_5).apply();
        }
    }

    @Override
    public void onClick(View view) {
        Log.d("wg", " : kg_1 " + kg_1);
        Log.d("wg", " : kg_2 " + kg_2);
        Log.d("wg", " : kg_3 " + kg_3);
        Log.d("wg", " : kg_4 " + kg_4);
        Log.d("wg", " : kg_5 " + kg_5);
        switch (view.getId()) {
            case R.id.bt_ly_lj: // 蓝牙连接
                // 搜索蓝牙设备
                scanBleDevice();
                // 初始化数据
                initData();
                // 注册蓝牙信息接收器
                registerBleReceiver();
                dialogUtil.showDeviceListDialog(mDevicesListAdapter);
                break;
            case R.id.bt_1:
                if (kg_1 == 0) {
                    bt_1.setText("前左胸 开");
                    tv_1_1.setText("开启");
                    kg_1 = 1;
                    sendData("010100");
                    pdQkQgState();
                } else if (kg_1 == 1) {
                    bt_1.setText("前左胸 关");
                    tv_1_1.setText("关闭");
                    kg_1 = 0;
                    sendData("010000");
                    pdQkQgState();
                }
                sp.edit().putInt("kg_1", kg_1).apply();
                break;
            case R.id.bt_2:
                if (kg_2 == 0) {
                    bt_2.setText("前右胸 开");
                    tv_2_1.setText("开启");
                    kg_2 = 1;
                    sendData("020100");
                    pdQkQgState();
                } else if (kg_2 == 1) {
                    bt_2.setText("前右胸 关");
                    tv_2_1.setText("关闭");
                    kg_2 = 0;
                    sendData("020000");
                    pdQkQgState();
                }
                sp.edit().putInt("kg_2", kg_2).apply();
                break;
            case R.id.bt_3:
                if (kg_3 == 0) {
                    bt_3.setText("后左背 开");
                    tv_3_1.setText("开启");
                    kg_3 = 1;
                    sendData("030100");
                    pdQkQgState();
                } else if (kg_3 == 1) {
                    bt_3.setText("后左背 关");
                    tv_3_1.setText("关闭");
                    kg_3 = 0;
                    sendData("030000");
                    pdQkQgState();
                }
                sp.edit().putInt("kg_3", kg_3).apply();
                break;
            case R.id.bt_4:
                if (kg_4 == 0) {
                    bt_4.setText("后右背 开");
                    tv_4_1.setText("开启");
                    kg_4 = 1;
                    sendData("040100");
                    pdQkQgState();
                } else if (kg_4 == 1) {
                    bt_4.setText("后右背 关");
                    tv_4_1.setText("关闭");
                    kg_4 = 0;
                    sendData("040000");
                    pdQkQgState();
                }
                sp.edit().putInt("kg_4", kg_4).apply();
                break;
            case R.id.bt_5:
                if (kg_5 == 0) {
                    bt_5.setText("全开");
                    setDataState("开启", "开启", "开启", "开启");

                    bt_1.setText("前左胸 开");
                    bt_2.setText("前右胸 开");
                    bt_3.setText("后左背 开");
                    bt_4.setText("后右背 开");

                    kg_1 = 1;
                    kg_2 = 1;
                    kg_3 = 1;
                    kg_4 = 1;

                    kg_5 = 1;
                    sendData("050100");
                } else if (kg_5 == 1) {
                    bt_5.setText("全关");
                    setDataState("关闭", "关闭", "关闭", "关闭");

                    bt_1.setText("前左胸 关");
                    bt_2.setText("前右胸 关");
                    bt_3.setText("后左背 关");
                    bt_4.setText("后右背 关");

                    kg_1 = 0;
                    kg_2 = 0;
                    kg_3 = 0;
                    kg_4 = 0;

                    kg_5 = 0;
                    sendData("050000");
                }
                sp.edit().putInt("kg_5", kg_5).apply();
                break;
            case R.id.settingBt:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            default:
                break;
        }
    }

    private void formatTm() {
        clickedWd = -1;
        if (wdAdapter != null) {
            wdAdapter.notifyDataSetChanged();
        }
    }

    private void setDataState(String s1, String s2, String s3, String s4) {
        tv_1_1.setText(s1);
        tv_2_1.setText(s2);
        tv_3_1.setText(s3);
        tv_4_1.setText(s4);
    }

    /**
     * 蓝牙信息接收器
     */
    private class BleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            switch (action) {
                case BleService.ACTION_READ_DATA_AVAILABLE:
//                    byte[] readData = intent.getByteArrayExtra(BleService.EXTRA_DATA);

//                    String s1 = ByteUtils.byteArrayToHexString(readData);

//                    ly_kg_state.setText("开启：" + s1);
//
//                    switch (s1) {
//                        case "00": //全部关闭
//                            setDataState("关闭", "关闭", "关闭", "关闭");
//                            break;
//                        case "04": //全部打开
//                            setDataState("开启", "开启", "开启", "开启");
//                            int wd_index_11 = sp.getInt("wd_index_1", 30);
//                            int wd_index_22 = sp.getInt("wd_index_2", 30);
//                            int wd_index_33 = sp.getInt("wd_index_3", 30);
//                            int wd_index_44 = sp.getInt("wd_index_4", 30);
//                            sendData("1" + wd_index_11);
//                            sendData("2" + wd_index_22);
//                            sendData("3" + wd_index_33);
//                            sendData("4" + wd_index_44);
//                            break;
//                        case "01":
//                            setDataState("开启", "关闭", "关闭", "关闭");
//                            int wd_index_1 = sp.getInt("wd_index_1", 30);
//                            sendData("1" + wd_index_1);
//                            break;
//                        case "02":
//                            setDataState("关闭", "开启", "关闭", "关闭");
//                            int wd_index_2 = sp.getInt("wd_index_2", 30);
//                            sendData("2" + wd_index_2);
//                            break;
//                        case "03":
//                            setDataState("关闭", "关闭", "开启", "关闭");
//                            int wd_index_3 = sp.getInt("wd_index_3", 30);
//                            sendData("3" + wd_index_3);
//                            break;
//                        default:
//                            break;
//                    }

//                    Log.d("wg", "ACTION_READ_DATA_AVAILABLE: ---- " + ASCII_HEX_Util.hexToAscii(ByteUtils.byteArrayToHexString(readData)));
                    break;

                case BleService.ACTION_GATT_CONNECTED:
                    Toast.makeText(MainActivity.this, "蓝牙已连接", Toast.LENGTH_SHORT).show();

                    String msg = intent.getStringExtra(BleService.EXTRA_DATA);

                    ly_name_state.setText(msg);

                    String[] s = msg.split(" ");
                    if (s.length == 2) {
                        sp.edit().putString("mac", s[1] != null ? s[1] : "").apply();
                    }

                    dialogUtil.missLoading();

                    // 连接成功，连接按钮不给点击
                    bt_ly_lj.setEnabled(false);
                    bt_ly_lj.setText("已连接");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            readData();
                        }
                    }, 1000);

                    break;
                case BleService.ACTION_GATT_DISCONNECTED:
                    dialogUtil.missLoading();
                    Toast.makeText(MainActivity.this, "蓝牙已断开", Toast.LENGTH_SHORT).show();
                    mBleService.release();

                    // 连接成功，连接按钮给点击
                    bt_ly_lj.setEnabled(true);
                    bt_ly_lj.setText("未连接");
                    ly_name_state.setText("");
                    ly_kg_state.setText("");
                    break;

                case BleService.ACTION_CONNECTING_FAIL:
                    dialogUtil.missLoading();
                    Toast.makeText(MainActivity.this, "蓝牙已断开", Toast.LENGTH_SHORT).show();
                    mBleService.disconnect();

                    // 连接成功，连接按钮给点击
                    bt_ly_lj.setEnabled(true);
                    bt_ly_lj.setText("未连接");
                    ly_name_state.setText("");
                    ly_kg_state.setText("");
                    break;

                case BleService.ACTION_DATA_AVAILABLE: // 每隔一秒上报的数据接收
                    byte[] data = intent.getByteArrayExtra(BleService.EXTRA_DATA);
                    Log.d("wg", "onReceive: ---- " + ASCII_HEX_Util.hexToAscii(ByteUtils.byteArrayToHexString(data)));

                    notifyDataFormat(ASCII_HEX_Util.hexToAscii(ByteUtils.byteArrayToHexString(data)));


                    break;
                default:
                    break;
            }
        }
    }

    private void notifyDataFormat(String data) {
//        adc0=2011
        if (data != null && data.contains("=")) {
            String[] split = data.split("=");
            if (split.length == 2) {
                String s1 = split[0];
                String s2 = split[1] != null && !split[1].equals("") ? split[1].trim() : "";

                try {
                    String aFloat = (Float
                            .parseFloat(s2) / 10) + ""; //  ℃

                    if (s1 != null && !s1.equals("")) {
                        String index = s1.substring(s1.length() - 1);
                        switch (index) {
                            case "1":
                                tv_s_l.setText(aFloat);
                                tv_1_2.setText(aFloat);
                                tv_1_3.setText(sdf.format(new Date()));
                                break;
                            case "2":
                                tv_s_r.setText(aFloat);
                                tv_2_2.setText(aFloat);
                                tv_2_3.setText(sdf.format(new Date()));
                                break;
                            case "3":
                                tv_x_l.setText(aFloat);
                                tv_3_2.setText(aFloat);
                                tv_3_3.setText(sdf.format(new Date()));
                                break;
                            case "4":
                                tv_x_r.setText(aFloat);
                                tv_4_2.setText(aFloat);
                                tv_4_3.setText(sdf.format(new Date()));
                                break;
                            case "0"://电池电量
//                                tv_s_z.setText(aFloat);
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mBleService != null && sp != null) {
                int wd_index_1 = sp.getInt("wd_index_1", 30);
                int wd_index_2 = sp.getInt("wd_index_2", 30);
                int wd_index_3 = sp.getInt("wd_index_3", 30);
                int wd_index_4 = sp.getInt("wd_index_4", 30);

//                sendData("01" + wd_index_1 + "01");
//                sendData("02" + wd_index_2 + "01");
//                sendData("03" + wd_index_3 + "01");
//                sendData("04" + wd_index_4 + "01");

                sendData("000001" + wd_index_1 + wd_index_2 + wd_index_3 + wd_index_4);


                Log.d("wg", "温度设置1: " + wd_index_1);
                Log.d("wg", "温度设置2: " + wd_index_2);
                Log.d("wg", "温度设置3: " + wd_index_3);
                Log.d("wg", "温度设置4: " + wd_index_4);
                wd_tv.setText(
                        "前左胸温度：" + wd_index_1 + " ℃" + "\n" +
                                "前右胸温度：" + wd_index_2 + " ℃" + "\n" +
                                "后左胸温度：" + wd_index_3 + " ℃" + "\n" +
                                "后右胸温度：" + wd_index_4 + " ℃" + "\n"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean readData() {
        if (mBleService != null) {
            return mBleService.readData();
        } else {
            return false;
        }

    }

    private void sendData(String cmd) {
        Log.d("wg", "sendData: " + cmd);
        if (mBleService != null) {
            if (mBleService.sendData(ByteUtils.hexStr2Byte(cmd))) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        readData();
                    }
                }, 1000);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // 搜索蓝牙设备
                scanBleDevice();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBleReceiver != null) {
            unregisterReceiver(mBleReceiver);
            mBleReceiver = null;
        }
        unbindService(mServiceConnection);
        mBleService = null;
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    private boolean isLocServiceEnable() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 结束程序
     */
    private void exit() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}