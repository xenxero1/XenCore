package me.neoblade298.neocore.bukkit.effects;


import java.util.LinkedList;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

// By default, draws the shape flat on the ground
public abstract class ParticleShape2D {
	// Vertical and horizontal axis are normalized vectors, for a flat draw just use x and z vector
	public abstract void playWithCache(LinkedList<Player> cache, ParticleContainer edges, Location center, LocalAxes axes, ParticleContainer fill);
	
	public abstract ParticleShapeMemory calculate(Location center, LocalAxes axes);
	
	public void play(Player origin, ParticleContainer edges, Location center, LocalAxes axes, @Nullable ParticleContainer fill) {
		playWithCache(Effect.calculateCache(origin, center, edges.forceVisibility, ParticleContainer.HIDE_TAG), edges, center, axes, fill);
	}
	
	public void play(ParticleContainer edges, Location center, LocalAxes axes, @Nullable ParticleContainer fill) {
		playWithCache(Effect.calculateCache(center), edges, center, axes, fill);
	}
}
