package me.neoblade298.neocore.bukkit.player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.events.PlayerFieldChangedEvent;
import me.neoblade298.neocore.bukkit.events.ValueChangeType;

public class PlayerFields {
	private final String key;
	private HashMap<UUID, HashMap<String, Value>> values;
	private HashMap<UUID, HashSet<String>> changedValues;
	private HashMap<String, Object> defaults;
	private final boolean hidden;
	
	public PlayerFields (String key, boolean hidden) {
		this.key = key;
		this.values = new HashMap<UUID, HashMap<String, Value>>();
		this.defaults = new HashMap<String, Object>();
		this.changedValues = new HashMap<UUID, HashSet<String>>();
		this.hidden = hidden;
	}
	
	public String getKey() {
		return key;
	}
	
	public Set<String> getAllKeys() {
		return this.defaults.keySet();
	}
	
	public Object getValue(UUID uuid, String key) {
		if (!defaults.containsKey(key)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to get field " + this.getKey() + "." + key + " for " + uuid + ". Field doesn't exist.");
			return null;
		}
		if (!values.containsKey(uuid)) {
			return defaults.get(key);
		}
		HashMap<String, Value> pValues = values.get(uuid);
		if (pValues.containsKey(key)) {
			// If value has expired, remove it
			if (pValues.get(key).isExpired()) {
				pValues.remove(key);
				changedValues.get(uuid).add(key);
			}
			else {
				return pValues.get(key).getValue();
			}
		}
		return defaults.get(key);
	}
	
	public Object getDefault(String key) {
		if (!defaults.containsKey(key)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to get default field " + this.getKey() + "." + key + ". Field doesn't exist.");
			return null;
		}
		return defaults.get(key);
	}
	
	public boolean exists(String field, UUID uuid) {
		if (!defaults.containsKey(field)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to get field " + this.getKey() + "." + field + " for " + uuid + ". Field doesn't exist.");
			return false;
		}
		if (!values.containsKey(uuid)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to get field " + this.getKey() + "." + key + " for " + uuid + ". UUID not initialized.");
			return false;
		}
		HashMap<String, Value> pValues = values.get(uuid);
		if (pValues.containsKey(field)) {
			// If value has expired, remove it
			if (pValues.get(field).isExpired()) {
				Bukkit.getPluginManager().callEvent(new PlayerFieldChangedEvent(Bukkit.getPlayer(uuid), this.key, field, pValues.get(field), ValueChangeType.EXPIRED));
				pValues.remove(field);
				changedValues.get(uuid).add(key);
				return false;
			}
			return true;
		}
		return false;
	}
	
	// Only happens on logout atm
	public void save(Statement insert, Statement delete, UUID uuid) {
		if (changedValues.containsKey(uuid) && !changedValues.get(uuid).isEmpty()) {
			HashMap<String, Value> pValues = values.get(uuid);
			if (NeoCore.isDebug()) {
				Bukkit.getLogger().log(Level.INFO, "[NeoCore] Debug: Changed values: " + this.getKey() + " " + changedValues.get(uuid) + " for " + uuid + ".");
			}
			
			// Only save changed values
			for (String key : changedValues.get(uuid)) {
				
				// If value was changed to something else
				if (pValues.containsKey(key)) {
					Object value = pValues.get(key).getValue();
					
					// Skip and remove expired values
					if (pValues.get(key).isExpired()) {
						pValues.remove(key);
						continue;
					}
					long expiration = pValues.get(key).getExpiration();
					
					
					try {
						Bukkit.getLogger().log(Level.INFO, "[NeoCore] Saving " + this.getKey() + "." + key + " to " + value + " for " + uuid + ".");
						if (value instanceof String) {
							insert.addBatch("REPLACE INTO neocore_fields_strings VALUES ('" + uuid + "','" + this.getKey()
							+ "','" + key + "','" + value + "'," + expiration + ");");
						}
						else if (value instanceof Boolean) {
							insert.addBatch("REPLACE INTO neocore_fields_strings VALUES ('" + uuid + "','" + this.getKey()
							+ "','" + key + "','" + value + "'," + expiration + ");");
						}
						else if (value instanceof Integer) {
							insert.addBatch("REPLACE INTO neocore_fields_integers VALUES ('" + uuid + "','" + this.getKey()
							+ "','" + key + "','" + value + "'," + expiration + ");");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// If value was set back to default
				else {
					Object def = defaults.get(key);
					Bukkit.getLogger().log(Level.INFO, "[NeoCore] Defaulting " + this.getKey() + "." + key + " to " + def + " for " + uuid + ".");
					try {
						if (def instanceof String || def instanceof Boolean) {
							delete.addBatch("DELETE FROM neocore_fields_strings WHERE `key` = '" + this.getKey() + "' AND field = '" + key +
							"' AND uuid = '" + uuid + "';");
						}
						else if (def instanceof Integer) {
							delete.addBatch("DELETE FROM neocore_fields_integers WHERE `key` = '" + this.getKey() + "' AND field = '" + key +
							"' AND uuid = '" + uuid + "';");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		values.remove(uuid);
		changedValues.remove(uuid);
	}
	
	public void load(Statement stmt, UUID uuid) {
		HashMap<String, Value> pFields = new HashMap<String, Value>();
		this.values.put(uuid, pFields);
		this.changedValues.put(uuid, new HashSet<String>());
		try {
			ResultSet rs = stmt.executeQuery("SELECT * FROM neocore_fields_strings WHERE uuid = '" + uuid + "' AND `key` = '" + this.getKey() + "';");
			while (rs.next()) {
				String field = rs.getString(3);
				long expiration = rs.getLong(5);
				Object o = defaults.get(field);
				Object value = null;
				if (o == null) {
					Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to load field " + this.getKey() + "." + field + " for " + uuid + ". Field doesn't exist.");
					return;
				}
				else if (o instanceof String) {
					value = rs.getString(4);
				}
				else if (o instanceof Boolean) {
					value = rs.getBoolean(4);
				}
				
				if (value == null) {
					Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to load field " + this.getKey() + "." + field + " for " + uuid + ". Value is null.");
				}
				if (NeoCore.isDebug()) {
					Bukkit.getLogger().log(Level.INFO, "[NeoCore] Debug: Loading field: " + this.getKey() + "." + field + " for " + uuid + ".");
				}
				
				if (expiration == -1 || expiration < System.currentTimeMillis()) {
					pFields.put(field, new Value(value, expiration));
				}
			}
			
			rs = stmt.executeQuery("SELECT * FROM neocore_fields_integers WHERE uuid = '" + uuid + "' AND `key` = '" + this.getKey() + "';");
			while (rs.next()) {
				String field = rs.getString(3);
				long expiration = rs.getLong(5);
				Object o = defaults.get(field);
				Object value = null;
				if (o == null) {
					Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to load field " + this.getKey() + "." + field + " for " + uuid + ". Field doesn't exist.");
					return;
				}
				else if (o instanceof Integer) {
					value = rs.getInt(4);
				}
				
				if (value == null) {
					Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to load field " + this.getKey() + "." + field + " for " + uuid + ". Value is null.");
				}
				if (NeoCore.isDebug()) {
					Bukkit.getLogger().log(Level.INFO, "[NeoCore] Debug: Loading field: " + this.getKey() + "." + field + " for " + uuid + ".");
				}
				pFields.put(field, new Value(value, expiration));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void initializeField(String key, Object def) {
		this.defaults.put(key, def);
	}
	
	public boolean changeField(String key, String v, UUID uuid) {
		return changeField(key, v, uuid, -1);
	}
	
	// If expiration is 0, the original expiration is kept
	public boolean changeField(String key, String v, UUID uuid, long expiration) {
		Object value = null;
		if (!defaults.containsKey(key)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to change field " + this.getKey() + "." + key + " for " + uuid + ". Field doesn't exist.");
			return false;
		}
		
		// Try to change String o into the proper class
		try {
			if (defaults.get(key).getClass() == Boolean.class) {
				value = Boolean.parseBoolean(v);
			}
			else if (defaults.get(key).getClass() == String.class) {
				value = v;
			}
			else if (defaults.get(key).getClass() == Integer.class) {
				value = Integer.parseInt(v);
				if ((Integer) value > 100000 || (Integer) value < -99) {
					Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to change field " + this.getKey() + "." + key + " for " + uuid + ". Value was out of bounds.");
					return false;
				}
			}
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to change field " + this.getKey() + "." + key + " for " + uuid + ". Couldn't convert string to class.");
			e.printStackTrace();
			return false;
		}

		if (value == null) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to change field " + this.getKey() + "." + key + " for " + uuid + ". Value failed to parse and was null.");
			return false;
		}
		
		if (value.equals(defaults.get(key))) {
			return resetField(key, uuid);
		}
		
		if (!values.containsKey(uuid)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to change field " + this.getKey() + "." + key + " for " + uuid + ". UUID not initialized.");
			return false;
		}

		changedValues.get(uuid).add(key);
		
		Value curr = null;
		if (values.get(uuid).containsKey(key)) {
			curr = values.get(uuid).get(key);
			curr.setValue(value);
			if (expiration != 0) {
				curr.setExpiration(expiration);
			}
			Bukkit.getPluginManager().callEvent(new PlayerFieldChangedEvent(Bukkit.getPlayer(uuid), this.key, key, curr, ValueChangeType.CHANGED));
		}
		else {
			curr = new Value(value, expiration);
			values.get(uuid).put(key, curr);
			Bukkit.getPluginManager().callEvent(new PlayerFieldChangedEvent(Bukkit.getPlayer(uuid), this.key, key, curr, ValueChangeType.ADDED));
		}
		
		if (NeoCore.isDebug()) {
			Bukkit.getLogger().log(Level.INFO, "[NeoCore] Changed field " + this.getKey() + "." + key + " for " + uuid + " to " +
					curr.getValue() + ".");
		}
		return true;
	}
	
	public boolean addToField(String key, int v, UUID uuid) {
		// Default to no expiration change
		return addToField(key, v, uuid, 0);
	}
	
	// Returns true if successful
	public boolean addToField(String key, int v, UUID uuid, long expiration) {
		if (!defaults.containsKey(key)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to change field " + this.getKey() + "." + key + " for " + uuid + ". Field doesn't exist.");
			return false;
		}
		
		// Make sure the changed field is an integer
		if (defaults.get(key).getClass() != Integer.class) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to change field " + this.getKey() + "." + key + " for " + uuid + ". Field was not of type Integer.");
			return false;
		}
		
		if (!values.containsKey(uuid)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to change field " + this.getKey() + "." + key + " for " + uuid + ". UUID not initialized.");
			return false;
		}
		
		int original = (int) defaults.get(key);
		if (values.containsKey(uuid)) {
			original = (int) values.get(uuid).get(key).getValue();
		}
		int newValue = original + v;
		
		if (newValue > 100000 || newValue < 1) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to change field " + this.getKey() + "." + key + " for " + uuid + ". Value was out of bounds.");
			return false;
		}
		
		if (newValue == (int) defaults.get(key)) {
			return resetField(key, uuid);
		}

		changedValues.get(uuid).add(key);
		Value curr = values.get(uuid).get(key);
		curr.setValue(newValue);
		Bukkit.getPluginManager().callEvent(new PlayerFieldChangedEvent(Bukkit.getPlayer(uuid), this.key, key, curr, ValueChangeType.CHANGED));
		if (expiration != 0) {
			curr.setExpiration(expiration);
		}
		if (NeoCore.isDebug()) {
			Bukkit.getLogger().log(Level.INFO, "[NeoCore] Added " + v + " to field " + this.getKey() + "." + key + " for " + uuid + ". Before: " +
					original + ", after: " + curr.getValue() + ".");
		}
		return true;
	}
	
	public boolean resetAllFields(UUID uuid) {
		ArrayList<String> fields = new ArrayList<String>(values.get(uuid).keySet());
		Bukkit.getLogger().log(Level.INFO, "[NeoCore] Resetting all fields of " + this.getKey() + " for " + uuid + ".");
		for (String key : fields) {
			resetField(key, uuid);
		}
		return true;
	}
	
	public boolean resetField(String key, UUID uuid) {
		if (!defaults.containsKey(key)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to reset field " + this.getKey() + "." + key + " for " + uuid + ". Field doesn't exist.");
			return false;
		}
		if (!values.containsKey(uuid)) {
			Bukkit.getLogger().log(Level.WARNING, "[NeoCore] Failed to reset field " + this.getKey() + "." + key + " for " + uuid + ". UUID not initialized.");
			return false;
		}
		changedValues.get(uuid).add(key);
		Value removed = values.get(uuid).remove(key);
		Bukkit.getPluginManager().callEvent(new PlayerFieldChangedEvent(Bukkit.getPlayer(uuid), this.key, key, removed, ValueChangeType.REMOVED));
		if (NeoCore.isDebug()) Bukkit.getLogger().log(Level.INFO, "[NeoCore] Reset field " + this.getKey() + "." + key + " for " + uuid + ".");
		return true;
	}
	
	public boolean isHidden() {
		return hidden;
	}
}
