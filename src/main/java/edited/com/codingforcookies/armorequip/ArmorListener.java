package edited.com.codingforcookies.armorequip;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import nashi.NRPG.Main;
import nashi.NRPG.API.Utils;

public class ArmorListener implements Listener {
	private final List<String> blockedMaterials;

	public ArmorListener(List<String> blockedMaterials) {
		this.blockedMaterials = blockedMaterials;
	}

	@EventHandler
	public final void inventoryClick(InventoryClickEvent e) {
		boolean shift = false;
		boolean numberkey = false;
		if (e.isCancelled()) {
			return;
		}
		if (e.getAction() == InventoryAction.NOTHING) {
			return;
		}
		if ((e.getClick().equals(ClickType.SHIFT_LEFT)) || (e.getClick().equals(ClickType.SHIFT_RIGHT))) {
			shift = true;
		}
		if (e.getClick().equals(ClickType.NUMBER_KEY)) {
			numberkey = true;
		}
		if ((e.getSlotType() != InventoryType.SlotType.ARMOR) && (e.getSlotType() != InventoryType.SlotType.QUICKBAR)
				&& (e.getSlotType() != InventoryType.SlotType.CONTAINER)) {
			return;
		}
		if ((e.getClickedInventory() != null) && (!e.getClickedInventory().getType().equals(InventoryType.PLAYER))) {
			return;
		}
		if ((!e.getInventory().getType().equals(InventoryType.CRAFTING))
				&& (!e.getInventory().getType().equals(InventoryType.PLAYER))) {
			return;
		}
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		ArmorType newArmorType = ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
		if ((!shift) && (newArmorType != null) && (e.getRawSlot() != newArmorType.getSlot())) {
			return;
		}
		if (shift) {
			newArmorType = ArmorType.matchType(e.getCurrentItem());
			if (newArmorType != null) {
				boolean equipping = true;
				if (e.getRawSlot() == newArmorType.getSlot()) {
					equipping = false;
				}
				if ((!newArmorType.equals(ArmorType.HELMET))
						|| (equipping ? !isAirOrNull(e.getWhoClicked().getInventory().getHelmet())
								: isAirOrNull(e.getWhoClicked().getInventory().getHelmet()))) {
					if ((!newArmorType.equals(ArmorType.CHESTPLATE))
							|| (equipping ? !isAirOrNull(e.getWhoClicked().getInventory().getChestplate())
									: isAirOrNull(e.getWhoClicked().getInventory().getChestplate()))) {
						if ((!newArmorType.equals(ArmorType.LEGGINGS))
								|| (equipping ? !isAirOrNull(e.getWhoClicked().getInventory().getLeggings())
										: isAirOrNull(e.getWhoClicked().getInventory().getLeggings()))) {
							if ((!newArmorType.equals(ArmorType.BOOTS))
									|| (equipping ? !isAirOrNull(e.getWhoClicked().getInventory().getBoots())
											: isAirOrNull(e.getWhoClicked().getInventory().getBoots()))) {
								return;
							}
						}
					}
				}
				ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(),
						ArmorEquipEvent.EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : e.getCurrentItem(),
						equipping ? e.getCurrentItem() : null);
				Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
				if (armorEquipEvent.isCancelled()) {
					e.setCancelled(true);
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
					@Override
					public void run() {
						AfterArmorEquipEvent ae = new AfterArmorEquipEvent((Player) e.getWhoClicked(), armorEquipEvent);
						Bukkit.getServer().getPluginManager().callEvent(ae);
					}
				}, 5L);
			}
		} else {
			ItemStack newArmorPiece = e.getCursor();
			ItemStack oldArmorPiece = e.getCurrentItem();
			if (numberkey) {
				if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
					ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
					if (!isAirOrNull(hotbarItem)) {
						newArmorType = ArmorType.matchType(hotbarItem);
						newArmorPiece = hotbarItem;
						oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
					} else {
						newArmorType = ArmorType
								.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
					}
				}
			} else if ((isAirOrNull(e.getCursor())) && (!isAirOrNull(e.getCurrentItem()))) {
				newArmorType = ArmorType.matchType(e.getCurrentItem());
			}
			if ((newArmorType != null) && (e.getRawSlot() == newArmorType.getSlot())) {
				ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.PICK_DROP;
				if ((e.getAction().equals(InventoryAction.HOTBAR_SWAP)) || (numberkey)) {
					method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
				}
				ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, newArmorType,
						oldArmorPiece, newArmorPiece);
				Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
				if (armorEquipEvent.isCancelled()) {
					e.setCancelled(true);
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
					@Override
					public void run() {
						AfterArmorEquipEvent ae = new AfterArmorEquipEvent((Player) e.getWhoClicked(), armorEquipEvent);
						Bukkit.getServer().getPluginManager().callEvent(ae);
					}
				}, 5L);
			}
		}
	}

	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e) {
		if (e.getAction() == Action.PHYSICAL) {
			return;
		}
		if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			Player player = e.getPlayer();
			Material mat;
			if ((e.getClickedBlock() != null) && (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
				mat = e.getClickedBlock().getType();
				for (String s : this.blockedMaterials) {
					if (mat.name().equalsIgnoreCase(s)) {
						return;
					}
				}
			}
			ArmorType newArmorType = ArmorType.matchType(e.getItem());
			if ((newArmorType != null) && (((newArmorType.equals(ArmorType.HELMET))
					&& (isAirOrNull(e.getPlayer().getInventory().getHelmet())))
					|| ((newArmorType.equals(ArmorType.CHESTPLATE))
							&& (isAirOrNull(e.getPlayer().getInventory().getChestplate())))
					|| ((newArmorType.equals(ArmorType.LEGGINGS))
							&& (isAirOrNull(e.getPlayer().getInventory().getLeggings())))
					|| ((newArmorType.equals(ArmorType.BOOTS))
							&& (isAirOrNull(e.getPlayer().getInventory().getBoots()))))) {
				ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), ArmorEquipEvent.EquipMethod.HOTBAR,
						ArmorType.matchType(e.getItem()), null, e.getItem());
				Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
				if (armorEquipEvent.isCancelled()) {
					e.setCancelled(true);
					player.updateInventory();
				} else {
					switch (newArmorType) {
					case CHESTPLATE:
						player.getInventory().setChestplate(e.getItem().clone());
						break;
					case BOOTS:
						player.getInventory().setBoots(e.getItem().clone());
						break;
					case HELMET:
						player.getInventory().setHelmet(e.getItem().clone());
						break;
					case LEGGINGS:
						player.getInventory().setLeggings(e.getItem().clone());
						break;
					}
					e.getItem().setAmount(0);
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
						@Override
						public void run() {
							AfterArmorEquipEvent ae = new AfterArmorEquipEvent(player, armorEquipEvent);
							Bukkit.getServer().getPluginManager().callEvent(ae);
						}
					}, 5L);
				}
			}
		}
	}

	@EventHandler
	public void inventoryDrag(InventoryDragEvent event) {
		ArmorType type = ArmorType.matchType(event.getOldCursor());
		if (event.getRawSlots().isEmpty()) {
			return;
		}
		if ((type != null)
				&& (type.getSlot() == ((Integer) event.getRawSlots().stream().findFirst().orElse(Integer.valueOf(0)))
						.intValue())) {
			ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked(),
					ArmorEquipEvent.EquipMethod.DRAG, type, null, event.getOldCursor());
			Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
			if (armorEquipEvent.isCancelled()) {
				event.setResult(Result.DENY);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void itemBreakEvent(PlayerItemBreakEvent e) {
		ArmorType type = ArmorType.matchType(e.getBrokenItem());
		if (type != null) {
			Player p = e.getPlayer();
			ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.BROKE, type,
					e.getBrokenItem(), null);
			Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
			if (armorEquipEvent.isCancelled()) {
				ItemStack i = e.getBrokenItem().clone();
				i.setAmount(1);

				ItemMeta im = i.getItemMeta();
				((Damageable) im).setDamage(((Damageable) im).getDamage() - 1);
				i.setItemMeta(im);

				if (type.equals(ArmorType.HELMET)) {
					p.getInventory().setHelmet(i);
				} else if (type.equals(ArmorType.CHESTPLATE)) {
					p.getInventory().setChestplate(i);
				} else if (type.equals(ArmorType.LEGGINGS)) {
					p.getInventory().setLeggings(i);
				} else if (type.equals(ArmorType.BOOTS)) {
					p.getInventory().setBoots(i);
				}
			} else {
				switch (type) {
				case CHESTPLATE:
					p.getInventory().setChestplate(null);
					break;
				case BOOTS:
					p.getInventory().setBoots(null);
					break;
				case HELMET:
					p.getInventory().setHelmet(null);
					break;
				case LEGGINGS:
					p.getInventory().setLeggings(null);
					break;
				}
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 2, 2);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
					@Override
					public void run() {
						AfterArmorEquipEvent ae = new AfterArmorEquipEvent(p, armorEquipEvent);
						Bukkit.getServer().getPluginManager().callEvent(ae);
					}
				}, 5L);
			}
		}
	}

	@EventHandler
	public void playerDeathEvent(PlayerDeathEvent e) {
		Player p = e.getEntity();
		for (ItemStack i : p.getInventory().getArmorContents()) {
			if (!isAirOrNull(i)) {
				Bukkit.getServer().getPluginManager().callEvent(
						new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DEATH, ArmorType.matchType(i), i, null));
			}
		}
	}

	private boolean isAirOrNull(ItemStack item) {
		return (item == null) || (item.getType().equals(Material.AIR) || Utils.isNotDrop(item));
	}
}
