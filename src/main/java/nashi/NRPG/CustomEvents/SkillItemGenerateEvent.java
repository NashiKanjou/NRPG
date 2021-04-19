package nashi.NRPG.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class SkillItemGenerateEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private ItemStack item;
	private String skillname;

	public SkillItemGenerateEvent(Player player, ItemStack item, String skillname) {
		this.player = player;
		this.item = item;
		this.skillname = skillname;
	}

	public String getSkillName() {
		return skillname;
	}

	public ItemStack getItem() {
		return this.item;
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
