package se.fredsfursten.telepadplugin;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

class StorageModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private double targetX;
	private double targetY;
	private double targetZ;
	private float targetYaw;
	private float targetPitch;
	private double sourceBlockX;
	private double sourceBlockY;
	private double sourceBlockZ;
	private float sourceYaw;
	private float sourcePitch;
	private UUID worldId;
	private String name;
	private UUID creatorId;
	private String creatorName;
	
	public StorageModel(String name, Location source, Location target, UUID creatorId, String creatorName)
	{
		this.name = name;
		this.sourceBlockX =source.getBlockX();
		this.sourceBlockY = source.getBlockY();
		this.sourceBlockZ = source.getBlockZ();
		this.sourceYaw = source.getYaw();
		this.sourcePitch = source.getPitch();
		this.worldId = source.getWorld().getUID();
		
		// target location
		this.targetX = target.getX();
		this.targetY = target.getY();
		this.targetZ = target.getZ();
		this.targetYaw = target.getYaw();
		this.targetPitch = target.getPitch();
		
		this.creatorId = creatorId;
		this.creatorName = creatorName;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public World getWorld()
	{
		return Bukkit.getServer().getWorld(this.worldId);
	}
	
	public Block getBlock()
	{
		return getLocation().getBlock();
	}
	
	public Location getLocation()
	{
		return new Location(getWorld(), this.sourceBlockX, this.sourceBlockY, this.sourceBlockZ, this.sourceYaw, this.sourcePitch);
	}
	
	public Location getTargetLocation()
	{
		return new Location(getWorld(), this.targetX, this.targetY, this.targetZ, this.targetYaw, this.targetPitch);
	}
	
	public Player getCreator()
	{
		Player creator = Bukkit.getServer().getPlayer(this.creatorId);
		return creator;
	}
	
	public String getCreatorName()
	{
		Player creator = getCreator();
		if (creator != null){
			this.creatorName = creator.getName();
		}
		return this.creatorName;
	}
}
