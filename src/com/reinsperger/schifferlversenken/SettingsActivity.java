package com.reinsperger.schifferlversenken;

import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.reinsperger.schifferlversenken.R;

public class SettingsActivity extends PreferenceActivity {

	Dialog dialog;
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        dialog = new Dialog(this);
    }
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart() {
	    super.onStart();
	    if (MainActivity.googleanalytics) EasyTracker.getInstance().activityStart(this); // Add this method.
	    
	    Preference myPref = (Preference) findPreference("pref_kaufen");
	    myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	                 public boolean onPreferenceClick(Preference preference) {
	                          	 
	                	if (!MainActivity.einstellungen.getBoolean("pref_spende", false)) {
	                		Random r = new Random();
	         			
	                		MainActivity.zufall = "";
	         			
	                		for (int i = 1; i <= 100; i++) {
	                			MainActivity.zufall = MainActivity.zufall + (char)(r.nextInt(93) + 33);
	                		}
	         			
	                		try {
	                			Bundle buyIntentBundle = MainActivity.mService.getBuyIntent(3, getPackageName(), "spende", "inapp", MainActivity.zufall);
	                			PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
	                			startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
	                		} catch (Exception e) {
	                			e.printStackTrace();
	                		}
	                	} else {
	                		dialog.setContentView(R.layout.dialog);
	            			dialog.setTitle(getResources().getString(R.string.dialogschonunterstuetzt_titel));
	            		
	            			TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
	            			text.setText(getResources().getString(R.string.dialogschonunterstuetzt_text));
	            		
	            			Button dialogButton = (Button) dialog.findViewById(R.id.dialogbutton_OK);
	            			dialogButton.setOnClickListener(new OnClickListener() {
	            				public void onClick(View v) {
	            					dialog.dismiss();
	            				}
	            			});
	            		
	            			dialog.show();
	                	}
	                	 
	                	 return true;
	                 }
	             });
	    
	}
	
	@Override
	public void onStop() {
	    super.onStop();
	    if (MainActivity.googleanalytics) EasyTracker.getInstance().activityStop(this); // Add this method.
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	   if (requestCode == 1001) {           
	      int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
	      String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
	      String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
	        
	      if (resultCode == RESULT_OK) {
	         try {
	            JSONObject jo = new JSONObject(purchaseData);
	            String vergleich = jo.getString("developerPayload");
	            String sku = jo.getString("productId");
//	            Log.d("SV", "dataSignature = " + vergleich);
//	  	      	Log.d("SV", "Vergleich = " + MainActivity.zufall);
//	            Log.d("SV", "You have bought the " + sku + ". Excellent choice, adventurer!");
	            
//	            if (vergleich.equals(MainActivity.zufall)) {
	            	SharedPreferences.Editor editor = MainActivity.einstellungen.edit();
	            	editor.putBoolean("pref_spende", true);									
	        		editor.commit();
//	            }
	          }
	          catch (JSONException e) {
	             Log.d("SV", "Failed to parse purchase data.");
	             e.printStackTrace();
	          }
	      }
	   }
	}
}
