package net.winsoft.myble;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.util.UUID;

public class BleService extends Service {

    private final String TAG = BleService.class.getSimpleName();
    private BluetoothGatt mBluetoothGatt;

    // 蓝牙连接状态
    private int mConnectionState = 0;
    // 蓝牙连接已断开
    private final int STATE_DISCONNECTED = 0;
    // 蓝牙正在连接
    private final int STATE_CONNECTING = 1;
    // 蓝牙已连接
    private final int STATE_CONNECTED = 2;

    // 蓝牙已连接
    public final static String ACTION_GATT_CONNECTED = "net.winsoft.myble.ACTION_GATT_CONNECTED";
    // 蓝牙已断开
    public final static String ACTION_GATT_DISCONNECTED = "net.winsoft.myble.ACTION_GATT_DISCONNECTED";
    // 发现GATT服务
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "net.winsoft.myble.ACTION_GATT_SERVICES_DISCOVERED";
    // 收到蓝牙数据
    public final static String ACTION_DATA_AVAILABLE = "net.winsoft.myble.ACTION_DATA_AVAILABLE";

    public final static String ACTION_READ_DATA_AVAILABLE = "net.winsoft.myble.ACTION_READ_DATA_AVAILABLE"; // 读取数据的action

    // 连接失败
    public final static String ACTION_CONNECTING_FAIL = "net.winsoft.myble.ACTION_CONNECTING_FAIL";
    // 蓝牙数据
    public final static String EXTRA_DATA = "net.winsoft.myble.EXTRA_DATA";

    // 服务标识
    private final UUID SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    // 特征标识（读取数据）
    private final UUID CHARACTERISTIC_READ_UUID = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
    // 特征标识（发送数据）
    private final UUID CHARACTERISTIC_WRITE_UUID = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
    // 描述标识
    private final UUID DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // 服务相关
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        release();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        Intent mainIntent = new Intent(this, MainActivity.class);
//        PendingIntent goMainPendIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 获取系统 通知管理 服务
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        // 构建 Notification
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("服务开启中")
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentIntent(goMainPendIntent)
                .setContentText("服务开启中");

        // 兼容  API 26，Android 8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 第三个参数表示通知的重要程度，默认则只在通知栏闪烁一下
            NotificationChannel notificationChannel = new NotificationChannel("SSDWNotificationId", "SSDWNotificationName", NotificationManager.IMPORTANCE_HIGH);
            // 注册通道，注册后除非卸载再安装否则不改变
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId("SSDWNotificationId");
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        startForeground(1, notification);
    }

    /**
     * 蓝牙操作回调
     * 蓝牙连接状态才会回调
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 蓝牙已连接
                mConnectionState = STATE_CONNECTED;
                sendBleBroadcast(ACTION_GATT_CONNECTED, gatt.getDevice().getName() + " " + gatt.getDevice().getAddress());

                // 搜索GATT服务
                mBluetoothGatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // 蓝牙已断开连接
                mConnectionState = STATE_DISCONNECTED;
                sendBleBroadcast(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // 发现GATT服务
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "发现GATT服务 发现GATT服务 发现GATT服务: ");
                setBleNotification();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 收到数据
            Log.d(TAG, "onCharacteristicChanged: 收到数据收到数据收到数据收到数据收到数据收到数据");
            sendBleBroadcast(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "ReadWrite1: " + ByteUtils.byteArrayToHexString(characteristic.getValue()));
//            super.onCharacteristicRead(gatt, characteristic, status);
            sendBleBroadcast(ACTION_READ_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "ReadWrite2: " + ByteUtils.byteArrayToHexString(characteristic.getValue()));
//            super.onCharacteristicWrite(gatt, characteristic, status);
        }
    };

    /**
     * 发送通知
     *
     * @param action 广播Action
     */
    private void sendBleBroadcast(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }


    /**
     * 连接成功，发送 设备名称和mac，前台显示
     */
    private void sendBleBroadcast(String action, String msg) {
        Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, msg);
        sendBroadcast(intent);
    }


    /**
     * 发送通知
     *
     * @param action         广播Action
     * @param characteristic 数据
     */
    private void sendBleBroadcast(String action, BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(action);
        if (CHARACTERISTIC_READ_UUID.equals(characteristic.getUuid())) {
            intent.putExtra(EXTRA_DATA, characteristic.getValue());
        }
        sendBroadcast(intent);
    }

    /**
     * 蓝牙连接
     *
     * @param bluetoothAdapter BluetoothAdapter
     * @param address          设备mac地址
     * @return true：成功 false：
     */
    public boolean connect(BluetoothAdapter bluetoothAdapter, String address) {
        if (bluetoothAdapter == null || TextUtils.isEmpty(address)) {
            return false;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * 蓝牙断开连接
     */
    public void disconnect() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * 释放相关资源
     */
    public void release() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * 设置蓝牙设备在数据改变时，通知App
     */
    public void setBleNotification() {
        if (mBluetoothGatt == null) {
            sendBleBroadcast(ACTION_CONNECTING_FAIL);
            return;
        }

        // 获取蓝牙设备的服务
        BluetoothGattService gattService = mBluetoothGatt.getService(SERVICE_UUID);
        if (gattService == null) {
            sendBleBroadcast(ACTION_CONNECTING_FAIL);
            return;
        }

        // 获取蓝牙设备的特征
        BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(CHARACTERISTIC_READ_UUID);
        if (gattCharacteristic == null) {
            sendBleBroadcast(ACTION_CONNECTING_FAIL);
            return;
        }

        // 获取蓝牙设备特征的描述符
        BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(DESCRIPTOR_UUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        if (mBluetoothGatt.writeDescriptor(descriptor)) {
            // 蓝牙设备在数据改变时，通知App，App在收到数据后回调onCharacteristicChanged方法
            Log.d(TAG, "蓝牙设备在数据改变时，通知App，App在收到数据后回调onCharacteristicChanged方法: ");
            mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
        }
    }

    /**
     * 读取值
     */
    public boolean readData() {
        // 获取蓝牙设备的服务
        BluetoothGattService gattService = null;
        if (mBluetoothGatt != null) {
            gattService = mBluetoothGatt.getService(SERVICE_UUID);
        }
        if (gattService == null) {
            return false;
        }

        // 获取蓝牙设备的特征
        BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(CHARACTERISTIC_WRITE_UUID);
        if (gattCharacteristic == null) {
            return false;
        }

        // 发送数据
        gattCharacteristic.setValue("");

        return mBluetoothGatt.readCharacteristic(gattCharacteristic);
    }

    /**
     * 发送数据
     *
     * @param data 数据
     * @return true：发送成功 false：发送失败
     */
    public boolean sendData(byte[] data) {
        // 获取蓝牙设备的服务
        BluetoothGattService gattService = null;
        if (mBluetoothGatt != null) {
            gattService = mBluetoothGatt.getService(SERVICE_UUID);
        }
        if (gattService == null) {
            return false;
        }

        // 获取蓝牙设备的特征
        BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(CHARACTERISTIC_WRITE_UUID);
        if (gattCharacteristic == null) {
            return false;
        }

        // 发送数据
        gattCharacteristic.setValue(data);
        return mBluetoothGatt.writeCharacteristic(gattCharacteristic);
    }
}