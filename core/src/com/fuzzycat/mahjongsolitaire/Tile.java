package com.fuzzycat.mahjongsolitaire;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Tile {
	public static float WIDTH = 2.0f;
	public static float HEIGHT = WIDTH * 4.0f / 3.0f;
	public static float DEPTH = 1.0f;
	public static float WIDTH2 = 0.5f * WIDTH;
	public static float HEIGHT2 = 0.5f * HEIGHT;
	public static float DEPTH2 = 0.5f * DEPTH;
	
	private TileLocation location;
	private int skin;
	private ModelInstance skinModelInstance;
	
	public Tile(TileLocation location, int skin) {
		this.location = new TileLocation(location);
		this.skin = skin;
		skinModelInstance = null;
	}
	
	public Tile(Tile tile) {
		location = new TileLocation(tile.location);
		skin = tile.skin;
		if (tile.skinModelInstance != null)
			skinModelInstance = tile.skinModelInstance.copy();
	}
	
	public TileLocation getLocation() {
		return location;
	}
	
	public int getSkin() {
		return skin;
	}
	
	public void setSkin(int skin) {
		this.skin = skin;
	}
	
	public void setSkinModelInstance(ModelInstance skinModelInstance) {
		if (skinModelInstance != null)
			this.skinModelInstance = skinModelInstance.copy();
	}
	
	public ModelInstance getSkinModelInstance() {
		return skinModelInstance;
	}
}
