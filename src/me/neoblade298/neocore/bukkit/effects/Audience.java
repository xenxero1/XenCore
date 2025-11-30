package me.neoblade298.neocore.bukkit.effects;

public enum Audience {
	NONE(false, false),
	ORIGIN(true, false),
	NOT_ORIGIN(false, true),
	ALL(true, true);
	private boolean origin, nonOrigin;
	private Audience(boolean origin, boolean nonOrigin) {
		this.origin = origin;
		this.nonOrigin = nonOrigin;
	}
	
	public boolean containsOrigin() {
		return origin;
	}
	
	public boolean containsNonOrigin() {
		return nonOrigin;
	}
}
