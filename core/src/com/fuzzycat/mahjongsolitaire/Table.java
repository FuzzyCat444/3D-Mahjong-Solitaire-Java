package com.fuzzycat.mahjongsolitaire;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;

public class Table {
	private OrderedMap<TileLocation, Tile> tiles;
	private Array<Integer> tilesPerLayer;
	
	private ModelInstance tileOverlayModelInstance;
	
	private Tile hoveredTile;
	private ModelInstance hoveredTileOverlayModelInstance;
	
	private Tile selectedTile;
	private ModelInstance selectedTileOverlayModelInstance;
	
	private static class MatchedTile {
		public static final float DURATION = 0.75f;
		
		public Tile tile;
		public float timer;
		
		public MatchedTile(Tile tile) {
			this.tile = tile;
			this.timer = DURATION;
		}
	}
	private Array<MatchedTile> matchedTiles;
	
	private static class InvalidTile {
		public static final float DURATION = 0.5f;
		
		public Tile tile;
		public ModelInstance overlayModelInstance;
		public float timer;
		
		public InvalidTile(Tile tile, ModelInstance tileOverlayModelInstance) {
			this.tile = tile;
			overlayModelInstance = tileOverlayModelInstance.copy();
			timer = DURATION;
		}
	}
	private Array<InvalidTile> invalidTiles;
	
	public Table(Model tileOverlayModel) {
		tiles = new OrderedMap<TileLocation, Tile>();
		tilesPerLayer = new Array<Integer>();
		
		tileOverlayModelInstance = new ModelInstance(tileOverlayModel);
		
		hoveredTile = null;
		hoveredTileOverlayModelInstance = tileOverlayModelInstance.copy();
		hoveredTileOverlayModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(0.5f, 0.5f, 0.5f, 0.5f));
		
		selectedTile = null;
		selectedTileOverlayModelInstance = tileOverlayModelInstance.copy();
		selectedTileOverlayModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(1.0f, 1.0f, 0.0f, 0.5f));
		
		matchedTiles = new Array<MatchedTile>();
		
		invalidTiles = new Array<InvalidTile>();
	}
	
	public void render(ModelBatch modelBatch, Environment environment) {
		float delta = Gdx.graphics.getDeltaTime();
		
		for (Tile tile : tiles.values()) {
			TileLocation location = tile.getLocation();
			ModelInstance modelInstance = tile.getSkinModelInstance();
			if (modelInstance != null) {
				modelInstance.transform.setToTranslation(
					Tile.WIDTH2 * location.column, 
					Tile.DEPTH2 + Tile.DEPTH * location.layer, 
					Tile.HEIGHT2 * location.row
				);
				modelBatch.render(modelInstance, environment);
			}
		}
		
		if (hoveredTile != null && !Gdx.input.isCursorCatched()) {
			TileLocation hoveredLocation = hoveredTile.getLocation();
			hoveredTileOverlayModelInstance.transform.setToTranslation(
				Tile.WIDTH2 * hoveredLocation.column, 
				0.005f + Tile.DEPTH * (hoveredLocation.layer + 1), 
				Tile.HEIGHT2 * hoveredLocation.row
			);
			modelBatch.render(hoveredTileOverlayModelInstance);
		}
		
		if (selectedTile != null) {
			TileLocation selectedLocation = selectedTile.getLocation();
			selectedTileOverlayModelInstance.transform.setToTranslation(
				Tile.WIDTH2 * selectedLocation.column, 
				0.01f + Tile.DEPTH * (selectedLocation.layer + 1), 
				Tile.HEIGHT2 * selectedLocation.row
			);
			modelBatch.render(selectedTileOverlayModelInstance);
		}
		
		Iterator<MatchedTile> matchedTileIt = matchedTiles.iterator();
		MatchedTile matchedTile;
		while (matchedTileIt.hasNext()) {
			matchedTile = matchedTileIt.next();
			
			matchedTile.timer -= delta;
			if (matchedTile.timer < 0.0f) {
				matchedTileIt.remove();
				continue;
			}
			
			TileLocation matchedLocation = matchedTile.tile.getLocation();
			ModelInstance tileSkinModelInstance = matchedTile.tile.getSkinModelInstance();
			float t = 1.0f - matchedTile.timer / MatchedTile.DURATION;
			tileSkinModelInstance.transform.setToTranslation(
				Tile.WIDTH2 * matchedLocation.column, 
				Tile.DEPTH2 + Tile.DEPTH * matchedLocation.layer + 20.0f * t * (1.0f / (1.0f - t) - 1.0f), 
				Tile.HEIGHT2 * matchedLocation.row
			);
			
			modelBatch.render(tileSkinModelInstance, environment);
		}
		
		Iterator<InvalidTile> invalidTileIt = invalidTiles.iterator();
		InvalidTile invalidTile;
		while (invalidTileIt.hasNext()) {
			invalidTile = invalidTileIt.next();
			
			invalidTile.timer -= delta;
			if (invalidTile.timer < 0.0f) {
				invalidTileIt.remove();
				continue;
			}
			
			TileLocation invalidLocation = invalidTile.tile.getLocation();
			ModelInstance invalidTileOverlayModelInstance = invalidTile.overlayModelInstance;
			invalidTileOverlayModelInstance.materials.get(0).set(
				ColorAttribute.createDiffuse(1.0f, 0.0f, 0.0f, 
					0.5f * invalidTile.timer / InvalidTile.DURATION)
			);
			invalidTileOverlayModelInstance.transform.setToTranslation(
				Tile.WIDTH2 * invalidLocation.column, 
				0.01f + Tile.DEPTH * (invalidLocation.layer + 1), 
				Tile.HEIGHT2 * invalidLocation.row
			);
			
			modelBatch.render(invalidTileOverlayModelInstance);
		}
	}
	
	// Test mouse cursor hovering/clicking on tile by sending a ray from the camera
	public void hover(Ray ray, boolean clicked) {
		Plane plane = new Plane();
		Vector3 planePoint = new Vector3();
		Vector3 planeNormal = new Vector3(0.0f, 1.0f, 0.0f);
		Vector3 planeIntersection = new Vector3();
		TileLocation hoveredLocation = new TileLocation(0, 0, 0);
		hoveredTile = null;
		for (int i = tilesPerLayer.size - 1; i >= 0; i--) {
			if (tilesPerLayer.get(i) == 0)
				continue;
			
			planePoint.set(0.0f, Tile.DEPTH * (i + 1), 0.0f);
			plane.set(planePoint, planeNormal);
			boolean didIntersect = Intersector.intersectRayPlane(ray, plane, planeIntersection);
			if (!didIntersect || planeIntersection.y > ray.origin.y)
				continue;
			
			hoveredLocation.column = MathUtils.floor(planeIntersection.x / Tile.WIDTH2);
			hoveredLocation.row = MathUtils.floor(planeIntersection.z / Tile.HEIGHT2);
			hoveredLocation.layer = i;
			
			hoveredTile = getTileAt(hoveredLocation);
			if (hoveredTile != null) {
				break;
			}
		}
		
		if (clicked) {
			doSelectTile(hoveredTile);
		}
	}

	private void doSelectTile(Tile tile) {
		if (tile == null) {
			selectedTile = null;
			return;
		}
		
		Tile[] neighbors = new Tile[26];
		getTileNeighbors(tiles, neighbors, tile.getLocation());
		
		if (isTileRemovable(neighbors)) {
			if (selectedTile == null) {
				selectedTile = tile;
			} else {
				if (!tile.getLocation().equals(selectedTile.getLocation())) {
					if (tile.getSkin() == selectedTile.getSkin()) {
						removeTile(tile);
						removeTile(selectedTile);
						matchedTiles.add(new MatchedTile(tile));
						matchedTiles.add(new MatchedTile(selectedTile));
						selectedTile = null;
					} else {
						doInvalidTile(tile);
					}
				} else {
					selectedTile = null;
				}
			}
		} else {
			doInvalidTile(tile);
		}
	}
	
	private void doInvalidTile(Tile tile) {
		InvalidTile invalidTile = new InvalidTile(tile, tileOverlayModelInstance);
		for (int i = 0; i < invalidTiles.size; i++) {
			if (invalidTiles.get(i).tile.getLocation().equals(tile.getLocation())) {
				invalidTiles.set(i, invalidTile);
			}
		}
		invalidTiles.add(invalidTile);
	}

	private void addTile(Tile tile) {
		TileLocation loc = tile.getLocation();
		int oldTilesPerLayerSize = tilesPerLayer.size;
		if (loc.layer >= oldTilesPerLayerSize) {
			tilesPerLayer.setSize(loc.layer + 1);
			for (int i = oldTilesPerLayerSize; i < tilesPerLayer.size; i++)
				tilesPerLayer.set(i, 0);
		}
		tilesPerLayer.set(loc.layer, tilesPerLayer.get(loc.layer) + 1);
		tiles.put(loc, tile);
	}
	
	private void removeTile(Tile tile) {
		TileLocation loc = tile.getLocation();
		tiles.remove(loc);
		tilesPerLayer.set(loc.layer, tilesPerLayer.get(loc.layer) - 1);
		while (tilesPerLayer.size > 0 && tilesPerLayer.peek() == 0) {
			tilesPerLayer.pop();
		}
	}
	
	private Tile getTileAt(TileLocation loc) {
		TileLocation tileLoc = new TileLocation(loc);
		Tile tile = tiles.get(tileLoc);
		if (tile == null) {
			tileLoc.column++;
			tile = tiles.get(tileLoc);
			if (tile == null) {
				tileLoc.row++;
				tile = tiles.get(tileLoc);
				if (tile == null) {
					tileLoc.column--;
					tile = tiles.get(tileLoc);
				}
			}
		}
		return tile;
	}
	
	// No neighbors to the left
	private boolean isTileFreeLeft(Tile[] n) {
		return n[0] == null && 
			   n[1] == null && 
			   n[2] == null;
	}
	
	// No neighbors to the right
	private boolean isTileFreeRight(Tile[] n) {
		return n[3] == null && 
			   n[4] == null && 
			   n[5] == null;
	}
	
	// No neighbors stacked on top
	private boolean isTileFreeStacked(Tile[] n) {
		return n[8]  == null &&
			   n[9]  == null &&
			   n[10] == null &&
			   n[11] == null &&
			   n[12] == null &&
			   n[13] == null &&
			   n[14] == null &&
			   n[15] == null &&
			   n[16] == null;
	}
	
	// The rules of Mahjong Solitaire in a nutshell
	private boolean isTileRemovable(Tile[] n) {
		return isTileFreeStacked(n) && (isTileFreeLeft(n) || isTileFreeRight(n));
	}
	
	// 26 potential neighbors total (not all at once)
	private static void getTileNeighbors(OrderedMap<TileLocation, Tile> map, Tile[] n, TileLocation loc) {
		TileLocation tileLoc = new TileLocation(loc);
		
		// Left
		tileLoc.column -= 2;
		n[0] = map.get(tileLoc);
		
		// Top left
		tileLoc.row--;
		n[1] = map.get(tileLoc);
		
		// Bottom left
		tileLoc.row += 2;
		n[2] = map.get(tileLoc);
		
		// Right
		tileLoc.column += 4;
		tileLoc.row--;
		n[3] = map.get(tileLoc);
		
		// Top right
		tileLoc.row--;
		n[4] = map.get(tileLoc);
		
		// Bottom right
		tileLoc.row += 2;
		n[5] = map.get(tileLoc);
		
		// Top
		tileLoc.column -= 2;
		tileLoc.row -= 3;
		n[6] = map.get(tileLoc);
		
		// Bottom
		tileLoc.row += 4;
		n[7] = map.get(tileLoc);
		
		// Stacked
		tileLoc.row -= 2;
		tileLoc.layer++;
		n[8] = map.get(tileLoc);
		
		// Stacked left
		tileLoc.column--;
		n[9] = map.get(tileLoc);
		
		// Stacked right
		tileLoc.column += 2;
		n[10] = map.get(tileLoc);
		
		// Stacked top
		tileLoc.column--;
		tileLoc.row--;
		n[11] = map.get(tileLoc);
		
		// Stacked bottom
		tileLoc.row += 2;
		n[12] = map.get(tileLoc);
		
		// Stacked top left
		tileLoc.column--;
		tileLoc.row -= 2;
		n[13] = map.get(tileLoc);
		
		// Stacked top right
		tileLoc.column += 2;
		n[14] = map.get(tileLoc);
		
		// Stacked bottom right
		tileLoc.row += 2;
		n[15] = map.get(tileLoc);
		
		// Stacked bottom left
		tileLoc.column -= 2;
		n[16] = map.get(tileLoc);
		
		// Underneath
		tileLoc.column++;
		tileLoc.row++;
		tileLoc.layer -= 2;
		n[17] = map.get(tileLoc);
		
		// Underneath left
		tileLoc.column--;
		n[18] = map.get(tileLoc);
		
		// Underneath right
		tileLoc.column += 2;
		n[19] = map.get(tileLoc);
		
		// Underneath top
		tileLoc.column--;
		tileLoc.row--;
		n[20] = map.get(tileLoc);
		
		// Underneath bottom
		tileLoc.row += 2;
		n[21] = map.get(tileLoc);
		
		// Underneath top left
		tileLoc.column--;
		tileLoc.row -= 2;
		n[22] = map.get(tileLoc);
		
		// Underneath top right
		tileLoc.column += 2;
		n[23] = map.get(tileLoc);
		
		// Underneath bottom left
		tileLoc.column -= 2;
		tileLoc.row += 2;
		n[24] = map.get(tileLoc);
		
		// Underneath bottom right
		tileLoc.column += 2;
		n[25] = map.get(tileLoc);
	}
	
	public void clear() {
		tiles.clear();
		tilesPerLayer.clear();
		hoveredTile = null;
		selectedTile = null;
		matchedTiles.clear();
		invalidTiles.clear();
	}
	
	public void addTilesAsArray3D(int column, int row, int layer, int width, int height, int depth) {
		int x = column;
		for (int i = 0; i < width; i++) {
			int y = row;
			for (int j = 0; j < height; j++) {
				int z = layer;
				for (int k = 0; k < depth; k++) {
					TileLocation loc = new TileLocation(x, y, z);
					tiles.put(loc, new Tile(loc, 0));
					z++;
				}
				y += 2;
			}
			x += 2;
		}
	}
	
	/* Select free tiles based on probability for generating puzzle without getting locked up. In this case, 
	 * free tiles stacked higher have a greater chance of being eliminated early during puzzle generation
	 * (probability of selection proportional to height, see calculation of variable "p").
	 * See comment marked "Puzzle generation fail". */
	private int randomIndex(Array<TileLocation> tileLocations) {
		int totalProbability = 0;
		for (int i = 0; i < tileLocations.size; i++) {
			int p = tileLocations.get(i).layer + 1;
			totalProbability += p;
		}
		int random = MathUtils.random(totalProbability - 1);
		totalProbability = 0;
		for (int i = 0; i < tileLocations.size; i++) {
			int p = tileLocations.get(i).layer + 1;
			totalProbability += p;
			if (random < totalProbability) {
				return i;
			}
		}
		return tileLocations.size - 1;
	}
	
	// Convert unskinned tile configuration to skinned in such a way that the puzzle has at least one solution
	private boolean finalizeTiles(Array<ModelInstance> skinModelInstances) {
		Tile[] neighbors = new Tile[26];
		Tile[] neighbors2 = new Tile[26];
		OrderedMap<TileLocation, Tile> tiles = new OrderedMap<TileLocation, Tile>(this.tiles);
		OrderedMap<TileLocation, Tile> freeTiles = new OrderedMap<TileLocation, Tile>();
		OrderedMap<TileLocation, Tile> finalTiles = new OrderedMap<TileLocation, Tile>();
		
		Entries<TileLocation, Tile> tilesIt = tiles.entries();
		while (tilesIt.hasNext()) {
			Entry<TileLocation, Tile> entry = tilesIt.next();
			getTileNeighbors(tiles, neighbors, entry.key);
			if (isTileRemovable(neighbors))
				freeTiles.put(entry.key, entry.value);
		}
		
		Array<TileLocation> freeTilesKeys = freeTiles.orderedKeys();
		TileLocation[] tileLoc = new TileLocation[2];
		Tile[] freeTilesPair = new Tile[2];
		while (tiles.notEmpty()) {
			/* Puzzle generation fail:
			 * Sometimes, by chance, there is no pair of free tiles available after randomly deleting "removable" tile pairs
			 * In this case, the finalizeTiles() method should be called again for another attempt. */
			if (freeTiles.size < 2) {
				return false;
			}
			
			int skin = MathUtils.random(skinModelInstances.size - 1);
			ModelInstance skinModelInstance = skinModelInstances.get(skin);
			
			int r1 = randomIndex(freeTilesKeys);
			int r2 = randomIndex(freeTilesKeys);
			if (r1 == r2) {
				r2 = randomIndex(freeTilesKeys);
				if (r1 == r2) {
					r2++;
					if (r2 == freeTilesKeys.size)
						r2 -= 2;
				}
			}
			tileLoc[0] = freeTilesKeys.get(r1);
			tileLoc[1] = freeTilesKeys.get(r2);
			
			for (int i = 0; i < 2; i++) {
				freeTilesPair[i] = freeTiles.get(tileLoc[i]);
				freeTilesPair[i].setSkin(skin);
				freeTilesPair[i].setSkinModelInstance(skinModelInstance);
				
				tiles.remove(tileLoc[i]);
				freeTiles.remove(tileLoc[i]);
				finalTiles.put(tileLoc[i], freeTilesPair[i]);
			}

			for (int i = 0; i < 2; i++) {
				getTileNeighbors(tiles, neighbors2, tileLoc[i]);
				for (int j = 0; j < 26; j++) {
					Tile n = neighbors2[j];
					if (n != null) {
						TileLocation nLoc = n.getLocation();
						if (!freeTiles.containsKey(nLoc)) {
							getTileNeighbors(tiles, neighbors, nLoc);
							if (isTileRemovable(neighbors))
								freeTiles.put(nLoc, n);
						}
					}
				}
			}
		}
		
		clear();
		
		for (Tile tile : finalTiles.values()) {
			addTile(tile);
		}
		
		return true;
	}
	
	/* attempts - number of times we should try to finalize the tiles.
	 * If -1, try forever until we succeed. */
	public void tryFinalizeTiles(Array<ModelInstance> skinModelInstances, int attempts) {
		if (tiles.size % 2 != 0) {
			System.err.println("Error: There must be an even number of tiles in the map for it to be playable.");
			return;
		}
		
		if (attempts == -1) {
			while (!finalizeTiles(skinModelInstances));
		} else {
			for (int i = 0; i < attempts; i++) {
				if (finalizeTiles(skinModelInstances))
					break;
			}
		}
	}
	
	public void finalizeTilesDebug(ModelInstance skinModelInstance) {
		OrderedMap<TileLocation, Tile> finalTiles = new OrderedMap<TileLocation, Tile>(this.tiles);
		for (Tile tile : finalTiles.values()) {
			tile.setSkinModelInstance(skinModelInstance);
		}
		
		clear();
		
		for (Tile tile : finalTiles.values()) {
			addTile(tile);
		}
	}
}
