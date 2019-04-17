package band.sdk.sampleapp2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import band.sdk.sampleapp.R;

public class SaitisListActivity extends Activity {
    private ListView list;
    String filepath;

    private List<String> fileList=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_list_statis);


        Intent intent=getIntent();
        filepath = intent.getStringExtra("path");
        inintView();
    }
    public static ArrayList<String> getFiles(String path) {
        ArrayList<String> files = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i].toString());
            }
            if (tempList[i].isDirectory()) {
            }
        }
        return files;
    }
    private void inintView() {
        list= (ListView) findViewById(R.id.list);

        fileList=getFiles(filepath);
        if (fileList==null&&fileList.size()<0){
            Toast.makeText(this, "暂无文件数据", Toast.LENGTH_SHORT).show();
            finish();
        }
list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(SaitisListActivity.this,SaitisActivity.class)
                .putExtra("path",fileList.get(i))
        );
    }
});
        list.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return fileList.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                TextView textView=new TextView(SaitisListActivity.this);
                textView.setText(fileList.get(i));
                return textView;
            }
        });


    }

}
