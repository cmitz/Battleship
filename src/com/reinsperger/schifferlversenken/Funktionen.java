package com.reinsperger.schifferlversenken;

import java.util.Random;

public class Funktionen {

public static void SchiffeSetzen(int[][] feld) {
		
		int shipsToGo = 0;
    	boolean shipOK = true;
    	Random rand = new Random();
		
    	// Kreuzfahrtschiff (5-Felder)
    	DrawView.einser = MainActivity.einstellungen.getBoolean("pref_schiffslaenge", false);
    	
    	if (DrawView.einser) {
    		shipsToGo = 0;
    	} else {
    		shipsToGo = 1;
    	}
    	
    	while (shipsToGo > 0) {
    		shipOK = true;
    		int x = rand.nextInt(10);
    		int y = rand.nextInt(10);
    		int r = rand.nextInt(4);	// 0: oben, 1: rechts, 2: unten, 3: links
    		if ((r == 0) && (y < 4)) shipOK = false;
    		if ((r == 1) && (x > 5)) shipOK = false;
    		if ((r == 2) && (y > 5)) shipOK = false;
    		if ((r == 3) && (x < 4)) shipOK = false;
    		
    		if (shipOK) {
    			feld[x][y] = 5;
    			int dx = 0;
    			int dy = 0;
    		
    			if (r == 0) dy = -1;
    			if (r == 1) dx = 1;
    			if (r == 2) dy = 1;
    			if (r == 3) dx = -1;
    			
    			for (int i = 1; i <= 4; i++) {
    				feld[x + i * dx][y + i * dy] = 5;
    			}
    			
    			for (int i = 0; i <= 4; i++) {
    				if ((x - 1 + dx * i >= 0) && (y - 1 + dy * i >= 0)) feld[x - 1 + dx * i][y - 1 + dy * i] = feld[x - 1 + dx * i][y - 1 + dy * i] + 10;
    				if ((x + 1 + dx * i <= 9) && (y - 1 + dy * i >= 0)) feld[x + 1 + dx * i][y - 1 + dy * i] = feld[x + 1 + dx * i][y - 1 + dy * i] + 10;
    				if ((x - 1 + dx * i >= 0) && (y + 1 + dy * i <= 9)) feld[x - 1 + dx * i][y + 1 + dy * i] = feld[x - 1 + dx * i][y + 1 + dy * i] + 10;
    				if ((x + 1 + dx * i <= 9) && (y + 1 + dy * i <= 9)) feld[x + 1 + dx * i][y + 1 + dy * i] = feld[x + 1 + dx * i][y + 1 + dy * i] + 10;
    			}
    			
    			if ((x + dx * (-1) >= 0) && (y + dy * (-1) >= 0) && (x + dx * (-1) <= 9) && (y + dy * (-1) <= 9)) feld[x + dx * (-1)][y + dy * (-1)] = feld[x + dx * (-1)][y + dy * (-1)] + 10;
    			if ((x + 5 * dx >= 0) && (y + 5 * dy >= 0) && (x + 5 * dx <= 9) && (y + 5 * dy <= 9)) feld[x + 5 * dx][y + 5 * dy] = feld[x + 5 * dx][y + 5 * dy] + 10;
    			
    			shipsToGo--;
    		}
    	}
    	
		// Flugzeugträger (4-Felder):
    	if (DrawView.einser) {
    		shipsToGo = 1;
    	} else {
    		shipsToGo = 2;
    	}
    	
    	while (shipsToGo > 0) {
    		shipOK = true;
    		int x = rand.nextInt(10);
    		int y = rand.nextInt(10);
    		int r = rand.nextInt(4);	// 0: oben, 1: rechts, 2: unten, 3: links
    		if ((r == 0) && (y < 3)) shipOK = false;
    		if ((r == 1) && (x > 6)) shipOK = false;
    		if ((r == 2) && (y > 6)) shipOK = false;
    		if ((r == 3) && (x < 3)) shipOK = false;
    		
    		int dx = 0;
    		int dy = 0;
    		if (r == 0) dy = -1;
			if (r == 1) dx = 1;
			if (r == 2) dy = 1;
			if (r == 3) dx = -1;
			for (int i = 0; i <= 3; i++) {
				for (int m = 0; m <= 2; m++) {
					for (int n = 0; n <= 2; n++) {
						if ((x + dx * i + m - 1 >= 0) && (x + dx * i + m - 1 <= 9) && (y + dy * i + n - 1 >= 0) && (y + dy * i + n - 1 <= 9)) {
							if ((feld[x + dx * i + m - 1][y + dy * i + n - 1] != 0) && (feld[x + dx * i + m - 1][y + dy * i + n - 1] < 10)) shipOK = false;
						}
					}
				}
			}
    		
    		if (shipOK) {
    			feld[x][y] = 4;
    			dx = 0;
    			dy = 0;
    		
    			if (r == 0) dy = -1;
    			if (r == 1) dx = 1;
    			if (r == 2) dy = 1;
    			if (r == 3) dx = -1;
    			
    			for (int i = 1; i <= 3; i++) {
    				feld[x + i * dx][y + i * dy] = 4;
    			}
    			
    			for (int i = 0; i <= 3; i++) {
    				if ((x - 1 + dx * i >= 0) && (y - 1 + dy * i >= 0)) feld[x - 1 + dx * i][y - 1 + dy * i] = feld[x - 1 + dx * i][y - 1 + dy * i] + 10;
    				if ((x + 1 + dx * i <= 9) && (y - 1 + dy * i >= 0)) feld[x + 1 + dx * i][y - 1 + dy * i] = feld[x + 1 + dx * i][y - 1 + dy * i] + 10;
    				if ((x - 1 + dx * i >= 0) && (y + 1 + dy * i <= 9)) feld[x - 1 + dx * i][y + 1 + dy * i] = feld[x - 1 + dx * i][y + 1 + dy * i] + 10;
    				if ((x + 1 + dx * i <= 9) && (y + 1 + dy * i <= 9)) feld[x + 1 + dx * i][y + 1 + dy * i] = feld[x + 1 + dx * i][y + 1 + dy * i] + 10;
    			}
    			
    			if ((x + dx * (-1) >= 0) && (y + dy * (-1) >= 0) && (x + dx * (-1) <= 9) && (y + dy * (-1) <= 9)) feld[x + dx * (-1)][y + dy * (-1)] = feld[x + dx * (-1)][y + dy * (-1)] + 10;
    			if ((x + 4 * dx >= 0) && (y + 4 * dy >= 0) && (x + 4 * dx <= 9) && (y + 4 * dy <= 9)) feld[x + 4 * dx][y + 4 * dy] = feld[x + 4 * dx][y + 4 * dy] + 10;
    			
    			shipsToGo--;
    		}
    	}
    	
    	// Kreuzer (3-Felder):
    	if (DrawView.einser) {
    		shipsToGo = 2;
    	} else {
    		shipsToGo = 3;
    	}
    	
    	while (shipsToGo > 0) {
    		shipOK = true;
    		int x = rand.nextInt(10);
    		int y = rand.nextInt(10);
    		int r = rand.nextInt(4);	// 0: oben, 1: rechts, 2: unten, 3: links
    		if ((r == 0) && (y < 2)) shipOK = false;
    		if ((r == 1) && (x > 7)) shipOK = false;
    		if ((r == 2) && (y > 7)) shipOK = false;
    		if ((r == 3) && (x < 2)) shipOK = false;
    		
    		// Anderes Schiff suchen:
    		int dx = 0;
    		int dy = 0;
    		if (r == 0) dy = -1;
			if (r == 1) dx = 1;
			if (r == 2) dy = 1;
			if (r == 3) dx = -1;
			for (int i = 0; i <= 2; i++) {
				for (int m = 0; m <= 2; m++) {
					for (int n = 0; n <= 2; n++) {
						if ((x + dx * i + m - 1 >= 0) && (x + dx * i + m - 1 <= 9) && (y + dy * i + n - 1 >= 0) && (y + dy * i + n - 1 <= 9)) {
							if ((feld[x + dx * i + m - 1][y + dy * i + n - 1] != 0) && (feld[x + dx * i + m - 1][y + dy * i + n - 1] < 10)) shipOK = false;
						}
					}
				}
			}
    		
    		if (shipOK) {
    			feld[x][y] = 3;
    			dx = 0;
    			dy = 0;
    		
    			if (r == 0) dy = -1;
    			if (r == 1) dx = 1;
    			if (r == 2) dy = 1;
    			if (r == 3) dx = -1;
    			
    			for (int i = 1; i <= 2; i++) {
    				feld[x + i * dx][y + i * dy] = 3;
    			}
    			
    			for (int i = 0; i <= 2; i++) {
    				if ((x - 1 + dx * i >= 0) && (y - 1 + dy * i >= 0)) feld[x - 1 + dx * i][y - 1 + dy * i] = feld[x - 1 + dx * i][y - 1 + dy * i] + 10;
    				if ((x + 1 + dx * i <= 9) && (y - 1 + dy * i >= 0)) feld[x + 1 + dx * i][y - 1 + dy * i] = feld[x + 1 + dx * i][y - 1 + dy * i] + 10;
    				if ((x - 1 + dx * i >= 0) && (y + 1 + dy * i <= 9)) feld[x - 1 + dx * i][y + 1 + dy * i] = feld[x - 1 + dx * i][y + 1 + dy * i] + 10;
    				if ((x + 1 + dx * i <= 9) && (y + 1 + dy * i <= 9)) feld[x + 1 + dx * i][y + 1 + dy * i] = feld[x + 1 + dx * i][y + 1 + dy * i] + 10;
    			}
    			
    			if ((x + dx * (-1) >= 0) && (y + dy * (-1) >= 0) && (x + dx * (-1) <= 9) && (y + dy * (-1) <= 9)) feld[x + dx * (-1)][y + dy * (-1)] = feld[x + dx * (-1)][y + dy * (-1)] + 10;
    			if ((x + 3 * dx >= 0) && (y + 3 * dy >= 0) && (x + 3 * dx <= 9) && (y + 3 * dy <= 9)) feld[x + 3 * dx][y + 3 * dy] = feld[x + 3 * dx][y + 3 * dy] + 10;
    		
    			shipsToGo--;
    		}
    	}
    	
    	// Zerstörer (2-Felder):
    	if (DrawView.einser) {
    		shipsToGo = 3;
    	} else {
    		shipsToGo = 4;
    	}
    	
    	while (shipsToGo > 0) {
    		shipOK = true;
    		int x = rand.nextInt(10);
    		int y = rand.nextInt(10);
    		int r = rand.nextInt(4);	// 0: oben, 1: rechts, 2: unten, 3: links
    		if ((r == 0) && (y < 1)) shipOK = false;
    		if ((r == 1) && (x > 8)) shipOK = false;
    		if ((r == 2) && (y > 8)) shipOK = false;
    		if ((r == 3) && (x < 1)) shipOK = false;
    		
    		// Anderes Schiff suchen:
    		int dx = 0;
    		int dy = 0;
    		if (r == 0) dy = -1;
			if (r == 1) dx = 1;
			if (r == 2) dy = 1;
			if (r == 3) dx = -1;
			for (int i = 0; i <= 1; i++) {
				for (int m = 0; m <= 2; m++) {
					for (int n = 0; n <= 2; n++) {
						if ((x + dx * i + m - 1 >= 0) && (x + dx * i + m - 1 <= 9) && (y + dy * i + n - 1 >= 0) && (y + dy * i + n - 1 <= 9)) {
							if ((feld[x + dx * i + m - 1][y + dy * i + n - 1] != 0) && (feld[x + dx * i + m - 1][y + dy * i + n - 1] < 10)) shipOK = false;
						}
					}
				}
			}
    		
    		if (shipOK) {
    			feld[x][y] = 2;
    			dx = 0;
    			dy = 0;
    		
    			if (r == 0) dy = -1;
    			if (r == 1) dx = 1;
    			if (r == 2) dy = 1;
    			if (r == 3) dx = -1;
    			
    			for (int i = 1; i <= 1; i++) {
    				feld[x + i * dx][y + i * dy] = 2;
    			}
    			
    			for (int i = 0; i <= 1; i++) {
    				if ((x - 1 + dx * i >= 0) && (y - 1 + dy * i >= 0)) feld[x - 1 + dx * i][y - 1 + dy * i] = feld[x - 1 + dx * i][y - 1 + dy * i] + 10;
    				if ((x + 1 + dx * i <= 9) && (y - 1 + dy * i >= 0)) feld[x + 1 + dx * i][y - 1 + dy * i] = feld[x + 1 + dx * i][y - 1 + dy * i] + 10;
    				if ((x - 1 + dx * i >= 0) && (y + 1 + dy * i <= 9)) feld[x - 1 + dx * i][y + 1 + dy * i] = feld[x - 1 + dx * i][y + 1 + dy * i] + 10;
    				if ((x + 1 + dx * i <= 9) && (y + 1 + dy * i <= 9)) feld[x + 1 + dx * i][y + 1 + dy * i] = feld[x + 1 + dx * i][y + 1 + dy * i] + 10;
    			}
    			
    			if ((x + dx * (-1) >= 0) && (y + dy * (-1) >= 0) && (x + dx * (-1) <= 9) && (y + dy * (-1) <= 9)) feld[x + dx * (-1)][y + dy * (-1)] = feld[x + dx * (-1)][y + dy * (-1)] + 10;
    			if ((x + 2 * dx >= 0) && (y + 2 * dy >= 0) && (x + 2 * dx <= 9) && (y + 2 * dy <= 9)) feld[x + 2 * dx][y + 2 * dy] = feld[x + 2 * dx][y + 2 * dy] + 10;
    			
    			shipsToGo--;
    		}
    	}
    	
    	// U-Boote (1-Feld):
    	if (DrawView.einser) {
    		shipsToGo = 4;
    	} else {
    		shipsToGo = 0;
    	}
    	
    	while (shipsToGo > 0) {
    		shipOK = true;
    		int x = rand.nextInt(10);
    		int y = rand.nextInt(10);
    		    		
    		// Anderes Schiff suchen:
			for (int m = 0; m <= 2; m++) {
				for (int n = 0; n <= 2; n++) {
					if ((x + m - 1 >= 0) && (x + m - 1 <= 9) && (y + n - 1 >= 0) && (y + n - 1 <= 9)) {
						if ((feld[x + m - 1][y + n - 1] != 0) && (feld[x + m - 1][y + n - 1] < 10)) shipOK = false;
					}
				}
			}
			
    		
    		if (shipOK) {
    			feld[x][y] = 1;
    			if (x - 1 >= 0) feld[x - 1][y] = feld[x - 1][y] + 10;
    			if (x + 1 <= 9) feld[x + 1][y] = feld[x + 1][y] + 10;
    			if (y - 1 >= 0) feld[x][y - 1] = feld[x][y - 1] + 10;
    			if (y + 1 <= 9) feld[x][y + 1] = feld[x][y + 1] + 10;
    			if ((x - 1 >= 0) && (y - 1 >= 0)) feld[x - 1][y - 1] = feld[x - 1][y - 1] + 10;
    			if ((x + 1 <= 9) && (y - 1 >= 0)) feld[x + 1][y - 1] = feld[x + 1][y - 1] + 10;
    			if ((x - 1 >= 0) && (y + 1 <= 9)) feld[x - 1][y + 1] = feld[x - 1][y + 1] + 10;
    			if ((x + 1 <= 9) && (y + 1 <= 9)) feld[x + 1][y + 1] = feld[x + 1][y + 1] + 10;
    			
    			shipsToGo--;
    		}
    	}
	}
	
}
