package se.fredsfursten.telepadplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.Misc;

public class Commands {
	private static Commands singleton = null;
	private static final String ADD_COMMAND = "/telepad add <name> <up speed> [<forward speed>]";
	private static final String GOTO_COMMAND = "/telepad goto <name>";
	private static final String REMOVE_COMMAND = "/telepad remove <name>";
	private static final String TARGET_COMMAND = "/telepad target <name>";
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

	void targetCommand(Player player, String[] args)
	{
		if (!verifyPermission(player, "telepad.target")) return;
		if (!arrayLengthIsWithinInterval(args, 2, 2)) {
			player.sendMessage(TARGET_COMMAND);
			return;
		}
		String name = args[1];
		TelePadInfo info = this.allTelePads.getByName(name);
		if (info == null)
		{
			player.sendMessage("Unknown telepad: " + name);
			return;	
		}
		
		info.setTarget(player.getLocation());
		player.sendMessage(String.format("TelePad %s now has a target destination", name));
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
