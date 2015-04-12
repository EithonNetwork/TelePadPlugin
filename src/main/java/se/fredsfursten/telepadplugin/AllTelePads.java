package se.fredsfursten.telepadplugin;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import se.fredsfursten.plugintools.Json;
import se.fredsfursten.plugintools.Misc;

public class AllTelePads {
	private static AllTelePads singleton = null;

	private HashMap<String, TelePadInfo> telePadsByBlock = null;
	private HashMap<String, TelePadInfo> telePadsByName = null;

	private AllTelePads() {
		this.telePadsByBlock = new HashMap<String, TelePadInfo>();
		this.telePadsByName = new HashMap<String, TelePadInfo>();
	}

	static AllTelePads get() {
		if (singleton == null) {
			singleton = new AllTelePads();
		}
		return singleton;
	}

	void add(TelePadInfo info) {
		this.telePadsByBlock.put(info.getBlockHash(), info);
		this.telePadsByName.put(info.getName(), info);
	}

	void remove(TelePadInfo info) {
		this.telePadsByName.remove(info.getName());
		this.telePadsByBlock.remove(info.getBlockHash());
	}

	Collection<TelePadInfo> getAll() {
		return this.telePadsByName.values();
	}

	TelePadInfo getByLocation(Location location) {
		if (this.telePadsByBlock == null) return null;
		String position = TelePadInfo.toBlockHash(location);
		if (!this.telePadsByBlock.containsKey(position)) return null;
		return this.telePadsByBlock.get(position);
	}

	TelePadInfo getByName(String name) {
		if (!this.telePadsByName.containsKey(name)) return null;
		return this.telePadsByName.get(name);
	}

	public void delayedSave(JavaPlugin plugin, double seconds)
	{
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				save();
			}
		}, Misc.secondsToTicks(seconds));		
	}

	public void delayedLoad(JavaPlugin plugin, double seconds)
	{
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				load();
			}
		}, Misc.secondsToTicks(seconds));		
	}

	@SuppressWarnings("unchecked")
	void save() {
		JSONArray telePads = new JSONArray();
		for (TelePadInfo telePadInfo : getAll()) {
			telePads.add(telePadInfo.toJson());
		}
		if ((telePads == null) || (telePads.size() == 0)) {
			Misc.info("No TelePads saved.");
			return;
		}
		Misc.info("Saving %d TelePads", telePads.size());
		Json.save(TelePadPlugin.getStorageFile(), Json.fromBody("TelePad", 1, telePads));
	}

	void load() {
		JSONObject data = Json.load(TelePadPlugin.getStorageFile());
		if (data == null) {
			Misc.debugInfo("File was empty.");
			return;			
		}
		JSONArray array = Json.toBodyPayload(data);
		if ((array == null) || (array.size() == 0)) {
			Misc.debugInfo("The list of TelePads was empty.");
			return;
		}
		Misc.info("Restoring %d TelePads from loaded file.", array.size());
		this.telePadsByBlock = new HashMap<String, TelePadInfo>();
		this.telePadsByName = new HashMap<String, TelePadInfo>();
		for (int i = 0; i < array.size(); i++) {
			this.add(TelePadInfo.fromJson((JSONObject) array.get(i)));
		}
	}
}
