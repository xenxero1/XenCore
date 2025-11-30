package me.neoblade298.neocore.bukkit.effects;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Rectangle extends ParticleShape2D {
	private static final double DEFAULT_METERS = 0.5;
	private double length, height, metersPerParticle;
	
	public Rectangle(double length, double height) {
		this(length, height, DEFAULT_METERS);
	}
	
	public Rectangle(double length, double height, double metersPerParticle) {
		this.length = length;
		this.height = height;
		this.metersPerParticle = metersPerParticle;
	}

	@Override
	public void playWithCache(LinkedList<Player> cache, ParticleContainer particle, Location center, LocalAxes axes, ParticleContainer fill) {
		ParticleShapeMemory mem = calculate(center, axes);
		mem.playWithCache(cache, particle, fill);
	}

	@Override
	public ParticleShapeMemory calculate(Location center, LocalAxes axes) {
		Location bl = center.clone().add(axes.left().multiply(length * 0.5).add(axes.up().multiply(height * -0.5)));
		Location tl = bl.clone().add(axes.up().multiply(height));
		Vector right = axes.left().multiply(-length);
		Location br = bl.clone().add(right);
		Location tr = tl.clone().add(right);
		LinkedList<Location> edges = ParticleUtil.calculateLine(tl, bl, metersPerParticle, true);
		LinkedList<Location> leftEdge = new LinkedList<Location>(edges);
		edges.addAll(ParticleUtil.calculateLine(br, tr, metersPerParticle, true));
		edges.addAll(ParticleUtil.calculateLine(tr, tl, metersPerParticle));
		edges.addAll(ParticleUtil.calculateLine(bl, br, metersPerParticle));

		LinkedList<Location> fill = new LinkedList<Location>();
		for (Location upPoint : leftEdge) {
			fill.addAll(ParticleUtil.calculateLine(upPoint, upPoint.clone().add(right), metersPerParticle, true));
		}
		return new ParticleShapeMemory(center, edges, fill);
	}
}
