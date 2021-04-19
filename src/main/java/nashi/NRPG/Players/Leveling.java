package nashi.NRPG.Players;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import nashi.NRPG.Main;
import nashi.NRPG.API.SkillAPI;
import nashi.NRPG.API.Utils.Status;
import nashi.NRPG.listeners.MythicMobsListener;

public class Leveling {
	public static YamlConfiguration setting;

	public static void init() {
		setting = YamlConfiguration
				.loadConfiguration(new File(Main.getPlugin().getDataFolder() + File.separator + "mobs.yml"));

	}

	public static void check(Player player) {
		try {
			new BukkitRunnable() {
				@Override
				public void run() {
					HashMap<String, Integer> map = MythicMobsListener.MobsKilled.get(player);
					Players.PlayerData pd = Players.Data.get(player);
					Status s = pd.getStatus();
					int level = s.getLvL();
					for (String key : Leveling.setting.getConfigurationSection("" + level).getKeys(false)) {
						boolean b = true;
						for (String type : Leveling.setting.getConfigurationSection("" + level + "." + key)
								.getKeys(false)) {
							int a = Leveling.setting.getInt("" + level + "." + key + "." + type);
							if (!map.containsKey(type)) {
								b = false;
								break;
							}
							if (map.get(type) < a) {
								b = false;
								break;
							}
						}
						if (b) {
							s.setLvL(level + 1);
							map.clear();
							MythicMobsListener.MobsKilled.put(player, map);
							Bukkit.getScheduler().scheduleSyncDelayedTask(SkillAPI.plugin(), new Runnable() {
								public void run() {
									Players.recalcData(player, pd);
								}
							}, 0);
						}
					}
				}
			}.runTaskLaterAsynchronously(Main.getPlugin(), 0);
		} catch (Exception e) {
		}
	}
}
