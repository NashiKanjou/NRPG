package nashi.NRPG.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillLearnEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String skillname;
	private Player player;

	public SkillLearnEvent(Player player, String skillname) {
		this.player = player;
		this.skillname = skillname;
	}

	public String getSkillName() {
		return this.skillname;
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
