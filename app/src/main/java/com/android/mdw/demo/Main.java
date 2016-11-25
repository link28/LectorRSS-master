package com.android.mdw.demo;

import java.util.HashMap;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class Main extends Activity {

	static final String DATA_TITLE = "T";
	static final String DATA_LINK  = "L";
	static LinkedList<HashMap<String, String>> data;
	static String feedUrl;
	private ProgressDialog progressDialog;

	private final Handler progressHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				data = (LinkedList<HashMap<String, String>>)msg.obj;
				setData(data);					
			}
			progressDialog.dismiss();
	    }
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle("Lector RSS");
        Button btn = (Button) findViewById(R.id.btnLoad);

        btn.setOnClickListener(new OnClickListener() {			
    		@Override
    		public void onClick(View v) {
    			ListView lv = (ListView) findViewById(R.id.lstData);
    			if (lv.getAdapter() != null) {
    				AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
    				builder.setMessage("ya ha cargado datos, Esta seguro de hacerlo de nuevo?")
    				       .setCancelable(false)
    				       .setPositiveButton("Si", new DialogInterface.OnClickListener() {
    				           public void onClick(DialogInterface dialog, int id) {
    				        	   loadData();
    				           }
    				       })
    				       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    				           public void onClick(DialogInterface dialog, int id) {
    				                dialog.cancel();					            
    				           }
    				       })
    				       .create()
    				       .show();
    			} else {
    				loadData();
    			}
    		}
    	});
        
        ListView lv = (ListView) findViewById(R.id.lstData);
        lv.setOnItemClickListener(new OnItemClickListener() {

    		@Override
    		public void onItemClick(AdapterView<?> av, View v, int position,
    				long id) {
    			HashMap<String, String> entry = data.get(position);
    			Intent browserAction = new Intent(Intent.ACTION_VIEW,
    					Uri.parse(entry.get(DATA_LINK)));
    			startActivity(browserAction);				
    		}
    	});        
    }

	public void opciones (View view){
		switch (view.getId()){
			case R.id.rbCongreso:
				feedUrl =  "http://www.congresochihuahua.gob.mx/rss.php";
				break;
			case R.id.rbFinanciero:
				feedUrl =  "http://www.elfinanciero.com.mx/rss/";
				break;
			case R.id.rbDeportes:
				feedUrl =  "http://www.liderendeportes.com/cmspages/rss/lider/loultimo.aspx";
				break;
		}	}
    private void setData(LinkedList<HashMap<String, String>> data){
    	SimpleAdapter sAdapter = new SimpleAdapter(getApplicationContext(), data, 
    			android.R.layout.two_line_list_item, 
    			new String[] { DATA_TITLE, DATA_LINK }, 
    			new int[] { android.R.id.text1, android.R.id.text2 });
    	ListView lv = (ListView) findViewById(R.id.lstData);
    	lv.setAdapter(sAdapter);
    }
    private void loadData() {
    	progressDialog = ProgressDialog.show(
    			Main.this,"","Por favor espere mientras se cargan los datos...",true);
    	new Thread(new Runnable(){
    		@Override
    		public void run() {
    			XMLParser parser = new XMLParser(feedUrl); 
                Message msg = progressHandler.obtainMessage();
                msg.obj = parser.parse();
    			progressHandler.sendMessage(msg);
    		}}).start();
    }    
}