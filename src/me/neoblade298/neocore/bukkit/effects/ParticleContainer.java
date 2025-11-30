package me.neoblade298.neocore.bukkit.effects;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleContainer extends Effect {
	public static final String HIDE_TAG = "hide-particles";
	protected Particle particle;
	protected int count = 1;
	protected double spreadXZ, spreadY, speed, offsetForward, offsetForwardAngle, offsetY;
	protected BlockData blockData;
	protected DustOptions dustOptions;
	
	public ParticleContainer(Particle particle) {
		super(HIDE_TAG);
		this.particle = particle;
		
		if (particle == Particle.DUST) dustOptions = new DustOptions(Color.RED, count);
	}
	
	public ParticleContainer clone() {
		ParticleContainer pc = new ParticleContainer(particle);
		pc.count(count);
		pc.spread(spreadXZ, spreadY);
		pc.speed(speed);
		pc.blockData = blockData;
		pc.dustOptions = dustOptions;
		pc.forceVisibility = forceVisibility;
		return pc;
	}
	
	public ParticleContainer particle(Particle particle) {
		this.particle = particle;
		return this;
	}
	
	public ParticleContainer count(int count) {
		this.count = count;
		return this;
	}
	
	public ParticleContainer forceVisible(Audience forced) {
		this.forceVisibility = forced;
		return this;
	}
	
	public ParticleContainer spread(double spreadXZ, double spreadY) {
		this.spreadXZ = spreadXZ;
		this.spreadY = spreadY;
		return this;
	}
	
	public ParticleContainer offsetY(double offsetY) {
		this.offsetY = offsetY;
		return this;
	}
	
	public ParticleContainer offsetForward(double offsetForward) {
		this.offsetForward = offsetForward;
		return this;
	}
	
	public ParticleContainer offsetForward(double offsetForward, double offsetForwardAngle) {
		this.offsetForward = offsetForward;
		this.offsetForwardAngle = offsetForwardAngle;
		return this;
	}
	
	public ParticleContainer offsetForward(double offsetForward, double offsetForwardAngle, boolean offsetForwardUseOriginalY) {
		this.offsetForward = offsetForward;
		this.offsetForwardAngle = offsetForwardAngle;
		return this;
	}
	
	public ParticleContainer speed(double speed) {
		this.speed = speed;
		return this;
	}
	
	public ParticleContainer blockData(BlockData blockData) {
		this.dustOptions = null;
		this.blockData = blockData;
		return this;
	}
	
	public ParticleContainer dustOptions(DustOptions dustOptions) {
		this.blockData = null;
		this.dustOptions = dustOptions;
		return this;
	}
	
	private Location calculateOffset(Location loc) {
		loc = loc.clone();
		loc.add(0, offsetY, 0);
		Vector forward = loc.getDirection();
		forward.rotateAroundY(Math.toDegrees(offsetForwardAngle));
		return loc.add(forward.multiply(offsetForward));
	}
	
	@Override
	public void playEffect(Player p, Location loc) {
		p.spawnParticle(particle, calculateOffset(loc), count, spreadXZ, spreadY, spreadXZ, speed, blockData != null ? blockData : dustOptions);
	}

	@Override
	protected void playEffect(Location loc) {
		loc.getWorld().spawnParticle(particle, calculateOffset(loc), count, spreadXZ, spreadY, spreadXZ, speed, blockData != null ? blockData : dustOptions);
	}
}
