package edited.com.codingforcookies.armorequip;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.inventory.ItemStack;

public class DispenserArmorListener implements Listener {
	@EventHandler
	public void dispenseArmorEvent(BlockDispenseArmorEvent event) {
		ArmorType type = ArmorType.matchType(event.getItem());
		if ((type != null) && ((event.getTargetEntity() instanceof Player))) {
			Player p = (Player) event.getTargetEntity();
			ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DISPENSER, type, null,
					event.getItem());
			Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
			if (armorEquipEvent.isCancelled()) {
				event.setCancelled(true);
			} else {
				event.setCancelled(true);
				ItemStack item = event.getItem().clone();
				switch (type) {
				case CHESTPLATE:
					p.getInventory().setChestplate(item);
					break;
				case BOOTS:
					p.getInventory().setBoots(item);
					break;
				case HELMET:
					p.getInventory().setHelmet(item);
					break;
				case LEGGINGS:
					p.getInventory().setLeggings(item);
					break;
				}
				AfterArmorEquipEvent ae = new AfterArmorEquipEvent(p, armorEquipEvent);
				Bukkit.getServer().getPluginManager().callEvent(ae);
			}
		}
	}
}
