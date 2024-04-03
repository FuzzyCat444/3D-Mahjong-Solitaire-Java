package com.fuzzycat.mahjongsolitaire;

import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.PolarAcceleration;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier.Rotational2D;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer;
import com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue;

public class SteamEffect {
	
	public static final float TIMESTEP = 0.007f;
	
	// The variables and their meaning can be discovered by experimenting with the libGDX "Flame" particle editor GUI
	public static ParticleController create(BillboardParticleBatch batch, float scale) {
		RegularEmitter emitter = new RegularEmitter();
		emitter.setMinParticleCount(0);
		emitter.setMaxParticleCount(1000);
		emitter.getDuration().setLow(1.0f);
		emitter.getEmission().setTimeline(new float[] { 0.0f });
		emitter.getEmission().setScaling(new float[] { 1.0f });
		emitter.getEmission().setHigh(3.0f);
		emitter.getEmission().setLow(0.0f);
		emitter.getLife().setTimeline(new float[] { 0.0f });
		emitter.getLife().setScaling(new float[] { 1.0f });
		emitter.getLife().setHigh(4000.0f);
		emitter.getLife().setLow(0.0f);
		
		PointSpawnShapeValue pointSpawnShapeValue = new PointSpawnShapeValue();
		SpawnInfluencer spawnSource = new SpawnInfluencer(pointSpawnShapeValue);
		
		DynamicsInfluencer dynamicsInfluencer = new DynamicsInfluencer();
		PolarAcceleration polarAcceleration = new PolarAcceleration();
		polarAcceleration.strengthValue.setTimeline(new float[] { 0.0f });
		polarAcceleration.strengthValue.setScaling(new float[] { 1.0f });
		polarAcceleration.strengthValue.setHigh(0.2f * scale);
		polarAcceleration.strengthValue.setLow(0.0f);
		polarAcceleration.thetaValue.setTimeline(new float[] { 0.0f });
		polarAcceleration.thetaValue.setScaling(new float[] { 1.0f });
		polarAcceleration.thetaValue.setHigh(0.0f, 360.0f);
		polarAcceleration.thetaValue.setLow(0.0f);
		polarAcceleration.phiValue.setTimeline(new float[] { 0.0f, 0.3f, 0.7f, 1.0f });
		polarAcceleration.phiValue.setScaling(new float[] { 0.0f, 0.2f, 0.95f, 1.0f });
		polarAcceleration.phiValue.setHigh(50.0f);
		polarAcceleration.phiValue.setLow(-50.0f);
		dynamicsInfluencer.velocities.add(polarAcceleration);
		Rotational2D rotational2D = new Rotational2D();
		rotational2D.strengthValue.setTimeline(new float[] { 0.0f });
		rotational2D.strengthValue.setScaling(new float[] { 1.0f });
		rotational2D.strengthValue.setHigh(-50.0f, 50.0f);
		rotational2D.strengthValue.setLow(0.0f);
		dynamicsInfluencer.velocities.add(rotational2D);
		
		ScaleInfluencer scaleInfluencer = new ScaleInfluencer();
		scaleInfluencer.value.setTimeline(new float[] { 0.0f });
		scaleInfluencer.value.setScaling(new float[] { 1.0f });
		scaleInfluencer.value.setHigh(scale);
		scaleInfluencer.value.setLow(0.0f);
		
		ColorInfluencer.Single colorInfluencer = new ColorInfluencer.Single();
		colorInfluencer.colorValue.setTimeline(new float[] { 0.0f });
		colorInfluencer.colorValue.setColors(new float[] { 1.0f, 1.0f, 1.0f });
		colorInfluencer.alphaValue.setTimeline(new float[] { 0.0f, 0.33f, 0.66f, 1.0f });
		colorInfluencer.alphaValue.setScaling(new float[] { 0.0f, 0.1f, 0.1f, 0.0f });
		colorInfluencer.alphaValue.setHigh(1.0f);
		colorInfluencer.alphaValue.setLow(0.0f);
		
		ParticleController controller = new ParticleController("Steam Particle Controller", emitter, new BillboardRenderer(batch), spawnSource, dynamicsInfluencer, scaleInfluencer, colorInfluencer);
		controller.init();
		controller.start();
		
		return controller;
	}
	
	// Spawn some particles instantaneously so the effect doesn't have to "boot up"
	public static void simulateParticles(ParticleController controller, float seconds) {
		final int initIterations = (int) (seconds / TIMESTEP);
		for (int i = 0; i < initIterations; i++) {
			controller.update(TIMESTEP);
		}
	}
}
