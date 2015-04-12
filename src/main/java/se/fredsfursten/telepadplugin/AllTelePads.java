package se.fredsfursten.telepadplugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javafx.scene.paint.RadialGradient;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import se.fredsfursten.plugintools.Json;
import se.fredsfursten.plugintools.Misc;
import se.fredsfursten.plugintools.SavingAndLoadingBinary;

public class AllTelePads {
	private static AllTelePads singleton = null;

	private HashMap<String, TelePadInfo> telePadsByBlock = null;
	private HashMap<String, TelePadInfo> telePadsByName = null;
	private JavaPlugin _plugin = null;

	private AllTelePads() {
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

	void load(JavaPlugin plugin) {
		this._plugin = plugin;

		this.telePadsByBlock = new HashMap<String, TelePadInfo>();
		this.telePadsByName = new HashMap<String, TelePadInfo>();

		JSONArray jsonArray = Json.loadDataArray(TelePadPlugin.getStorageFile());
		if ((jsonArray != null) && (jsonArray.size() > 0)) {
			Misc.info("Loaded %d TelePads", jsonArray.size());
		} else {
			Misc.info("No TelePads loaded.");
			return;
		}
		rememberAllData(jsonArray);
	}

	private void rememberAllData(JSONArray telePads) {
		for (int i = 0; i < telePads.size(); i++) {
			this.add(TelePadInfo.createTelePadInfo((JSONObject) telePads.get(i)));
		}
	}

	void save() {
		int telePads = Json.saveData(TelePadPlugin.getStorageFile(), getAllData());
		if (telePads > 0) {
			Misc.info("Saved %d TelePads", telePads);
		} else {
			Misc.info("Saved no TelePads.");		
		}
	}

	private JSONArray getAllData() {
		JSONArray telePads = new JSONArray();
		for (TelePadInfo telePadInfo : getAll()) {
			telePads.add(telePadInfo.toJson());
		}
		return telePads;
	}
}
