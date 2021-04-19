package nashi.NRPG.API;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import nashi.NRPG.Main;
import nashi.NRPG.Items.CreateItem;
import nashi.NRPG.Players.Leveling;
import nashi.NRPG.Players.Players;
import nashi.NRPG.Players.Races;
import nashi.NRPG.Skills.Cast;
import nashi.NRPG.Skills.Skill;
import nashi.NRPG.Skills.SkillName;
import nashi.NRPG.Skills.Skill.Skills;
import nashi.NRPG.listeners.AllAboutDamage;

public class Utils {
	public static boolean Penalty_MakeUp;
	private static HashMap<Player, Status> playerstatus = new HashMap<Player, Status>();
	public static Random rand = new Random();
	public static double manaDMG;
	public static float SPDSpeed;
	public static double HPHealth;
	public static double ManaMP;
	public static double LUKAVD;
	public static double LUKCRIT;
	public static double basehp;
	public static double basemp;
	public static float basespd;
	public static List<Material> transparentBlocks = new ArrayList<Material>();
	public static int MaxSlot;
	public static int RMP;
	public static int RHP;
	public static NamespacedKey key = new NamespacedKey(Main.getPlugin(), "NRPG_Consumables");
	public static double defaultattackrange;
	public static double hunger;
	public static double exarmor;
	public static double arrowdamagemod;
	public static int STATRAND;
	public static double PercentDamageLVL;
	public static List<World> deathpenworld = new ArrayList<World>();
	public static boolean Penalty_LVL;
	public static boolean Penalty_Race;
	public static boolean Penalty_Status;
	public static List<String> MakeUpSkills = new ArrayList<String>();
	public static double MakeUpMod;

	public static void additem(Player player, ItemStack item) {
		if (item == null) {
			return;
		}
		if (player.getInventory().firstEmpty() != -1) {
			player.getInventory().addItem(item);
		} else {
			player.getWorld().dropItem(player.getLocation(), item);
		}
	}

	public static void util() {
		transparentBlocks.clear();
		transparentBlocks.add(Material.CAVE_AIR);
		transparentBlocks.add(Material.AIR);
		transparentBlocks.add(Material.BLACK_CARPET);
		transparentBlocks.add(Material.BLUE_CARPET);
		transparentBlocks.add(Material.BROWN_CARPET);
		transparentBlocks.add(Material.CYAN_CARPET);
		transparentBlocks.add(Material.MAGENTA_CARPET);
		transparentBlocks.add(Material.ORANGE_CARPET);
		transparentBlocks.add(Material.PINK_CARPET);
		transparentBlocks.add(Material.PURPLE_CARPET);
		transparentBlocks.add(Material.RED_CARPET);
		transparentBlocks.add(Material.WHITE_CARPET);
		transparentBlocks.add(Material.YELLOW_CARPET);
		transparentBlocks.add(Material.CARROT);
		transparentBlocks.add(Material.WHEAT);
		transparentBlocks.add(Material.DEAD_BUSH);
		transparentBlocks.add(Material.DETECTOR_RAIL);
		transparentBlocks.add(Material.ACACIA_FENCE_GATE);
		transparentBlocks.add(Material.BIRCH_FENCE_GATE);
		transparentBlocks.add(Material.DARK_OAK_FENCE_GATE);
		transparentBlocks.add(Material.JUNGLE_FENCE_GATE);
		transparentBlocks.add(Material.OAK_FENCE_GATE);
		transparentBlocks.add(Material.SPRUCE_FENCE_GATE);
		transparentBlocks.add(Material.FLOWER_POT);
		transparentBlocks.add(Material.LADDER);
		transparentBlocks.add(Material.LEVER);
		transparentBlocks.add(Material.GRASS);
		transparentBlocks.add(Material.POWERED_RAIL);
		transparentBlocks.add(Material.REDSTONE_TORCH);
		transparentBlocks.add(Material.REDSTONE_WIRE);
		transparentBlocks.add(Material.ACACIA_SIGN);
		transparentBlocks.add(Material.BIRCH_SIGN);
		transparentBlocks.add(Material.DARK_OAK_SIGN);
		transparentBlocks.add(Material.JUNGLE_SIGN);
		transparentBlocks.add(Material.OAK_SIGN);
		transparentBlocks.add(Material.SPRUCE_SIGN);
		transparentBlocks.add(Material.SNOW);
		transparentBlocks.add(Material.STONE_BUTTON);
		transparentBlocks.add(Material.SUGAR_CANE);
		transparentBlocks.add(Material.TORCH);
		transparentBlocks.add(Material.TRIPWIRE);
		transparentBlocks.add(Material.VINE);
		transparentBlocks.add(Material.ACACIA_WALL_SIGN);
		transparentBlocks.add(Material.BIRCH_WALL_SIGN);
		transparentBlocks.add(Material.DARK_OAK_WALL_SIGN);
		transparentBlocks.add(Material.JUNGLE_WALL_SIGN);
		transparentBlocks.add(Material.OAK_WALL_SIGN);
		transparentBlocks.add(Material.SPRUCE_WALL_SIGN);
		transparentBlocks.add(Material.WATER);
		transparentBlocks.add(Material.LAVA);
		transparentBlocks.add(Material.LARGE_FERN);
		transparentBlocks.add(Material.FERN);
		transparentBlocks.add(Material.TALL_GRASS);
		transparentBlocks.add(Material.SEAGRASS);
		transparentBlocks.add(Material.TALL_SEAGRASS);

	}

	public static long time = System.currentTimeMillis();

	public static String getRandomMakeUpSkills() {
		return MakeUpSkills.get(rand.nextInt(MakeUpSkills.size()));
	}

	public static Status getStatusFromInt(int i, int lvl) {
		Random rand = new Random(time + i);
		return new Status(rand.nextInt(Utils.STATRAND) + 1, rand.nextInt(Utils.STATRAND) + 1,
				rand.nextInt(Utils.STATRAND) + 1, rand.nextInt(Utils.STATRAND) + 1, rand.nextInt(Utils.STATRAND) + 1,
				rand.nextInt(Utils.STATRAND) + 1, rand.nextInt(Utils.STATRAND) + 1, rand.nextInt(Utils.STATRAND) + 1,
				lvl);
	}

	public static Status getStatusFromInt(int i) {
		Random rand = new Random(time + i);
		return new Status(rand.nextInt(Utils.STATRAND) + 1, rand.nextInt(Utils.STATRAND) + 1,
				rand.nextInt(Utils.STATRAND) + 1, rand.nextInt(Utils.STATRAND) + 1, rand.nextInt(Utils.STATRAND) + 1,
				rand.nextInt(Utils.STATRAND) + 1, rand.nextInt(Utils.STATRAND) + 1, rand.nextInt(Utils.STATRAND) + 1,
				1);
	}

	public static void init() {
		Races.init();
		Leveling.init();
		util();
		CreateItem.init();
		FileConfiguration yml = Main.getPlugin().getConfig();
		RMP = yml.getInt("ManaRecover");
		RHP = yml.getInt("HPRecover");
		manaDMG = yml.getDouble("ManaDamage");
		Cast.timelimit = yml.getLong("TimeLimit");
		MaxSlot = yml.getInt("MaxSlot");
		basespd = (float) yml.getDouble("BaseSpeed");
		basehp = yml.getDouble("BaseHP");
		basemp = yml.getDouble("BaseMP");
		LUKAVD = yml.getDouble("LUKAVD");
		LUKCRIT = yml.getDouble("LUKCRIT");
		HPHealth = yml.getDouble("HPMod");
		ManaMP = yml.getDouble("MPMod");
		SPDSpeed = (float) yml.getDouble("SpeedMod");
		defaultattackrange = yml.getDouble("DefaultAttackRange");
		hunger = yml.getDouble("HungerDamage");
		exarmor = yml.getDouble("LeakArmorMod");
		arrowdamagemod = yml.getDouble("ArrowEnchMod");
		STATRAND = yml.getInt("StatusRandomMax");
		PercentDamageLVL = yml.getDouble("RequiredDamageDueToMobs");
		Penalty_LVL = yml.getBoolean("Penalty.LevelLose");
		Penalty_Race = yml.getBoolean("Penalty.ResetRace");
		Penalty_Status = yml.getBoolean("Penalty.ResetStatus");
		Penalty_Status = yml.getBoolean("Penalty.MakeUp");
		MakeUpMod = yml.getDouble("MakeUpMod");
		Main.worlds.clear();
		deathpenworld.clear();
		MakeUpSkills.clear();
		try {
			for (String str : yml.getStringList("MakeUpSkills")) {
				MakeUpSkills.add(str);
			}
		} catch (Exception e) {
		}
		try {
			for (String str : yml.getStringList("DeathPenaltyWorlds")) {
				deathpenworld.add(Bukkit.getWorld(str));
			}
		} catch (Exception e) {
		}
		try {
			for (String str : yml.getStringList("Worlds")) {
				Main.worlds.add(Bukkit.getWorld(str));
			}
		} catch (Exception e) {
		}
		Players.skillsetting = YamlConfiguration
				.loadConfiguration(new File(Main.getPlugin().getDataFolder() + File.separator + "skills.yml"));

		new BukkitRunnable() {
			@Override
			public void run() {
				Utils.time = System.currentTimeMillis();
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(), 1728000, 1728000);
	}

	public static class Status {
		private int ATK;
		private int HP;
		private int SPD;
		private int LUK;
		private int MP;
		private int AR;
		private int AT;
		private int LVL;
		private int SKL;

		public Status() {
			this.ATK = 0;
			this.SPD = 0;
			this.HP = 0;
			this.MP = 0;
			this.LUK = 0;
			this.LVL = 1;
			this.AR = 0;
			this.AT = 0;
		}

		public Status(int SKL, int AR, int AT, int atk, int spd, int hp, int mp, int luk, int lvl) {
			this.SKL = SKL;
			this.AR = AR;
			this.AT = AT;
			this.ATK = atk;
			this.SPD = spd;
			this.HP = hp;
			this.MP = mp;
			this.LUK = luk;
			this.LVL = lvl;
		}

		public int getArmor() {
			return this.AR;
		}

		public int getArmorToughness() {
			return AT;
		}

		public int getLvL() {
			return this.LVL;
		}

		public void setLvL(int lvl) {
			this.LVL = lvl;
		}

		public int getATK() {
			return this.ATK;
		}

		public int getHP() {
			return this.HP;
		}

		public int getMP() {
			return this.MP;
		}

		public int getSpeed() {
			return this.SPD;
		}

		public int getLucky() {
			return this.LUK;
		}

		public int getSKL() {
			return this.SKL;
		}
	}

	public static Status getPlayerStatus(Player player) {
		return playerstatus.get(player);
	}

	public static void setPlayerStatus(Player player, Status status) {
		playerstatus.put(player, status);
	}

	public static boolean isSuccess(double rate) {
		return rand.nextInt(100) < rate;
	}

	public static ItemStack getSkillItem(String skillname, Player player) {
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta im = item.getItemMeta();
		skillname = SkillName.get(skillname);
		im.setDisplayName(ChatColor.LIGHT_PURPLE + skillname);
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.YELLOW + Players.skillsetting.getString(skillname + ".Description"));
		int cost;

		try {
			Skills sk = Skill.skills.get(skillname);
			Class<?> clazz = sk.skill;
			Object s = clazz.getDeclaredConstructor().newInstance();
			Method skill = clazz.getMethod("getManaCost", Player.class);
			cost = (int) skill.invoke(s, player);
		} catch (Exception e) {
			cost = (int) Players.skillsetting.getDouble(skillname + ".CostMP");
		}
		lore.add(ChatColor.YELLOW + "消耗魔力: " + cost);
		lore.add(ChatColor.YELLOW + "需要詠唱次數: " + Players.skillsetting.getString(skillname + ".Times"));
		lore.add(ChatColor.YELLOW + "冷卻時間: " + Players.skillsetting.getString(skillname + ".Cooldown"));
		im.setLore(lore);
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(AllAboutDamage.key, PersistentDataType.STRING, "ItemsNotDrop");
		item.setItemMeta(im);

		return item;
	}

	public static ItemStack getSkillItem(String skillname, String Cost) {
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta im = item.getItemMeta();
		skillname = SkillName.get(skillname);
		im.setDisplayName(ChatColor.LIGHT_PURPLE + skillname);
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.YELLOW + Players.skillsetting.getString(skillname + ".Description"));
		lore.add(Cost);
		lore.add(ChatColor.YELLOW + "需要詠唱次數: " + Players.skillsetting.getString(skillname + ".Times"));
		lore.add(ChatColor.YELLOW + "冷卻時間: " + Players.skillsetting.getString(skillname + ".Cooldown"));
		im.setLore(lore);
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(AllAboutDamage.key, PersistentDataType.STRING, "ItemsNotDrop");
		item.setItemMeta(im);
		return item;
	}

	public static void setNotDrop(ItemStack item) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(AllAboutDamage.key, PersistentDataType.STRING, "ItemsNotDrop");
		item.setItemMeta(im);
	}

	public static boolean isNotDrop(ItemStack item) {
		try {
			ItemMeta im = item.getItemMeta();
			PersistentDataContainer pdc = im.getPersistentDataContainer();
			return pdc.has(AllAboutDamage.key, PersistentDataType.STRING);
		} catch (NullPointerException e) {
			return false;
		}
	}
}
