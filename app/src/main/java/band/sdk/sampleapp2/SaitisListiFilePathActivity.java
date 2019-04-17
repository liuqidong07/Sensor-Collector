package band.sdk.sampleapp2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import band.sdk.sampleapp.R;

public class SaitisListiFilePathActivity extends Activity {
    String filepath_acc;
    String filepath_cal;
    String filepath_gyr;
    String filepath_ski;
    String filepath_uv;
    String filepath_dis;

    public String Category = "walk";
    public String username = "NULL";
    public String sdcard = android.os.Environment.getExternalStorageDirectory().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_list_filestatis);

        inintView();
        Intent intent=getIntent();
        Category = intent.getStringExtra("Category");
        username = intent.getStringExtra("name");
        filepath_acc = sdcard + "/101SensorData/" + username + "/" + Category + "/加速度";
        filepath_cal = sdcard + "/101SensorData/" + username + "/" + Category + "/陀螺仪";
        filepath_gyr = sdcard + "/101SensorData/" + username + "/" + Category + "/重力";
        filepath_ski = sdcard + "/101SensorData/" + username + "/" + Category + "/磁力计";
        filepath_uv = sdcard + "/101SensorData/" + username + "/" + Category + "/线性加速度";
        filepath_dis = sdcard + "/101SensorData/" + username + "/" + Category + "/rotation vector";
    }

    private void inintView() {
       findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(SaitisListiFilePathActivity.this,SaitisListActivity.class)
               .putExtra("path",filepath_acc)
               );
           }
       });  findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(SaitisListiFilePathActivity.this,SaitisListActivity.class)
               .putExtra("path",filepath_cal)
               );
           }
       });  findViewById(R.id.tv3).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(SaitisListiFilePathActivity.this,SaitisListActivity.class)
               .putExtra("path",filepath_gyr)
               );
           }
       });  findViewById(R.id.tv4).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(SaitisListiFilePathActivity.this,SaitisListActivity.class)
               .putExtra("path",filepath_ski)
               );
           }
       });  findViewById(R.id.tv5).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(SaitisListiFilePathActivity.this,SaitisListActivity.class)
               .putExtra("path",filepath_uv)
               );
           }
       });  findViewById(R.id.tv6).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(SaitisListiFilePathActivity.this,SaitisListActivity.class)
               .putExtra("path",filepath_dis)
               );
           }
       });




    }

}
