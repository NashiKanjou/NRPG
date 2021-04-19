package nashi.NRPG.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nashi.NRPG.Players.Races.Race;

public class RaceChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Race race;
	private Race old;

	public RaceChangeEvent(Player player, Race old, Race race) {
		this.player = player;
		this.race = race;
		this.old = old;
	}

	public Race getOldRace() {
		return old;
	}

	public Race getRace() {
		return this.race;
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
