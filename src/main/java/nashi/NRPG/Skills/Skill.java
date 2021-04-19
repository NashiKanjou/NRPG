package nashi.NRPG.Skills;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import nashi.NRPG.Main;
import nashi.NRPG.API.SkillAPI;
import nashi.NRPG.CustomEvents.PreSkillUseEvent;
import nashi.NRPG.CustomEvents.SkillUsedEvent;
import nashi.NRPG.Players.Players;
import nashi.NRPG.Players.Players.PlayerData;

public abstract class Skill {
	public static HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	public static HashMap<String, Skills> skills = new HashMap<String, Skills>();
	public static Set<String> GeneralSkills = new HashSet<String>();
	public static DecimalFormat formatter = new DecimalFormat("#.##");

	public static void cast(String skillname, Player player, boolean isSkillItem) {
		World world = player.getWorld();
		try {
			if (Main.worlds.contains(world)) {
				player.sendMessage(ChatColor.RED + "你無法在這個世界使用技能");
				return;
			}
		} catch (Exception e) {
		}
		if (SkillAPI.Silence.containsKey(player.getUniqueId())) {
			double secondsLeft = SkillAPI.Silence.get(player.getUniqueId()) - System.currentTimeMillis();
			if (secondsLeft > 0) {
				player.sendMessage(ChatColor.RED + "你已被沉默 " + Skill.formatter.format((secondsLeft / 1000)) + " 秒");
				return;
			}
		}
		int cooldownTime = (int) SkillAPI.getPublicSkillInfo(skillname, "Cooldown");
		if (cooldowns.containsKey(player.getUniqueId() + "." + skillname)) {
			double secondsLeft = cooldowns.get(player.getUniqueId() + "." + skillname) - System.currentTimeMillis();
			if (secondsLeft > 0) {
				player.sendMessage(ChatColor.RED + "技能冷卻時間剩餘:" + Skill.formatter.format((secondsLeft / 1000)) + " 秒");
				return;
			}
		}
		PlayerData p = Players.Data.get(player);
		double cost = SkillAPI.getPublicSkillInfo(skillname, "CostMP");
		if (p.getCurrentMP() - cost < 0) {
			player.sendMessage(ChatColor.RED + "魔力不足");
			return;
		}

		PreSkillUseEvent psue = new PreSkillUseEvent(player, skillname, isSkillItem);
		Bukkit.getPluginManager().callEvent(psue);
		if (psue.isCancelled()) {
			if (psue.toCooldown()) {
				cooldowns.put(player.getUniqueId() + "." + skillname, System.currentTimeMillis() + cooldownTime);
			}
			return;
		}

		try {

			cooldowns.put(player.getUniqueId() + "." + skillname, System.currentTimeMillis() + cooldownTime);
			Skills sk = skills.get(skillname);
			Class<?> clazz = sk.skill;
			Object s = clazz.getDeclaredConstructor().newInstance();
			Method skill = clazz.getMethod("skill", Player.class);
			boolean b = (boolean) skill.invoke(s, player);
			if (!b) {
				cooldowns.put(player.getUniqueId() + "." + skillname, (long) (-cooldownTime));
			}

			SkillUsedEvent sue = new SkillUsedEvent(player, skillname, b);
			Bukkit.getPluginManager().callEvent(sue);

		} catch (Exception e) {
			if (e instanceof NoSuchMethodException) {
				return;
			}
			e.printStackTrace();
		}
		return;
	}

	public static class Skills {
		public Method method;
		public Object object;
		public Class<?> skill;

		public Skills(Method l, Object v, Class<?> c) {
			method = l;
			object = v;
			skill = c;
		}

	}

}
