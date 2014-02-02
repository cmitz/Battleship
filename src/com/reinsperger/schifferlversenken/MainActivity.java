package com.reinsperger.schifferlversenken;

import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.reinsperger.schifferlversenken.R;

public class MainActivity extends Activity {

	DrawView drawView;
	AdView adView;
	
	//KI-Variablen
	int xCom = 0, yCom = 0;
	int length = 0;
	boolean begin = true;
	int xStart = 0, yStart = 0;
	boolean nextint = false;
	boolean unten = false, oben = false, rechts = false, links = false;
	boolean u = false, o = false, r = false, l = false; 
	int davor = 0, dahinter = 0;
	int xNext = 0, yNext = 0;
	String wahl;
	int xbegin = 0, ybegin = 0, shiplength = 0, dx = 0, dy = 0;
	Dialog dialog;
	boolean verschieben = true;
	static boolean googleanalytics = true;
	static SharedPreferences einstellungen;
	
	static IInAppBillingService mService;
	ServiceConnection mServiceConn = new ServiceConnection() {
	   @Override
	   public void onServiceDisconnected(ComponentName name) {
	       mService = null;
	   }

	   @Override
	   public void onServiceConnected(ComponentName name, 
	      IBinder service) {
	       mService = IInAppBillingService.Stub.asInterface(service);
	   }
	};
	
	static String zufall;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	

		dialog = new Dialog(this);
		
		setContentView(R.layout.activity_main);
		
		drawView = (DrawView) findViewById(R.id.draw1);
//		adView = (AdView) findViewById(R.id.werbung);
		
		drawView.setBackgroundColor(Color.WHITE);
		
		drawView.dialog = new Dialog(this);
		
		einstellungen = PreferenceManager.getDefaultSharedPreferences(this);
		verschieben = einstellungen.getBoolean("pref_verschieben", true);
		googleanalytics = einstellungen.getBoolean("pref_googleanalytics", true);
		
		drawView.invalidate();
		
		bindService(new 
		        Intent("com.android.vending.billing.InAppBillingService.BIND"),
		                mServiceConn, Context.BIND_AUTO_CREATE);

	}
	
	@Override
	public void onResume() {
		super.onResume();
		verschieben = einstellungen.getBoolean("pref_verschieben", true);
		googleanalytics = einstellungen.getBoolean("pref_googleanalytics", true);
		
		if (googleanalytics) EasyTracker.getInstance().activityStart(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		 if (googleanalytics) EasyTracker.getInstance().activityStop(this);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    
	    if (mServiceConn != null) {
	        unbindService(mServiceConn);
	    }
	}
	
	@Override
	public void onStart() {
	    super.onStart();
	    if (googleanalytics) EasyTracker.getInstance().activityStart(this); // Add this method.

	}
	
	@Override
	public void onStop() {
	    super.onStop();
	    if (googleanalytics) EasyTracker.getInstance().activityStop(this); // Add this method.
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
		if (item.getItemId() == R.id.neue_anordnung) {
			drawView.SchiffeNeu = true;
			DrawView.setSpielStatus(false);
			DrawView.setSchiffeSetzen(true);
			
			length = 0;
			begin = true;
			xStart = 0;
			yStart = 0;
			nextint = false;
			unten = false;
			oben = false;
			rechts = false;
			links = false;
			xNext = 0;
			yNext = 0;
			wahl = "";
			drawView.fingerx = 10;
			drawView.fingery = 10;
			
			drawView.invalidate();
			
			return true;
		} else if (item.getItemId() == R.id.hilfe) {
			
			dialog.setContentView(R.layout.dialog);
			dialog.setTitle(getResources().getString(R.string.Hilfe));
		
			TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
			text.setText(getResources().getString(R.string.Hilfetext));
		
			Button dialogButton = (Button) dialog.findViewById(R.id.dialogbutton_OK);
			dialogButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		
			dialog.show();
			return true;
			
		} else if (item.getItemId() == R.id.settings) {
			
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		
		} else return super.onOptionsItemSelected(item);
		
    }
	
	public boolean onTouchEvent(MotionEvent event) {

		super.onTouchEvent(event);
		
		if ((event.getAction() == MotionEvent.ACTION_UP) && (DrawView.getSpielStatus() == true)) {
			
			int[] coords = new int[2];
			drawView.getLocationOnScreen(coords);
			
			int x = (int)((event.getX() - coords[0] - drawView.Rand_L) / drawView.raster);
			int y = (int)((event.getY() - coords[1] - drawView.Rand_O) / drawView.raster);
			
			if ((x >= 0) && (x <= 9) && (y >= 0) && (y <= 9)) {
				if (drawView.feldoffen[x][y] == false) {
					DrawView.schussZahl++;
					if ((drawView.spielfeld[x][y] > 0) && (drawView.spielfeld[x][y] < 10)) DrawView.trefferZahl++;
					
					drawView.feldoffen[x][y] = true;
					
					KI();
					
					drawView.FelderZeichnen = true;
		        	drawView.invalidate();
				}
			}
        	
		} else if ((event.getAction() == MotionEvent.ACTION_DOWN) && (DrawView.getSpielStatus() == true)) { 
			
			int[] coords = new int[2];
			drawView.getLocationOnScreen(coords);
			
			int x = (int)((event.getX() - coords[0] - drawView.Rand_L) / drawView.raster);
			int y = (int)((event.getY() - coords[1] - drawView.Rand_O) / drawView.raster);
			
			if ((x >= 0) && (x <= 9) && (y >= 0) && (y <= 9)) {
				if (drawView.feldoffen[x][y] == false) {
					drawView.fingerx = x;
					drawView.fingery = y;
				} else {
					drawView.fingerx = 10;
					drawView.fingery = 10;
				}
			} else {
				drawView.fingerx = 10;
				drawView.fingery = 10;
			}
		
			drawView.FelderZeichnen = true;
        	drawView.invalidate();
			
		} else if ((event.getAction() == MotionEvent.ACTION_MOVE) && (DrawView.getSpielStatus() == true)) { 
			
			int[] coords = new int[2];
			drawView.getLocationOnScreen(coords);
			
			int x = (int)((event.getX() - coords[0] - drawView.Rand_L) / drawView.raster);
			int y = (int)((event.getY() - coords[1] - drawView.Rand_O) / drawView.raster);
			
			if ((x >= 0) && (x <= 9) && (y >= 0) && (y <= 9)) {
				if (drawView.feldoffen[x][y] == false) {
					drawView.fingerx = x;
					drawView.fingery = y;
				} else {
					drawView.fingerx = 10;
					drawView.fingery = 10;
				}
			} else {
				drawView.fingerx = 10;
				drawView.fingery = 10;
			}
		
			drawView.FelderZeichnen = true;
        	drawView.invalidate();
			
		} else if ((event.getAction() == MotionEvent.ACTION_DOWN) & (DrawView.getSchiffeSetzen()) & (verschieben)) {

			int[] coords = new int[2];
			drawView.getLocationOnScreen(coords);
			
			int x = (int)((event.getX() - coords[0] - drawView.Rand_L * 3 - 10 * drawView.raster) / drawView.raster);
			int y = (int)((event.getY() - coords[1] - drawView.Rand_O) / drawView.raster);
			
			if ((x >= 0) && (x <= 9) && (y >= 0) && (y <= 9)) {
				if ((drawView.spielfeldCom[x][y] != 0) && (drawView.spielfeldCom[x][y] < 10)) {
					xbegin = x;
					ybegin = y;
					shiplength = drawView.spielfeldCom[x][y];
					
					drawView.druecken = true;
					
					if (shiplength > 1) {
						if ((x - 1 >= 0) && (drawView.spielfeldCom[x - 1][y] > 1) && (drawView.spielfeldCom[x - 1][y] < 10)) {
							dx = -1;
							dy = 0;
							l = true;
						} 
						
						if ((x + 1 <= 9) && (drawView.spielfeldCom[x + 1][y] > 1) && (drawView.spielfeldCom[x + 1][y] < 10)) {
							dx = 1;
							dy = 0;
							r = true;
						} 
						
						if ((y - 1 >= 0) && (drawView.spielfeldCom[x][y - 1] > 1) && (drawView.spielfeldCom[x][y - 1] < 10)) {
							dy = -1;
							dx = 0;
							o = true;
						} 
						
						if ((y + 1 <= 9) && (drawView.spielfeldCom[x][y + 1] > 1) && (drawView.spielfeldCom[x][y + 1] < 10)) {
							dy = 1;
							dx = 0;
							u = true;
						}
					}
					
					if (((l) && (r)) || ((o) && (u))) {
						
						int i = 0;
						
						while ((xbegin + i * dx >= 0) && (xbegin + i * dx <= 9) && (ybegin + i * dy >= 0) && (ybegin + i * dy <= 9) && (drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] == shiplength)) {
							
							if ((xbegin + i * dx - 1 >= 0) && (xbegin + i * dx - 1 <= 9) && (ybegin + i * dy - 1 >= 0) && (ybegin + i * dy - 1 <= 9) && (drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy - 1] >= 10)) drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy - 1] = drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy - 1] - 10;
							if ((xbegin + i * dx + 1 >= 0) && (xbegin + i * dx + 1 <= 9) && (ybegin + i * dy - 1 >= 0) && (ybegin + i * dy - 1 <= 9) && (drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy - 1] >= 10)) drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy - 1] = drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy - 1] - 10;
							if ((xbegin + i * dx - 1 >= 0) && (xbegin + i * dx - 1 <= 9) && (ybegin + i * dy + 1 >= 0) && (ybegin + i * dy + 1 <= 9) && (drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy + 1] >= 10)) drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy + 1] = drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy + 1] - 10;
							if ((xbegin + i * dx + 1 >= 0) && (xbegin + i * dx + 1 <= 9) && (ybegin + i * dy + 1 >= 0) && (ybegin + i * dy + 1 <= 9) && (drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy + 1] >= 10)) drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy + 1] = drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy + 1] - 10;
							
							
							i++;
							
						}
						
						if ((xbegin + i * dx >= 0) && (xbegin + i * dx <= 9) && (ybegin + i * dy >= 0) && (ybegin + i * dy <= 9)) drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] = drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] - 10;
						davor = i - 1;
						
						i = -1;
						
						while ((xbegin + i * dx >= 0) && (xbegin + i * dx <= 9) && (ybegin + i * dy >= 0) && (ybegin + i * dy <= 9) && (drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] == shiplength)) {
							
							if ((xbegin + i * dx - 1 >= 0) && (xbegin + i * dx - 1 <= 9) && (ybegin + i * dy - 1 >= 0) && (ybegin + i * dy - 1 <= 9) && (drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy - 1] >= 10)) drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy - 1] = drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy - 1] - 10;
							if ((xbegin + i * dx + 1 >= 0) && (xbegin + i * dx + 1 <= 9) && (ybegin + i * dy - 1 >= 0) && (ybegin + i * dy - 1 <= 9) && (drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy - 1] >= 10)) drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy - 1] = drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy - 1] - 10;
							if ((xbegin + i * dx - 1 >= 0) && (xbegin + i * dx - 1 <= 9) && (ybegin + i * dy + 1 >= 0) && (ybegin + i * dy + 1 <= 9) && (drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy + 1] >= 10)) drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy + 1] = drawView.spielfeldCom[xbegin + i * dx - 1][ybegin + i * dy + 1] - 10;
							if ((xbegin + i * dx + 1 >= 0) && (xbegin + i * dx + 1 <= 9) && (ybegin + i * dy + 1 >= 0) && (ybegin + i * dy + 1 <= 9) && (drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy + 1] >= 10)) drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy + 1] = drawView.spielfeldCom[xbegin + i * dx + 1][ybegin + i * dy + 1] - 10;
							
							i--;
							
						}
						
						if ((xbegin + i * dx >= 0) && (xbegin + i * dx <= 9) && (ybegin + i * dy >= 0) && (ybegin + i * dy <= 9)) drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] = drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] - 10;
						dahinter = i + 1;
						
					} else if (((l) || (r)) || ((o) || (u)) || (shiplength == 1)) {
						
						for (int i = 0; i < shiplength; i++) {
							if ((xbegin - 1 + i * dx >= 0) && (ybegin - 1 + i * dy >= 0) && (drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin - 1 + i * dy] >= 10)) drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin - 1 + i * dy] = drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin - 1 + i * dy] - 10;
							if ((xbegin + 1 + i * dx >= 0) && (xbegin + 1 + i * dx <= 9) && (ybegin - 1 + i * dy >= 0) && (ybegin - 1 + i * dy <= 9) && (drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin - 1 + i * dy] >= 10)) drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin - 1 + i * dy] = drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin - 1 + i * dy] - 10;
							if ((xbegin - 1 + i * dx >= 0) && (ybegin + 1 + i * dy <= 9) && (drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin + 1 + i * dy] >= 10)) drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin + 1 + i * dy] = drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin + 1 + i * dy] - 10;
							if ((xbegin + 1 + i * dx <= 9) && (ybegin + 1 + i * dy <= 9) && (drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin + 1 + i * dy] >= 10)) drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin + 1 + i * dy] = drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin + 1 + i * dy] - 10;
						}
						
						if ((shiplength >= 2) && (shiplength < 10)) {
							if ((xbegin + dx * (- 1) >= 0) && (xbegin + dx * (- 1) <= 9) && (ybegin + dy * (- 1) >= 0) && (ybegin + dy * (- 1) <= 9) && (drawView.spielfeldCom[xbegin + dx * (- 1)][ybegin + dy * (- 1)] >= 10)) drawView.spielfeldCom[xbegin + dx * (- 1)][ybegin + dy * (- 1)] = drawView.spielfeldCom[xbegin + dx * (- 1)][ybegin + dy * (- 1)] - 10;
							if ((xbegin + dx * shiplength >= 0) && (xbegin + dx * shiplength <= 9) && (ybegin + dy * shiplength >= 0) && (ybegin + dy * shiplength <= 9) && (drawView.spielfeldCom[xbegin + dx * shiplength][ybegin + dy * shiplength] >= 10)) drawView.spielfeldCom[xbegin + dx * shiplength][ybegin + dy * shiplength] = drawView.spielfeldCom[xbegin + dx * shiplength][ybegin + dy * shiplength] - 10;
						}
						
					}
					
					if (shiplength == 1) {
						if ((xbegin - 1 >= 0) && (drawView.spielfeldCom[xbegin - 1][ybegin] >= 10)) drawView.spielfeldCom[xbegin - 1][ybegin] = drawView.spielfeldCom[xbegin - 1][ybegin] - 10;
						if ((xbegin + 1 <= 9) && (drawView.spielfeldCom[xbegin + 1][ybegin] >= 10)) drawView.spielfeldCom[xbegin + 1][ybegin] = drawView.spielfeldCom[xbegin + 1][ybegin] - 10;
						if ((ybegin - 1 >= 0) && (drawView.spielfeldCom[xbegin][ybegin - 1] >= 10)) drawView.spielfeldCom[xbegin][ybegin - 1] = drawView.spielfeldCom[xbegin][ybegin - 1] - 10;
						if ((ybegin + 1 <= 9) && (drawView.spielfeldCom[xbegin][ybegin + 1] >= 10)) drawView.spielfeldCom[xbegin][ybegin + 1] = drawView.spielfeldCom[xbegin][ybegin + 1] - 10;
					}
					
				} else {
					xbegin = x;
					ybegin = y;
					shiplength = 0;
				}
				
				drawView.FelderZeichnen = true;
	        	drawView.invalidate();
			}
		
		} else if ((event.getAction() == MotionEvent.ACTION_MOVE) & (DrawView.getSchiffeSetzen()) & (verschieben)) {

			int[] coords = new int[2];
			drawView.getLocationOnScreen(coords);
			
			int x = (int)((event.getX() - coords[0] - drawView.Rand_L * 3 - 10 * drawView.raster) / drawView.raster);
			int y = (int)((event.getY() - coords[1] - drawView.Rand_O) / drawView.raster);
			
			if ((x >= 0) && (x <= 9) && (y >= 0) && (y <= 9) && ((x != xbegin) || (y != ybegin))) {
				
				if (((l) && (r)) || ((o) && (u))) {
					
					if ((shiplength > 0) && (shiplength < 10) && (x + davor * dx >= 0) && (x + davor * dx <= 9) && (x + dahinter * dx >= 0) && (x + dahinter * dx <= 9) && (y + davor * dy >= 0) && (y + davor * dy <= 9) && (y + dahinter * dy >= 0) && (y + dahinter * dy <= 9)) {
						
						for (int i = 0; i <= davor; i++) {
							drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] = 0;
						}
						for (int i = -1; i >= dahinter; i--) {
							drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] = 0;
						}
						
						boolean ok = true;
						for (int i = 0; i <= davor; i++) {
							if (drawView.spielfeldCom[x + i * dx][y + i * dy] >0 ) ok = false;
						}
						for (int i = -1; i >= dahinter; i--) {
							if (drawView.spielfeldCom[x + i * dx][y + i * dy] >0 ) ok = false;
						}
						
						if (ok) {
							for (int i = 0; i <= davor; i++) {
								drawView.spielfeldCom[x + i * dx][y + i * dy] = shiplength;
							}
							for (int i = -1; i >= dahinter; i--) {
								drawView.spielfeldCom[x + i * dx][y + i * dy] = shiplength;
							}
						
							xbegin = x;
							ybegin = y;
						} else {
							for (int i = 0; i <= davor; i++) {
								drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] = shiplength;
							}
							for (int i = -1; i >= dahinter; i--) {
								drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] = shiplength;
							}
						}
					}
					
				} else if (((l) || (r)) || ((o) || (u)) || (shiplength == 1)) {
					if ((shiplength > 0) && (shiplength < 10) && (x + (shiplength - 1) * dx >= 0) && (x + (shiplength - 1) * dx <= 9) && (y + (shiplength - 1) * dy >= 0) && (y + (shiplength - 1) * dy <= 9)) {
						
						for (int i = 0; i < shiplength; i++) {
							drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] = 0;
						}
						
						boolean ok = true;
						for (int i = 0; i < shiplength; i++) {
							if (drawView.spielfeldCom[x + i * dx][y + i * dy] >0 ) ok = false;
						}
						
						if (ok) {						
							for (int i = 0; i < shiplength; i++) {
								drawView.spielfeldCom[x + i * dx][y + i * dy] = shiplength;
							}
						
							xbegin = x;
							ybegin = y;
						} else {
							for (int i = 0; i < shiplength; i++) {
								drawView.spielfeldCom[xbegin + i * dx][ybegin + i * dy] = shiplength;
							}
						}
					}
				}
				
				drawView.FelderZeichnen = true;
	        	drawView.invalidate();
			}
		}
		
		if ((event.getAction() == MotionEvent.ACTION_UP) && (DrawView.getSchiffeSetzen())) {
			
			int[] coords = new int[2];
			drawView.getLocationOnScreen(coords);
			
			int y = (int)((event.getY() - coords[1] - drawView.Rand_O) / drawView.raster);
			
			int x2 = (int)((event.getX() - coords[0] - drawView.Rand_L) / drawView.raster);
			
			drawView.druecken = false;
			
			if (verschieben) {
				
				if (((l) && (r)) || ((o) && (u))) {
					
					for (int i = 0; i <= davor; i++) {
						if ((xbegin - 1 + i * dx >= 0) && (ybegin - 1 + i * dy >= 0)) drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin - 1 + i * dy] = drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin - 1 + i * dy] + 10;
						if ((xbegin + 1 + i * dx <= 9) && (ybegin - 1 + i * dy >= 0)) drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin - 1 + i * dy] = drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin - 1 + i * dy] + 10;
						if ((xbegin - 1 + i * dx >= 0) && (ybegin + 1 + i * dy <= 9)) drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin + 1 + i * dy] = drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin + 1 + i * dy] + 10;
						if ((xbegin + 1 + i * dx <= 9) && (ybegin + 1 + i * dy <= 9)) drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin + 1 + i * dy] = drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin + 1 + i * dy] + 10;
					}
					for (int i = -1; i >= dahinter; i--) {
						if ((xbegin - 1 + i * dx >= 0) && (ybegin - 1 + i * dy >= 0)) drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin - 1 + i * dy] = drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin - 1 + i * dy] + 10;
						if ((xbegin + 1 + i * dx <= 9) && (ybegin - 1 + i * dy >= 0)) drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin - 1 + i * dy] = drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin - 1 + i * dy] + 10;
						if ((xbegin - 1 + i * dx >= 0) && (ybegin + 1 + i * dy <= 9)) drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin + 1 + i * dy] = drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin + 1 + i * dy] + 10;
						if ((xbegin + 1 + i * dx <= 9) && (ybegin + 1 + i * dy <= 9)) drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin + 1 + i * dy] = drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin + 1 + i * dy] + 10;
					}
					
					if ((shiplength >= 2) && (shiplength < 10)) {
						if ((xbegin + dx * (davor + 1) >= 0) && (xbegin + dx * (davor + 1) <= 9) && (ybegin + dy * (davor + 1) >= 0) && (ybegin + dy * (davor + 1) <= 9)) drawView.spielfeldCom[xbegin + dx * (davor + 1)][ybegin + dy * (davor + 1)] += 10;
						if ((xbegin + dx * (dahinter - 1) >= 0) && (xbegin + dx * (dahinter - 1) <= 9) && (ybegin + dy * (dahinter - 1) >= 0) && (ybegin + dy * (dahinter - 1) <= 9)) drawView.spielfeldCom[xbegin + dx * (dahinter - 1)][ybegin + dy * (dahinter - 1)] += 10;
					}
					
				} else if (((l) || (r)) || ((o) || (u)) || (shiplength == 1)) {
					
					for (int i = 0; i < shiplength; i++) {
						if ((xbegin - 1 + i * dx >= 0) && (ybegin - 1 + i * dy >= 0)) drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin - 1 + i * dy] = drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin - 1 + i * dy] + 10;
						if ((xbegin + 1 + i * dx <= 9) && (ybegin - 1 + i * dy >= 0)) drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin - 1 + i * dy] = drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin - 1 + i * dy] + 10;
						if ((xbegin - 1 + i * dx >= 0) && (ybegin + 1 + i * dy <= 9)) drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin + 1 + i * dy] = drawView.spielfeldCom[xbegin - 1 + i * dx][ybegin + 1 + i * dy] + 10;
						if ((xbegin + 1 + i * dx <= 9) && (ybegin + 1 + i * dy <= 9)) drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin + 1 + i * dy] = drawView.spielfeldCom[xbegin + 1 + i * dx][ybegin + 1 + i * dy] + 10;
					}
						
					if (shiplength == 1) {
						if (xbegin - 1 >= 0) drawView.spielfeldCom[xbegin - 1][ybegin] = drawView.spielfeldCom[xbegin - 1][ybegin] + 10;
						if (xbegin + 1 <= 9) drawView.spielfeldCom[xbegin + 1][ybegin] = drawView.spielfeldCom[xbegin + 1][ybegin] + 10;
						if (ybegin - 1 >= 0) drawView.spielfeldCom[xbegin][ybegin - 1] = drawView.spielfeldCom[xbegin][ybegin - 1] + 10;
						if (ybegin + 1 <= 9) drawView.spielfeldCom[xbegin][ybegin + 1] = drawView.spielfeldCom[xbegin][ybegin + 1] + 10;
					} else if ((shiplength >= 2) && (shiplength < 10)) {
						if ((xbegin + dx * (- 1) >= 0) && (xbegin + dx * (- 1) <= 9) && (ybegin + dy * (- 1) >= 0) && (ybegin + dy * (- 1) <= 9)) drawView.spielfeldCom[xbegin + dx * (- 1)][ybegin + dy * (- 1)] = drawView.spielfeldCom[xbegin + dx * (- 1)][ybegin + dy * (- 1)] + 10;
						if ((xbegin + dx * shiplength >= 0) && (xbegin + dx * shiplength <= 9) && (ybegin + dy * shiplength >= 0) && (ybegin + dy * shiplength <= 9)) drawView.spielfeldCom[xbegin + dx * shiplength][ybegin + dy * shiplength] = drawView.spielfeldCom[xbegin + dx * shiplength][ybegin + dy * shiplength] + 10;
					}
					
				}
				
			}
					
			if ((x2 >= 2) && (x2 <= 7) && (y >= 2) && (y <= 7)) {
				DrawView.setSpielStatus(true);
				DrawView.setSchiffeSetzen(false);
			}
					
			l = false;
			r = false;
			o = false;
			u = false;
			davor = 0;
			dahinter = 0;
			
			drawView.FelderZeichnen = true;
		    drawView.invalidate();
		    
		}
		
		
		
		return true;
	}
	
	public boolean UnterSchiff(int x1, int y1, int laenge, int aex, int aey, int[][] feld) {
		
		boolean resultat = true;
		
		for (int i = 0; i < laenge; i++) {
			if (feld[x1 + i * aex][y1 + i * aey] != shiplength) {
				if ((feld[x1 + i * aex][y1 + i * aey] > 0) /*&& (feld[x1 + i * aex][y1 + i * aey] < 10)*/) {
					resultat = false;
				}
			}
		}
		
		return resultat;
		
	}
	
	public void KI() {
		
		Random rand = new Random();
		
		if (length == 0) {
			xCom = rand.nextInt(10);
			yCom = rand.nextInt(10);
		
			while (drawView.feldoffenCom[xCom][yCom]) {
				xCom = rand.nextInt(10);
				yCom = rand.nextInt(10);
			}
		} else {
			xCom = xNext;
			yCom = yNext;
		}
		
		drawView.feldoffenCom[xCom][yCom] = true;
		drawView.feldgetroffenCom[xCom][yCom] = true;
		if ((drawView.spielfeldCom[xCom][yCom] > 0) && (drawView.spielfeldCom[xCom][yCom] < 10)) {
			if (length == 0) {
				
				if ((xCom > 0) && (yCom > 0)) drawView.feldoffenCom[xCom - 1][yCom - 1] = true;
				if ((xCom > 0) && (yCom < 9)) drawView.feldoffenCom[xCom - 1][yCom + 1] = true;
				if ((xCom < 9) && (yCom > 0)) drawView.feldoffenCom[xCom + 1][yCom - 1] = true;
				if ((xCom < 9) && (yCom < 9)) drawView.feldoffenCom[xCom + 1][yCom + 1] = true;
			
				if (drawView.spielfeldCom[xCom][yCom] == 1) {
					if (xCom > 0) drawView.feldoffenCom[xCom - 1][yCom] = true;
					if (xCom < 9) drawView.feldoffenCom[xCom + 1][yCom] = true;
					if (yCom > 0) drawView.feldoffenCom[xCom][yCom - 1] = true;
					if (yCom < 9) drawView.feldoffenCom[xCom][yCom + 1] = true;
				}
			
				if ((drawView.spielfeldCom[xCom][yCom] > 1) && (drawView.spielfeldCom[xCom][yCom] < 10) && (begin)) {
					length = drawView.spielfeldCom[xCom][yCom] - 1;
					begin = false;
					xStart = xCom;
					yStart = yCom;
					nextint = false;
					
					while (!nextint) {
						int i = rand.nextInt(4);
						
						if ((i == 0) && (!unten) && (yStart + 1 <= 9) && (!drawView.feldoffenCom[xStart][yStart + 1])) {
							yNext = yStart + 1;
							xNext = xStart;
							nextint = true;
							unten = true;
							wahl = "unten";
						} else if ((i == 1) && (!oben) && (yStart - 1 >= 0) && (!drawView.feldoffenCom[xStart][yStart - 1])) {
							yNext = yStart - 1;
							xNext = xStart;
							nextint = true;
							oben = true;
							wahl = "oben";
						} else if ((i == 2) && (!links) && (xStart - 1 >= 0) && (!drawView.feldoffenCom[xStart - 1][yStart])) {
							xNext = xStart - 1;
							yNext = yStart;
							nextint = true;
							links = true;
							wahl = "links";
						} else if ((i == 3) && (!rechts) && (xStart + 1 <= 9) && (!drawView.feldoffenCom[xStart + 1][yStart])) {
							xNext = xStart + 1;
							yNext = yStart;
							nextint = true;
							rechts = true;
							wahl = "rechts";
						}
					}
				}
			} else {
				length--;
				
				if ((xCom > 0) && (yCom > 0)) drawView.feldoffenCom[xCom - 1][yCom - 1] = true;
				if ((xCom > 0) && (yCom < 9)) drawView.feldoffenCom[xCom - 1][yCom + 1] = true;
				if ((xCom < 9) && (yCom > 0)) drawView.feldoffenCom[xCom + 1][yCom - 1] = true;
				if ((xCom < 9) && (yCom < 9)) drawView.feldoffenCom[xCom + 1][yCom + 1] = true;
				
				if (length > 0) {
					if ((wahl == "oben") && (yNext - 1 >= 0)) {
						yNext--;
					} else if ((wahl == "unten") && (yNext + 1 <= 9)) {
						yNext++;
					} else if ((wahl == "links") && (xNext - 1 >= 0)) {
						xNext--;
					} else if ((wahl == "rechts") && (xNext + 1 <= 9)) {
						xNext++;
					} else if (wahl == "oben") {
						yNext = yStart + 1;
						wahl = "unten";
					} else if (wahl == "unten") {
						yNext = yStart - 1;
						wahl = "oben";
					} else if (wahl == "links") {
						xNext = xStart + 1;
						wahl = "rechts";
					} else if (wahl == "rechts") {
						xNext = xStart - 1;
						wahl = "links";
					}
				} else {
					if ((wahl == "oben") && (yNext - 1 >= 0)) {
						drawView.feldoffenCom[xNext][yNext - 1] = true;
					} else if ((wahl == "unten") && (yNext + 1 <= 9)) {
						drawView.feldoffenCom[xNext][yNext + 1] = true;
					} else if ((wahl == "links") && (xNext - 1 >= 0)) {
						drawView.feldoffenCom[xNext - 1][yNext] = true;
					} else if ((wahl == "rechts") && (xNext + 1 <= 9)) {
						drawView.feldoffenCom[xNext + 1][yNext] = true;
					}
					
					if ((wahl == "oben") && (yStart + 1 <= 9)) {
						drawView.feldoffenCom[xStart][yStart + 1] = true;
					} else if ((wahl == "unten") && (yStart - 1 >= 0)) {
						drawView.feldoffenCom[xStart][yStart - 1] = true;
					} else if ((wahl == "links") && (xStart + 1 <= 9)) {
						drawView.feldoffenCom[xStart + 1][yStart] = true;
					} else if ((wahl == "rechts") && (xStart - 1 >= 0)) {
						drawView.feldoffenCom[xStart - 1][yStart] = true;
					}
					
					length = 0;
					begin = true;
					xStart = 0;
					yStart = 0;
					nextint = false;
					unten = false;
					oben = false;
					rechts = false;
					links = false;
					xNext = 0;
					yNext = 0;
					wahl = "";
				}
			}
			
			DrawView.schussZahlCom++;
			DrawView.trefferZahlCom++;
			
			
		} else if (length > 0) {
			nextint = false;
			
			while (!nextint) {
				int i = rand.nextInt(4);
				
				if ((i == 0) && (!unten) && (yStart + 1 <= 9) && (!drawView.feldoffenCom[xStart][yStart + 1])) {
					yNext = yStart + 1;
					xNext = xStart;
					nextint = true;
					unten = true;
					wahl = "unten";
				} else if ((i == 1) && (!oben) && (yStart - 1 >= 0) && (!drawView.feldoffenCom[xStart][yStart - 1])) {
					yNext = yStart - 1;
					xNext = xStart;
					nextint = true;
					oben = true;
					wahl = "oben";
				} else if ((i == 2) && (!links) && (xStart - 1 >= 0) && (!drawView.feldoffenCom[xStart - 1][yStart])) {
					xNext = xStart - 1;
					yNext = yStart;
					nextint = true;
					links = true;
					wahl = "links";
				} else if ((i == 3) && (!rechts) && (xStart + 1 <= 9) && (!drawView.feldoffenCom[xStart + 1][yStart])) {
					xNext = xStart + 1;
					yNext = yStart;
					nextint = true;
					rechts = true;
					wahl = "rechts";
				}
			}
			DrawView.schussZahlCom++;
		} else DrawView.schussZahlCom++;
		
		drawView.FelderZeichnen = true;
    	drawView.invalidate();
	}

}
