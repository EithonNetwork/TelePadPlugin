package se.fredsfursten.telepadplugin;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import se.fredsfursten.plugintools.Json;

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

	TelePadInfo(String name, Location sourceLocation, Location targetLocation, UUID creatorId, String creatorName)
	{
		this.name = name;
		this.sourceLocation = sourceLocation;
		this.targetLocation = targetLocation;
		this.creatorId = creatorId;
		this.creatorName = creatorName;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("name", this.name);
		json.put("sourceLocation", Json.fromLocation(this.sourceLocation, true));
		json.put("targetLocation", Json.fromLocation(this.targetLocation, true));
		json.put("creator", Json.fromPlayer(this.creatorId, this.creatorName));
		return json;
	}

	public static TelePadInfo fromJson(JSONObject json)
	{
		String name = (String) json.get("name");
		Location sourceLocation = Json.toLocation((JSONObject)json.get("sourceLocation"), null);
		Location targetLocation = Json.toLocation((JSONObject)json.get("targetLocation"), null);
		UUID creatorId = Json.toPlayerId((JSONObject) json.get("creator"));
		String creatorName = Json.toPlayerName((JSONObject) json.get("creator"));
		
		return new TelePadInfo(name, sourceLocation, targetLocation, creatorId, creatorName);
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

	Location getSourceAsTarget() {
		Location location = this.sourceLocation.clone();
		location.setX(location.getX() + 0.5);
		location.setZ(location.getZ() + 0.5);
		return location;
	}

	String getBlockHash() {
		return TelePadInfo.toBlockHash(this.sourceLocation);
	}

	static String toBlockHash(Location location)
	{
		if (location == null) return null;
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

	public String toString() {
		return String.format("%s (%s): from %s toy %s", getName(), getCreatorName(), getSource().getBlock().toString(), getTargetLocation().toString());
	}
}
