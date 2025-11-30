package me.neoblade298.neocore.bukkit.effects;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Cone extends ParticleShape2D {
	private static final double POINTS_PER_CIRCUMFERENCE = 1;
	private static final double DEFAULT_METERS = 0.5;
	
	private double length, degrees, metersPerParticle;
	private int lines;
	
	public Cone(double length, double degrees, int lines, double metersPerParticle) {
		this.length = length;
		this.degrees = degrees;
		this.lines = lines;
		this.metersPerParticle = metersPerParticle;
	}
	
	public Cone(double length, double degrees, int lines) {
		this(length, degrees, lines, DEFAULT_METERS);
	}
	
	public Cone(double length, double degrees) {
		this(length, degrees, (int) (POINTS_PER_CIRCUMFERENCE * (Math.PI * length * length * (degrees / 360))), DEFAULT_METERS);
	}

	@Override
	public void playWithCache(LinkedList<Player> cache, ParticleContainer particle, Location center, LocalAxes axes, ParticleContainer fill) {
		calculate(center, axes).playWithCache(cache, particle, fill);
	}

	@Override
	public ParticleShapeMemory calculate(Location center, LocalAxes axes) {
		Vector line = axes.forward().multiply(length).rotateAroundAxis(axes.up(), Math.toRadians(-degrees / 2));
		LinkedList<Location> edges = ParticleUtil.calculateLine(center, center.clone().add(line), metersPerParticle);
		LinkedList<Location> fill = new LinkedList<Location>();
		double rotationPerLine = Math.toRadians(degrees / lines);
		for (int i = 1; i < lines; i++) {
			line.rotateAroundAxis(axes.up(), rotationPerLine);
			Location edge = center.clone().add(line);
			edges.add(edge);
			fill.addAll(ParticleUtil.calculateLine(center, edge, metersPerParticle, true));
		}
		line.rotateAroundAxis(axes.up(), rotationPerLine);
		Location edge = center.clone().add(line);
		edges.add(edge);
		edges.addAll(ParticleUtil.calculateLine(center, edge, metersPerParticle, true));
		return new ParticleShapeMemory(center, edges, fill);
	}
}
