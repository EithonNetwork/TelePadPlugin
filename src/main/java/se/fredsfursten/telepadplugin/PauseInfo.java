package se.fredsfursten.telepadplugin;

import java.time.LocalDateTime;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import se.fredsfursten.plugintools.IJson;
import se.fredsfursten.plugintools.IUuidAndName;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

class PauseInfo implements IJson<PauseInfo>, IUuidAndName {
	private UUID playerId;
	private String playerName;
	private LocalDateTime pauseUntil;

	PauseInfo(Player player, int minimumSecondsToPause)
	{
		if (player != null)
		{
			this.playerId = player.getUniqueId();
			this.playerName = player.getName();
		} else {
			this.playerId = null;
			this.playerName = null;
		}
		this.pauseUntil = LocalDateTime.now().plusSeconds(minimumSecondsToPause);
	}

	PauseInfo() {
	}

	public String getName() {
		return this.playerName;
	}

	public UUID getUniqueId() {
		return this.playerId;
	}
	
	public boolean isPausOver() {
		return LocalDateTime.now().isAfter(this.pauseUntil);
	}

	public String toString() {
		return String.format("%s (%s)", getName(), this.pauseUntil.toString());
	}

	@Override
	public PauseInfo factory() {
		return new PauseInfo();
	}

	@Override
	public void fromJson(Object json) {
		throw new NotImplementedException();
	}

	@Override
	public JSONObject toJson() {
		throw new NotImplementedException();
	}
}
