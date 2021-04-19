package nashi.NRPG.listeners;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityShootBowEvent;

import nashi.NRPG.Main;
import nashi.NRPG.API.Utils;
import nashi.NRPG.Players.Players;
import nashi.NRPG.Players.Players.PlayerData;

@SuppressWarnings("deprecation")
public class AllAboutDamage implements Listener {
	public static NamespacedKey key = new NamespacedKey(Main.getPlugin(), "NRPG_ItemsNotDrop");

	@EventHandler
	public void hunger(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.STARVATION) {
			e.setDamage(Utils.hunger);
		}
	}

	public static double damagecalc(double damage, double defense, double toughness) {
		double max = Math.max(defense / 5, defense - damage / (toughness / 4 + 2));
		double damageTaken = damage * (1 - Math.min(20, max) / 25);
		if (max > 20) {
			damageTaken -= (max - 20) * Utils.exarmor;
		}
		if (damageTaken > 1) {
			return damageTaken;
		}
		return 1;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void arrow(EntityShootBowEvent e) {
		Projectile p = (Projectile) e.getProjectile();
		p.setShooter(e.getEntity());
	}

	@EventHandler//(priority = EventPriority.LOW)
	public void attack(EntityDamageByEntityEvent e) {
		Entity enty = e.getEntity();
		Entity damer = e.getDamager();
		if(!(enty instanceof LivingEntity)) {
			return;
		}
		LivingEntity ent = (LivingEntity) enty;
		if(ent.getNoDamageTicks()>0) {
			return;
		}
		if(e.getDamage()==0) {
			return;
		}
		if (ent instanceof Player) {
			Player player = (Player) ent;
			PlayerData pdata = Players.Data.get(player);
			try {
				if (Utils.isSuccess(pdata.getAvoid())) {
					e.setCancelled(true);
					return;
				}
			} catch (Exception e1) {

			}
		}
		if (damer instanceof Projectile) {
			Projectile p = (Projectile) damer;
			if (p.getShooter() instanceof Player) {
				Player atker = (Player) p.getShooter();
				Players.PlayerData adata = Players.Data.get(atker);
				double dmg = e.getDamage();
				double extra = atker.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
				double damage = dmg + adata.getDamage();
				if (damage < 1.0) {
					damage = 1.0;
				}

				if (Utils.isSuccess((double) adata.getCrit())) {
					damage = (damage + extra) * 2.0 * adata.getRangeDamage();
				} else {
					damage = (damage + extra) * adata.getRangeDamage();
				}
				if (ent instanceof Player) {
					Player player = (Player) ent;
					PlayerData pdata = Players.Data.get(player);
					e.setDamage(DamageModifier.ARMOR, 0);
					e.setDamage(damagecalc(damage, pdata.armor, pdata.armor_toughness));
				} else {
					e.setDamage(damage);
				}
				return;
			}
		}
		if (e.getCause() != DamageCause.ENTITY_ATTACK) {
			return;
		}
		if (damer instanceof Player) {
			double damage;
			Player atker = (Player) damer;
			PlayerData adata = Players.Data.get(atker);
			double range = ModifyItem.getPlayerAttackRange(atker);
			if (ent.getLocation().distance(atker.getLocation()) > range) {
				e.setCancelled(true);
				return;
			}
			double dmg = e.getDamage();
			double mod = 1.0;
			boolean skill = true;
			if (range != Double.MAX_VALUE) {
				skill = false;
				double base = atker.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()
						* ModifyItem.AtkModBasedCD(atker);
				if (ent instanceof LivingEntity) {
					base += ModifyItem.extraATKBasedEnch(atker, (LivingEntity) ent);
				}
				mod = base / dmg;
			}
			damage = dmg;
			if (damage < 1.0) {
				damage = 1.0;
			}
			if (Utils.isSuccess(adata.getCrit())) {
				damage = (mod * damage * 2.0);
				if (Main.cancel) {
					Location loc = atker.getLocation();
					Sound sound = Sound.ENTITY_PLAYER_ATTACK_CRIT;
					ProtocolLibEvent.setWhitelist(sound);
					loc.getWorld().playSound(loc, sound, 1.0f, 2.0f);
				}
			} else {
				damage = (mod * damage);
				if (Main.cancel) {
					Location loc = atker.getLocation();
					Sound sound = Sound.ENTITY_PLAYER_ATTACK_WEAK;
					ProtocolLibEvent.setWhitelist(sound);
					loc.getWorld().playSound(loc, sound, 1.0f, 2.0f);
				}
			}
			if (ent instanceof Player) {
				Player player = (Player) ent;
				PlayerData pdata = Players.Data.get(player);
				e.setDamage(DamageModifier.ARMOR, 0);
				if (skill) {
					e.setDamage(damagecalc(damage * adata.getSkillDamage(), pdata.armor, pdata.armor_toughness));
				} else {
					e.setDamage(damagecalc(damage * adata.getDamage(), pdata.armor, pdata.armor_toughness));
				}
			} else {
				if (skill) {
					e.setDamage(damage * adata.getSkillDamage());
				} else {
					e.setDamage(damage * adata.getDamage());
				}
			}
			ModifyItem.atktime.put(atker, System.currentTimeMillis());

			return;
		}
	}
}
