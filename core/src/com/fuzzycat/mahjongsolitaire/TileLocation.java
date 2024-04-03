package com.fuzzycat.mahjongsolitaire;

public class TileLocation {
	public int column, row, layer;
	
	public TileLocation(int column, int row, int layer) {
		set(column, row, layer);
	}
	
	public TileLocation(TileLocation location) {
		set(location.column, location.row, location.layer);
	}
	
	public void set(int column, int row, int layer) {
		this.column = column;
		this.row = row;
		this.layer = layer;
	}
	
	@Override
	public int hashCode() {
		int hash = column;
		hash = 31 * hash + row;
		hash = 31 * hash + layer;
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || obj.getClass() != getClass())
			return false;
		TileLocation loc = (TileLocation) obj;
		return column == loc.column && row == loc.row && layer == loc.layer;
	}
}
