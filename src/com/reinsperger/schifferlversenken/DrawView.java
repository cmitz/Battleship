package com.reinsperger.schifferlversenken;

import java.util.Random;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.reinsperger.schifferlversenken.R;
import com.reinsperger.schifferlversenken.*;

public class DrawView extends View {
    Paint paint = new Paint();
    static int[][] spielfeld = new int[10][10];
    int[][] spielfeldCom = new int[10][10];
    int raster = 40;
    static int trefferZahl = 0;
    static int schussZahl = 0;
    static int trefferZahlCom = 0;
    static int schussZahlCom = 0;
    boolean[][] feldoffen = new boolean[10][10];
    boolean[][] feldoffenCom = new boolean[10][10];
    boolean[][] feldgetroffenCom = new boolean[10][10];
    boolean SchiffeNeu = true;
    boolean Loesung = false;
    boolean FelderZeichnen = false;
    boolean RasterNeu = true;
    static boolean einser = false;
    private static boolean SpielStatus = false;
    private static boolean SchiffeSetzen = true;
    Dialog dialog;
    int Rand_O = 50;
    int Rand_L = 15;
    int textsize = 35;
    boolean druecken = false;
    int fingerx = 10;
    int fingery = 10;
    
    
    // Farben
    final int RED = Color.parseColor("#FF4444");
    final int BLUE = Color.parseColor("#33B5E5");
    final int GREEN = Color.parseColor("#99CC00");
    final int YELLOW = Color.parseColor("#FFBB33");
    final int GREY = Color.parseColor("#E0E0E0");
    final int DARKGREY = Color.parseColor("#BABABA");
    final int PURPLE = Color.parseColor("#AA66CC");
    
    public DrawView(Context context) {
        super(context);
    }
    
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public void onDraw(Canvas canvas) {
		
		if (RasterNeu) {
			RastergroesseNeu();
			RasterNeu = false;
		}
    	    	    		
    	if (SchiffeNeu) {
    		SchiffeNeuSetzen(canvas);
    		SchiffeNeu = false;
//    		setSpielStatus(true);
    	}
    	
    	if (Loesung) {
    		LoesungZeichnen(canvas);
    		Loesung = false;
    	}
    	
    	if (FelderZeichnen) {
    		FeldZeichnen(canvas);
    		FelderZeichnen = false;
    	}
        
    }
  
    public void RastergroesseNeu() {
    	int[] coords = new int[2];
		getLocationOnScreen(coords);
		
		int width = getWidth();
		int height = getHeight();
		
		Rand_O = (int)(50 * height / 671);
		Rand_L = (int)(15 * width / 1280);
		textsize = (int)(35 * height / 671);
		
		if ((height - Rand_O - 8) / 10 < (width - Rand_L * 4) / 20) {
			raster = (int)((height - Rand_O - 8) / 10);
		} else {
			raster = (int)((width - Rand_L * 4) / 20);
		}
    }
    
    public void FeldZeichnen(Canvas canvas) {
 
    	paint.setTextSize(textsize);
    	paint.setColor(Color.BLACK);
    	paint.setTextAlign(Align.CENTER);
    	
    	canvas.drawText(getResources().getString(R.string.MeinSpielfeld), Rand_L + 5 * raster, Rand_O - Rand_O / 5, paint);
    	canvas.drawText(getResources().getString(R.string.GegnerSpielfeld), 3 * Rand_L + 15 * raster, Rand_O - Rand_O / 5, paint);
    	
    	paint.setColor(GREY);
    	
    	for (int i = 0; i <= 9; i++) {
    		for (int j = 0; j <= 9; j++) {
    			if (!getSchiffeSetzen()) canvas.drawRect(Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
    		}
    	}
    	
    	paint.setColor(DARKGREY);
    	if ((!getSchiffeSetzen()) && (fingerx != 10) && (fingery != 10)) canvas.drawRect(Rand_L + (fingerx + 1) * raster - (raster - 1), Rand_O + (fingery + 1) * raster - (raster - 1), Rand_L + (fingerx + 1) * raster - 1, Rand_O + (fingery + 1) * raster - 1, paint);
    	
    	for (int i = 0; i <= 9; i++) {
    		for (int j = 0; j <= 9; j++) {
    			    			
    			if (feldoffen[i][j]) {
    				
    				paint.setColor(Color.WHITE);
					canvas.drawRect(Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
					paint.setColor(BLUE);
    				
    				if ((spielfeld[i][j] == 0) || (spielfeld[i][j] >= 10)) {
    					canvas.drawRect(Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
    				} else {
    					
    					canvas.drawRect(Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
    					
    					if (((spielfeld[i][j] == 4) && (einser)) || ((spielfeld[i][j] == 5) && (!einser))) paint.setColor(RED);
    	    			if (((spielfeld[i][j] == 3) && (einser)) || ((spielfeld[i][j] == 4) && (!einser))) paint.setColor(GREEN);
    	    			if (((spielfeld[i][j] == 2) && (einser)) || ((spielfeld[i][j] == 3) && (!einser))) paint.setColor(YELLOW);
    	    			if (((spielfeld[i][j] == 1) && (einser)) || ((spielfeld[i][j] == 2) && (!einser))) paint.setColor(PURPLE);
    					
    					// Rumpf zeichnen
    					Path pfad = new Path();
    					pfad.moveTo(i * raster + Rand_L + 3f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster);
    					pfad.lineTo((i + 1f) * raster + Rand_L - 3f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster);
    					pfad.lineTo((i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 5f / 40f * raster);
    					pfad.lineTo(i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 5f / 40f * raster);
    					pfad.close();
    					canvas.drawPath(pfad, paint);
    					
    					// Segel zeichnen
    					paint.setColor(Color.WHITE);
    					Path segel = new Path();
    					segel.moveTo(i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster);
    					segel.lineTo(i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster);
    					segel.lineTo((i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster);
    					segel.close();
    					canvas.drawPath(segel, paint);
    					
    					paint.setColor(Color.BLACK);
    					canvas.drawLine(i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster, paint);
    					canvas.drawLine(i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster, (i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, paint);
    					canvas.drawLine((i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, paint);
    					
    					// Mast zeichnen
    					paint.setColor(Color.BLACK);
    					canvas.drawRect(Rand_L + i * raster + raster / 2f - 1f / 40f * raster, j * raster + Rand_O + 5f / 40f * raster, Rand_L + i * raster + raster / 2f + 1f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster, paint);
    				}
    			}
    			
    			
    			// Zweites Feld daneben zeichnen
    			paint.setColor(Color.WHITE);
    			canvas.drawRect(2 * Rand_L + 10 * raster + Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), 2 * Rand_L + 10 * raster + Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
    			paint.setColor(BLUE);
        				
        		if ((spielfeldCom[i][j] == 0) || (spielfeldCom[i][j] >= 10)) {
        			if ((spielfeldCom[i][j] >= 10) && (getSchiffeSetzen()) && (druecken)) paint.setColor(Color.RED);
        			canvas.drawRect(2 * Rand_L + 10 * raster + Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), 2 * Rand_L + 10 * raster + Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
        		} else {
        				
        			canvas.drawRect(2 * Rand_L + 10 * raster + Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), 2 * Rand_L + 10 * raster +  Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
        				
        			if (((spielfeldCom[i][j] == 4) && (einser)) || ((spielfeldCom[i][j] == 5) && (!einser))) paint.setColor(RED);
	    			if (((spielfeldCom[i][j] == 3) && (einser)) || ((spielfeldCom[i][j] == 4) && (!einser))) paint.setColor(GREEN);
	    			if (((spielfeldCom[i][j] == 2) && (einser)) || ((spielfeldCom[i][j] == 3) && (!einser))) paint.setColor(YELLOW);
	    			if (((spielfeldCom[i][j] == 1) && (einser)) || ((spielfeldCom[i][j] == 2) && (!einser))) paint.setColor(PURPLE);
        					
        			// Rumpf zeichnen
        			Path pfad = new Path();
        			pfad.moveTo(2 * Rand_L + 10 * raster + i * raster + Rand_L + 3f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster);
        			pfad.lineTo(2 * Rand_L + 10 * raster + (i + 1f) * raster + Rand_L - 3f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster);
        			pfad.lineTo(2 * Rand_L + 10 * raster + (i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 5f / 40f * raster);
        			pfad.lineTo(2 * Rand_L + 10 * raster + i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 5f / 40f * raster);
        			pfad.close();
        			canvas.drawPath(pfad, paint);
        					
        			// Segel zeichnen
        			paint.setColor(Color.WHITE);
        			Path segel = new Path();
        			segel.moveTo(2 * Rand_L + 10 * raster + i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster);
        			segel.lineTo(2 * Rand_L + 10 * raster + i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster);
        			segel.lineTo(2 * Rand_L + 10 * raster + (i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster);
        			segel.close();
        			canvas.drawPath(segel, paint);
        					
        			paint.setColor(Color.BLACK);
        			canvas.drawLine(2 * Rand_L + 10 * raster + i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, 2 * Rand_L + 10 * raster + i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster, paint);
        			canvas.drawLine(2 * Rand_L + 10 * raster + i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster, 2 * Rand_L + 10 * raster + (i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, paint);
        			canvas.drawLine(2 * Rand_L + 10 * raster + (i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, 2 * Rand_L + 10 * raster + i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, paint);
        				
        			// Mast zeichnen
        			paint.setColor(Color.BLACK);
        			canvas.drawRect(2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2f - 1f / 40f * raster, j * raster + Rand_O + 5f / 40f * raster, 2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2f + 1f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster, paint);
        		}
        		
        		if (feldgetroffenCom[i][j]) {
        			paint.setColor(Color.RED);
        			Path kreuz = new Path();
        			int abstand = 3;
        			kreuz.moveTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2, Rand_O + j * raster + raster / 2 - abstand);
        			kreuz.lineTo(2 * Rand_L - 1 + 10 * raster + Rand_L + i * raster + raster - abstand, Rand_O + j * raster + 1);
        			kreuz.lineTo(2 * Rand_L - 1 + 10 * raster + Rand_L + i * raster + raster, Rand_O + j * raster + abstand + 1);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2 + abstand, Rand_O + j * raster + raster / 2);
        			kreuz.lineTo(2 * Rand_L - 1 + 10 * raster + Rand_L + i * raster + raster, Rand_O + j * raster + raster - abstand - 1);
        			kreuz.lineTo(2 * Rand_L - 1 + 10 * raster + Rand_L + i * raster + raster - abstand, Rand_O + j * raster + raster - 1);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2, Rand_O + j * raster + raster / 2 + abstand);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + abstand, Rand_O + j * raster + raster - 1);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster, Rand_O + j * raster + raster - abstand - 1);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2 - abstand, Rand_O + j * raster + raster / 2);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster, Rand_O + j * raster + abstand + 1);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + abstand, Rand_O + j * raster + 1);
        			kreuz.close();
        				
        			canvas.drawPath(kreuz, paint);
        		}   			
     		}
    	}
    	
    	if (((trefferZahl == 20) && (einser)) || ((trefferZahl == 30) && (!einser))) {
    		// Wenn gewonnen, dann Meldung
    		
    		if (((trefferZahlCom == 20) && (einser)) || ((trefferZahlCom == 30) && (!einser))) {
    			
    			dialog.setContentView(R.layout.dialog);
    			dialog.setTitle(getResources().getString(R.string.Unentschieden));
    		
    			TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
    			text.setText(getResources().getString(R.string.UnentschiedenText));
    		
    			Loesung = true;
            	invalidate();
    			
    			Button dialogButton = (Button) dialog.findViewById(R.id.dialogbutton_OK);
    			dialogButton.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog.dismiss();
    				}
    			});
    		
    			dialog.show();
    			
    		} else {
    		
    			dialog.setContentView(R.layout.dialog);
    			dialog.setTitle(getResources().getString(R.string.Gewonnen));
    		
    			TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
    			text.setText(getResources().getString(R.string.GewonnenText));
    		
    			Loesung = true;
            	invalidate();
    			
    			Button dialogButton = (Button) dialog.findViewById(R.id.dialogbutton_OK);
    			dialogButton.setOnClickListener(new OnClickListener() {
    				public void onClick(View v) {
    					dialog.dismiss();
    				}
    			});
    		
    			dialog.show();
    		}
    		
    		setSpielStatus(false);
    	} else if (((trefferZahlCom == 20) && (einser)) || ((trefferZahlCom == 30) && (!einser))) {
    		dialog.setContentView(R.layout.dialog);
    		dialog.setTitle(getResources().getString(R.string.Verloren));
    		
    		TextView text = (TextView) dialog.findViewById(R.id.dialog_text);
			text.setText(getResources().getString(R.string.VerlorenText));
			
			Loesung = true;
        	invalidate();
    		
    		Button dialogButton = (Button) dialog.findViewById(R.id.dialogbutton_OK);
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
    		
    		dialog.show();
    		
    		setSpielStatus(false);
    	}
    	
    	if (getSchiffeSetzen()) {
    		paint.setColor(GREY);
    		canvas.drawRect(Rand_L + 2 * raster, Rand_O + 2 * raster, Rand_L + 8 * raster, Rand_O + 8 * raster, paint);
    		paint.setColor(Color.BLACK);
    		canvas.drawText(getResources().getString(R.string.SpielStarten), Rand_L + 5 * raster, Rand_O + 5 * raster, paint);
    	}
    }
    
    public void LoesungZeichnen(Canvas canvas) {
    	
    	paint.setColor(Color.WHITE);
    	canvas.drawRect(Rand_L, Rand_O, Rand_L + 10 * raster, Rand_O + 10 * raster, paint);
    	paint.setColor(Color.BLACK);
    	
    	paint.setTextSize(textsize);
    	paint.setColor(Color.BLACK);
    	paint.setTextAlign(Align.CENTER);
    	
    	canvas.drawText(getResources().getString(R.string.MeinSpielfeld), Rand_L + 5 * raster, Rand_O - Rand_O / 5, paint);
    	canvas.drawText(getResources().getString(R.string.GegnerSpielfeld), 3 * Rand_L + 15 * raster, Rand_O - Rand_O / 5, paint);
    	
    	for (int i = 0; i <= 9; i++) {
    		for (int j = 0; j <= 9; j++) {
    			
    			paint.setColor(BLUE);
    			
    			if ((spielfeld[i][j] == 0) || (spielfeld[i][j] >= 10)) {
					canvas.drawRect(Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
				} else {
					
					canvas.drawRect(Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
					
					if (((spielfeld[i][j] == 4) && (einser)) || ((spielfeld[i][j] == 5) && (!einser))) paint.setColor(RED);
	    			if (((spielfeld[i][j] == 3) && (einser)) || ((spielfeld[i][j] == 4) && (!einser))) paint.setColor(GREEN);
	    			if (((spielfeld[i][j] == 2) && (einser)) || ((spielfeld[i][j] == 3) && (!einser))) paint.setColor(YELLOW);
	    			if (((spielfeld[i][j] == 1) && (einser)) || ((spielfeld[i][j] == 2) && (!einser))) paint.setColor(PURPLE);
					
					// Rumpf zeichnen
					Path pfad = new Path();
					pfad.moveTo(i * raster + Rand_L + 3f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster);
					pfad.lineTo((i + 1f) * raster + Rand_L - 3f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster);
					pfad.lineTo((i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 5f / 40f * raster);
					pfad.lineTo(i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 5f / 40f * raster);
					pfad.close();
					canvas.drawPath(pfad, paint);
					
					// Segel zeichnen
					paint.setColor(Color.WHITE);
					Path segel = new Path();
					segel.moveTo(i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster);
					segel.lineTo(i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster);
					segel.lineTo((i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster);
					segel.close();
					canvas.drawPath(segel, paint);
					
					paint.setColor(Color.BLACK);
					canvas.drawLine(i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster, paint);
					canvas.drawLine(i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster, (i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, paint);
					canvas.drawLine((i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, paint);
					
					// Mast zeichnen
					paint.setColor(Color.BLACK);
					canvas.drawRect(Rand_L + i * raster + raster / 2f - 1f / 40f * raster, j * raster + Rand_O + 5f / 40f * raster, Rand_L + i * raster + raster / 2f + 1f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster, paint);
				}
    			
    			if (feldoffen[i][j]) {
        			paint.setColor(Color.RED);
        			Path kreuz = new Path();
        			int abstand = 3;
        			kreuz.moveTo(Rand_L + i * raster + raster / 2, Rand_O + j * raster + raster / 2 - abstand);
        			kreuz.lineTo(Rand_L + i * raster + raster - abstand, Rand_O + j * raster + 1);
        			kreuz.lineTo(Rand_L + i * raster + raster, Rand_O + j * raster + abstand + 1);
        			kreuz.lineTo(Rand_L + i * raster + raster / 2 + abstand, Rand_O + j * raster + raster / 2);
        			kreuz.lineTo(Rand_L + i * raster + raster, Rand_O + j * raster + raster - abstand - 1);
        			kreuz.lineTo(Rand_L + i * raster + raster - abstand, Rand_O + j * raster + raster - 1);
        			kreuz.lineTo(Rand_L + i * raster + raster / 2, Rand_O + j * raster + raster / 2 + abstand);
        			kreuz.lineTo(Rand_L + i * raster + abstand, Rand_O + j * raster + raster - 1);
        			kreuz.lineTo(Rand_L + i * raster, Rand_O + j * raster + raster - abstand - 1);
        			kreuz.lineTo(Rand_L + i * raster + raster / 2 - abstand, Rand_O + j * raster + raster / 2);
        			kreuz.lineTo(Rand_L + i * raster, Rand_O + j * raster + abstand + 1);
        			kreuz.lineTo(Rand_L + i * raster + abstand, Rand_O + j * raster + 1);
        			kreuz.close();
        				
        			canvas.drawPath(kreuz, paint);
        		}
    			
    			paint.setColor(BLUE);
    			
    			if ((spielfeldCom[i][j] == 0) || (spielfeldCom[i][j] >= 10)) {
					canvas.drawRect(2 * Rand_L + 10 * raster + Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), 2 * Rand_L + 10 * raster + Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
				} else {
					
					canvas.drawRect(2 * Rand_L + 10 * raster + Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), 2 * Rand_L + 10 * raster + Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
					
					if (((spielfeldCom[i][j] == 4) && (einser)) || ((spielfeldCom[i][j] == 5) && (!einser))) paint.setColor(RED);
	    			if (((spielfeldCom[i][j] == 3) && (einser)) || ((spielfeldCom[i][j] == 4) && (!einser))) paint.setColor(GREEN);
	    			if (((spielfeldCom[i][j] == 2) && (einser)) || ((spielfeldCom[i][j] == 3) && (!einser))) paint.setColor(YELLOW);
	    			if (((spielfeldCom[i][j] == 1) && (einser)) || ((spielfeldCom[i][j] == 2) && (!einser))) paint.setColor(PURPLE);
					
					// Rumpf zeichnen
					Path pfad = new Path();
					pfad.moveTo(2 * Rand_L + 10 * raster + i * raster + Rand_L + 3f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster);
					pfad.lineTo(2 * Rand_L + 10 * raster + (i + 1f) * raster + Rand_L - 3f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster);
					pfad.lineTo(2 * Rand_L + 10 * raster + (i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 5f / 40f * raster);
					pfad.lineTo(2 * Rand_L + 10 * raster + i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 5f / 40f * raster);
					pfad.close();
					canvas.drawPath(pfad, paint);
										
					// Segel zeichnen
					paint.setColor(Color.WHITE);
					Path segel = new Path();
					segel.moveTo(2 * Rand_L + 10 * raster + i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster);
					segel.lineTo(2 * Rand_L + 10 * raster + i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster);
					segel.lineTo(2 * Rand_L + 10 * raster + (i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster);
					segel.close();
					canvas.drawPath(segel, paint);
					
					paint.setColor(Color.BLACK);
					canvas.drawLine(2 * Rand_L + 10 * raster + i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, 2 * Rand_L + 10 * raster + i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster, paint);
					canvas.drawLine(2 * Rand_L + 10 * raster + i * raster + Rand_L + raster / 2f, j * raster + Rand_O + 7f / 40f * raster, 2 * Rand_L + 10 * raster + (i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, paint);
					canvas.drawLine(2 * Rand_L + 10 * raster + (i + 1f) * raster + Rand_L - 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, 2 * Rand_L + 10 * raster + i * raster + Rand_L + 7f / 40f * raster, (j + 1f) * raster + Rand_O - 18f / 40f * raster, paint);
					
					// Mast zeichnen
					paint.setColor(Color.BLACK);
					canvas.drawRect(2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2f - 1f / 40f * raster, j * raster + Rand_O + 5f / 40f * raster, 2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2f + 1f / 40f * raster, (j + 1f) * raster + Rand_O - 15f / 40f * raster, paint);
				}
    			
    			if (feldgetroffenCom[i][j]) {
        			paint.setColor(Color.RED);
        			Path kreuz = new Path();
        			int abstand = 3;
        			kreuz.moveTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2, Rand_O + j * raster + raster / 2 - abstand);
        			kreuz.lineTo(2 * Rand_L - 1 + 10 * raster + Rand_L + i * raster + raster - abstand, Rand_O + j * raster + 1);
        			kreuz.lineTo(2 * Rand_L - 1 + 10 * raster + Rand_L + i * raster + raster, Rand_O + j * raster + abstand + 1);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2 + abstand, Rand_O + j * raster + raster / 2);
        			kreuz.lineTo(2 * Rand_L - 1 + 10 * raster + Rand_L + i * raster + raster, Rand_O + j * raster + raster - abstand - 1);
        			kreuz.lineTo(2 * Rand_L - 1 + 10 * raster + Rand_L + i * raster + raster - abstand, Rand_O + j * raster + raster - 1);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2, Rand_O + j * raster + raster / 2 + abstand);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + abstand, Rand_O + j * raster + raster - 1);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster, Rand_O + j * raster + raster - abstand - 1);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + raster / 2 - abstand, Rand_O + j * raster + raster / 2);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster, Rand_O + j * raster + abstand + 1);
        			kreuz.lineTo(2 * Rand_L + 10 * raster + Rand_L + i * raster + abstand, Rand_O + j * raster + 1);
        			kreuz.close();
        				
        			canvas.drawPath(kreuz, paint);
        		}
    		}
    	}
    	
    	setSpielStatus(false);
    	
    }
    
    public void SchiffeNeuSetzen(Canvas canvas) {
    	
    	paint.setColor(GREY);
    	
    	for (int i = 0; i <= 9; i++) {
    		for (int j = 0; j <= 9; j++) {
    			if (!getSchiffeSetzen()) canvas.drawRect(Rand_L + (i + 1) * raster - (raster - 1), Rand_O + (j + 1) * raster - (raster - 1), Rand_L + (i + 1) * raster - 1, Rand_O + (j + 1) * raster - 1, paint);
    		}
    	}
    	
    	for (int i = 0; i <= 9; i++) {
    		for (int j = 0; j <= 9; j++) {
    			spielfeld[i][j] = 0;
    			spielfeldCom[i][j] = 0;
    			feldoffen[i][j] = false;
    			feldoffenCom[i][j] = false;
    			feldgetroffenCom[i][j] = false;
    		}
    	}
    	
    	Funktionen.SchiffeSetzen(spielfeld);
    	Funktionen.SchiffeSetzen(spielfeldCom);
    	
    	FeldZeichnen(canvas);
    	
    }

	public static boolean getSpielStatus() {
		return SpielStatus;
	}

	public static void setSpielStatus(boolean spiel) {
		SpielStatus = spiel;
		trefferZahl = 0;
		schussZahl = 0;
		trefferZahlCom = 0;
		schussZahlCom = 0;
		
	}

	public static boolean getSchiffeSetzen() {
		return SchiffeSetzen;
	}

	public static void setSchiffeSetzen(boolean schiffeSetzen) {
		SchiffeSetzen = schiffeSetzen;
	}
}
