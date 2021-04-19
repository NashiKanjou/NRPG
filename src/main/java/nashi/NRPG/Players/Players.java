package nashi.NRPG.Players;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import nashi.NRPG.Skills.Cast;
import nashi.NRPG.Skills.Magic;
import nashi.NRPG.listeners.EventListener;
import nashi.NRPG.listeners.ModifyItem;
import nashi.NRPG.listeners.MythicMobsListener;
import nashi.NRPG.Main;
import nashi.NRPG.API.SkillAPI;
import nashi.NRPG.API.Utils;
import nashi.NRPG.API.Utils.Status;
import nashi.NRPG.Commands.RaceCommand;
import nashi.NRPG.CustomEvents.RaceChangeEvent;
import nashi.NRPG.Items.CreateItem;
import nashi.NRPG.Players.Races.Race;

public class Players {
	public static HashMap<Player, YamlConfiguration> file = new HashMap<Player, YamlConfiguration>();
	public static YamlConfiguration skillsetting;
	public static YamlConfiguration mobsetting;
	public static HashMap<Player, PlayerData> Data = new HashMap<Player, PlayerData>();

	public static YamlConfiguration getYaml(Player player) {
		return file.get(player);
	}

	public static void loadYaml(Player player) {
		// File p = new File(
		// Main.getPlugin().getDataFolder() + File.separator + "players" +
		// File.separator + "players.yml");

		File f = new File(Main.getPlugin().getDataFolder() + File.separator + "players" + File.separator
				+ player.getUniqueId().toString() + ".yml");
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		if (!f.exists()) {
			try {
				f.createNewFile();
				YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
				y.set("ID", player.getName());
				y.set("CastSpeed", 10);
				y.set("Race", Races.defaultRace.getName());
				Status stat = Utils.getStatusFromInt(player.getUniqueId().hashCode());
				Utils.setPlayerStatus(player, stat);
				y.set("Status.ATK", stat.getATK());
				y.set("Status.SKL", stat.getSKL());
				y.set("Status.AR", stat.getArmor());
				y.set("Status.AT", stat.getArmorToughness());
				y.set("Status.Speed", stat.getSpeed());
				y.set("Status.HP", stat.getHP());
				y.set("Status.MP", stat.getMP());
				y.set("Status.LUK", stat.getLucky());
				y.set("Status.LVL", stat.getLvL());
				y.set("UniqueSkillSlots",
						Utils.MaxSlot
								- (stat.getSKL() + stat.getATK() + stat.getHP() + stat.getLucky() + stat.getSpeed()
										+ stat.getMP() + stat.getArmor() + stat.getArmorToughness()) / Utils.STATRAND);
				y.set("UsedSlots", 0);
				y.set("Exterior", false);
				file.put(player, y);
				Cast.castspeed.put(player, 10);
				RaceCommand.disable(player);
				y.save(f);
				Data.put(player, new PlayerData(player, stat));
				y.set("MP", Data.get(player).getCurrentMP());
				if ((stat.getATK() + stat.getSKL() + stat.getHP() + stat.getLucky() + stat.getSpeed() + stat.getMP()
						+ stat.getArmor() + stat.getArmorToughness()) < ((Utils.STATRAND) + 1) * 8 * Utils.MakeUpMod) {
					Magic.learnSkill(player, Utils.getRandomMakeUpSkills(), true);
				}
				/*
				 * if (!p.exists()) { try { p.createNewFile(); YamlConfiguration py =
				 * YamlConfiguration.loadConfiguration(p); if
				 * (!py.contains(player.getUniqueId().toString())) {
				 * py.set(player.getUniqueId().toString(), true); py.save(p); if ((stat.getATK()
				 * + stat.getHP() + stat.getLucky() + stat.getSpeed() + stat.getMP() +
				 * stat.getArmor() + stat.getArmorToughness()) < ((Utils.STATRAND) + 1) * 7
				 * Utils.MakeUpMod) { Magic.learnSkill(player, Utils.getRandomMakeUpSkills(),
				 * true); } } } catch (IOException e) { e.printStackTrace(); } } else {
				 * YamlConfiguration py = YamlConfiguration.loadConfiguration(p); if
				 * (!py.contains(player.getUniqueId().toString())) {
				 * py.set(player.getUniqueId().toString(), true); py.save(p); if ((stat.getATK()
				 * + stat.getHP() + stat.getLucky() + stat.getSpeed() + stat.getMP() +
				 * stat.getArmor() + stat.getArmorToughness()) < ((Utils.STATRAND) + 1) * 7
				 * Utils.MakeUpMod) { Magic.learnSkill(player, Utils.getRandomMakeUpSkills()); }
				 * } }
				 */
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
			file.put(player, y);
			Cast.castspeed.put(player, y.getInt("CastSpeed"));
			int lvl = y.getInt("Status.LVL");
			if (lvl <= 0) {
				lvl = 1;
			}
			Status stat = new Status(y.getInt("Status.SKL"), y.getInt("Status.AR"), y.getInt("Status.AT"),
					y.getInt("Status.ATK"), y.getInt("Status.Speed"), y.getInt("Status.HP"), y.getInt("Status.MP"),
					y.getInt("Status.LUK"), lvl);
			Utils.setPlayerStatus(player, stat);
			try {
				Data.put(player, new PlayerData(player, Races.getRace(y.getString("Race")), stat, y.getDouble("MP")));
			} catch (Exception e) {
				Data.put(player,
						new PlayerData(player, Races.getRace(Races.defaultRace.getName()), stat, y.getDouble("MP")));
			}
			if (y.getBoolean("Exterior")) {
				RaceCommand.enable(player);
			} else {
				RaceCommand.disable(player);
			}
			try {
				for (String key : y.getConfigurationSection("MobsKilled").getKeys(false)) {
					map.put(key, y.getInt("MobsKilled." + key));
				}
			} catch (Exception e) {
			}
		}
		MythicMobsListener.MobsKilled.put(player, map);
	}

	public static void save(Player player) {
		File f = new File(Main.getPlugin().getDataFolder() + File.separator + "players",
				player.getUniqueId().toString() + ".yml");
		PlayerData pd = Data.get(player);
		YamlConfiguration y = file.get(player);
		y.set("Race", pd.getRace().getName());
		y.set("MP", Data.get(player).getCurrentMP());
		y.set("CastSpeed", Cast.castspeed.get(player));
		y.set("Status.SKL", pd.getStatus().getSKL());
		y.set("Status.AR", pd.getStatus().getArmor());
		y.set("Status.AT", pd.getStatus().getArmorToughness());
		y.set("Status.ATK", pd.getStatus().getATK());
		y.set("Status.MP", pd.getStatus().getMP());
		y.set("Status.LUK", pd.getStatus().getLucky());
		y.set("Status.HP", pd.getStatus().getHP());
		y.set("Status.Speed", pd.getStatus().getSpeed());
		y.set("Status.LVL", pd.getStatus().getLvL());
		HashMap<String, Integer> map = MythicMobsListener.MobsKilled.get(player);
		try {
			for (String key : map.keySet()) {
				y.set("MobsKilled." + key, map.get(key));
			}
		} catch (Exception e) {
		}
		map.clear();
		MythicMobsListener.MobsKilled.remove(player);
		try {
			y.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void delete(Player player) {
		unload(player);
		File f = new File(Main.getPlugin().getDataFolder() + File.separator + "players",
				player.getUniqueId().toString() + ".yml");
		f.delete();
		loadYaml(player);
	}

	public static void unload(Player player) {
		RaceCommand.disable(player);
		save(player);
		file.remove(player);
	}

	public static void recalcData(Player player, PlayerData pd) {

		float speed = 0.0f;
		double hp = 0.0;
		double mp = 0.0;
		double ar = 0.0;
		double at = 0.0;
		ItemStack helmet = player.getInventory().getHelmet();
		if (CreateItem.getItemForgeSlot(helmet) == 4) {
			ModifyItem.updateItem(helmet, EquipmentSlot.HEAD);
			mp += ModifyItem.getItemMP(helmet);
			hp += ModifyItem.getItemHP(helmet);
			speed += ModifyItem.getItemSD(helmet);
			ar += ModifyItem.getItemAR(helmet);
			at += ModifyItem.getItemAT(helmet);
		}

		ItemStack chest = player.getInventory().getChestplate();
		if (CreateItem.getItemForgeSlot(chest) == 3) {
			ModifyItem.updateItem(chest, EquipmentSlot.CHEST);
			mp += ModifyItem.getItemMP(chest);
			hp += ModifyItem.getItemHP(chest);
			speed += ModifyItem.getItemSD(chest);
			ar += ModifyItem.getItemAR(chest);
			at += ModifyItem.getItemAT(chest);
		}

		ItemStack leg = player.getInventory().getLeggings();
		if (CreateItem.getItemForgeSlot(leg) == 2) {
			ModifyItem.updateItem(leg, EquipmentSlot.LEGS);
			mp += ModifyItem.getItemMP(leg);
			hp += ModifyItem.getItemHP(leg);
			speed += ModifyItem.getItemSD(leg);
			ar += ModifyItem.getItemAR(leg);
			at += ModifyItem.getItemAT(leg);
		}

		ItemStack boot = player.getInventory().getBoots();
		if (CreateItem.getItemForgeSlot(boot) == 1) {
			ModifyItem.updateItem(boot, EquipmentSlot.FEET);
			mp += ModifyItem.getItemMP(boot);
			hp += ModifyItem.getItemHP(boot);
			speed += ModifyItem.getItemSD(boot);
			ar += ModifyItem.getItemAR(boot);
			at += ModifyItem.getItemAT(boot);
		}

		ItemStack off = player.getInventory().getItemInOffHand();
		if (CreateItem.getItemForgeSlot(off) == 5) {
			ModifyItem.updateItem(off, EquipmentSlot.OFF_HAND);
			mp += ModifyItem.getItemMP(off);
			hp += ModifyItem.getItemHP(off);
			speed += ModifyItem.getItemSD(off);
			ar += ModifyItem.getItemAR(off);
			at += ModifyItem.getItemAT(off);
		}

		pd.calc(player, mp, hp, speed, ar, at);
		EventListener.setData(player, pd);

	}

	public static class PlayerData {
		private double hp;
		private double mp;
		private double dmg;
		private double dmg_skill;
		private double dmg_range;
		private float spd;
		private double avd;
		private double crit;
		private Status stat;
		private double CurrentMP;
		private Race race;
		public double armor;
		public double armor_toughness;

		private double STSKL;
		private double STATK;
		private double STHP;
		private double STSPD;
		private double STLUK;
		private double STMP;
		private double STAR;
		private double START;

		public PlayerData(Player player, Race race, Status stat, double currentMP) {
			this.race = race;
			this.stat = stat;

			this.STAR = stat.getArmor() * race.getArmorMod();
			this.START = stat.getArmorToughness() * race.getArmorToughnessMod();
			this.STSKL = stat.getSKL() * race.getSkillModify();
			this.STATK = stat.getATK() * race.getAttackModify();
			this.STHP = stat.getHP() * race.getHealthModify();
			this.STSPD = stat.getSpeed() * race.getSpeedModify();
			this.STMP = stat.getMP() * race.getManaModify();
			this.STLUK = stat.getLucky() * race.getLuckyModify();
			double temp = this.STHP * Utils.HPHealth * Utils.basehp * stat.getLvL();
			if (temp < 1) {
				temp = 1;
			}
			this.hp = temp;
			this.mp = (this.STMP) * Utils.ManaMP * Utils.basemp * stat.getLvL();
			if (this.STSPD * Utils.SPDSpeed + Utils.basespd > 1) {
				this.spd = 1;
			} else {
				this.spd = (float) (this.STSPD * Utils.SPDSpeed + Utils.basespd);
			}
			this.avd = this.STLUK * Utils.LUKAVD;
			this.crit = this.STLUK * Utils.LUKCRIT;
			this.dmg_skill = (this.STSKL);
			this.dmg = (this.STATK);
			this.dmg_range = (stat.getATK() * race.getRangeModify());
			player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
			this.CurrentMP = currentMP;
		}

		public void setRace(Player player, Race race) {
			Race old = this.race;
			this.race = race;
			YamlConfiguration yml = getYaml(player);
			int slot = yml.getInt("UniqueSkillSlots") + race.getSlotModify() - old.getSlotModify();
			yml.set("UniqueSkillSlots", slot);
			for (String skillname : yml.getStringList("UniqeSkills")) {
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
						case 6:
							mod = st.getTotalStatusSKL();
							break;
						default:
							mod = 1;
							break;
						}
						yml.set("Skills." + skillname + ".Max." + str,
								ori * yml.getDouble("Skills." + skillname + ".Mod." + str) * mod);
					}
				} catch (Exception e1) {
				}
			}
			this.STAR = stat.getArmor() * race.getArmorMod();
			this.START = stat.getArmorToughness() * race.getArmorToughnessMod();
			this.STATK = stat.getATK() * race.getAttackModify();
			this.STHP = stat.getHP() * race.getHealthModify();
			this.STSPD = stat.getSpeed() * race.getSpeedModify();
			this.STMP = stat.getMP() * race.getManaModify();
			this.STLUK = stat.getLucky() * race.getLuckyModify();
			this.STSKL = stat.getSKL() * race.getSkillModify();
			double temp = (this.STHP) * Utils.HPHealth * stat.getLvL() * Utils.basehp;
			if (temp < 1) {
				temp = 1;
			}
			this.hp = temp;
			this.mp = (this.STMP) * Utils.ManaMP * stat.getLvL() * Utils.basemp;
			if ((this.STSPD) * Utils.SPDSpeed + Utils.basespd > 1) {
				this.spd = 1;
			} else {
				this.spd = (float) ((this.STSPD) * Utils.SPDSpeed + Utils.basespd);
			}
			this.avd = this.STLUK * Utils.LUKAVD;
			this.crit = this.STLUK * Utils.LUKCRIT;
			this.dmg = (this.STATK);
			this.dmg_skill = (this.STSKL);
			this.dmg_range = (stat.getATK() * race.getRangeModify());
			player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
			Magic.check(player);
			RaceChangeEvent le = new RaceChangeEvent(player, old, race);
			Bukkit.getServer().getPluginManager().callEvent(le);
		}

		public double getTotalStatusArmor() {
			return this.STAR;
		}

		public double getTotalStatusArmorToughness() {
			return this.START;
		}

		public double getTotalStatusATK() {
			return this.STATK;
		}

		public double getTotalStatusHP() {
			return this.STHP;
		}

		public double getTotalStatusMP() {
			return this.STMP;
		}

		public double getTotalStatusLUK() {
			return this.STLUK;
		}

		public double getTotalStatusSpeed() {
			return this.STSPD;
		}

		public PlayerData(Player player, Status stat) {
			this.race = new Race(null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null);
			this.stat = stat;
			setRace(player, Races.defaultRace);
			this.STAR = stat.getArmor() * race.getArmorMod();
			this.START = stat.getArmorToughness() * race.getArmorToughnessMod();
			this.STATK = stat.getATK() * race.getAttackModify();
			this.STHP = stat.getHP() * race.getHealthModify();
			this.STSPD = stat.getSpeed() * race.getSpeedModify();
			this.STMP = stat.getMP() * race.getManaModify();
			this.STLUK = stat.getLucky() * race.getLuckyModify();
			double temp = this.STHP * Utils.HPHealth * Utils.basehp * stat.getLvL();
			if (temp < 1) {
				temp = 1;
			}
			this.hp = temp;
			this.mp = this.STMP * Utils.ManaMP * Utils.basemp * stat.getLvL();
			if (this.STSPD * Utils.SPDSpeed + Utils.basespd > 1) {
				this.spd = 1;
			} else {
				this.spd = (float) (this.STSPD * Utils.SPDSpeed + Utils.basespd);
			}
			this.avd = (stat.getLucky() + race.getLuckyModify()) * Utils.LUKAVD;
			this.crit = (stat.getLucky() + race.getLuckyModify()) * Utils.LUKCRIT;
			this.dmg = (this.STATK);
			this.dmg_skill = (this.STSKL);
			this.dmg_range = (stat.getATK() * race.getRangeModify());
			player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
			this.CurrentMP = this.mp;
		}

		public Race getRace() {
			return this.race;
		}

		public double getCurrentMP() {
			return this.CurrentMP;
		}

		public void setCurrentMP(double mp) {
			this.CurrentMP = mp;
		}

		public void addCurrentMP(double amp) {
			if (this.CurrentMP + amp < 0) {
				this.CurrentMP = 0;
			} else if (this.CurrentMP + amp >= this.mp) {
				this.CurrentMP = this.mp;
			} else {
				this.CurrentMP += amp;
			}
		}

		public double getMaxHealth() {
			return this.hp;
		}

		public double getMaxMana() {
			return this.mp;
		}

		public float getSpeed() {
			return this.spd;
		}

		public double getCrit() {
			return this.crit;
		}

		public double getAvoid() {
			return this.avd;
		}

		public Status getStatus() {
			return this.stat;
		}

		public double getDamage() {
			return this.dmg * this.stat.getLvL();
		}

		public double getSkillDamage() {
			return this.dmg_skill * this.stat.getLvL();
		}

		public double getRangeDamage() {
			return this.dmg_range * this.stat.getLvL();
		}

		public void calc(Player player, double mana, double health, float speed, double ar, double at) {
			double temp = (this.STHP) * Utils.HPHealth * (Utils.basehp + health) * stat.getLvL();
			if (temp < 1) {
				temp = 1;
			}
			this.hp = temp;
			this.mp = this.STMP * Utils.ManaMP * (mana + Utils.basemp) * stat.getLvL();
			float tempspeed = (float) (this.STSPD * Utils.SPDSpeed * speed + Utils.basespd);
			if (tempspeed > 1) {
				this.spd = 1;
			} else {
				this.spd = tempspeed;
			}
			this.avd = (stat.getLucky() * race.getLuckyModify()) * Utils.LUKAVD;
			this.crit = (stat.getLucky() * race.getLuckyModify()) * Utils.LUKCRIT;
			this.dmg = (stat.getATK() * race.getAttackModify());
			this.dmg_skill = (stat.getSKL() * race.getSkillModify());
			this.dmg_range = (stat.getATK() * race.getRangeModify());
			player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
			armor = (ar) * this.STAR;
			player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
			armor_toughness = (at) * this.START;
			// player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(armor_toughness);
		}

		public boolean costMP(double mp) {
			if (mp <= 0) {
				return true;
			}
			if (this.CurrentMP - mp >= 0) {
				this.CurrentMP -= mp;
				return true;
			}
			return false;

		}

		public double getTotalStatusSKL() {
			return this.STSKL;
		}
	}
}
