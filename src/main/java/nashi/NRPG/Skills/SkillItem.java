package nashi.NRPG.Skills;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import nashi.NRPG.Main;

public class SkillItem {
	public static NamespacedKey Item_Skillname = new NamespacedKey(Main.getPlugin(), "NRPG_SkillItem_SkillName");
	public static NamespacedKey Item_SkillDifficulty = new NamespacedKey(Main.getPlugin(), "NRPG_SkillItem_Difficulty");
	public static NamespacedKey Item_SkillTime = new NamespacedKey(Main.getPlugin(), "NRPG_SkillItem_Times");
	public static NamespacedKey Item_SkillLimitTimeUse = new NamespacedKey(Main.getPlugin(),
			"NRPG_SkillItem_LimitTimeUse");
	public static NamespacedKey Item_SkillUseDamage = new NamespacedKey(Main.getPlugin(), "NRPG_SkillItem_UseDamage");


	public static void setSkillItem(ItemStack item, String skillname, int damage, int time, int limit, int difficulty) {
		setSkill(item, skillname);
		setUseDamage(item, damage);
		setSkillTime(item, time);
		setSkillLimitTimeUse(item, limit);
		setSkillDifficulty(item, difficulty);
	}

	public static void setSkill(ItemStack item, String skillname) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(SkillItem.Item_Skillname, PersistentDataType.STRING, skillname);
		item.setItemMeta(im);
	}

	public static String getSkill(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return null;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(SkillItem.Item_Skillname, PersistentDataType.STRING)) {
			return pdc.get(SkillItem.Item_Skillname, PersistentDataType.STRING);
		}
		return null;
	}

	public static void setUseDamage(ItemStack item, int damage) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(SkillItem.Item_SkillUseDamage, PersistentDataType.INTEGER, damage);
		item.setItemMeta(im);
	}

	public static int getUseDamage(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return -1;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(SkillItem.Item_SkillUseDamage, PersistentDataType.INTEGER)) {
			return pdc.get(SkillItem.Item_SkillUseDamage, PersistentDataType.INTEGER);
		}
		return -1;
	}

	public static void setSkillTime(ItemStack item, int time) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(SkillItem.Item_SkillTime, PersistentDataType.INTEGER, time);
		item.setItemMeta(im);
	}

	public static int getSkillTime(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return -1;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(SkillItem.Item_SkillTime, PersistentDataType.INTEGER)) {
			return pdc.get(SkillItem.Item_SkillTime, PersistentDataType.INTEGER);
		}
		return -1;
	}

	public static void setSkillLimitTimeUse(ItemStack item, int limit) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(SkillItem.Item_SkillLimitTimeUse, PersistentDataType.INTEGER, limit);
		item.setItemMeta(im);
	}

	public static int getSkillLimitTimeUse(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return -1;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(SkillItem.Item_SkillLimitTimeUse, PersistentDataType.INTEGER)) {
			return pdc.get(SkillItem.Item_SkillLimitTimeUse, PersistentDataType.INTEGER);
		}
		return -1;
	}

	public static void setSkillDifficulty(ItemStack item, int difficulty) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(SkillItem.Item_SkillDifficulty, PersistentDataType.INTEGER, difficulty);
		item.setItemMeta(im);
	}

	public static int getSkillDifficulty(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return -1;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(SkillItem.Item_SkillDifficulty, PersistentDataType.INTEGER)) {
			return pdc.get(SkillItem.Item_SkillDifficulty, PersistentDataType.INTEGER);
		}
		return -1;
	}
}
