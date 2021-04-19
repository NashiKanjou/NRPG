package nashi.NRPG.Items;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import nashi.NRPG.Main;

public class CreateItem {
	public static Note notes[] = new Note[25];
	public static final char blank = ' ';
	public static final int size = 25;
	public static int max;
	public static long rate;
	private static NamespacedKey key_item_forge = new NamespacedKey(Main.getPlugin(), "NRPG_Forge_Item_Forge");
	private static NamespacedKey key_item_time = new NamespacedKey(Main.getPlugin(), "NRPG_Forge_Item_Time");
	private static NamespacedKey key_item_type = new NamespacedKey(Main.getPlugin(), "NRPG_Forge_Item_Type");
	public static NamespacedKey key_material = new NamespacedKey(Main.getPlugin(), "NRPG_Forge_Material");
	public static NamespacedKey key_click = new NamespacedKey(Main.getPlugin(), "NRPG_Forge_Click");
	public static ItemStack click;
	public static HashMap<String, Technique> limits = new HashMap<String, Technique>();
	public static HashMap<String, Technique[]> technique = new HashMap<String, Technique[]>();
	public static HashMap<String, Technique[]> rand_technique = new HashMap<String, Technique[]>();

	public static void init() {
		notes[0] = new Note(0, Tone.A, false);
		notes[1] = new Note(0, Tone.A, true);
		notes[2] = new Note(0, Tone.B, false);
		notes[3] = new Note(0, Tone.C, false);
		notes[4] = new Note(0, Tone.C, true);
		notes[5] = new Note(0, Tone.D, false);
		notes[6] = new Note(0, Tone.D, true);
		notes[7] = new Note(0, Tone.E, false);
		notes[8] = new Note(0, Tone.F, false);
		notes[9] = new Note(0, Tone.F, true);
		notes[10] = new Note(0, Tone.G, false);
		notes[11] = new Note(0, Tone.G, true);

		notes[12] = new Note(1, Tone.A, false);
		notes[13] = new Note(1, Tone.A, true);
		notes[14] = new Note(1, Tone.B, false);
		notes[15] = new Note(1, Tone.C, false);
		notes[16] = new Note(1, Tone.C, true);
		notes[17] = new Note(1, Tone.D, false);
		notes[18] = new Note(1, Tone.D, true);
		notes[19] = new Note(1, Tone.E, false);
		notes[20] = new Note(1, Tone.F, false);
		notes[21] = new Note(1, Tone.F, true);
		notes[22] = new Note(1, Tone.G, false);
		notes[23] = new Note(1, Tone.G, true);

		notes[24] = new Note(2, Tone.F, true);
		FileConfiguration yml = Main.getPlugin().getConfig();
		YamlConfiguration forge = YamlConfiguration
				.loadConfiguration(new File(Main.getPlugin().getDataFolder() + File.separator + "forge.yml"));
		Set<String> slot = forge.getConfigurationSection("technique").getKeys(false);
		technique.clear();
		rand_technique.clear();
		for (String slotkey : slot) {
			Set<String> tier = forge.getConfigurationSection("technique." + slotkey).getKeys(false);
			for (String tierkey : tier) {
				Technique[] tech = new Technique[25];
				for (int i = 0; i < 25; i++) {
					String str = forge.getString("technique." + slotkey + "." + tierkey + "." + i);
					tech[i] = new Technique(str, Integer.parseInt(slotkey));
				}
				technique.put(slotkey + "." + tierkey, tech);
			}
		}
		Set<String> slots = forge.getConfigurationSection("max").getKeys(false);
		limits.clear();
		for (String slotkey : slots) {
			String str = forge.getString("max." + slotkey);
			limits.put(slotkey, new Technique(str, Integer.parseInt(slotkey)));
		}
		// Random
		Set<String> RandomLevel = forge.getConfigurationSection("Random").getKeys(false);
		for (String slotkey : RandomLevel) {
			Technique[] tech = new Technique[6];
			for (int s = 0; s <= 5; s++) {
				String str = forge.getString("Random." + slotkey + "." + s);
				tech[s] = new Technique(str);
			}
			rand_technique.put("r" + slotkey, tech);
		}
		// Random Ended
		max = yml.getInt("MaxForge");
		rate = yml.getLong("RateOfNote");
		ItemStack item = getCustomTextureHead(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDNkMDRkYmE1MWY4OTI0OTU4MzRmZjcxYTQyOWE4YTkxMDE1YTVhNzg2Yjg1NmZmZTljMDI0Y2RiNTJmYmM4ZiJ9fX0=");
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "鍛造");
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(key_click, PersistentDataType.INTEGER, 1);
		item.setItemMeta(im);
		click = item;
	}

	public static ItemStack getCustomTextureHead(String value) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), "");
		profile.getProperties().put("textures", new Property("textures", value));
		Field profileField = null;
		try {
			profileField = meta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(meta, profile);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		head.setItemMeta(meta);
		return head;
	}

	public static class Technique {
		public boolean isRandom = false;
		public double Rand_Max;
		public double Rand_Min;

		public double MAX_MANA;
		public double MAX_HEALTH;
		public double ARMOR_TOUGHNESS;
		public double ARMOR;
		public double ATTACK_DAMAGE;
		public double ATTACK_SPEED;
		public double ATTACK_RANGE;
		public double KNOCKBACK_RESISTANCE;
		public double MOVEMENT_SPEED;
		public double CAST_MOD;

		public EquipmentSlot slot;

		public Technique(String raw, int slot) {
			String[] str = raw.split(",");
			this.MAX_MANA = Double.parseDouble(str[0]);
			this.MAX_HEALTH = Double.parseDouble(str[1]);
			this.ARMOR_TOUGHNESS = Double.parseDouble(str[2]);
			this.ARMOR = Double.parseDouble(str[3]);
			this.ATTACK_DAMAGE = Double.parseDouble(str[4]);
			this.ATTACK_SPEED = Double.parseDouble(str[5]);
			this.ATTACK_RANGE = Double.parseDouble(str[6]);
			this.KNOCKBACK_RESISTANCE = Double.parseDouble(str[7]);
			this.MOVEMENT_SPEED = Double.parseDouble(str[8]);
			this.CAST_MOD = Double.parseDouble(str[9]);
			this.slot = getEquipmentSlotFromInt(slot);
			this.isRandom = false;
		}

		public Technique(double arr[], int slot) {
			this.MAX_MANA = arr[0];
			this.MAX_HEALTH = arr[1];
			this.ARMOR_TOUGHNESS = arr[2];
			this.ARMOR = arr[3];
			this.ATTACK_DAMAGE = arr[4];
			this.ATTACK_SPEED = arr[5];
			this.ATTACK_RANGE = arr[6];
			this.KNOCKBACK_RESISTANCE = arr[7];
			this.MOVEMENT_SPEED = arr[8];
			this.CAST_MOD = arr[9];
			this.slot = getEquipmentSlotFromInt(slot);
			this.isRandom = false;
		}

		public Technique(double arr[]) {// for Random
			this.Rand_Min = arr[0];
			this.Rand_Max = arr[1];
			this.MAX_MANA = arr[2];
			this.MAX_HEALTH = arr[3];
			this.ARMOR_TOUGHNESS = arr[4];
			this.ARMOR = arr[5];
			this.ATTACK_DAMAGE = arr[6];
			this.ATTACK_SPEED = arr[7];
			this.ATTACK_RANGE = arr[8];
			this.KNOCKBACK_RESISTANCE = arr[9];
			this.MOVEMENT_SPEED = arr[10];
			this.CAST_MOD = arr[11];
			this.isRandom = true;
		}

		public Technique(String raw) {// for Random
			String[] str = raw.split(",");
			this.Rand_Min = Double.parseDouble(str[0]);
			this.Rand_Max = Double.parseDouble(str[1]);
			this.MAX_MANA = Double.parseDouble(str[2]);
			this.MAX_HEALTH = Double.parseDouble(str[3]);
			this.ARMOR_TOUGHNESS = Double.parseDouble(str[4]);
			this.ARMOR = Double.parseDouble(str[5]);
			this.ATTACK_DAMAGE = Double.parseDouble(str[6]);
			this.ATTACK_SPEED = Double.parseDouble(str[7]);
			this.ATTACK_RANGE = Double.parseDouble(str[8]);
			this.KNOCKBACK_RESISTANCE = Double.parseDouble(str[9]);
			this.MOVEMENT_SPEED = Double.parseDouble(str[10]);
			this.CAST_MOD = Double.parseDouble(str[11]);
			this.isRandom = true;
		}

		public boolean isRandom() {
			return this.isRandom;
		}
	}

	public static EquipmentSlot getEquipmentSlotFromInt(int i) {
		switch (i) {
		case 0:
			return EquipmentSlot.HAND;
		case 1:
			return EquipmentSlot.FEET;
		case 2:
			return EquipmentSlot.LEGS;
		case 3:
			return EquipmentSlot.CHEST;
		case 4:
			return EquipmentSlot.HEAD;
		default:
			return EquipmentSlot.OFF_HAND;

		}
	}

	public static void setItemForgeMaterialTier(ItemStack item, String tier) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(key_material, PersistentDataType.STRING, tier);
		item.setItemMeta(im);
	}

	public static void setItemForgeKey(ItemStack item, int i) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(key_item_forge, PersistentDataType.INTEGER, i);
		item.setItemMeta(im);
	}

	public static void setItemForgeSlot(ItemStack item, int i) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(key_item_type, PersistentDataType.INTEGER, i);
		item.setItemMeta(im);
	}

	public static int getItemForgeKey(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return -1;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(key_item_forge, PersistentDataType.INTEGER)) {
			return pdc.get(key_item_forge, PersistentDataType.INTEGER);
		}
		return -1;
	}

	public static int getItemForgeSlot(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return -1;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(key_item_type, PersistentDataType.INTEGER)) {
			return pdc.get(key_item_type, PersistentDataType.INTEGER);
		}
		return -1;
	}

	public static int getItemForgeTime(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return -1;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(key_item_time, PersistentDataType.INTEGER)) {
			return pdc.get(key_item_time, PersistentDataType.INTEGER);
		}
		return -1;
	}

	public static void setItemForgeTime(ItemStack item, int i) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(key_item_time, PersistentDataType.INTEGER, i);
		item.setItemMeta(im);
	}

}
