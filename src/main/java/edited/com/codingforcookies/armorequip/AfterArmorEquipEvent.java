package edited.com.codingforcookies.armorequip;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class AfterArmorEquipEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private static ArmorEquipEvent event;

	public AfterArmorEquipEvent(Player player, ArmorEquipEvent event) {
		super(player);
		AfterArmorEquipEvent.event = event;
	}

	public static ArmorEquipEvent getEquipEvent() {
		return event;
	}

	public static final HandlerList getHandlerList() {
		return handlers;
	}

	public final HandlerList getHandlers() {
		return handlers;
	}

}
