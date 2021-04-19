package nashi.NRPG.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import nashi.NRPG.Main;
import nashi.NRPG.API.SkillAPI;
import nashi.NRPG.Players.Players;
import nashi.NRPG.Skills.Cast;
import nashi.NRPG.Skills.Skill;
import nashi.NRPG.Skills.SkillItem;
import nashi.NRPG.listeners.EventListener.Casting;

public class SkillItemListener implements Listener {

	@EventHandler
	public void use(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (EventListener.cast.containsKey(player)) {
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		long cooldowntime = 100L;
		if (EventListener.scooldowns.containsKey(player.getUniqueId())
				&& System.currentTimeMillis() - EventListener.scooldowns.get(player.getUniqueId()) <= cooldowntime) {
			return;
		}
		try {
			ItemStack item = player.getInventory().getItemInMainHand();
			int limit = SkillItem.getSkillLimitTimeUse(item);
			if (limit == 0) {
				return;
			}
			EventListener.scooldowns.put(player.getUniqueId(), System.currentTimeMillis());
			String sk = SkillItem.getSkill(item);
			if (sk == null) {
				return;
			}
			event.setCancelled(true);
			World world = player.getWorld();
			try {
				if (Main.worlds.contains(world)) {
					player.sendMessage(ChatColor.RED + "你無法在這個世界使用技能");
					return;
				}
			} catch (Exception e) {
			}
			if (SkillAPI.Silence.containsKey(player.getUniqueId())) {
				double secondsLeft = SkillAPI.Silence.get(player.getUniqueId()) - System.currentTimeMillis();
				if (secondsLeft > 0) {
					player.sendMessage(ChatColor.RED + "你已被沉默 " + Skill.formatter.format((secondsLeft / 1000)) + " 秒");
					return;
				}
			}
			if (Skill.cooldowns.containsKey(player.getUniqueId() + "." + sk)) {
				double secondsLeft = Skill.cooldowns.get(player.getUniqueId() + "." + sk) - System.currentTimeMillis();
				if (secondsLeft > 0) {
					player.sendMessage(
							ChatColor.RED + "技能冷卻時間剩餘:" + Skill.formatter.format((secondsLeft / 1000)) + " 秒");
					return;
				}
			}
			double cost = SkillAPI.getPublicSkillInfo(sk, "CostMP");
			Players.PlayerData pd = Players.Data.get(player);
			if (pd.getCurrentMP() - cost < 0.0) {
				player.sendMessage(ChatColor.RED + "魔力不足");
				return;
			}
			int difficulty = SkillItem.getSkillDifficulty(item);
			if (difficulty == -1) {
				difficulty = (int) SkillAPI.getPublicSkillInfo(sk, "Difficulty");
			}
			int req = SkillItem.getSkillTime(item);
			if (req != -1) {
				if (req == 0) {
					Skill.cast(sk, player, true);
				} else {
					long time = SkillAPI.getRemainingCooldown(player, sk);
					if (time > 0L) {
						player.sendMessage(ChatColor.RED + "技能冷卻時間剩餘:" + Skill.formatter.format((time / 1000)) + " 秒");
						return;
					}
					Cast.Abracadabra abra = Cast.getabracadabra((difficulty));
					EventListener.playercast(player, new Casting(sk, abra, req), (Cast.castspeed.get(player)),
							Cast.timelimit);
				}
			} else {
				if (SkillAPI.getPublicSkillInfo(sk, "Times") < 1.0) {
					Skill.cast(sk, player, true);
				} else {
					long time = SkillAPI.getRemainingCooldown(player, sk);
					if (time > 0L) {
						player.sendMessage(ChatColor.RED + "技能冷卻時間剩餘:" + Skill.formatter.format((time / 1000)) + " 秒");
						return;
					}
					Cast.Abracadabra abra = Cast.getabracadabra((difficulty));
					EventListener.playercast(player,
							new Casting(sk, abra, (int) SkillAPI.getPublicSkillInfo(sk, "Times")),
							(Cast.castspeed.get(player)), Cast.timelimit);
				}
			}
			SkillItem.setSkillLimitTimeUse(item, limit - 1);
			if (limit - 1 == 0) {
				ItemMeta im = item.getItemMeta();
				List<String> lore;
				if (im.hasLore()) {
					lore = im.getLore();
				} else {
					lore = new ArrayList<String>();
				}
				lore.add(ChatColor.RED + "技能次數已耗盡");
				im.setLore(lore);
				item.setItemMeta(im);
			}
			int damage = SkillItem.getUseDamage(item);
			if (damage > 0) {
				Damageable dmg = ((Damageable) item.getItemMeta());
				dmg.setDamage(dmg.getDamage() + damage);
				item.setItemMeta((ItemMeta) dmg);
				if (item.getType().getMaxDurability() < dmg.getDamage()) {
					item.setAmount(0);
				}
			}
		} catch (NullPointerException sk) {
			// empty catch block
		}
	}
}
