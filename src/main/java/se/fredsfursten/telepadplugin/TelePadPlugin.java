package se.fredsfursten.telepadplugin;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import se.fredsfursten.plugintools.PluginConfig;
import se.fredsfursten.telepadplugin.Teleer;

public final class TelePadPlugin extends JavaPlugin implements Listener {
	private static File telePadStorageFile;
	private static PluginConfig configuration;

	@Override
	public void onEnable() {
		if (configuration == null) {
			configuration = new PluginConfig(this, "config.yml");
		} else {
			configuration.load();
		}

		telePadStorageFile = new File(getDataFolder(), "telepads.bin");
		getServer().getPluginManager().registerEvents(this, this);		
		Teleer.get().enable(this);
		Commands.get().enable(this);
	}

	@Override
	public void onDisable() {
		Teleer.get().disable();
		Commands.get().disable();
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.isCancelled()) return;
		if (event.getAction() != Action.PHYSICAL) return;
		Player player = event.getPlayer();
		Block pressurePlate = event.getClickedBlock();
		if (pressurePlate == null) return;
		if (pressurePlate.getType() != Material.STONE_PLATE) return;
		Teleer.get().maybeTele(player, pressurePlate);
	}

	@EventHandler
	public void listenToCommands(PlayerCommandPreprocessEvent event) {
		Commands.get().listenToCommands(event.getPlayer(), event.getMessage());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player!");
			return false;
		}
		if (args.length < 1) {
			sender.sendMessage("Incomplete command...");
			return false;
		}

		Player player = (Player) sender;

		String command = args[0].toLowerCase();
		if (command.equals("add")) {
			Commands.get().addCommand(player, args);
		} else if (command.equals("target")) {
			Commands.get().linkCommand(player, args);
		} else if (command.equals("remove")) {
			Commands.get().removeCommand(player, args);
		} else if (command.equals("list")) {
			Commands.get().listCommand(player);
		} else if (command.equals("goto")) {
			Commands.get().gotoCommand(player, args);
		} else {
			sender.sendMessage("Could not understand command.");
			return false;
		}
		return true;
	}



	public static File getStorageFile()
	{
		return telePadStorageFile;
	}

	public static FileConfiguration getPluginConfig()
	{
		return configuration.getFileConfiguration();
	}}
