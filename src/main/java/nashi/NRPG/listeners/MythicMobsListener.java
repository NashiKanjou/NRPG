package nashi.NRPG.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import nashi.NRPG.Players.Leveling;

public class MythicMobsListener implements Listener {
	public static HashMap<Player, HashMap<String, Integer>> MobsKilled = new HashMap<Player, HashMap<String, Integer>>();
	public static HashMap<ActiveMob, HashMap<String, Double>> DamageDelt = new HashMap<ActiveMob, HashMap<String, Double>>();
	public static MythicMobs mythicMobs = MythicMobs.inst();
	public static MobManager mobManager = mythicMobs.getMobManager();
	public static final String total = "total";
	public static final double amount = 0.3;

	@EventHandler
	public void MyThicMobsDie(MythicMobDeathEvent event) {
		String type = event.getMobType().getInternalName();
		HashMap<String, Double> map = DamageDelt.get(event.getMob());
		double delt = map.get(total);
		for (String str : map.keySet()) {
			if (str.equals(total)) {
				continue;
			}
			if (map.get(str) / delt < amount) {
				continue;
			}
			OfflinePlayer offp = Bukkit.getServer().getOfflinePlayer(UUID.fromString(str));
			if (offp.isOnline()) {
				Player player = offp.getPlayer();
				HashMap<String, Integer> mk;
				if (MobsKilled.containsKey(player)) {
					mk = MobsKilled.get(player);
				} else {
					mk = new HashMap<String, Integer>();
				}
				int n;
				if (mk.containsKey(type)) {
					n = mk.get(type);
				} else {
					n = 0;
				}
				mk.put(type, n + 1);
				MobsKilled.put(player, mk);
				Leveling.check(player);
			}

		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void DmgDeltCalc(EntityDamageEvent event) {
		ActiveMob mob = mobManager.getMythicMobInstance(event.getEntity());
		if (mob == null) {
			return;
		}
		HashMap<String, Double> map;
		if (!DamageDelt.containsKey(mob)) {
			map = new HashMap<String, Double>();
		} else {
			map = DamageDelt.get(mob);
		}
		double damage = event.getDamage();
		double c;
		if (map.containsKey(total)) {
			c = map.get(total);
		} else {
			c = 0;
		}
		map.put(total, c + damage);
		DamageDelt.put(mob, map);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void DmgDeltCalc(EntityDamageByEntityEvent event) {
		ActiveMob mob = mobManager.getMythicMobInstance(event.getEntity());
		if (mob == null) {
			return;
		}
		HashMap<String, Double> map;
		if (!DamageDelt.containsKey(mob)) {
			map = new HashMap<String, Double>();
		} else {
			map = DamageDelt.get(mob);
		}
		double damage = event.getDamage();
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			double c;
			if (map.containsKey(player.getUniqueId().toString())) {
				c = map.get(player.getUniqueId().toString());
			} else {
				c = 0;
			}
			map.put(player.getUniqueId().toString(), c + damage);
		}
		DamageDelt.put(mob, map);
	}

}
