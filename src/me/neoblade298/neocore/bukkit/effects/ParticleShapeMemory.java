package me.neoblade298.neocore.bukkit.effects;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleShapeMemory {
	private Location center;
	private LinkedList<Location> edges, fill;
	
	public ParticleShapeMemory(Location center, LinkedList<Location> edges, LinkedList<Location> fill) {
		this.center = center;
		this.edges = edges;
		this.fill = fill;
	}
	
	public void play(ParticleContainer edge) {
		play(edge, null);
	}
	
	public void play(ParticleContainer edge, ParticleContainer fill) {
		for (Location loc : edges) {
			edge.play(loc);
		}
		if (fill != null) {
			for (Location loc : this.fill) {
				fill.play(loc);
			}
		}
	}
	
	public void play(Player origin, ParticleContainer edge) {
		playWithCache(Effect.calculateCache(origin, center, edge.getForcedVisibility(), ParticleContainer.HIDE_TAG), edge);
	}
	
	public void play(Player origin, ParticleContainer edge, ParticleContainer fill) {
		playWithCache(Effect.calculateCache(origin, center, edge.getForcedVisibility(), ParticleContainer.HIDE_TAG), edge, fill);
	}
	
	public void playWithCache(LinkedList<Player> cache, ParticleContainer edge) {
		playWithCache(cache, edge, null);
	}
	
	public void playWithCache(LinkedList<Player> cache, ParticleContainer edge, ParticleContainer fill) {
		for (Location loc : edges) {
			edge.playWithCache(cache, loc);
		}
		if (fill != null) {
			for (Location loc : this.fill) {
				fill.playWithCache(cache, loc);
			}
		}
	}

	public LinkedList<Location> getEdges() {
		return edges;
	}

	public LinkedList<Location> getFill() {
		return fill;
	}
	
	public LinkedList<Vector> getEdgeVectors() {
		LinkedList<Vector> evs = new LinkedList<Vector>();
		for (Location loc : edges) {
			evs.add(loc.clone().subtract(center).toVector());
		}
		return evs;
	}
	
	public LinkedList<Vector> getFillVectors() {
		LinkedList<Vector> fvs = new LinkedList<Vector>();
		for (Location loc : fill) {
			fvs.add(loc.clone().subtract(center).toVector());
		}
		return fvs;
	}
}
