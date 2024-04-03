package com.fuzzycat.mahjongsolitaire;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader.AlignMode;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

public class MahjongSolitaire extends ApplicationAdapter {
	private AssetManager assets;

	private Environment tableEnvironment;
	private Environment tilesEnvironment;
	private Environment teaEnvironment;
	private PerspectiveCamera camera;
	private RotationalCameraInputController cameraController;
	private ModelBatch modelBatch;
	
	private ModelInstance tableModelInstance;
	private ModelInstance teapotModelInstance;
	private ModelInstance teacupModelInstance;
	private static final int NUM_TILE_SKINS = 34;
	private Array<ModelInstance> tileSkinModelInstances;
	private Model tileOverlayModel;
	
	private BillboardParticleBatch steamParticleBatch;
	private ParticleController steamParticleController;
	private float steamTime;
	
	private Table table;
	private static final int NUM_TABLE_CONFIGURATIONS = 2;
	private int tableConfiguration;
	
	@Override
	public void create() {
		assets = new AssetManager();
		assets.load("table.g3db", Model.class);
		assets.load("teapot.g3db", Model.class);
		assets.load("teacup.g3db", Model.class);
		assets.load("mahjong_tile.g3db", Model.class);
		assets.load("wood.png", Texture.class);
		for (int i = 0; i < NUM_TILE_SKINS; i++)
			assets.load(String.format("mahjong_tile_%d.png", i + 1), Texture.class);
		assets.load("steam.png", Texture.class);
		assets.finishLoading();
		
		for (int i = 0; i < NUM_TILE_SKINS; i++) {
			Texture tileTexture = assets.get(String.format("mahjong_tile_%d.png", i + 1), Texture.class);
			tileTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		tableEnvironment = new Environment();
		tableEnvironment.add(new PointLight().set(1.0f, 0.96f, 0.83f, 0.0f, 25.0f, 0.0f, 1000.0f));
		
		tilesEnvironment = new Environment();
		tilesEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1f));
		tilesEnvironment.add(new PointLight().set(1.0f, 0.96f, 0.83f, 0.0f, 50.0f, 0.0f, 500.0f));
		tilesEnvironment.add(new DirectionalLight().set(0.33f, 0.33f, 0.33f, -1.0f, -1.0f, -1.0f));
		tilesEnvironment.add(new DirectionalLight().set(0.33f, 0.33f, 0.33f, 1.0f, -1.0f, -1.0f));
		tilesEnvironment.add(new DirectionalLight().set(0.33f, 0.33f, 0.33f, 0.0f, -1.0f, 1.0f));
		
		teaEnvironment = new Environment();
		teaEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		teaEnvironment.add(new PointLight().set(1.0f, 0.96f, 0.83f, 0.0f, 25.0f, 0.0f, 375.0f));
		teaEnvironment.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, -1.0f, -1.0f, -1.0f));
		teaEnvironment.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, 1.0f, -1.0f, -1.0f));
		teaEnvironment.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, 0.0f, -1.0f, 1.0f));
		
		camera = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0f, 10f, 10f);
		camera.lookAt(0, 0, 0);
		camera.near = 1f;
		camera.far = 1000f;
		camera.update();
		
		cameraController = new RotationalCameraInputController(camera, 1.0f, 1.0f, 1.0f, 1.0f, 4.0f);
		cameraController.setZoom(3.0f);
		cameraController.getRotation().x = -30.0f;
		Gdx.input.setInputProcessor(new InputMultiplexer(cameraController, new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				Graphics g = Gdx.graphics;
				if (keycode == Input.Keys.F) {
					if (g.isFullscreen()) {
						g.setWindowedMode(800, 600);
					} else {
						g.setFullscreenMode(g.getDisplayMode(g.getPrimaryMonitor()));
					}
				} else if (keycode == Input.Keys.ESCAPE) {
					Gdx.app.exit();
				} else if (keycode == Input.Keys.LEFT) {
					tableConfiguration--;
					if (tableConfiguration < 0)
						tableConfiguration = NUM_TABLE_CONFIGURATIONS - 1;
					configureTable();
				} else if (keycode == Input.Keys.RIGHT) {
					tableConfiguration++;
					if (tableConfiguration >= NUM_TABLE_CONFIGURATIONS)
						tableConfiguration = 0;
					configureTable();
				} else {
					return false;
				}
				return true;
			}
		}));
		
		modelBatch = new ModelBatch();
		
		Model tileModel = assets.get("mahjong_tile.g3db", Model.class);
		
		tileSkinModelInstances = new Array<ModelInstance>();
		tileSkinModelInstances.setSize(NUM_TILE_SKINS);
		for (int i = 0; i < NUM_TILE_SKINS; i++) {
			tileSkinModelInstances.set(i, new ModelInstance(tileModel));
			tileSkinModelInstances.get(i).getMaterial("MahjongTile").set(
				TextureAttribute.createDiffuse(assets.get(String.format("mahjong_tile_%d.png", i + 1), Texture.class)),
				FloatAttribute.createShininess(200.0f)
			);
		}
		
		// Create the tile highlight rectangle so we don't have to load it from a file
		ModelBuilder builder = new ModelBuilder();
		tileOverlayModel = builder.createRect(
			-Tile.WIDTH2, 0.0f, -Tile.HEIGHT2, 
			-Tile.WIDTH2, 0.0f, Tile.HEIGHT2, 
			Tile.WIDTH2, 0.0f, Tile.HEIGHT2, 
			Tile.WIDTH2, 0.0f, -Tile.HEIGHT2, 
			0.0f, 1.0f, 0.0f, 
			new Material(new BlendingAttribute()), 
			Usage.Position | Usage.Normal | Usage.ColorPacked);
		
		Texture woodTexture = assets.get("wood.png", Texture.class);
		woodTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		woodTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Model tableModel = assets.get("table.g3db", Model.class);
		tableModelInstance = new ModelInstance(tableModel);
		tableModelInstance.transform = new Matrix4()
			.scale(50.0f, 50.0f, 50.0f);
		tableModelInstance.getMaterial("Table").set(
			TextureAttribute.createDiffuse(woodTexture)
		);
		tableModelInstance.getMaterial("Table").remove(ColorAttribute.Specular);
		
		Model teapotModel = assets.get("teapot.g3db", Model.class);
		teapotModelInstance = new ModelInstance(teapotModel);
		teapotModelInstance.transform = new Matrix4()
			.translate(20.0f, 0.0f, -8.0f)
			.rotate(0.0f, 1.0f, 0.0f, -45.0f)
			.scale(4.0f, 4.0f, 4.0f);
		teapotModelInstance.getMaterial("Teapot").set(
			ColorAttribute.createDiffuse(0.39f, 0.09f, 0.07f, 1.0f),
			ColorAttribute.createSpecular(1.0f, 1.0f, 1.0f, 1.0f),
			FloatAttribute.createShininess(100.0f)
		);
				
		Model teacupModel = assets.get("teacup.g3db", Model.class);
		teacupModelInstance = new ModelInstance(teacupModel);
		Matrix4 teacupTransform = new Matrix4()
			.translate(-18.0f, 0.0f, 3.0f)
			.rotate(0.0f, 1.0f, 0.0f, -45.0f);
		teacupModelInstance.transform = new Matrix4(teacupTransform)
			.scale(4.0f, 4.0f, 4.0f);
		teacupModelInstance.getMaterial("Teacup").set(
			ColorAttribute.createDiffuse(0.39f, 0.09f, 0.07f, 1.0f),
			ColorAttribute.createSpecular(1.0f, 1.0f, 1.0f, 1.0f),
			FloatAttribute.createShininess(100.0f)
		);
		teacupModelInstance.getMaterial("Tea").set(
			ColorAttribute.createDiffuse(0.32f, 0.3f, 0.14f, 0.7f),
			new BlendingAttribute()
		);
		
		steamParticleBatch = new BillboardParticleBatch();
		steamParticleBatch.setAlignMode(AlignMode.Screen);
		steamParticleBatch.getBlendingAttribute().sourceFunction = GL30.GL_SRC_ALPHA;
		steamParticleBatch.getBlendingAttribute().destFunction = GL30.GL_ONE_MINUS_SRC_ALPHA;
		steamParticleBatch.getBlendingAttribute().blended = true;
		steamParticleBatch.setTexture(assets.get("steam.png", Texture.class));
		steamParticleBatch.setCamera(camera);
		steamParticleController = SteamEffect.create(steamParticleBatch, 8.0f);
		steamParticleController.setTransform(new Matrix4(teacupTransform).translate(0.0f, 6.0f, 0.0f));
		// Spawn some particles instantaneously so the effect doesn't have to "boot up"
		SteamEffect.simulateParticles(steamParticleController, 3.0f);
		steamTime = 0.0f;
		
		table = new Table(tileOverlayModel);
		tableConfiguration = 0;
		
		configureTable();
	}
	
	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}
	
	@Override
	public void render() {
		cameraController.update();
		table.hover(camera.getPickRay(Gdx.input.getX(), Gdx.input.getY()), Gdx.input.isButtonJustPressed(Input.Buttons.LEFT));
		
		// Particle effects should use a fixed timestep, otherwise emitter can stop working at high FPS
		steamTime += Gdx.graphics.getDeltaTime();
		while (steamTime >= SteamEffect.TIMESTEP) {
			steamParticleController.update(SteamEffect.TIMESTEP);
			steamTime -= SteamEffect.TIMESTEP;
		}
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(camera);
		
		modelBatch.render(tableModelInstance, tableEnvironment);
		modelBatch.render(teapotModelInstance, teaEnvironment);
		modelBatch.render(teacupModelInstance, teaEnvironment);
		table.render(modelBatch, tilesEnvironment);
		
		steamParticleBatch.begin();
		steamParticleController.draw();
		steamParticleBatch.end();
		
		modelBatch.render(steamParticleBatch, teaEnvironment);
		
		modelBatch.end();
	}
	
	private void configureTable() {
		switch (tableConfiguration) {
		case 0:
			TableConfigurations.createTurtle(table);
			break;
		case 1:
			TableConfigurations.createFish(table);
			break;
		}
		table.tryFinalizeTiles(tileSkinModelInstances, -1);
	}
	
	@Override
	public void dispose() {
		assets.dispose();
		tileOverlayModel.dispose();
		modelBatch.dispose();
		steamParticleController.dispose();
	}
}
