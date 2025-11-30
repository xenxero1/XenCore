package me.neoblade298.neocore.bukkit.effects;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Circle extends ParticleShape2D {
	private static final double POINTS_PER_CIRCUMFERENCE = 1;
	private static final double DEFAULT_METERS = 0.5;
	
	private double radius, metersPerParticle;
	private int points;
	private LinkedList<Vector> flatEdges, flatFill; // For flat circles only, can be moved anywhere
	
	public Circle(double radius, int points, double metersPerParticle) {
		this.radius = radius;
		this.points = points;
		this.metersPerParticle = metersPerParticle;
	}
	
	public Circle(double radius, int points) {
		this(radius, points, DEFAULT_METERS);
	}
	
	public Circle(double radius) {
		this(radius, (int) (POINTS_PER_CIRCUMFERENCE * (Math.PI * radius * radius)), DEFAULT_METERS);
	}

	@Override
	public void playWithCache(LinkedList<Player> cache, ParticleContainer particle, Location center, LocalAxes axes, ParticleContainer fill) {
		// If circle is flat, no need to recreate circle except for the first time
		if (axes.isXZ()) {
			drawFlatWithCache(cache, particle, center, fill);
		}
		else {
			calculate(center, axes).playWithCache(cache, particle, fill);
		}
	}
	
	private void drawFlatWithCache(LinkedList<Player> cache, ParticleContainer particle, Location center, ParticleContainer fill) {
		LocalAxes axes = LocalAxes.xz();
		if (flatEdges == null) {
			ParticleShapeMemory mem = calculate(center, axes);
			flatEdges = mem.getEdgeVectors();
			flatFill = mem.getFillVectors();
			mem.playWithCache(cache, particle, fill);
			return;
		}
		
		for (Vector v : flatEdges) {
			particle.playWithCache(cache, center.clone().add(v));
		}
		if (fill == null) return;
		for (Vector v : flatFill) {
			fill.playWithCache(cache, center.clone().add(v));
		}
	}

	@Override
	public ParticleShapeMemory calculate(Location center, LocalAxes axes) {
		double rotationPerPoint = (2 * Math.PI) / (double) points;
		Vector rotator = axes.up().multiply(radius);
		
		LinkedList<Location> edges = new LinkedList<Location>();
		for (int i = 0; i < points; i++) {
			edges.add(center.clone().add(rotator.rotateAroundAxis(axes.forward(), rotationPerPoint)));
		}

		LinkedList<Location> fill = new LinkedList<Location>();
		Location topLeft = center.clone().add(axes.left().multiply(radius)).add(axes.up().multiply(radius));
		Vector right = axes.left().multiply(radius * -2);
		Vector down = axes.up().multiply(radius * -2);
		double radiusSq = radius * radius;
		for (Location horizontal : ParticleUtil.calculateLine(topLeft, topLeft.clone().add(right), metersPerParticle, true)) {
			for (Location point : ParticleUtil.calculateLine(horizontal, horizontal.clone().add(down), metersPerParticle, true)) {
				
				if (point.distanceSquared(center) >= radiusSq) continue;
				fill.add(point);
			}
		}
		
		return new ParticleShapeMemory(center, edges, fill);
	}
}
