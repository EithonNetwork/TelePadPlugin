package se.fredsfursten.jumppadplugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

public class Jumper {
	private static Jumper singleton = null;

	private PlayerInfo<Object> playersThatHasBeenInformedToReadTheRules = null;
	private PlayerInfo<JumpPadInfo> playersAboutToJump = null;
	private PlayerInfo<Object> playersWithTemporaryJumpPause = null;
	private AllJumpPads allJumpPads = null;
	private JavaPlugin plugin = null;

	private Jumper() {
		this.allJumpPads = AllJumpPads.get();
	}

	static Jumper get()
	{
		if (singleton == null) {
			singleton = new Jumper();
		}
		return singleton;
	}

	void enable(JavaPlugin plugin){
		this.plugin = plugin;
		this.playersThatHasBeenInformedToReadTheRules = new PlayerInfo<Object>();
		this.playersWithTemporaryJumpPause = new PlayerInfo<Object>();
		this.playersAboutToJump = new PlayerInfo<JumpPadInfo>();
	}

	void disable() {
	}

	void maybeJump(Player player, Block pressurePlate) {
		if (pressurePlate.getType() != Material.STONE_PLATE) return;
		Location location = pressurePlate.getLocation();
		JumpPadInfo info = this.allJumpPads.getByLocation(location);
		if (info == null) {
			mustReadRules(player, true);
			playerCanJump(player, true);
			return;
		}
		
		if (!hasReadRules(player)) {
			maybeTellPlayerToReadTheRules(player);
			return;
		}
		if (hasTemporaryJumpPause(player)) return;
		if (isAboutToJump(player)) return;
		
		float oldWalkSpeed = stopPlayer(player);
		jumpSoon(player, info, oldWalkSpeed);
	}

	boolean isAboutToJump(Player player) {
		return this.playersAboutToJump.hasInformation(player);
	}

	void setPlayerIsAboutToJump(Player player, JumpPadInfo info, boolean isAboutToJump) {
		if (isAboutToJump) {
			if (isAboutToJump(player)) return;
			this.playersAboutToJump.put(player, info);
		} else {
			if (!isAboutToJump(player)) return;
			this.playersAboutToJump.remove(player);
		}
	}

	private void jumpSoon(Player player, JumpPadInfo info, float oldWalkSpeed) {
		setPlayerIsAboutToJump(player, info, true);
		ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();
		effects.add(new PotionEffect(PotionEffectType.CONFUSION, 40, 4));
		effects.add(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
		player.addPotionEffects(effects);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				if (!isAboutToJump(player)) return;
				setPlayerIsAboutToJump(player, info, false);
				player.setWalkSpeed(oldWalkSpeed);
				jump(player, info);
			}
		}, 20L);
	}

	private float stopPlayer(Player player) {
		player.setVelocity(new Vector(0.0, 0.0, 0.0));
		float walkSpeed = player.getWalkSpeed();
		player.setWalkSpeed(0.0F);
		return walkSpeed;
	}

	void jump(Player player, JumpPadInfo info) {
		Location targetLocation = info.getTargetLocation();
		player.teleport(targetLocation);
	}

	boolean isGoingDown(Location from, Location to) {
		return to.getY() < from.getY();
	}

	private void maybeTellPlayerToReadTheRules(Player player) {
		if (shouldReadRules(player)) {
			player.sendMessage("Please read the global rules (/rules) to get access to the jump pads.");
			mustReadRules(player, true);
		}
	}

	void playerCanJump(Player player, boolean canJump) {
		if (canJump){
			if (hasTemporaryJumpPause(player)) {
				this.playersWithTemporaryJumpPause.remove(player);
			}
		} else {
			if (!hasTemporaryJumpPause(player)) {
				this.playersWithTemporaryJumpPause.put(player, 1);
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

	private boolean hasTemporaryJumpPause(Player player) {
		return this.playersWithTemporaryJumpPause.hasInformation(player);
	}

	private boolean hasReadRules(Player player) {
		return player.hasPermission("jumppad.jump");
	}
}
