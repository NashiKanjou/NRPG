package nashi.NRPG.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nashi.NRPG.listeners.EventListener.Casting;

public class CastEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Casting cast;
	private Player player;
	private boolean b;

	public CastEvent(Player player, Casting cast) {
		this.cast = cast;
		this.player = player;
	}

	public Casting getCast() {
		return cast;
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
		// TODO Auto-generated method stub
		return b;
	}

	@Override
	public void setCancelled(boolean arg0) {
		b = arg0;

	}
}
