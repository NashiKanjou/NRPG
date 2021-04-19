package nashi.NRPG.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Multimap;

import nashi.NRPG.Main;
import nashi.NRPG.API.Utils;
import nashi.NRPG.Items.CreateItem;
import nashi.NRPG.Items.CreateItem.Technique;

public class ForgeListener implements Listener {

	@EventHandler
	public void openGUI(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!(player.isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& (event.getClickedBlock().getType() == Material.ANVIL
						|| event.getClickedBlock().getType() == Material.CHIPPED_ANVIL
						|| event.getClickedBlock().getType() == Material.DAMAGED_ANVIL))) {
			return;
		}
		event.setCancelled(true);
		Inventory inv = Bukkit.createInventory(player, InventoryType.FURNACE, "裝備/武器鍛造");
		player.openInventory(inv);
	}

	@EventHandler
	public void ForgeGUI(InventoryClickEvent event) {
		if (!event.getView().getTitle().equals("裝備/武器鍛造")) {
			return;
		}
		event.setCancelled(true);
		Inventory inv = event.getView().getTopInventory();
		if (event.getCurrentItem() == null) {
			return;
		}
		ItemStack clicked = event.getCurrentItem().clone();
		if (!clicked.hasItemMeta()) {
			return;
		}
		Player player = (Player) event.getWhoClicked();
		ItemStack item = inv.getItem(0);
		ItemStack mat = inv.getItem(1);
		if (clicked.getItemMeta().getPersistentDataContainer().has(CreateItem.key_click, PersistentDataType.INTEGER)) {
			int time = CreateItem.getItemForgeTime(item);
			if (CreateItem.max - time < 1) {
				player.sendMessage(ChatColor.RED + "此道具已無法繼續鍛造");
				player.closeInventory();
				return;
			}
			int key = CreateItem.getItemForgeKey(item);
			CreateItem.setItemForgeKey(item, -1);
			if (key != -1) {
				CreateItem.setItemForgeTime(item, time + 1);
				int a = mat.getAmount();
				Technique tech = getTechnique(item, mat, key);
				if (tech.isRandom()) {
					int bond = (int) (tech.Rand_Max - tech.Rand_Min);

					double r = Utils.rand.nextInt(bond) + tech.Rand_Min;

					double[] raw = new double[10];
					raw[0] = r * tech.MAX_MANA;
					r = Utils.rand.nextInt(bond) + tech.Rand_Min;
					raw[1] = r * tech.MAX_HEALTH;
					r = Utils.rand.nextInt(bond) + tech.Rand_Min;
					raw[2] = r * tech.ARMOR_TOUGHNESS;
					r = Utils.rand.nextInt(bond) + tech.Rand_Min;
					raw[3] = r * tech.ARMOR;
					r = Utils.rand.nextInt(bond) + tech.Rand_Min;
					raw[4] = r * tech.ATTACK_DAMAGE;
					r = Utils.rand.nextInt(bond) + tech.Rand_Min;
					raw[5] = r * tech.ATTACK_SPEED;
					r = Utils.rand.nextInt(bond) + tech.Rand_Min;
					raw[6] = r * tech.ATTACK_RANGE;
					r = Utils.rand.nextInt(bond) + tech.Rand_Min;
					raw[7] = r * tech.KNOCKBACK_RESISTANCE;
					r = Utils.rand.nextInt(bond) + tech.Rand_Min;
					raw[8] = r * tech.MOVEMENT_SPEED;
					r = Utils.rand.nextInt(bond) + tech.Rand_Min;
					raw[9] = r * tech.CAST_MOD;
					tech = new Technique(raw, CreateItem.getItemForgeSlot(item));
				}
				Technique t = getLimitTechnique(item);
				if (a > 1) {
					mat.setAmount(a - 1);
					inv.setItem(1, mat);
				} else {
					inv.setItem(1, null);
				}
				ItemMeta meta = item.getItemMeta();
				EquipmentSlot equip = tech.slot;
				int castmod = 0;
				double mp = 0, hp = 0, armor = 0, armor_toughness = 0, movement_speed = 0, attack_range = 0,
						attack_speed = 0, knockback_resistance = 0, damage = 0;
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
					g_armor.clear();
					if (add_amount + tech.ARMOR != 0) {
						double amount = add_amount + tech.ARMOR;
						armor = amount;
					}
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
					if (add_amount + tech.MAX_HEALTH != 0) {
						double amount = add_amount + tech.MAX_HEALTH;
						hp = amount;
					}
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
					if (add_amount + tech.ARMOR_TOUGHNESS != 0) {
						double amount = add_amount + tech.ARMOR_TOUGHNESS;
						armor_toughness = amount;
					}
					if (scalar_amount != 0) {
						at.add(new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", scalar_amount,
								AttributeModifier.Operation.ADD_SCALAR, equip));
					}
					if (addmult_amount != 0) {
						at.add(new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", addmult_amount,
								AttributeModifier.Operation.MULTIPLY_SCALAR_1, equip));
					}

					Collection<AttributeModifier> ad = map.get(Attribute.GENERIC_ATTACK_DAMAGE);
					add_amount = 0;
					scalar_amount = 0;
					addmult_amount = 0;
					for (AttributeModifier am : ad) {
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
					ad.clear();
					if (add_amount + tech.ATTACK_DAMAGE != 0) {
						double amount = add_amount + tech.ATTACK_DAMAGE;
						if (amount > t.ATTACK_DAMAGE) {
							amount = t.ATTACK_DAMAGE;
						}
						damage = amount;
						ad.add(new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", amount,
								AttributeModifier.Operation.ADD_NUMBER, equip));
					}
					if (scalar_amount != 0) {
						ad.add(new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", scalar_amount,
								AttributeModifier.Operation.ADD_SCALAR, equip));
					}
					if (addmult_amount != 0) {
						ad.add(new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", addmult_amount,
								AttributeModifier.Operation.MULTIPLY_SCALAR_1, equip));
					}

					Collection<AttributeModifier> as = map.get(Attribute.GENERIC_ATTACK_SPEED);
					add_amount = 0;
					scalar_amount = 0;
					addmult_amount = 0;
					for (AttributeModifier am : as) {
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
					as.clear();
					if (add_amount + tech.ATTACK_SPEED != 0) {
						double amount = add_amount + tech.ATTACK_SPEED;
						if (amount > t.ATTACK_SPEED) {
							amount = t.ATTACK_SPEED;
						}
						attack_speed = amount;
						as.add(new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", amount,
								AttributeModifier.Operation.ADD_NUMBER, equip));
					}
					if (scalar_amount != 0) {
						as.add(new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", scalar_amount,
								AttributeModifier.Operation.ADD_SCALAR, equip));
					}
					if (addmult_amount != 0) {
						as.add(new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", addmult_amount,
								AttributeModifier.Operation.MULTIPLY_SCALAR_1, equip));
					}

					Collection<AttributeModifier> kr = map.get(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
					add_amount = 0;
					scalar_amount = 0;
					addmult_amount = 0;
					for (AttributeModifier am : kr) {
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
					kr.clear();
					if (add_amount + tech.KNOCKBACK_RESISTANCE != 0) {
						double amount = add_amount + tech.KNOCKBACK_RESISTANCE;
						if (amount > t.KNOCKBACK_RESISTANCE) {
							amount = t.KNOCKBACK_RESISTANCE;
						}
						knockback_resistance = amount;
						kr.add(new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", amount,
								AttributeModifier.Operation.ADD_NUMBER, equip));
					}
					if (scalar_amount != 0) {
						kr.add(new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", scalar_amount,
								AttributeModifier.Operation.ADD_SCALAR, equip));
					}
					if (addmult_amount != 0) {
						kr.add(new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", addmult_amount,
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
					if (add_amount + tech.MOVEMENT_SPEED != 0) {
						double amount = add_amount + tech.MOVEMENT_SPEED;
						if (amount > t.MOVEMENT_SPEED) {
							amount = t.MOVEMENT_SPEED;
						}
						movement_speed = amount;
						if (equip.equals(EquipmentSlot.HAND)) {
							ms.add(new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", amount,
									AttributeModifier.Operation.ADD_NUMBER, equip));
						}
					}
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

				} else {// if no attribute then add
					meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(),
							"generic.attackDamage", tech.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_NUMBER, equip));
					meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(),
							"generic.attackSpeed", tech.ATTACK_SPEED, AttributeModifier.Operation.ADD_NUMBER, equip));
					meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
							new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance",
									tech.KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADD_NUMBER, equip));
					if (equip.equals(EquipmentSlot.HAND)) {
						meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
								(new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", tech.MOVEMENT_SPEED,
										AttributeModifier.Operation.ADD_NUMBER, equip)));
					}
					hp = tech.MAX_HEALTH;
					armor = tech.ARMOR;
					armor_toughness = tech.ARMOR_TOUGHNESS;
					movement_speed = tech.MOVEMENT_SPEED;
					attack_speed = tech.ATTACK_SPEED;
					knockback_resistance = tech.KNOCKBACK_RESISTANCE;
					damage = tech.ATTACK_DAMAGE;
				}

				item.setItemMeta(meta);
				double cast = ModifyItem.getItemCastMod(item) + tech.CAST_MOD;
				double range = ModifyItem.getItemRangeMod(item) + tech.ATTACK_RANGE;
				double mana = ModifyItem.getItemMP(item) + tech.MAX_MANA;
				if (cast != 0) {
					if (cast > t.CAST_MOD) {
						cast = t.CAST_MOD;
					}
					ModifyItem.setItemCastMod(item, cast);
					castmod = (int) cast;
				}
				if (range != 0) {
					if (range > t.ATTACK_RANGE) {
						range = t.ATTACK_RANGE;
					}
					ModifyItem.setItemRangeMod(item, range);
					attack_range = range;
				}
				if (mana != 0) {
					if (mana > t.MAX_MANA) {
						mana = t.MAX_MANA;
					}
					ModifyItem.setItemMP(item, mana);
					mp = mana;
				}
				// weapon mod
				player.getLocation().getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 3, 1);
				ItemMeta im = item.getItemMeta();
				List<String> lore;
				if (im.hasLore()) {
					lore = im.getLore();
				} else {
					lore = new ArrayList<String>();
				}
				hp += ModifyItem.getItemHP(item);
				armor += ModifyItem.getItemAR(item);
				armor_toughness += ModifyItem.getItemAT(item);
				movement_speed += ModifyItem.getItemSD(item);
				if (movement_speed > t.MOVEMENT_SPEED) {
					movement_speed = t.MOVEMENT_SPEED;
				}
				if (hp > t.MAX_HEALTH) {
					hp = t.MAX_HEALTH;
				}
				if (armor > t.ARMOR) {
					armor = t.ARMOR;
				}
				if (armor_toughness > t.ARMOR_TOUGHNESS) {
					armor_toughness = t.ARMOR_TOUGHNESS;
				}
				ModifyItem.setItemHP(item, hp);
				ModifyItem.setItemAR(item, armor);
				ModifyItem.setItemAT(item, armor_toughness);
				if (!equip.equals(EquipmentSlot.HAND)) {
					ModifyItem.setItemSD(item, movement_speed);
				}
				ModifyItem.LoreBuilder(item, tech.slot, hp, mp, armor, armor_toughness, movement_speed, attack_range,
						attack_speed, knockback_resistance, castmod, damage, time + 1, lore);
			}
			return;
		}
		int clickSlot = event.getSlot();
		int type = CreateItem.getItemForgeSlot(clicked);
		if (type != -1) {
			int oldtype = CreateItem.getItemForgeSlot(inv.getItem(0));
			if (oldtype != -1) {
				return;
			} else {
				ItemStack stack = clicked.clone();
				stack.setAmount(1);
				inv.setItem(0, stack);
				int amount = clicked.getAmount();
				if (amount > 1) {
					clicked.setAmount(amount - 1);
					player.getInventory().setItem(clickSlot, clicked);
				} else {
					player.getInventory().setItem(clickSlot, null);
				}
			}
		} else {
			String oldtier = getItemForgeMaterialTier(inv.getItem(1));
			if (oldtier == "-1") {
				String tier = getItemForgeMaterialTier(clicked);
				if (tier != "-1") {
					inv.setItem(1, clicked);
					player.getInventory().setItem(clickSlot, null);
				}
			} else {
				return;
			}
		}

		item = inv.getItem(0);
		mat = inv.getItem(1);
		if (item != null && mat != null) {
			inv.setItem(2, CreateItem.click.clone());
			PlayerForging(player, inv, item);
		} else {
			inv.setItem(2, null);
		}

	}

	private static Technique getTechnique(ItemStack item, ItemStack material, int note) {
		String tier = getItemForgeMaterialTier(material);
		int slot = CreateItem.getItemForgeSlot(item);
		String str = slot + "." + tier;
		if (CreateItem.technique.containsKey(str)) {
			return CreateItem.technique.get(str)[note];
		} else if (CreateItem.rand_technique.containsKey(tier)) {
			return CreateItem.rand_technique.get(tier)[slot];
		} else {
			System.out.println("Wrong Config? Error: \"" + tier + "\"");
			return new Technique("0,0,0,0,0,0,0,0,0,0", slot);
		}
	}

	private static Technique getLimitTechnique(ItemStack item) {
		int slot = CreateItem.getItemForgeSlot(item);
		String str = "" + slot;
		if (CreateItem.limits.containsKey(str)) {
			return CreateItem.limits.get(str);
		} else {
			return new Technique("0,0,0,0,0,0,0,0,0,0", slot);
		}
	}

	private static String getItemForgeMaterialTier(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return "-1";
		}
		ItemMeta im = item.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		if (pdc.has(CreateItem.key_material, PersistentDataType.STRING)) {
			return pdc.get(CreateItem.key_material, PersistentDataType.STRING);
		}
		return "-1";
	}

	private static Note getNoteFromInt(int i) {
		return CreateItem.notes[i];
	}

	private static int getRandomIntforNote() {
		return Utils.rand.nextInt(CreateItem.size);
	}

	private static void playNote(Note note, Player player) {
		player.playNote(player.getLocation(), Instrument.BIT, note);
	}

	private static void PlayerForging(Player player, Inventory inv, ItemStack item) {
		int time = CreateItem.getItemForgeTime(item);
		if (CreateItem.max - time < 1) {
			player.sendMessage(ChatColor.RED + "此道具已無法繼續鍛造");
			player.closeInventory();
			return;
		}
		int i = getRandomIntforNote();
		Note note = getNoteFromInt(i);
		CreateItem.setItemForgeKey(item, i);
		playNote(note, player);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (inv.getItem(0) != null && inv.getItem(1) != null) {
					if (inv.getItem(0).equals(item)) {
						PlayerForging(player, inv, item);
					}
				}
			}
		}.runTaskLater(Main.getPlugin(), CreateItem.rate);
	}

	@EventHandler
	public void leave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		player.closeInventory();
	}

	@EventHandler
	public void closeEvent(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Inventory inv = event.getInventory();
		if (!event.getView().getTitle().equals("裝備/武器鍛造")) {
			return;
		}
		Utils.additem(player, inv.getItem(0));
		Utils.additem(player, inv.getItem(1));
		inv.setItem(0, null);
		inv.setItem(1, null);
		inv.setItem(2, null);
	}
}
