//Copyright (c) Microsoft Corporation All rights reserved.  
// 
//MIT License: 
// 
//Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
//documentation files (the  "Software"), to deal in the Software without restriction, including without limitation
//the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
//to permit persons to whom the Software is furnished to do so, subject to the following conditions: 
// 
//The above copyright notice and this permission notice shall be included in all copies or substantial portions of
//the Software. 
// 
//THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
//TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
//THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
//CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//IN THE SOFTWARE.
package band.sdk.sampleapp2;

import band.sdk.sampleapp.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
public class BandStreamingAppActivity extends Activity implements OnClickListener{
	private MyReceiver receiver;
	private String Category = "walk";
	private String username = "NULL";
	private final String tag="BandStreamActivity";
	private Button btnStart,bnStop;
	private TextView txtStatus;
	private RadioGroup cat;
	private RadioButton walk;
	private RadioButton run;
	private RadioButton upstairs;
	private RadioButton downstairs;
	private EditText timeSet;
    boolean onpause_flag=true;
	private PowerManager.WakeLock wakeLock=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.d(tag,"onCreat excuted");
        super.onCreate(savedInstanceState);
		PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
		wakeLock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"mywakelock");
		wakeLock.acquire();
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");//
        cat = (RadioGroup)findViewById(R.id.radio4cat);
        walk = (RadioButton)findViewById(R.id.radioWalk);
        run = (RadioButton)findViewById(R.id.radioRun);
        upstairs = (RadioButton)findViewById(R.id.radioUpstairs);
        downstairs = (RadioButton)findViewById(R.id.radioDownstairs);
        btnStart = (Button) findViewById(R.id.btnStart);
         findViewById(R.id.statis).setOnClickListener(this);
		bnStop=(Button)findViewById(R.id.stop);
		btnStart.setOnClickListener(this);
		bnStop.setOnClickListener(this);
		txtStatus=(TextView) findViewById(R.id.txtStatus);
		timeSet=(EditText)findViewById((R.id.editText));
        cat.setOnCheckedChangeListener(new OnCheckedChangeListener(){
        	@Override
        	public void onCheckedChanged(RadioGroup rg, int checkedId) {
        		// TODO Auto-generated method stub
        		if(checkedId == walk.getId()){
        			Category = "walk";
        		}else if (checkedId == run.getId()) {
        			Category = "run";
				}else if (checkedId == upstairs.getId()) {
        			Category = "upstairs";
				}else if (checkedId == downstairs.getId()) {
        			Category = "downstairs";
				}
        		txtStatus.setText(username +"---> "+ Category);
        	}
        });
    }
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnStart:
				//btnStart.setEnabled(false);
				try{
					Integer.parseInt(timeSet.getText().toString());
				}catch(NumberFormatException e){
					txtStatus.setText("please input collect time");
					break;
				}
				Intent startIntent = new Intent(this, BandService.class);
				startIntent.putExtra("name",username);
				startIntent.putExtra("Category",Category);
				startIntent.putExtra("num",timeSet.getText().toString());
				Log.d(tag,timeSet.getText().toString());
				startService(startIntent);
				receiver=new MyReceiver();
				IntentFilter filter=new IntentFilter();
				filter.addAction("band.sdk.sampleapp2.BandService");
				BandStreamingAppActivity.this.registerReceiver(receiver,filter);
				break;
			case R.id.stop:
				Intent stopIntent = new Intent(this, BandService.class);
				BandStreamingAppActivity.this.unregisterReceiver(receiver);
				stopService(stopIntent);
				timeSet.setText("");
				txtStatus.setText("");
				break;
				case R.id.statis:
				Intent statisIntent = new Intent(this, SaitisListiFilePathActivity.class);
					statisIntent.putExtra("name",username);
					statisIntent.putExtra("Category",Category);
				startActivity(statisIntent);
				break;
			default:
				break;
		}
	}
	public class MyReceiver extends BroadcastReceiver{
		public void onReceive(Context context,Intent intent){
            if(onpause_flag==true) {
                Bundle bundle = intent.getExtras();
                if (bundle.getInt("timeleft") < 1) {
					BandStreamingAppActivity.this.unregisterReceiver(receiver);
                    txtStatus.setText("finished");
                    timeSet.setText("");
                } else {
                    txtStatus.setText(bundle.getInt("timeleft") + "seconds left" + " " + bundle.getString("state"));
                }
            }
		}
	}
	@Override
	protected void onResume() {
        onpause_flag=true;
		super.onResume();
		Log.d(tag,"onresume executed");
		txtStatus.setText("");
	}

    @Override
	protected void onPause() {
        onpause_flag=false;
		Log.d(tag,"onpuse executed");
		super.onPause();
	}
	public void onDestroy(){
		Log.d(tag,"ondestory executed");
		Intent stopIntent = new Intent(this, BandService.class);
		stopService(stopIntent);
		if(wakeLock!=null){
			wakeLock.release();
			wakeLock=null;
		}
		super.onDestroy();
	}
	

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//boolean res = writeclose();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}

