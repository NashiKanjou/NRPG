package nashi.NRPG;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import edited.com.codingforcookies.armorequip.ArmorListener;
import edited.com.codingforcookies.armorequip.DispenserArmorListener;
import nashi.NRPG.API.Utils;
import nashi.NRPG.Commands.AbandonCommand;
import nashi.NRPG.Commands.CastCommand;
import nashi.NRPG.Commands.Commands;
import nashi.NRPG.Commands.RaceCommand;
import nashi.NRPG.Commands.ResetCommand;
import nashi.NRPG.Commands.ScaleCommand;
import nashi.NRPG.Commands.SkillEditCommand;
import nashi.NRPG.Commands.SkillSlotCommand;
import nashi.NRPG.Commands.StatCommand;
import nashi.NRPG.Players.Players;
import nashi.NRPG.Players.Players.PlayerData;
import nashi.NRPG.Skills.Magic;
import nashi.NRPG.Skills.NewSkill;
import nashi.NRPG.Skills.SkillName;
import nashi.NRPG.listeners.AllAboutDamage;
import nashi.NRPG.listeners.EventListener;
import nashi.NRPG.listeners.ForgeListener;
import nashi.NRPG.listeners.ModifyItem;
import nashi.NRPG.listeners.MythicMobsListener;
import nashi.NRPG.listeners.ProtocolLibEvent;
import nashi.NRPG.listeners.SkillItemListener;

public class Main extends JavaPlugin {
	private static Plugin plugin;
	private static boolean loaded;
	public static boolean cancel;
	public static Set<World> worlds = new HashSet<World>();

	public void onEnable() {
		plugin = this;
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}
		if (!new File(getDataFolder(), "skills.yml").exists()) {
			getPlugin().saveResource("skills.yml", false);
		}
		if (!new File(getDataFolder(), "mobs.yml").exists()) {
			getPlugin().saveResource("mobs.yml", false);
		}
		if (!new File(getDataFolder(), "blocked.yml").exists()) {
			getPlugin().saveResource("blocked.yml", false);
		}
		if (!new File(getDataFolder(), "races.yml").exists()) {
			getPlugin().saveResource("races.yml", false);
		}
		reloadConfig();
		Bukkit.getLogger().info("Loading Settings...");
		Utils.init();

		getCommand("nrpg").setExecutor(new Commands());

		getCommand("stat").setExecutor(new StatCommand());
		getCommand("skilledit").setExecutor(new SkillEditCommand());
		getCommand("cast").setExecutor(new CastCommand());
		getCommand("slot").setExecutor(new SkillSlotCommand());
		getCommand("scale").setExecutor(new ScaleCommand());
		getCommand("abandon").setExecutor(new AbandonCommand());
		getCommand("race").setExecutor(new RaceCommand());
		getCommand("reset").setExecutor(new ResetCommand());

		Bukkit.getLogger().info("Loading Skills..");
		NewSkill.loadskill();
		Bukkit.getLogger().info(SkillName.skillname.size() + " skill(s) loaded");

		getServer().getPluginManager().registerEvents(new EventListener(), this);
		getServer().getPluginManager().registerEvents(new AllAboutDamage(), this);
		getServer().getPluginManager().registerEvents(new ModifyItem(), this);
		getServer().getPluginManager().registerEvents(new ForgeListener(), this);
		getServer().getPluginManager().registerEvents(new SkillItemListener(), this);
		
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
			Bukkit.getLogger().info("MythicMobs Detected");
			getServer().getPluginManager().registerEvents(new MythicMobsListener(), this);
		} 
		
		YamlConfiguration block = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "blocked.yml"));
		getServer().getPluginManager().registerEvents(new ArmorListener(block.getStringList("blocked")), this);
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
			Bukkit.getLogger().info("ProtocolLib Detected");
			cancel = true;
			ProtocolLibEvent.run();
		} else {
			cancel = false;
		}
		try {
			Class.forName("org.bukkit.event.block.BlockDispenseArmorEvent");
			getServer().getPluginManager().registerEvents(new DispenserArmorListener(), this);
		} catch (Exception localException) {
		}
		if (Bukkit.getOnlinePlayers().size() > 0) {
			Bukkit.getLogger().warning("/reload command is not recommended");
			Bukkit.getLogger().warning("Might causes problems");
			Bukkit.getLogger().info("Loading All Online Players..");
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				Players.loadYaml(player);
				EventListener.skillmode.put(player, false);
				Magic.check(player);
				Main.setPlayerScoreboard(player);
				YamlConfiguration py = Players.getYaml(player);
				int a = py.getInt("Scale");
				if (a > 0) {
					player.setHealthScale(a);
				}
				PlayerData pd = Players.Data.get(player);

				float speed = 0.0f;
				double hp = 0.0;
				double mp = 0.0;
				double ar = 0.0;
				double at = 0.0;
				ItemStack helmet = player.getInventory().getHelmet();
				ModifyItem.updateItem(helmet, EquipmentSlot.HEAD);
				mp += ModifyItem.getItemMP(helmet);
				hp += ModifyItem.getItemHP(helmet);
				speed += ModifyItem.getItemSD(helmet);
				ar += ModifyItem.getItemAR(helmet);
				at += ModifyItem.getItemAT(helmet);

				ItemStack chest = player.getInventory().getChestplate();
				ModifyItem.updateItem(chest, EquipmentSlot.CHEST);
				mp += ModifyItem.getItemMP(chest);
				hp += ModifyItem.getItemHP(chest);
				speed += ModifyItem.getItemSD(chest);
				ar += ModifyItem.getItemAR(chest);
				at += ModifyItem.getItemAT(chest);
				
				ItemStack leg = player.getInventory().getLeggings();
				ModifyItem.updateItem(leg, EquipmentSlot.LEGS);
				mp += ModifyItem.getItemMP(leg);
				hp += ModifyItem.getItemHP(leg);
				speed += ModifyItem.getItemSD(leg);
				ar += ModifyItem.getItemAR(leg);
				at += ModifyItem.getItemAT(leg);
				
				ItemStack boot = player.getInventory().getBoots();
				ModifyItem.updateItem(boot, EquipmentSlot.FEET);
				mp += ModifyItem.getItemMP(boot);
				hp += ModifyItem.getItemHP(boot);
				speed += ModifyItem.getItemSD(boot);
				ar += ModifyItem.getItemAR(boot);
				at += ModifyItem.getItemAT(boot);
				
				ItemStack off = player.getInventory().getItemInOffHand();
				ModifyItem.updateItem(off, EquipmentSlot.OFF_HAND);
				mp += ModifyItem.getItemMP(off);
				hp += ModifyItem.getItemHP(off);
				speed += ModifyItem.getItemSD(off);
				ar += ModifyItem.getItemAR(off);
				at += ModifyItem.getItemAT(off);
				pd.calc(player, mp, hp, speed, ar, at);
				EventListener.setData(player, pd);
			}
		}

		loaded = true;
		a();
		b();
		Bukkit.getLogger().info("NRPG is loaded");
	}

	private static void a() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!loaded) {
					this.cancel();
				}
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (!player.isDead()) {
						PlayerData pd = Players.Data.get(player);
						pd.addCurrentMP(Utils.RMP);
						double hp = player.getHealth();
						double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
						if (hp + Utils.RHP < max) {
							player.setHealth(hp + Utils.RHP);
						} else if (hp < max) {
							player.setHealth(max);
						}
					}
				}

			}
		}.runTaskTimerAsynchronously(Main.getPlugin(), 200, 200);
	}

	private static void b() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!loaded) {
					this.cancel();
				}
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					Main.updateScoreBoard(player);
				}

			}
		}.runTaskTimerAsynchronously(Main.getPlugin(), 5, 5);
	}

	private static Scoreboard board;
	private static Objective o;

	public static void setPlayerScoreboard(Player p) {
		PlayerData pd = Players.Data.get(p);
		board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		o = board.registerNewObjective("NRPG", "dummy", "狀態欄");
		o.setDisplayName(ChatColor.GOLD + "狀態欄");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		Team HP = board.registerNewTeam("HP");
		Team MP = board.registerNewTeam("MP");
		HP.addEntry("" + ChatColor.RED + ChatColor.RED);
		MP.addEntry("" + ChatColor.LIGHT_PURPLE + ChatColor.LIGHT_PURPLE);
		DecimalFormat dff = new DecimalFormat("#");
		HP.setPrefix(ChatColor.RED + "HP:" + dff.format(p.getHealth()) + "/"
				+ dff.format(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
		o.getScore("" + ChatColor.RED + ChatColor.RED).setScore(14);
		MP.setPrefix(ChatColor.LIGHT_PURPLE + "MP:" + dff.format(pd.getCurrentMP()) + "/" + dff.format(pd.getMaxMana()));
		o.getScore("" + ChatColor.LIGHT_PURPLE + ChatColor.LIGHT_PURPLE).setScore(13);
		p.setScoreboard(board);
	}

	public static void updateScoreBoard(Player player) {
		PlayerData pd = Players.Data.get(player);
		DecimalFormat dff = new DecimalFormat("#");
		Scoreboard board = player.getScoreboard();
		board.getTeam("HP").setPrefix(ChatColor.RED + "HP:" + dff.format(player.getHealth()) + "/"
				+ dff.format(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
		board.getTeam("MP")
				.setPrefix(ChatColor.LIGHT_PURPLE + "MP:" + dff.format(pd.getCurrentMP()) + "/" + dff.format(pd.getMaxMana()));
	}

	public void onDisable() {
		Bukkit.getLogger().info("Disabling NRPG");
		loaded = false;

		NewSkill.unloadAllSkills();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.closeInventory();
			if (EventListener.backup.containsKey(player)) {
				PlayerInventory inv = player.getInventory();
				ItemStack[] back = EventListener.backup.get(player);
				int i = 0;
				for (ItemStack item : back) {
					inv.setItem(i, item);
					i++;
				}
				double d;
				if (EventListener.extra.containsKey(player)) {
					d = EventListener.extra.get(player);
				} else {
					d = 0;
				}
				EventListener.skillmode.put(player, false);
				player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
						.setBaseValue(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() - d);
				EventListener.extra.remove(player);
				EventListener.castmod.remove(player);
			}
			Players.unload(player);
			player.kickPlayer("伺服器重新載入中");
		}
		Bukkit.getLogger().info("NRPG Disabled");
	}

	public static Plugin getPlugin() {
		return plugin;
	}

}
