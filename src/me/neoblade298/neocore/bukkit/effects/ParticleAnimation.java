package me.neoblade298.neocore.bukkit.effects;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.neoblade298.neocore.bukkit.NeoCore;

public class ParticleAnimation {
	private ParticleContainer particle;
	private int steps, frequency;
	private ParticleFormula formula;
	
	public ParticleAnimation(ParticleContainer particle, ParticleFormula formula, int steps, int frequency) {
		this.particle = particle;
		this.formula = formula;
		this.steps = steps;
		this.frequency = frequency;
	}
	
	public ParticleAnimation(ParticleContainer particle, ParticleFormula formula, int duration) {
		this(particle, formula, duration, 1);
	}
	
	public ParticleAnimationInstance play(Player origin, Entity ent) {
		return new ParticleAnimationInstance(origin, this, ent);
	}
	
	public ParticleAnimationInstance play(Player origin, Location loc) {
		return new ParticleAnimationInstance(origin, this, loc);
	}
	
	public interface ParticleFormula {
		public LinkedList<Location> run(Location center, int step);
	}
	
	public class ParticleAnimationInstance {
		private LinkedList<BukkitTask> tasks;
		private Entity ent;
		private Location loc;
		
		private ParticleAnimationInstance(Player origin, ParticleAnimation anim, Location loc) {
			this.loc = loc;
			LinkedList<Player> cache = Effect.calculateCache(origin, loc, anim.particle.forceVisibility, ParticleContainer.HIDE_TAG);
			run(anim, cache);
		}
		
		private ParticleAnimationInstance(Player origin, ParticleAnimation anim, Entity ent) {
			this.ent = ent;
			LinkedList<Player> cache = Effect.calculateCache(origin, ent.getLocation(), anim.particle.forceVisibility, ParticleContainer.HIDE_TAG);
			run(anim, cache);
		}
		
		private void run(ParticleAnimation anim, LinkedList<Player> cache) {
			tasks = new LinkedList<BukkitTask>();
			
			if (ent != null) {
				for (int i = 0; i < anim.steps; i++) {
					final int step = i;
					tasks.add(new BukkitRunnable() {
						public void run() {
							for (Location l : anim.formula.run(ent.getLocation(), step)) {
								anim.particle.playWithCache(cache, l);
							}
						}
					}.runTaskLater(NeoCore.inst(), i * anim.frequency));
				}
			}
			else {
				for (int i = 0; i < anim.steps; i++) {
					final int step = i;
					tasks.add(new BukkitRunnable() {
						public void run() {
							for (Location l : anim.formula.run(loc, step)) {
								anim.particle.playWithCache(cache, l);
							}
						}
					}.runTaskLater(NeoCore.inst(), i * anim.frequency));
				}
			}
		}
		
		public void cancel() {
			for (BukkitTask task : tasks) {
				task.cancel();
			}
		}
	}
}
