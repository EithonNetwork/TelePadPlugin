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

import se.fredsfursten.plugintools.CoolDown;
import se.fredsfursten.plugintools.PlayerCollection;
import se.fredsfursten.plugintools.PluginConfig;

public class Teleer {
	private static Teleer singleton = null;

	private PlayerCollection<TelePadInfo> playersAboutToTele = null;
	CoolDown _coolDown = null;
	private AllTelePads allTelePads = null;
	private JavaPlugin plugin = null;
	private long ticksBeforeTele;
	private long nauseaTicks;
	private long slownessTicks;
	private long blindnessTicks;
	private long disableEffectsAfterTicks;
	private int secondsToPauseBeforeNextTeleport;

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
		loadConfiguration();
		this._coolDown = new CoolDown("telepad", this.secondsToPauseBeforeNextTeleport);
		this.playersAboutToTele = new PlayerCollection<TelePadInfo>(new TelePadInfo());
	}

	void disable() {
	}

	void maybeTele(Player player, Block pressurePlate) {
		if (pressurePlate.getType() != Material.STONE_PLATE) return;
		Location location = pressurePlate.getLocation();
		TelePadInfo info = this.allTelePads.getByLocation(location);
		if (info == null) return;
		
		/*
		if (!hasReadRules(player)) {
			maybeTellPlayerToReadTheRules(player);
			return;
		}
		*/
		if (this._coolDown.isInCoolDownPeriod(player)) return;
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
		final float nextWalkSpeed =  (oldWalkSpeed > 0.0F ? oldWalkSpeed : 1.0F);
		setPlayerIsAboutToTele(player, info, true);
		ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();
		PotionEffect nausea = null;
		if (this.nauseaTicks > 0) {
			nausea = new PotionEffect(PotionEffectType.CONFUSION, (int) this.nauseaTicks, 4);
			effects.add(nausea);
		}
		final boolean hasNausea = nausea != null;
		PotionEffect slowness = null;
		if (this.nauseaTicks > 0) {
			slowness = new PotionEffect(PotionEffectType.SLOW, (int) this.slownessTicks, 4);
			effects.add(slowness);
		}
		final boolean hasSlowness = slowness != null;
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
				player.setWalkSpeed(nextWalkSpeed);
				if (hasNausea) player.removePotionEffect(PotionEffectType.CONFUSION);
				if (hasSlowness) player.removePotionEffect(PotionEffectType.SLOW);
				if (hasBlindness) player.removePotionEffect(PotionEffectType.BLINDNESS);
			}
		}, this.disableEffectsAfterTicks);
		final Teleer instance = this;
		scheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				if (!isAboutToTele(player)) return;
				setPlayerIsAboutToTele(player, info, false);
				instance._coolDown.addPlayer(player);
				tele(player, info);
			}
		}, this.ticksBeforeTele);
	}

	private float stopPlayer(Player player) {
		float walkSpeed = player.getWalkSpeed();
		player.setWalkSpeed(0.0F);
		player.setVelocity(new Vector(0.0, 0.0, 0.0));
		return walkSpeed;
	}

	void tele(Player player, TelePadInfo info) {
		Location targetLocation = info.getTargetLocation();
		player.teleport(targetLocation);
	}

	public void loadConfiguration() {
		PluginConfig config = PluginConfig.get(this.plugin);
		this.ticksBeforeTele = config.getInt("TeleportAfterTicks", 0);
		this.nauseaTicks = config.getInt("NauseaTicks", 0);
		this.slownessTicks = config.getInt("SlownessTicks", 0);
		this.blindnessTicks = config.getInt("BlindnessTicks", 0);
		this.disableEffectsAfterTicks = config.getInt("DisableEffectsAfterTicks", 0);
		this.secondsToPauseBeforeNextTeleport = config.getInt("SecondsToPauseBeforeNextTeleport", 5);
	}
}
