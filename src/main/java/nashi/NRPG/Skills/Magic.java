package nashi.NRPG.Skills;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import nashi.NRPG.API.SkillAPI;
import nashi.NRPG.CustomEvents.SkillLearnEvent;
import nashi.NRPG.Players.Players;
import nashi.NRPG.Players.Players.PlayerData;

public class Magic {
	public static boolean learnSkill(Player player, String skillname, boolean isMakeUp) {
		YamlConfiguration yml = Players.getYaml(player);
		ConfigurationSection sec = Players.skillsetting.getConfigurationSection(skillname);
		yml.set("Skills." + skillname, sec);
		if (isMakeUp) {
			yml.set("Skills." + skillname + ".isMakeUp", true);
		}
		try {
			for (String str : yml.getConfigurationSection("Skills." + skillname + ".Max").getKeys(false)) {
				double ori = yml.getDouble("Skills." + skillname + ".Max." + str);
				PlayerData st = Players.Data.get(player);
				double mod;
				int i = (int) SkillAPI.getPublicSkillInfo(skillname, "ModType");
				switch (i) {
				case 1:
					mod = st.getTotalStatusATK();
					break;
				case 2:
					mod = st.getTotalStatusHP();
					break;
				case 3:
					mod = st.getTotalStatusMP();
					break;
				case 4:
					mod = st.getTotalStatusSpeed();
					break;
				case 5:
					mod = st.getTotalStatusLUK();
					break;
				default:
					mod = 1;
					break;
				}
				yml.set("Skills." + skillname + ".Max." + str,
						ori * yml.getDouble("Skills." + skillname + ".Mod." + str) * mod);
			}
		} catch (Exception e) {
		}

		List<String> list;
		try {
			list = yml.getStringList("UniqeSkills");
		} catch (Exception e) {
			list = new ArrayList<String>();
		}
		if (!list.contains(skillname)) {
			list.add(skillname);
			yml.set("UniqeSkills", list);
			yml.set("UsedSlots", yml.getInt("UsedSlots") + 1);
			SkillLearnEvent se = new SkillLearnEvent(player, skillname);
			Bukkit.getServer().getPluginManager().callEvent(se);
			return true;
		} else {
			return false;
		}
	}

	public static boolean learnSkill(Player player, String skillname) {
		YamlConfiguration yml = Players.getYaml(player);
		ConfigurationSection sec = Players.skillsetting.getConfigurationSection(skillname);
		yml.set("Skills." + skillname, sec);
		try {
			for (String str : yml.getConfigurationSection("Skills." + skillname + ".Max").getKeys(false)) {
				double ori = yml.getDouble("Skills." + skillname + ".Max." + str);
				PlayerData st = Players.Data.get(player);
				double mod;
				int i = (int) SkillAPI.getPublicSkillInfo(skillname, "ModType");
				switch (i) {
				case 1:
					mod = st.getTotalStatusATK();
					break;
				case 2:
					mod = st.getTotalStatusHP();
					break;
				case 3:
					mod = st.getTotalStatusMP();
					break;
				case 4:
					mod = st.getTotalStatusSpeed();
					break;
				case 5:
					mod = st.getTotalStatusLUK();
					break;
				default:
					mod = 1;
					break;
				}
				yml.set("Skills." + skillname + ".Max." + str,
						ori * yml.getDouble("Skills." + skillname + ".Mod." + str) * mod);
			}
		} catch (Exception e) {
		}

		List<String> list;
		try {
			list = yml.getStringList("UniqeSkills");
		} catch (Exception e) {
			list = new ArrayList<String>();
		}
		if (!list.contains(skillname)) {
			list.add(skillname);
			yml.set("UniqeSkills", list);
			yml.set("UsedSlots", yml.getInt("UsedSlots") + 1);
			SkillLearnEvent se = new SkillLearnEvent(player, skillname);
			Bukkit.getServer().getPluginManager().callEvent(se);
			return true;
		} else {
			return false;
		}
	}

	public static void learnGenSkill(Player player, String skillname) {
		YamlConfiguration yml = Players.getYaml(player);
		ConfigurationSection sec = Players.skillsetting.getConfigurationSection(skillname);
		yml.set("Skills." + skillname, sec);
		List<String> list;
		try {
			list = yml.getStringList("UniqeSkills");
		} catch (Exception e) {
			list = new ArrayList<String>();
		}
		list.add(skillname);
		yml.set("UniqeSkills", list);
		try {
			for (String str : yml.getConfigurationSection("Skills." + skillname + ".Max").getKeys(false)) {
				double ori = yml.getDouble("Skills." + skillname + ".Max." + str);
				PlayerData st = Players.Data.get(player);
				double mod;
				int i = (int) SkillAPI.getPublicSkillInfo(skillname, "ModType");
				switch (i) {
				case 1:
					mod = st.getTotalStatusATK();
					break;
				case 2:
					mod = st.getTotalStatusHP();
					break;
				case 3:
					mod = st.getTotalStatusMP();
					break;
				case 4:
					mod = st.getTotalStatusSpeed();
					break;
				case 5:
					mod = st.getTotalStatusLUK();
					break;
				default:
					mod = 1;
					break;
				}
				yml.set("Skills." + skillname + ".Max." + str,
						ori * yml.getDouble("Skills." + skillname + ".Mod." + str) * mod);
			}
		} catch (Exception e) {
		}
		SkillLearnEvent se = new SkillLearnEvent(player, skillname);
		Bukkit.getServer().getPluginManager().callEvent(se);
	}

	public static void check(Player player) {
		Set<String> add = new HashSet<String>();
		YamlConfiguration y = Players.getYaml(player);
		List<String> set;
		try {
			set = y.getStringList("UniqeSkills");
		} catch (NullPointerException e) {
			set = new ArrayList<String>();
		}
		try {
			for (String str : Skill.GeneralSkills) {
				if (!set.contains(str)) {
					add.add(str);
				}
			}
		} catch (NullPointerException e) {
		}

		for (int i = 0; i <= 9; i++) {
			try {
				if (!set.contains(y.getString("Slot." + i))) {
					y.set("Slot." + i, null);
				}
			} catch (NullPointerException e) {
			}
		}

		try {
			for (String str : Players.Data.get(player).getRace().getSkillList()) {
				if (!set.contains(str)) {
					add.add(str);
				}
			}
		} catch (NullPointerException e) {
		}
		try {
			for (String str : add) {
				learnGenSkill(player, str);
			}
		} catch (NullPointerException e) {
		}
	}
}
