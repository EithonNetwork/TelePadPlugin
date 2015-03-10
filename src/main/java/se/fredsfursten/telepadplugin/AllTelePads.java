package se.fredsfursten.telepadplugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

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

		ArrayList<StorageModel> telePadStorageList = loadData(plugin);
		if (telePadStorageList == null) return;
		rememberAllData(telePadStorageList);
		this._plugin.getLogger().info(String.format("Loaded %d TelePads", telePadStorageList.size()));
	}

	private ArrayList<StorageModel> loadData(JavaPlugin plugin) {
		File file = TelePadPlugin.getStorageFile();
		if(!file.exists()) return null;
		ArrayList<StorageModel> telePadStorageList = null;
		try {
			telePadStorageList = SavingAndLoadingBinary.load(file);
		} catch (FileNotFoundException e) {
			plugin.getLogger().info("No tele pad data file found.");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			plugin.getLogger().info("Failed to load data.");
			return null;
		}
		return telePadStorageList;
	}

	private void rememberAllData(ArrayList<StorageModel> storageModelList) {
		for (StorageModel storageModel : storageModelList) {
			this.add(TelePadInfo.createTelePadInfo(storageModel));
		}
	}

	void save() {
		ArrayList<StorageModel> telePadStorageList = getAllData();
		boolean success = saveData(telePadStorageList);
		if (success) {
			this._plugin.getLogger().info(String.format("Saved %d TelePads", telePadStorageList.size()));
		} else {
			this._plugin.getLogger().info("Failed to save data.");			
		}
	}

	private ArrayList<StorageModel> getAllData() {
		ArrayList<StorageModel> telePadStorageList = new ArrayList<StorageModel>();
		for (TelePadInfo telePadInfo : getAll()) {
			telePadStorageList.add(telePadInfo.getStorageModel());
		}
		return telePadStorageList;
	}

	private boolean saveData(ArrayList<StorageModel> telePadStorageList) {
		File file = TelePadPlugin.getStorageFile();
		try {
			SavingAndLoadingBinary.save(telePadStorageList, file);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
