package se.fredsfursten.telepadplugin;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import se.fredsfursten.plugintools.PlayerInfo;

public class Teleer {
	private static Teleer singleton = null;

	private PlayerInfo<Object> playersThatHasBeenInformedToReadTheRules = null;
	private PlayerInfo<TelePadInfo> playersAboutToTele = null;
	private PlayerInfo<Object> playersWithTemporaryTelePause = null;
	private AllTelePads allTelePads = null;
	private JavaPlugin plugin = null;
	private long ticksBeforeTele = 40;
	private long nauseaTicks = 100;
	private long blindnessTicks = 100;

	private Teleer() {
		this.allTelePads = AllTelePads.get();
	}

	static Teleer get()
	{
		if (singleton == null) {
			singleton = new Teleer();
		}
		return singleton;
	}

	void enable(JavaPlugin plugin){
		this.plugin = plugin;
		this.playersThatHasBeenInformedToReadTheRules = new PlayerInfo<Object>();
		this.playersWithTemporaryTelePause = new PlayerInfo<Object>();
		this.playersAboutToTele = new PlayerInfo<TelePadInfo>();
		this.ticksBeforeTele = TelePadPlugin.getPluginConfig().getInt("TicksBeforeTele");
		this.nauseaTicks = TelePadPlugin.getPluginConfig().getInt("NauseaTicks");
		this.blindnessTicks = TelePadPlugin.getPluginConfig().getInt("BlindnessTicks");
	}

	void disable() {
	}

	void maybeTele(Player player, Block pressurePlate) {
		if (pressurePlate.getType() != Material.STONE_PLATE) return;
		Location location = pressurePlate.getLocation();
		TelePadInfo info = this.allTelePads.getByLocation(location);
		if (info == null) {
			mustReadRules(player, true);
			playerCanTele(player, true);
			return;
		}
		
		if (!hasReadRules(player)) {
			maybeTellPlayerToReadTheRules(player);
			return;
		}
		//if (hasTemporaryTelePause(player)) return;
		if (isAboutToTele(player)) return;
		
		float oldWalkSpeed = stopPlayer(player);
		teleSoon(player, info, oldWalkSpeed);
	}

	boolean isAboutToTele(Player player) {
		return this.playersAboutToTele.hasInformation(player);
	}

	void setPlayerIsAboutToTele(Player player, TelePadInfo info, boolean isAboutToTele) {
		if (isAboutToTele) {
			if (isAboutToTele(player)) return;
			this.playersAboutToTele.put(player, info);
		} else {
			if (!isAboutToTele(player)) return;
			this.playersAboutToTele.remove(player);
		}
	}

	private void teleSoon(Player player, TelePadInfo info, float oldWalkSpeed) {
		setPlayerIsAboutToTele(player, info, true);
		ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();
		PotionEffect nausea = null;
		if (this.nauseaTicks > 0) {
			nausea = new PotionEffect(PotionEffectType.CONFUSION, (int) this.nauseaTicks, 4);
			effects.add(nausea);
		}
		final boolean hasNausea = nausea != null;
		PotionEffect blindness = null;
		if (this.blindnessTicks > 0) {
			blindness = new PotionEffect(PotionEffectType.BLINDNESS, (int) this.blindnessTicks, 4);
			effects.add(blindness);
		}
		final boolean hasBlindness = blindness != null;
		player.addPotionEffects(effects);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				if (!isAboutToTele(player)) return;
				setPlayerIsAboutToTele(player, info, false);
				player.setWalkSpeed(oldWalkSpeed);
				if (hasNausea) player.removePotionEffect(PotionEffectType.CONFUSION);
				if (hasBlindness) player.removePotionEffect(PotionEffectType.BLINDNESS);
				tele(player, info);
			}
		}, this.ticksBeforeTele);
	}

	private float stopPlayer(Player player) {
		player.setVelocity(new Vector(0.0, 0.0, 0.0));
		float walkSpeed = player.getWalkSpeed();
		player.setWalkSpeed(0.0F);
		return walkSpeed;
	}

	void tele(Player player, TelePadInfo info) {
		Location targetLocation = info.getTargetLocation();
		player.teleport(targetLocation);
	}

	boolean isGoingDown(Location from, Location to) {
		return to.getY() < from.getY();
	}

	private void maybeTellPlayerToReadTheRules(Player player) {
		if (shouldReadRules(player)) {
			player.sendMessage("Please read the global rules (/rules) to get access to the tele pads.");
			mustReadRules(player, true);
		}
	}

	void playerCanTele(Player player, boolean canTele) {
		if (canTele){
			if (hasTemporaryTelePause(player)) {
				this.playersWithTemporaryTelePause.remove(player);
			}
		} else {
			if (!hasTemporaryTelePause(player)) {
				this.playersWithTemporaryTelePause.put(player, 1);
			}
		}
	}

	private void mustReadRules(Player player, boolean mustReadRules) {
		if (mustReadRules) {
			if (!shouldReadRules(player)) {
				this.playersThatHasBeenInformedToReadTheRules.put(player, 1);
			}
		} else {
			if (shouldReadRules(player)) {
				this.playersThatHasBeenInformedToReadTheRules.remove(player);
			}
		}
	}

	private boolean shouldReadRules(Player player) {
		return !this.playersThatHasBeenInformedToReadTheRules.hasInformation(player);
	}

	private boolean hasTemporaryTelePause(Player player) {
		return this.playersWithTemporaryTelePause.hasInformation(player);
	}

	private boolean hasReadRules(Player player) {
		return player.hasPermission("telepad.tele");
	}
}
