package band.sdk.sampleapp2;


import band.sdk.sampleapp.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	private String nameString;
	private ImageButton enterbtn;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.startactivity);
        enterbtn = (ImageButton)findViewById(R.id.enterBtn);
        enterbtn.setOnClickListener(new OnClickListener() {		
        	@Override
        	public void onClick(View v) {
        		final EditText nameInput = new EditText(MainActivity.this);
        		new AlertDialog.Builder(MainActivity.this)  
        		.setTitle("Input your name")  
        		.setIcon(android.R.drawable.ic_dialog_info)  
        		.setView(nameInput)  
        		.setPositiveButton("Start", new DialogInterface.OnClickListener(){
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				// TODO Auto-generated method stub
        				nameString = nameInput.getText().toString();		
        				if (nameString.equals("")) {
							nameString = "namenull";
						}
        				StartCollecting();        			
        			}               	
        		})
        		.setNegativeButton("Cancel", null)
        		.show();
        	}
        });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items t o the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void StartCollecting(){
		Intent intent = new Intent();
        intent.setClass(MainActivity.this, BandStreamingAppActivity.class);
        intent.putExtra("username", nameString);
        startActivity(intent);
        finish();
	}
}
