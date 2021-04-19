package nashi.NRPG.Players;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import nashi.NRPG.Main;
import nashi.NRPG.API.Utils;

public class Races {
	public static HashMap<String, Race> races = new HashMap<String, Race>();
	public static Race defaultRace;

	public static Race getRace(String name) {
		if (races.containsKey(name.toLowerCase())) {
			return races.get(name.toLowerCase());
		}
		return null;
	}

	public static class Race {
		private String name;
		private String displayname;
		private String des;
		private int slotmod;
		private double strmod;
		private double spdmod;
		private double hpmod;
		private double mpmod;
		private double lukmod;
		private double rangemod;
		private List<String> skilllist;
		ItemStack[] items;
		private double armod;
		private double atmod;
		private double skmod;

		public Race(String name, String display, String des, int slot, double str, double spd, double hp, double mp,
				double luk, double ar, double at, double rangemod, double skmod, List<String> skills,
				ItemStack[] armor) {
			this.des = des;
			this.name = name;
			this.displayname = display;
			this.slotmod = slot;
			this.strmod = str;
			this.spdmod = spd;
			this.hpmod = hp;
			this.mpmod = mp;
			this.lukmod = luk;
			this.skilllist = skills;
			this.items = armor;
			this.armod = ar;
			this.atmod = at;
			this.rangemod = rangemod;
			this.skmod = skmod;
		}

		public String getDescription() {
			return this.des;
		}

		public ItemStack[] getArmor() {
			return this.items.clone();
		}

		public String getName() {
			return this.name;
		}

		public String getDisplayName() {
			return this.displayname;
		}

		public List<String> getSkillList() {
			return this.skilllist;
		}

		public int getSlotModify() {
			return this.slotmod;
		}

		public double getRangeModify() {
			return this.rangemod;
		}

		public double getAttackModify() {
			return this.strmod;
		}

		public double getHealthModify() {
			return this.hpmod;
		}

		public double getManaModify() {
			return this.mpmod;
		}

		public double getSkillModify() {
			return this.skmod;
		}

		public double getLuckyModify() {
			return this.lukmod;
		}

		public double getSpeedModify() {
			return this.spdmod;
		}

		public double getArmorMod() {
			return this.armod;
		}

		public double getArmorToughnessMod() {
			return this.atmod;
		}

	}

	public static void init() {
		races.clear();
		defaultRace = null;
		YamlConfiguration yml = YamlConfiguration
				.loadConfiguration(new File(Main.getPlugin().getDataFolder() + File.separator + "races.yml"));
		Set<String> key = yml.getKeys(false);
		for (String name : key) {
			String display = yml.getString(name + ".DisplayName");
			String des = yml.getString(name + ".Info");
			List<String> skills = yml.getStringList(name + ".SkillList");
			int slot = yml.getInt(name + ".Mod.Slot");
			double atk = yml.getDouble(name + ".Mod.ATK");
			double luk = yml.getDouble(name + ".Mod.LUK");
			double spd = yml.getDouble(name + ".Mod.Speed");
			double mp = yml.getDouble(name + ".Mod.MP");
			double hp = yml.getDouble(name + ".Mod.HP");
			double at = yml.getDouble(name + ".Mod.AT");
			double sk = yml.getDouble(name + ".Mod.SKL");
			double ar = yml.getDouble(name + ".Mod.AR");
			double range = yml.getDouble(name + ".Mod.RG");
			String[] color;
			int red;
			int green;
			int blue;

			ItemStack[] items = new ItemStack[5];
			ItemStack helmet = null;
			if (yml.getBoolean(name + ".Items.Helmet.enable")) {
				String helmat = yml.getString(name + ".Items.Helmet.Material");
				helmet = new ItemStack(Material.valueOf(helmat));
				Utils.setNotDrop(helmet);
				if (helmat.toLowerCase().contains("leather")) {
					LeatherArmorMeta lam = (LeatherArmorMeta) helmet.getItemMeta();
					lam.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(),
							"NRPGMOD", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));
					color = yml.getString(name + ".Items.Helmet.Color").replaceAll(" ", "").split(",");
					red = Integer.parseInt(color[0]);
					green = Integer.parseInt(color[1]);
					blue = Integer.parseInt(color[2]);
					lam.setColor(Color.fromRGB(red, green, blue));
					((Damageable) lam).setDamage(yml.getInt(name + ".Items.Helmet.damage"));
					String itemname = yml.getString(name + ".Items.Helmet.DisplayName");
					if (itemname != null) {
						lam.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemname));
					}
					lam.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
					lam.setUnbreakable(true);
					helmet.setItemMeta(lam);
				} else {
					ItemMeta lam = helmet.getItemMeta();
					lam.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(),
							"NRPGMOD", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));
					((Damageable) lam).setDamage(yml.getInt(name + ".Items.Helmet.damage"));
					lam.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
					lam.setUnbreakable(true);
					String itemname = yml.getString(name + ".Items.Helmet.DisplayName");
					if (itemname != null) {
						lam.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemname));
					}
					helmet.setItemMeta(lam);
				}
			}

			ItemStack chestplate = null;
			if (yml.getBoolean(name + ".Items.Chestplate.enable")) {
				String mat = yml.getString(name + ".Items.Chestplate.Material");
				chestplate = new ItemStack(Material.valueOf(mat));
				Utils.setNotDrop(chestplate);
				if (mat.toLowerCase().contains("leather")) {
					LeatherArmorMeta lam = (LeatherArmorMeta) chestplate.getItemMeta();
					lam.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(),
							"NRPGMOD", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
					color = yml.getString(name + ".Items.Chestplate.Color").replaceAll(" ", "").split(",");
					red = Integer.parseInt(color[0]);
					green = Integer.parseInt(color[1]);
					blue = Integer.parseInt(color[2]);
					lam.setColor(Color.fromRGB(red, green, blue));
					((Damageable) lam).setDamage(yml.getInt(name + ".Items.Chestplate.damage"));
					lam.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
					lam.setUnbreakable(true);
					String itemname = yml.getString(name + ".Items.Chestplate.DisplayName");
					if (itemname != null) {
						lam.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemname));
					}
					chestplate.setItemMeta(lam);
				} else {
					ItemMeta lam = chestplate.getItemMeta();
					lam.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(),
							"NRPGMOD", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
					((Damageable) lam).setDamage(yml.getInt(name + ".Items.Chestplate.damage"));
					lam.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
					lam.setUnbreakable(true);
					String itemname = yml.getString(name + ".Items.Chestplate.DisplayName");
					if (itemname != null) {
						lam.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemname));
					}
					chestplate.setItemMeta(lam);
				}
			}

			ItemStack leggings = null;
			if (yml.getBoolean(name + ".Items.Leggings.enable")) {
				String mat = yml.getString(name + ".Items.Leggings.Material");
				leggings = new ItemStack(Material.valueOf(mat));
				Utils.setNotDrop(leggings);
				if (mat.toLowerCase().contains("leather")) {
					LeatherArmorMeta lam = (LeatherArmorMeta) leggings.getItemMeta();
					lam.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(),
							"NRPGMOD", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
					color = yml.getString(name + ".Items.Leggings.Color").replaceAll(" ", "").split(",");
					red = Integer.parseInt(color[0]);
					green = Integer.parseInt(color[1]);
					blue = Integer.parseInt(color[2]);
					lam.setColor(Color.fromRGB(red, green, blue));
					((Damageable) lam).setDamage(yml.getInt(name + ".Items.Leggings.damage"));
					lam.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
					lam.setUnbreakable(true);
					String itemname = yml.getString(name + ".Items.Leggings.DisplayName");
					if (itemname != null) {
						lam.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemname));
					}
					leggings.setItemMeta(lam);
				} else {
					ItemMeta lam = leggings.getItemMeta();
					lam.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(),
							"NRPGMOD", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
					((Damageable) lam).setDamage(yml.getInt(name + ".Items.Leggings.damage"));
					lam.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
					lam.setUnbreakable(true);
					String itemname = yml.getString(name + ".Items.Leggings.DisplayName");
					if (itemname != null) {
						lam.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemname));
					}
					leggings.setItemMeta(lam);
				}
			}
			ItemStack boots = null;
			if (yml.getBoolean(name + ".Items.Boots.enable")) {
				String mat = yml.getString(name + ".Items.Boots.Material");
				boots = new ItemStack(Material.valueOf(mat));
				Utils.setNotDrop(boots);
				if (mat.toLowerCase().contains("leather")) {
					LeatherArmorMeta lam = (LeatherArmorMeta) boots.getItemMeta();
					lam.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(),
							"NRPGMOD", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
					color = yml.getString(name + ".Items.Boots.Color").replaceAll(" ", "").split(",");
					red = Integer.parseInt(color[0]);
					green = Integer.parseInt(color[1]);
					blue = Integer.parseInt(color[2]);
					lam.setColor(Color.fromRGB(red, green, blue));
					((Damageable) lam).setDamage(yml.getInt(name + ".Items.Boots.damage"));
					lam.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
					lam.setUnbreakable(true);
					String itemname = yml.getString(name + ".Items.Boots.DisplayName");
					if (itemname != null) {
						lam.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemname));
					}
					boots.setItemMeta(lam);
				} else {
					ItemMeta lam = boots.getItemMeta();
					lam.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(),
							"NRPGMOD", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
					((Damageable) lam).setDamage(yml.getInt(name + ".Items.Boots.damage"));
					lam.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
					lam.setUnbreakable(true);
					String itemname = yml.getString(name + ".Items.Boots.DisplayName");
					if (itemname != null) {
						lam.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemname));
					}
					boots.setItemMeta(lam);
				}
			}
			ItemStack off = null;
			if (yml.getBoolean(name + ".Items.OffHand.enable")) {
				String mat = yml.getString(name + ".Items.OffHand.Material");
				off = new ItemStack(Material.valueOf(mat));
				Utils.setNotDrop(off);
				if (mat.toLowerCase().contains("leather")) {
					LeatherArmorMeta lam = (LeatherArmorMeta) off.getItemMeta();
					lam.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(),
							"NRPGMOD", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
					color = yml.getString(name + ".Items.OffHand.Color").replaceAll(" ", "").split(",");
					red = Integer.parseInt(color[0]);
					green = Integer.parseInt(color[1]);
					blue = Integer.parseInt(color[2]);
					lam.setColor(Color.fromRGB(red, green, blue));
					((Damageable) lam).setDamage(yml.getInt(name + ".Items.OffHand.damage"));
					lam.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
					lam.setUnbreakable(true);
					String itemname = yml.getString(name + ".Items.OffHand.DisplayName");
					if (itemname != null) {
						lam.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemname));
					}
					off.setItemMeta(lam);
				} else {
					ItemMeta lam = off.getItemMeta();
					lam.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(),
							"NRPGMOD", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
					((Damageable) lam).setDamage(yml.getInt(name + ".Items.OffHand.damage"));
					lam.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
					lam.setUnbreakable(true);
					String itemname = yml.getString(name + ".Items.OffHand.DisplayName");
					if (itemname != null) {
						lam.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemname));
					}
					off.setItemMeta(lam);
				}
			}
			items[0] = helmet;
			items[1] = chestplate;
			items[2] = leggings;
			items[3] = boots;
			items[4] = off;
			Race race = new Race(name, display, des, slot, atk, spd, hp, mp, luk, ar, at, range, sk, skills, items);
			if (yml.getBoolean(name + ".default")) {
				Races.defaultRace = race;
			}
			races.put(name.toLowerCase(), race);
		}
	}
}
