package nashi.NRPG.Skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nashi.NRPG.API.Utils;
import nashi.NRPG.CustomEvents.SkillItemGenerateEvent;
import nashi.NRPG.Players.Players;

public class GUI {
	public static HashMap<Player, Integer> editpage = new HashMap<Player, Integer>();

	public static void EditGUI(Player player, int page) {
		editpage.put(player, page);
		Inventory inv = Bukkit.createInventory(player, 54, "技能調整介面");
		YamlConfiguration yml = Players.getYaml(player);
		// All Skill
		List<String> skilllist = yml.getStringList("UniqeSkills");
		int i = 0;
		int skip = (page - 1) * 53 - 1;
		for (String skillname : skilllist) {
			if (i >= 53) {
				break;
			}
			if (skip > 0) {
				skip--;
				continue;
			}
			try {
				if (yml.getConfigurationSection(("Skills." + skillname + ".Custom")).getKeys(false).size() > 0) {
					ItemStack item = Utils.getSkillItem(skillname,player);
					SkillItemGenerateEvent event = new SkillItemGenerateEvent(player, item, skillname);
					Bukkit.getPluginManager().callEvent(event);
					inv.setItem(i, item);
					i++;
				}
			} catch (Exception e) {
			}
		}
		if (i >= 53) {
			ItemStack item = new ItemStack(Material.ARROW);
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "下一頁");
			item.setItemMeta(im);
			Utils.setNotDrop(item);
			inv.setItem(53, item);
		}
		player.openInventory(inv);
	}

	public static void SlotGUI(Player player, int page) {
		Inventory inv = Bukkit.createInventory(player, 54, "技能欄位設定");
		YamlConfiguration yml = Players.getYaml(player);
		// Current Setting
		for (int s = 0; s < 9; s++) {
			String skillname = yml.getString("Slot." + s);
			if (skillname != null) {
				ItemStack item = Utils.getSkillItem(skillname,player);
				inv.setItem(s, item);
			}
		}
		// All Skill
		List<String> skilllist = yml.getStringList("UniqeSkills");
		ItemStack b = new ItemStack(Material.BARRIER);
		Utils.setNotDrop(b);
		for (int i = 9; i < 18; i++) {
			inv.setItem(i, b);
		}
		int i = 0;
		int skip = (page - 1) * 35 - 1;
		for (String skillname : skilllist) {
			if (i >= 35) {
				break;
			}
			if (skip > 0) {
				skip--;
				continue;
			}
			ItemStack item = Utils.getSkillItem(skillname,player);
			inv.setItem(i + 18, item);
			i++;
		}
		if (i >= 35) {
			ItemStack item = new ItemStack(Material.ARROW);
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "下一頁");
			item.setItemMeta(im);
			Utils.setNotDrop(item);
			inv.setItem(53, item);
		}
		player.openInventory(inv);
	}

	public static void openSkillEditGUI(Player player, String skillname) {
		YamlConfiguration yml = Players.getYaml(player);
		Inventory inv = Bukkit.createInventory(player, 27, "技能設定-" + skillname);
		ItemStack skill = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta skillmeta = skill.getItemMeta();
		skillmeta.setDisplayName(ChatColor.LIGHT_PURPLE + skillname);
		skill.setItemMeta(skillmeta);
		inv.setItem(9, skill);
		Set<String> key = yml.getConfigurationSection("Skills." + skillname + ".Custom").getKeys(false);
		int t = 1;
		int m = 10;
		int b = 19;
		if (key.size() <= 0) {
			player.sendMessage(ChatColor.RED + "此技能沒有可調整的數值");
			return;
		}
		for (String str : key) {
			if (t >= 8) {
				break;
			}
			ItemStack top = new ItemStack(Material.GREEN_WOOL);
			ItemStack mid = new ItemStack(Material.ACACIA_SIGN);
			ItemStack bot = new ItemStack(Material.RED_WOOL);
			ItemMeta tm = top.getItemMeta();
			ItemMeta mm = mid.getItemMeta();
			ItemMeta bm = bot.getItemMeta();
			tm.setDisplayName(ChatColor.GREEN + "提升:" + str);
			mm.setDisplayName(ChatColor.GOLD + str + "數值");
			bm.setDisplayName(ChatColor.RED + "降低:" + str);
			List<String> tl = new ArrayList<String>();// L 0.01 R 0.1 SL 0.05 SR 0.5
			tl.add(ChatColor.YELLOW + "點此左鍵來提升0.01");
			tl.add(ChatColor.YELLOW + "點此Shift+左鍵來提升0.1");
			tl.add(ChatColor.YELLOW + "點此右鍵來提升1");
			tl.add(ChatColor.YELLOW + "點此Shift+右鍵來提升10");
			List<String> bl = new ArrayList<String>();// L 0.01 R 0.1 SL 0.05 SR 0.5
			bl.add(ChatColor.YELLOW + "點此左鍵來降低0.01");
			bl.add(ChatColor.YELLOW + "點此Shift+左鍵來降低0.1");
			bl.add(ChatColor.YELLOW + "點此右鍵來降低1");
			bl.add(ChatColor.YELLOW + "點此Shift+右鍵來降低10");
			List<String> ml = new ArrayList<String>();
			ml.add(ChatColor.YELLOW + "當前: " + yml.getString("Skills." + skillname + ".Custom." + str));
			ml.add(ChatColor.YELLOW + "最大: " + yml.getString("Skills." + skillname + ".Max." + str));
			tm.setLore(tl);
			mm.setLore(ml);
			bm.setLore(bl);
			top.setItemMeta(tm);
			mid.setItemMeta(mm);
			bot.setItemMeta(bm);
			inv.setItem(t, top);
			inv.setItem(m, mid);
			inv.setItem(b, bot);
			t++;
			m++;
			b++;
		}
		player.openInventory(inv);
	}
}
