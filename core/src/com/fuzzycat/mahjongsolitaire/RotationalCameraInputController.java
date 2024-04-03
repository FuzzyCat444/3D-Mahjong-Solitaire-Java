package com.fuzzycat.mahjongsolitaire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class RotationalCameraInputController extends InputAdapter {
	private PerspectiveCamera camera;
	private Vector2 position;
	private float moveSpeed;
	private Vector2 rotation;
	private float rotateSpeed;
	private float zoom;
	private float zoomSpeed;
	private float minZoom, maxZoom;
	
	private int touchDraggedMethodCall;
	
	public RotationalCameraInputController(PerspectiveCamera camera, float moveSpeed, float rotateSpeed, float zoomSpeed, float minZoom, float maxZoom) {
		this.camera = camera;
		position = new Vector2();
		this.moveSpeed = moveSpeed;
		rotation = new Vector2();
		this.rotateSpeed = rotateSpeed;
		zoom = 0.0f;
		this.zoomSpeed = zoomSpeed;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		
		touchDraggedMethodCall = 0;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		float dx = 0.15f * Gdx.input.getDeltaX();
		float dy = 0.15f * Gdx.input.getDeltaY();
		
		/* touchDraggedMethodCall >= 1 prevents mouse delta spikes with setCursorCatched().
		 * I'm not sure what libgdx/lwjgl does on top of glfw to cause this issue, as I don't have the
		 * same issue when programming glfw apps in C. The virtual mouse position is occasionally incorrect 
		 * when touchDragged() is first called but the second call is correct relative to the first.
		 * One could easily implement their own setCursorCatched() functionality using functions to hide
		 * the cursor and set the cursor position as well as keeping track of mouse delta. */
		if (touchDraggedMethodCall >= 1) {
			if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
				rotation.x -= rotateSpeed * dy;
				rotation.y -= rotateSpeed * dx;
			} else if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
				zoom += 0.05f * zoomSpeed * dy;
				zoom = MathUtils.clamp(zoom, minZoom, maxZoom);
			}
		}
		touchDraggedMethodCall++;
		return true;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.RIGHT) {
			touchDraggedMethodCall = 0;
			Gdx.input.setCursorCatched(true);
		}
		return true;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.RIGHT) {
			Gdx.input.setCursorCatched(false);
		}
		return true;
	}
	
	@Override
	public boolean scrolled(float amountX, float amountY) {
		zoom += 0.15f * zoomSpeed * amountY;
		zoom = MathUtils.clamp(zoom, minZoom, maxZoom);
		return true;
	}

	public void update() {
		float delta = Gdx.graphics.getDeltaTime();
		
		float wKey = Gdx.input.isKeyPressed(Input.Keys.W) ? 1.0f : 0.0f;
		float aKey = Gdx.input.isKeyPressed(Input.Keys.A) ? 1.0f : 0.0f;
		float sKey = Gdx.input.isKeyPressed(Input.Keys.S) ? 1.0f : 0.0f;
		float dKey = Gdx.input.isKeyPressed(Input.Keys.D) ? 1.0f : 0.0f;
		
		Vector2 velocity = new Vector2();
		velocity.add(new Vector2(0.0f, -1.0f).scl(wKey));
		velocity.add(new Vector2(-1.0f, 0.0f).scl(aKey));
		velocity.add(new Vector2(0.0f, 1.0f).scl(sKey));
		velocity.add(new Vector2(1.0f, 0.0f).scl(dKey));
		velocity.setLength(20.0f * moveSpeed);
		velocity.rotateDeg(-rotation.y);
		
		position.add(new Vector2(velocity).scl(delta));
		
		clampRotationX();
		
		Vector3 actualPosition = getActualPosition();
		camera.position.set(actualPosition);
		camera.direction.set(new Vector3(getFocusedPosition()).sub(actualPosition).nor());
		camera.up.set(new Vector3(camera.direction).crs(0.0f, 1.0f, 0.0f).crs(camera.direction).nor());
		camera.update();
	}
	
	public Vector2 getRotation() {
		return rotation;
	}
	
	private void clampRotationX() {
		float tan = MathUtils.tanDeg(0.5f * camera.fieldOfView);
		float angle = MathUtils.atanDeg(tan / (getFocusedPosition().sub(getActualPosition()).len() / camera.near - 1.0f));
		rotation.x = MathUtils.clamp(rotation.x, -89.99f, -angle);
	}
	
	private Vector3 getFocusedPosition() {
		return new Vector3(position.x, 0.0f, position.y);
	}
	
	private Vector3 getActualPosition() {
		Vector3 origin = new Vector3(position.x, 0.0f, position.y);
		Vector3 rotationalPosition = new Vector3(0.0f, 0.0f, (float) Math.exp(zoom));
		rotationalPosition.rotate(rotation.x, 1.0f, 0.0f, 0.0f);
		rotationalPosition.rotate(rotation.y, 0.0f, 1.0f, 0.0f);
		origin.add(rotationalPosition);
		return origin;
	}
	
	public void setRotateSpeed(float rotateSpeed) {
		this.rotateSpeed = rotateSpeed;
	}
	
	public float getRotateSpeed() {
		return rotateSpeed;
	}
	
	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
	
	public float getZoom() {
		return zoom;
	}
	
	public void setZoomSpeed(float zoomSpeed) {
		this.zoomSpeed = zoomSpeed;
	}
	
	public float getZoomSpeed() {
		return zoomSpeed;
	}
	
	public void setMinZoom(float minZoom) {
		this.minZoom = minZoom;
	}
	
	public float getMinZoom() {
		return minZoom;
	}
	
	public void setMaxZoom(float maxZoom) {
		this.maxZoom = maxZoom;
	}
	
	public float getMaxZoom() {
		return maxZoom;
	}
}
