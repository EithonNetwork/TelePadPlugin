package se.fredsfursten.telepadplugin;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

class TelePadInfo {
	private Location sourceLocation;
	private Location targetLocation;
	private String name;
	private UUID creatorId;
	private String creatorName;

	TelePadInfo(String name, Location sourceLocation, Location targetLocation, Player creator)
	{
		this.name = name;
		this.sourceLocation = sourceLocation;
		this.targetLocation = targetLocation;
		if (creator != null)
		{
			this.creatorId = creator.getUniqueId();
			this.creatorName = creator.getName();
		} else {
			this.creatorId = null;
			this.creatorName = null;
		}
	}

	public static TelePadInfo createTelePadInfo(StorageModel storageModel)
	{
		Player creator = storageModel.getCreator();
		return new TelePadInfo(storageModel.getName(), storageModel.getLocation(), storageModel.getTargetLocation(), creator);
	}

	Location getTargetLocation() {
		return this.targetLocation;
	}

	public void setTarget(Location location) {
		this.targetLocation = location;
	}

	String getName() {
		return this.name;
	}

	Location getSource() {
		return this.sourceLocation;
	}

	String getBlockHash() {
		return TelePadInfo.toBlockHash(this.sourceLocation);
	}

	static String toBlockHash(Location location)
	{
		return toBlockHash(location.getBlock());
	}

	static String toBlockHash(Block block)
	{
		return String.format("%d;%d;%d", block.getX(), block.getY(), block.getZ());
	}

	Player getCreator()
	{
		return Bukkit.getServer().getPlayer(this.creatorId);
	}

	String getCreatorName() {
		return this.creatorName;
	}

	UUID getCreatorId() {
		return this.creatorId;
	}

	StorageModel getStorageModel() {
		return new StorageModel(getName(), getSource(), getTargetLocation(), getCreatorId(), getCreatorName());

	}

	public String toString() {
		return String.format("%s (%s): from %s toy %s", getName(), getCreatorName(), getSource().getBlock().toString(), getTargetLocation().toString());
	}
}
