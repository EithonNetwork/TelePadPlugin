package se.fredsfursten.jumppadplugin;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class StorageModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private double targetX;
	private double targetY;
	private double targetZ;
	private double blockX;
	private double blockY;
	private double blockZ;
	private UUID worldId;
	private String name;
	private UUID creatorId;
	private String creatorName;
	
	public StorageModel(String name, Block block, Location target, UUID creatorId, String creatorName)
	{
		this.name = name;
		this.blockX =block.getX();
		this.blockY = block.getY();
		this.blockZ = block.getZ();
		this.worldId = block.getWorld().getUID();
		this.targetX = target.getX();
		this.targetY = target.getY();
		this.targetZ = target.getZ();
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
		return new Location(getWorld(), this.blockX, this.blockY, this.blockZ);
	}
	
	public Location getTargetLocation()
	{
		return new Location(getWorld(), this.targetX, this.targetY, this.targetZ);
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
