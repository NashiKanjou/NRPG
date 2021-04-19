package nashi.NRPG.listeners;

import java.util.HashSet;
import java.util.LinkedHashSet;

import org.bukkit.Sound;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import nashi.NRPG.API.SkillAPI;

public class ProtocolLibEvent {
	public static HashSet<Sound> sounds = new LinkedHashSet<Sound>(500);

	public static void run() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(SkillAPI.plugin(),
				ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.getPacketType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
					Sound sound = event.getPacket().getSoundEffects().read(0);
					if (!sound.name().contains("ENTITY_PLAYER_ATTACK")) {
						return;
					}
					if (sounds.contains(sound)) {
						return;
					}
					event.setCancelled(true);
				}
			}
		});
	}

	public static void setWhitelist(Sound sound) {
		ProtocolLibEvent.sounds.add(sound);
	}
}
