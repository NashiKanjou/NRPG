package nashi.NRPG.CustomEvents;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import nashi.NRPG.listeners.ModifyItem;
import nashi.NRPG.listeners.EventListener.Casting;

public class CastStartEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Casting cast;
	private Player player;

	public CastStartEvent(Player player, Casting cast) {
		this.cast = cast;
		this.player = player;
		ItemStack item = player.getInventory().getItemInOffHand();
		if (item == null || item.getType() == Material.AIR) {
			ItemStack n = new ItemStack(Material.STICK);
			ItemMeta m = n.getItemMeta();
			m.setDisplayName(ChatColor.GOLD + "施法判定用道具");
			PersistentDataContainer pdc = m.getPersistentDataContainer();
			byte a = 100;
			pdc.set(ModifyItem.CastHelp, PersistentDataType.BYTE, a);
			n.setItemMeta(m);
			player.getInventory().setItemInOffHand(n);
		}
	}

	public Casting getCast() {
		return cast;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return b;
	}

	private boolean b;

	@Override
	public void setCancelled(boolean arg0) {
		b = arg0;

	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
