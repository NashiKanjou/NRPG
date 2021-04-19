package nashi.NRPG.listeners;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.collect.Multimap;

import nashi.NRPG.Main;
import nashi.NRPG.API.SkillAPI;
import nashi.NRPG.API.Utils;

public class ModifyItem implements Listener {
	public static NamespacedKey Item_Range = new NamespacedKey(Main.getPlugin(), "NRPG_AttackRange");
	public static NamespacedKey Castkey = new NamespacedKey(Main.getPlugin(), "NRPG_CastMod");
	public static HashMap<Player, Double> edit = new HashMap<Player, Double>();
	public static HashMap<Player, Long> atktime = new HashMap<Player, Long>();
	public static NamespacedKey CastHelp = new NamespacedKey(Main.getPlugin(), "NRPG_CastHelp");
	public static NamespacedKey Item_MP = new NamespacedKey(Main.getPlugin(), "NRPG_ItemMP");// mp
	public static NamespacedKey Item_HP = new NamespacedKey(Main.getPlugin(), "NRPG_ItemHP");// hp
	public static NamespacedKey Item_AR = new NamespacedKey(Main.getPlugin(), "NRPG_ItemAR");// armor
	public static NamespacedKey Item_AT = new NamespacedKey(Main.getPlugin(), "NRPG_ItemAT");// armor toughness
	public static NamespacedKey Item_SD = new NamespacedKey(Main.getPlugin(), "NRPG_ItemSD");// speed

	public static void updateItem(ItemStack item, EquipmentSlot equip) {
		if (item == null || item.getType().equals(Material.AIR) || equip.equals(EquipmentSlot.HAND)) {
			return;
		}
		double hp = 0, armor = 0, armor_toughness = 0, movement_speed = 0;
		try {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasAttributeModifiers()) {
				Multimap<Attribute, AttributeModifier> map = meta.getAttributeModifiers(equip);
				Collection<AttributeModifier> g_armor = map.get(Attribute.GENERIC_ARMOR);
				double add_amount = 0;
				double scalar_amount = 0;
				double addmult_amount = 0;
				for (AttributeModifier am : g_armor) {
					switch (am.getOperation()) {
					case ADD_NUMBER:
						add_amount += am.getAmount();
						break;
					default:
						break;
					}
				}
				g_armor.clear();
				armor = add_amount;
				if (scalar_amount != 0) {
					g_armor.add(new AttributeModifier(UUID.randomUUID(), "generic.armor", scalar_amount,
							AttributeModifier.Operation.ADD_SCALAR, equip));
				}
				if (addmult_amount != 0) {
					g_armor.add(new AttributeModifier(UUID.randomUUID(), "generic.armor", addmult_amount,
							AttributeModifier.Operation.MULTIPLY_SCALAR_1, equip));
				}

				Collection<AttributeModifier> g_hp = map.get(Attribute.GENERIC_MAX_HEALTH);
				add_amount = 0;
				scalar_amount = 0;
				addmult_amount = 0;
				for (AttributeModifier am : g_hp) {
					switch (am.getOperation()) {
					case ADD_NUMBER:
						add_amount += am.getAmount();
						break;
					case ADD_SCALAR:
						scalar_amount += am.getAmount();
						break;
					case MULTIPLY_SCALAR_1:
						addmult_amount += am.getAmount();
						break;
					default:
						break;
					}
				}
				g_hp.clear();
				hp = add_amount;
				if (scalar_amount != 0) {
					g_hp.add(new AttributeModifier(UUID.randomUUID(), "generic.maxHealth", scalar_amount,
							AttributeModifier.Operation.ADD_SCALAR, equip));
				}
				if (addmult_amount != 0) {
					g_hp.add(new AttributeModifier(UUID.randomUUID(), "generic.maxHealth", addmult_amount,
							AttributeModifier.Operation.MULTIPLY_SCALAR_1, equip));
				}

				Collection<AttributeModifier> at = map.get(Attribute.GENERIC_ARMOR_TOUGHNESS);
				add_amount = 0;
				scalar_amount = 0;
				addmult_amount = 0;
				for (AttributeModifier am : at) {
					switch (am.getOperation()) {
					case ADD_NUMBER:
						add_amount += am.getAmount();
						break;
					case ADD_SCALAR:
						scalar_amount += am.getAmount();
						break;
					case MULTIPLY_SCALAR_1:
						addmult_amount += am.getAmount();
						break;
					default:
						break;
					}
				}
				at.clear();

				armor_toughness = add_amount;
				if (scalar_amount != 0) {
					at.add(new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", scalar_amount,
							AttributeModifier.Operation.ADD_SCALAR, equip));
				}
				if (addmult_amount != 0) {
					at.add(new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", addmult_amount,
							AttributeModifier.Operation.MULTIPLY_SCALAR_1, equip));
				}

				Collection<AttributeModifier> ms = map.get(Attribute.GENERIC_MOVEMENT_SPEED);
				add_amount = 0;
				scalar_amount = 0;
				addmult_amount = 0;
				for (AttributeModifier am : ms) {
					switch (am.getOperation()) {
					case ADD_NUMBER:
						add_amount += am.getAmount();
						break;
					case ADD_SCALAR:
						scalar_amount += am.getAmount();
						break;
					case MULTIPLY_SCALAR_1:
						addmult_amount += am.getAmount();
						break;
					default:
						break;
					}
				}
				ms.clear();

				movement_speed = add_amount;
				if (scalar_amount != 0) {
					ms.add(new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", scalar_amount,
							AttributeModifier.Operation.ADD_SCALAR, equip));
				}
				if (addmult_amount != 0) {
					ms.add(new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", addmult_amount,
							AttributeModifier.Operation.MULTIPLY_SCALAR_1, equip));
				}
				meta.setAttributeModifiers(map);
				// mod attribute
				if (hp + armor + armor_toughness + movement_speed != 0) {
					item.setItemMeta(meta);
					setItemHP(item, getItemHP(item) + hp);
					setItemAR(item, getItemAR(item) + armor);
					setItemAT(item, getItemAT(item) + armor_toughness);
					setItemSD(item, getItemSD(item) + movement_speed);
				}
			}

		} catch (Exception e) {

		}
	}

	public static void setItemHP(ItemStack item, double amount) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(ModifyItem.Item_HP, PersistentDataType.DOUBLE, amount);
		item.setItemMeta(im);
	}

	public static double getItemHP(ItemStack item) {
		double range = 0;
		if (item == null || item.getType() == Material.AIR) {
			return range;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(Item_HP, PersistentDataType.DOUBLE)) {
			range += pdc.get(Item_HP, PersistentDataType.DOUBLE);
		}
		return range;
	}

	public static void setItemAR(ItemStack item, double amount) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(ModifyItem.Item_AR, PersistentDataType.DOUBLE, amount);
		item.setItemMeta(im);
	}

	public static double getItemAR(ItemStack item) {
		double range = 0;
		if (item == null || item.getType() == Material.AIR) {
			return range;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(Item_AR, PersistentDataType.DOUBLE)) {
			range += pdc.get(Item_AR, PersistentDataType.DOUBLE);
		}
		return range;
	}

	public static void setItemAT(ItemStack item, double amount) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(ModifyItem.Item_AT, PersistentDataType.DOUBLE, amount);
		item.setItemMeta(im);
	}

	public static double getItemAT(ItemStack item) {
		double range = 0;
		if (item == null || item.getType() == Material.AIR) {
			return range;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(Item_AT, PersistentDataType.DOUBLE)) {
			range += pdc.get(Item_AT, PersistentDataType.DOUBLE);
		}
		return range;
	}

	public static void setItemSD(ItemStack item, double amount) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(ModifyItem.Item_SD, PersistentDataType.DOUBLE, amount);
		item.setItemMeta(im);
	}

	public static double getItemSD(ItemStack item) {
		double range = 0;
		if (item == null || item.getType() == Material.AIR) {
			return range;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(Item_SD, PersistentDataType.DOUBLE)) {
			range += pdc.get(Item_SD, PersistentDataType.DOUBLE);
		}
		return range;
	}

	@EventHandler
	public void click(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if ((event.getAction().equals(Action.LEFT_CLICK_AIR))) {
			double range = getPlayerAttackRange(player);
			LivingEntity e = SkillAPI.getEntityInLineOfSight(player, range);
			if (e == null) {
				return;
			}
			Location loc = player.getEyeLocation();
			if (e.getLocation().distance(loc) > 4) {
				double dmg = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() * AtkModBasedCD(player)
						+ extraATKBasedEnch(player, e);
				e.damage(dmg, player);
			}
			atktime.put(player, System.currentTimeMillis());
		}
		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			atktime.put(player, System.currentTimeMillis());
		}
	}

	@EventHandler
	public void itemswitch(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		atktime.put(player, System.currentTimeMillis());
	}

	private static String LorePrefix = ChatColor.WHITE + "〕";
	private static DecimalFormat df = new DecimalFormat("#.##");
	private static String LoreLine = ChatColor.WHITE + "==============================";

	public static void LoreBuilder(ItemStack item, EquipmentSlot slot, double hp, double mp, double armor,
			double armor_toughness, double movement_speed, double attack_range, double attack_speed,
			double knockback_resistance, int castmod, double damage, int forge_time, List<String> oldLore) {
		if (!item.hasItemMeta()) {
			return;
		}
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		List<String> lore = new ArrayList<String>();
		String str = LorePrefix + "欄位: ";
		switch (slot) {
		case CHEST:
			str += "胸甲";
			break;
		case FEET:
			str += "鞋子";
			break;
		case LEGS:
			str += "褲甲";
			break;
		case HEAD:
			str += "頭盔";
			break;
		case OFF_HAND:
			str += "副手";
			break;
		case HAND:
			str += "武器";
			break;
		}
		lore.add(str);
		if (hp != 0) {
			lore.add(ModifyItem.HPLoreBuilder(hp));
		}
		if (mp != 0) {
			lore.add(ModifyItem.MPLoreBuilder(mp));
		}
		if ((int) (armor * 100) != 0) {
			lore.add(ModifyItem.ArmorLoreBuilder(armor));
		}
		if ((int) (armor_toughness * 100) != 0) {
			lore.add(ModifyItem.ArmorToughnessLoreBuilder(armor_toughness));
		}
		if ((int) (damage * 100) != 0) {
			lore.add(ModifyItem.DamageLoreBuilder(damage));
		}
		if ((int) (attack_range * 100) != 0) {
			lore.add(ModifyItem.AttackRangeLoreBuilder(attack_range));
		}
		if ((int) (attack_speed * 100) != 0) {
			lore.add(ModifyItem.AttackSpeedLoreBuilder(attack_speed));
		}
		if ((int) (movement_speed * 100) != 0) {
			lore.add(ModifyItem.MovementSpeedLoreBuilder(movement_speed));
		}
		if ((int) (knockback_resistance * 100) != 0) {
			lore.add(ModifyItem.KnockbackResistanceLoreBuilder(knockback_resistance));
		}
		if (castmod != 0) {
			lore.add(ModifyItem.CastModLoreBuilder(castmod));
		}
		if (forge_time != 0) {
			lore.add(ForgeLoreBuilder(forge_time));
		}
		if (oldLore.size() > 0) {
			boolean t = true;
			for (String l : oldLore) {
				if (!l.startsWith(ModifyItem.LorePrefix) && !l.equals(ModifyItem.LoreLine)) {
					if (t) {
						lore.add(ModifyItem.LoreLine);// 分隔線
						t = false;
					}
					lore.add(l);
				}
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public static String ForgeLoreBuilder(int amount) {
		String str = LorePrefix + "已鍛造次數: ";
		str += amount;
		return str;
	}

	public static String MPLoreBuilder(double amount) {
		String str = LorePrefix + "魔力: ";
		if (amount < 0) {
			str += df.format(amount);
		} else {
			str += "+" + df.format(amount);
		}
		return str;
	}

	public static String HPLoreBuilder(double amount) {
		String str = LorePrefix + "血量: ";
		if (amount < 0) {
			str += df.format(amount);
		} else {
			str += "+" + df.format(amount);
		}
		return str;
	}

	public static String ArmorLoreBuilder(double amount) {
		String str = LorePrefix + "裝甲: ";
		if (amount < 0) {
			str += df.format(amount);
		} else {
			str += "+" + df.format(amount);
		}
		return str;
	}

	public static String ArmorToughnessLoreBuilder(double amount) {
		String str = LorePrefix + "抗性: ";
		if (amount < 0) {
			str += df.format(amount);
		} else {
			str += "+" + df.format(amount);
		}
		return str;
	}

	public static String MovementSpeedLoreBuilder(double amount) {
		String str = LorePrefix + "移動速度: ";
		if (amount < 0) {
			str += df.format(amount);
		} else {
			str += "+" + df.format(amount);
		}
		return str;
	}

	public static String AttackRangeLoreBuilder(double amount) {
		String str = LorePrefix + "攻擊距離: ";
		if (amount < 0) {
			str += df.format(amount);
		} else {
			str += "+" + df.format(amount);
		}
		return str;
	}

	public static String AttackSpeedLoreBuilder(double amount) {
		String str = LorePrefix + "攻擊速度: ";
		if (amount < 0) {
			str += df.format(amount);
		} else {
			str += "+" + df.format(amount);
		}
		return str;
	}

	public static String KnockbackResistanceLoreBuilder(double amount) {
		String str = LorePrefix + "抗擊退機率: ";
		amount *= 100;
		if (amount < 0) {
			str += ((int) amount) + "%";
		} else {
			str += "+" + ((int) amount) + "%";
		}
		return str;
	}

	public static String CastModLoreBuilder(int amount) {
		String str = LorePrefix + "技能詠唱次數需求";
		if (amount < 0) {
			str += "增加: " + df.format(Math.abs(amount));
		} else {
			str += "減少: " + df.format(amount);
		}
		return str;
	}

	public static String DamageLoreBuilder(double amount) {
		String str = LorePrefix + "傷害: ";
		if (amount < 0) {
			str += df.format(amount);
		} else {
			str += "+" + df.format(amount);
		}
		return str;
	}

	public static double getPlayerAttackRange(Player player) {
		if (edit.containsKey(player)) {
			return edit.get(player);
		}
		ItemStack item = player.getInventory().getItemInMainHand();
		double range = Utils.defaultattackrange;
		if (item == null || item.getType() == Material.AIR) {
			return range;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(Item_Range, PersistentDataType.DOUBLE)) {
			range += pdc.get(Item_Range, PersistentDataType.DOUBLE);
		}
		return range;
	}

	public static double getItemRangeMod(ItemStack item) {
		double range = 0;
		if (item == null || item.getType() == Material.AIR) {
			return range;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(Item_Range, PersistentDataType.DOUBLE)) {
			range += pdc.get(Item_Range, PersistentDataType.DOUBLE);
		}
		return range;
	}

	public static double getItemMP(ItemStack item) {
		double range = 0;
		if (item == null || item.getType() == Material.AIR) {
			return range;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(Item_MP, PersistentDataType.DOUBLE)) {
			range += pdc.get(Item_MP, PersistentDataType.DOUBLE);
		}
		return range;
	}

	public static void setItemRangeMod(ItemStack item, double range) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(ModifyItem.Item_Range, PersistentDataType.DOUBLE, range);
		item.setItemMeta(im);
	}

	public static void setItemMP(ItemStack item, double amount) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(ModifyItem.Item_MP, PersistentDataType.DOUBLE, amount);
		item.setItemMeta(im);
	}

	public static void setItemCastMod(ItemStack item, double time) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(ModifyItem.Castkey, PersistentDataType.DOUBLE, time);
		item.setItemMeta(im);
	}

	public static int getItemCastMod(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return 0;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(ModifyItem.Castkey, PersistentDataType.DOUBLE)) {
			return pdc.get(ModifyItem.Castkey, PersistentDataType.DOUBLE).intValue();
		}
		return 0;
	}

	public static double AtkModBasedCD(Player player) {
		if (!atktime.containsKey(player)) {
			return 1;
		}
		long o = atktime.get(player);
		long n = System.currentTimeMillis();
		double t = (n - o) / 50.0;
		double T = 1 / player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue() * 20;
		double mult = 0.2 + ((t + 0.5) / T) * ((t + 0.5) / T) * 0.8;
		if (mult > 1) {
			mult = 1;
		}
		return mult;
	}

	public static double extraATKBasedEnch(Player player, LivingEntity e) {
		double dmg = 0;
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.containsEnchantment(Enchantment.DAMAGE_ALL)) {
			dmg += item.getEnchantmentLevel(Enchantment.DAMAGE_ALL) * 0.5;
		}
		EntityType type = e.getType();
		if (type == EntityType.SPIDER || type == EntityType.CAVE_SPIDER || type == EntityType.ENDERMITE
				|| type == EntityType.SILVERFISH) {
			if (item.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS)) {
				dmg += item.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS) * 2.5;
			}
		}
		if (type == EntityType.SKELETON || type == EntityType.SKELETON_HORSE || type == EntityType.STRAY
				|| type == EntityType.WITHER_SKELETON || type == EntityType.WITHER || type == EntityType.ZOMBIE
				|| type == EntityType.HUSK || type == EntityType.ZOMBIE_VILLAGER || type == EntityType.ZOMBIFIED_PIGLIN
				|| type == EntityType.DROWNED || type == EntityType.ZOMBIE_HORSE || type == EntityType.PHANTOM) {
			if (item.containsEnchantment(Enchantment.DAMAGE_UNDEAD)) {
				dmg += item.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD) * 2.5;
			}
		}
		double mult;
		if (!atktime.containsKey(player)) {
			mult = 1;
		} else {
			long o = atktime.get(player);
			long n = System.currentTimeMillis();
			long t = (n - o) / 50;
			double T = 1 / player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue() * 20;
			mult = 0.2 + ((t + 0.5) / T) * 0.8;
			if (mult > 1) {
				mult = 1;
			}
		}
		return dmg * mult;
	}

	public static NamespacedKey Potion_HP = new NamespacedKey(Main.getPlugin(), "NRPG_Potion_HP");
	public static NamespacedKey Potion_MP = new NamespacedKey(Main.getPlugin(), "NRPG_Potion_MP");

	public static int getPotionMP(ItemStack item) {
		int range = 0;
		if (item == null || item.getType() == Material.AIR) {
			return range;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(Potion_MP, PersistentDataType.INTEGER)) {
			range += pdc.get(Potion_MP, PersistentDataType.INTEGER);
		}
		return range;
	}

	public static double getPotionHP(ItemStack item) {
		int range = 0;
		if (item == null || item.getType() == Material.AIR) {
			return range;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(Potion_HP, PersistentDataType.INTEGER)) {
			range += pdc.get(Potion_HP, PersistentDataType.INTEGER);
		}
		return range;
	}

	public static void setPotionHP(ItemStack item, int amount) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(Potion_HP, PersistentDataType.INTEGER, amount);
		item.setItemMeta(im);
	}

	public static void setPotionMP(ItemStack item, int amount) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(Potion_MP, PersistentDataType.INTEGER, amount);
		item.setItemMeta(im);
	}

	public static NamespacedKey SkillBook_key = new NamespacedKey(Main.getPlugin(), "NRPG_SkillBook_Skillname");

	public static String getSkillBookSkill(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return null;
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(SkillBook_key, PersistentDataType.STRING)) {
			return pdc.get(SkillBook_key, PersistentDataType.STRING);
		}
		return null;
	}

	public static void setSkillBookSkill(ItemStack item, String amount) {
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(SkillBook_key, PersistentDataType.STRING, amount);
		item.setItemMeta(im);
	}
}
