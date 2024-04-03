package com.fuzzycat.mahjongsolitaire;

public class TableConfigurations {
	
	public static void createTurtle(Table table) {
		table.clear();
		
		// Layer 0
		table.addTilesAsArray3D(-7, -5, 0, 8, 6, 1);
		table.addTilesAsArray3D(-11, -7, 0, 12, 1, 1);
		table.addTilesAsArray3D(-11, 7, 0, 12, 1, 1);
		table.addTilesAsArray3D(-9, -3, 0, 1, 4, 1);
		table.addTilesAsArray3D(9, -3, 0, 1, 4, 1);
		table.addTilesAsArray3D(-11, -1, 0, 1, 2, 1);
		table.addTilesAsArray3D(11, -1, 0, 1, 2, 1);
		table.addTilesAsArray3D(-13, 0, 0, 1, 1, 1);
		table.addTilesAsArray3D(13, 0, 0, 2, 1, 1);
		
		// Layer 1
		table.addTilesAsArray3D(-5, -5, 1, 6, 6, 1);
		
		// Layer 2
		table.addTilesAsArray3D(-3, -3, 2, 4, 4, 1);
		
		// Layer 3
		table.addTilesAsArray3D(-1, -1, 3, 2, 2, 1);
		
		// Layer 4
		table.addTilesAsArray3D(0, 0, 4, 1, 1, 1);
	}
	
	public static void createFish(Table table) {
		table.clear();
		
		// Layer 0
		table.addTilesAsArray3D(-10, -7, 0, 1, 8, 1);
		table.addTilesAsArray3D(-8, -5, 0, 1, 6, 1);
		table.addTilesAsArray3D(-6, -3, 0, 1, 4, 1);
		table.addTilesAsArray3D(-4, -1, 0, 1, 2, 1);
		table.addTilesAsArray3D(-2, -3, 0, 1, 4, 1);
		table.addTilesAsArray3D(0, -5, 0, 1, 6, 1);
		table.addTilesAsArray3D(2, -7, 0, 1, 8, 1);
		table.addTilesAsArray3D(4, -7, 0, 1, 8, 1);
		table.addTilesAsArray3D(6, -7, 0, 1, 8, 1);
		table.addTilesAsArray3D(8, -5, 0, 1, 6, 1);
		table.addTilesAsArray3D(10, -3, 0, 1, 4, 1);
		table.addTilesAsArray3D(12, -1, 0, 1, 2, 1);
		
		// Layer 1
		table.addTilesAsArray3D(-10, -6, 1, 1, 7, 1);
		table.addTilesAsArray3D(-8, -4, 1, 1, 5, 1);
		table.addTilesAsArray3D(-6, -2, 1, 1, 3, 1);
		table.addTilesAsArray3D(-4, 0, 1, 1, 1, 1);
		table.addTilesAsArray3D(-2, -2, 1, 1, 3, 1);
		table.addTilesAsArray3D(0, -4, 1, 1, 5, 1);
		table.addTilesAsArray3D(2, -6, 1, 1, 7, 1);
		table.addTilesAsArray3D(4, -6, 1, 1, 7, 1);
		table.addTilesAsArray3D(6, -6, 1, 1, 7, 1);
		table.addTilesAsArray3D(8, -4, 1, 1, 5, 1);
		table.addTilesAsArray3D(10, -2, 1, 1, 3, 1);
		table.addTilesAsArray3D(12, 0, 1, 1, 1, 1);
		
		// Layer 2
		table.addTilesAsArray3D(-10, -5, 2, 1, 6, 1);
		table.addTilesAsArray3D(-8, -3, 2, 1, 4, 1);
		table.addTilesAsArray3D(-6, -1, 2, 1, 2, 1);
		table.addTilesAsArray3D(-4, 0, 2, 1, 1, 1);
		table.addTilesAsArray3D(-2, -1, 2, 1, 2, 1);
		table.addTilesAsArray3D(0, -3, 2, 1, 4, 1);
		table.addTilesAsArray3D(2, -5, 2, 1, 6, 1);
		table.addTilesAsArray3D(4, -5, 2, 1, 6, 1);
		table.addTilesAsArray3D(6, -5, 2, 1, 6, 1);
		table.addTilesAsArray3D(8, -3, 2, 1, 4, 1);
		table.addTilesAsArray3D(10, -1, 2, 1, 2, 1);
		table.addTilesAsArray3D(12, 0, 2, 1, 1, 1);
		
		// Layer 3
		table.addTilesAsArray3D(-8, -2, 3, 1, 3, 1);
		table.addTilesAsArray3D(-6, 0, 3, 1, 1, 1);
		table.addTilesAsArray3D(-2, 0, 3, 1, 1, 1);
		table.addTilesAsArray3D(0, -2, 3, 5, 3, 1);
		table.addTilesAsArray3D(2, -4, 3, 3, 1, 1);
		table.addTilesAsArray3D(2, 4, 3, 3, 1, 1);
		table.addTilesAsArray3D(10, -1, 3, 1, 2, 1);
		
		// Layer 4
		table.addTilesAsArray3D(-8, -1, 4, 1, 2, 1);
		table.addTilesAsArray3D(-6, 0, 4, 1, 1, 1);
		table.addTilesAsArray3D(6, -2, 4, 1, 1, 1);
	}
}
