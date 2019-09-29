package net.winsoft.myble.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.winsoft.myble.ItemClickAdapter;
import net.winsoft.myble.R;

public class DialogUtil {

    private Context mContent;
    private AlertDialog deviceListDialog;
    private ProgressDialog loadingDialog;

    public DialogUtil(Context mContent) {
        this.mContent = mContent;
    }

    public void showTwoBtDialog(String msg, boolean cancelable, String positive, String negative, final IBtOnClickListen iBtOnClickListen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContent)
                .setMessage(msg)
                .setCancelable(cancelable)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        iBtOnClickListen.positiveClick(dialogInterface);
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        iBtOnClickListen.negativeClick(dialogInterface);
                    }
                });
        builder.create().show();
    }

    public void showDeviceListDialog(ItemClickAdapter adapter) {
        View view = LayoutInflater.from(mContent).inflate(R.layout.dialog_device, null);

        RecyclerView recyclerView = view.findViewById(R.id.device_rl);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContent));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContent, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContent)
                .setView(view);
        deviceListDialog = builder.create();

        deviceListDialog.show();
    }

    public void missDeviceListDialog() {
        if (deviceListDialog != null && deviceListDialog.isShowing()) {
            deviceListDialog.dismiss();
        }
    }

    public void showLoading(String msg, boolean cancelable) {
        loadingDialog = new ProgressDialog(mContent);
        loadingDialog.setMessage(msg);
        loadingDialog.setCancelable(cancelable);
        loadingDialog.show();
    }

    public void missLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public interface IBtOnClickListen {
        void positiveClick(DialogInterface dialogInterface);

        void negativeClick(DialogInterface dialogInterface);
    }
}
