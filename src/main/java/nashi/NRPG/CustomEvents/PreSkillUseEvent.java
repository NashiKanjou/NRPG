package nashi.NRPG.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PreSkillUseEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private String SkillName;
	private boolean cd;
	private boolean isCancelled;
	private boolean isitem;

	public PreSkillUseEvent(Player player, String skillname, boolean isSkillItem) {
		this.player = player;
		this.SkillName = skillname;
		this.isitem = isSkillItem;
	}

	public boolean isCastByItem() {
		return this.isitem;
	}

	public boolean toCooldown() {
		return this.cd;
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

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}
}
