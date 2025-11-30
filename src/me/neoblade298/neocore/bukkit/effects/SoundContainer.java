package me.neoblade298.neocore.bukkit.effects;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundContainer extends Effect {
	public static final String HIDE_TAG = "mute-sound";
	private float volume, pitch;
	private Sound sound;
	
	public SoundContainer(Sound sound) {
		this(sound, 1F, 1F);
	}
	
	public SoundContainer(Sound sound, float pitch) {
		this(sound, pitch, 1F);
	}
	
	public SoundContainer(Sound sound, float pitch, float volume) {
		super(HIDE_TAG);
		this.sound = sound;
		this.pitch = pitch;
		this.volume = volume;
	}
	
	public SoundContainer forceVisible(Audience forced) {
		this.forceVisibility = forced;
		return this;
	}

	@Override
	protected void playEffect(Player p, Location loc) {
		p.playSound(loc, sound, volume, pitch);
	}

	@Override
	protected void playEffect(Location loc) {
		loc.getWorld().playSound(loc, sound, volume, pitch);
	}
}
