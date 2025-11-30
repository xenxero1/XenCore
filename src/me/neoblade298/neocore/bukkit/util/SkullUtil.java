package me.neoblade298.neocore.bukkit.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

/**
 * A library for the Bukkit API to create player skulls from names, base64
 * strings, and texture URLs.
 *
 * Does not use any NMS code, and should work across all versions.
 *
 * @author Dean B on 12/28/2016.
 */
public class SkullUtil {

	/**
	 * Creates a player skull based on a base64 string containing the link to the
	 * skin.
	 *
	 * @param base64 The base64 string containing the texture
	 * @return The head with a custom texture
	 */
	public static ItemStack fromBase64(String base64) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
		profile.setProperty(new ProfileProperty("textures", base64));
		meta.setPlayerProfile(profile);
		item.setItemMeta(meta);
		return item;
	}
}
