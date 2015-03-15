package se.fredsfursten.telepadplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.Misc;

public class Commands {
	private static Commands singleton = null;
	private static final String ADD_COMMAND = "/telepad add <name>";
	private static final String GOTO_COMMAND = "/telepad goto <name>";
	private static final String RELOAD_COMMAND = "/telepad reload";
	private static final String REMOVE_COMMAND = "/telepad remove <name>";
	private static final String LINK_COMMAND = "/telepad link <name 1> <name 2>";
	private static final String RULES_COMMAND_BEGINNING = "/rules";

	private JavaPlugin plugin = null;
	private AllTelePads allTelePads = null;

	private Commands() {
		this.allTelePads = AllTelePads.get();
	}

	static Commands get()
	{
		if (singleton == null) {
			singleton = new Commands();
		}
		return singleton;
	}

	void enable(JavaPlugin plugin){
		this.plugin = plugin;
		this.allTelePads.load(plugin);
	}

	void disable() {
		this.allTelePads.save();
	}

	void addCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "telepad.add")) return;
		if (!arrayLengthIsWithinInterval(args, 2, 2)) {
			player.sendMessage(ADD_COMMAND);
			return;
		}

		String name = args[1];
		if (!verifyNameIsNew(player, name)) return;	

		double upSpeed = 0.0;
		double forwardSpeed = 0.0;

		createOrUpdateTelePad(player, name, upSpeed, forwardSpeed);
		player.sendMessage(String.format("TelePad %s has been added.", name));
		player.sendMessage(String.format("Now add a target destination with '/telepad target %s'.", name));
	}

	void removeCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "telepad.remove")) return;
		if (!arrayLengthIsWithinInterval(args, 2, 2)) {
			player.sendMessage(REMOVE_COMMAND);
			return;
		}
		String name = args[1];
		TelePadInfo info = this.allTelePads.getByName(name);
		if (info == null)
		{
			player.sendMessage("Unknown telepad: " + name);
			return;	
		}
		this.allTelePads.remove(info);
		player.sendMessage(String.format("TelePad %s has been removed.", name));
	}

	void linkCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "telepad.target")) return;
		if (!arrayLengthIsWithinInterval(args, 3, 3)) {
			player.sendMessage(LINK_COMMAND);
			return;
		}
		String name1 = args[1];
		TelePadInfo info1 = this.allTelePads.getByName(name1);
		if (info1 == null)
		{
			player.sendMessage("Unknown telepad: " + name1);
			return;	
		}
		String name2 = args[2];
		TelePadInfo info2 = this.allTelePads.getByName(name2);
		if (info2 == null)
		{
			player.sendMessage("Unknown telepad: " + name2);
			return;	
		}
		
		info1.setTarget(info2.getLocation());
		info2.setTarget(info1.getLocation());
		player.sendMessage(String.format("TelePad %s and %s now have each other as target destinations.", name1, name2));
	}

	void gotoCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "telepad.goto")) return;
		if (args.length < 2) {
			player.sendMessage(GOTO_COMMAND);
			return;
		}
		String name = args[1];
		TelePadInfo info = this.allTelePads.getByName(name);
		if (info == null)
		{
			player.sendMessage("Unknown telepad: " + name);
			return;			
		}
		player.teleport(info.getLocation());
		// Temporarily disable jump for this player to avoid an immediate jump at the jump pad
		Teleer.get().playerCanTele(player, false);
		player.sendMessage(String.format("You have been teleported to TelePad %s.", name));
	}

	void reloadCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "telepad.reload")) return;
		if (args.length < 1) {
			player.sendMessage(RELOAD_COMMAND);
			return;
		}
		Teleer.get().loadConfiguration();
		player.sendMessage("The configuration file has been reloaded.");
	}

	void listCommand(Player player)
	{
		if (!verifyPermission(player, "telepad.list")) return;

		player.sendMessage("Tele pads:");
		for (TelePadInfo info : this.allTelePads.getAll()) {
			player.sendMessage(info.toString());
		}
	}

	private boolean verifyNameIsNew(Player player, String name) {
		TelePadInfo info = this.allTelePads.getByName(name);
		if (info != null)
		{
			player.sendMessage("Telepad already exists: " + name);
			return true;		
		}
		return true;
	}

	private void createOrUpdateTelePad(Player player, String name, double upSpeed, double forwardSpeed) {
		Block pressurePlate = Misc.getFirstBlockOfMaterial(Material.STONE_PLATE, player.getLocation(), 3);
		if (pressurePlate == null) {
			player.sendMessage("No stone plate within 3 blocks");
			return;
		}
		
		Location location;
		location = pressurePlate.getLocation();
		try {
			TelePadInfo newInfo = new TelePadInfo(name, location, location, player);
			this.allTelePads.add(newInfo);
			if (player != null) {
				Teleer.get().playerCanTele(player, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private boolean verifyPermission(Player player, String permission)
	{
		if (player.hasPermission(permission)) return true;
		player.sendMessage("You must have permission " + permission);
		return false;
	}

	void listenToCommands(Player player, String message) {
		if (message.toLowerCase().startsWith(RULES_COMMAND_BEGINNING))
		{
			player.sendMessage("Getting permission");
			player.addAttachment(this.plugin, "telepad.jump", true);
		}
	}

	private boolean arrayLengthIsWithinInterval(Object[] args, int min, int max) {
		return (args.length >= min) && (args.length <= max);
	}
}
