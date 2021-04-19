package nashi.NRPG.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillUsedEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private String SkillName;
	private boolean success;

	public SkillUsedEvent(Player player, String skillname, boolean isSuccess) {
		this.player = player;
		this.SkillName = skillname;
		this.success = isSuccess;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public String getSkillName() {
		return this.SkillName;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
