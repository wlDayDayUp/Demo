package net.winsoft.myble;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView teTv1, teTv2, teTv3, teTv4;
    private SharedPreferences sp;
    private String[] tes = {
            "30", "40", "50", "60", "70"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//
//        //设置为ActionBar
//        setSupportActionBar((Toolbar) findViewById(R.id.mainToolbar));
//        //显示那个箭头
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);

        int wd_index_1 = sp.getInt("wd_index_1", 30);
        int wd_index_2 = sp.getInt("wd_index_2", 30);
        int wd_index_3 = sp.getInt("wd_index_3", 30);
        int wd_index_4 = sp.getInt("wd_index_4", 30);

        teTv1 = findViewById(R.id.teTv1);
        teTv1.setOnClickListener(this);
        teTv2 = findViewById(R.id.teTv2);
        teTv2.setOnClickListener(this);
        teTv3 = findViewById(R.id.teTv3);
        teTv3.setOnClickListener(this);
        teTv4 = findViewById(R.id.teTv4);
        teTv4.setOnClickListener(this);

        teTv1.setText(wd_index_1 + " ℃");
        teTv2.setText(wd_index_2 + " ℃");
        teTv3.setText(wd_index_3 + " ℃");
        teTv4.setText(wd_index_4 + " ℃");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.teTv1:
                showTeSelectDialog(teTv1, 1);
                break;
            case R.id.teTv2:
                showTeSelectDialog(teTv2, 2);
                break;
            case R.id.teTv3:
                showTeSelectDialog(teTv3, 3);
                break;
            case R.id.teTv4:
                showTeSelectDialog(teTv4, 4);
                break;
            default:
                break;
        }
    }

    // "30", "40", "50", "60", "70"
    private void showTeSelectDialog(final TextView tv, final int index) {
        new AlertDialog.Builder(this)
                .setTitle("请选择温度")
                .setSingleChoiceItems(tes, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            if (tv != null) {
                                tv.setText(tes[i] + " ℃");
                            }
                            sp.edit().putInt("wd_index_" + index, Integer.parseInt(tes[i])).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }
}
