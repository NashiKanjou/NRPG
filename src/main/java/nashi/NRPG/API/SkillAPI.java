package nashi.NRPG.API;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import nashi.NRPG.Main;
import nashi.NRPG.Players.Players;
import nashi.NRPG.Players.Players.PlayerData;
import nashi.NRPG.Skills.Skill;
import nashi.NRPG.listeners.EventListener;
import nashi.NRPG.listeners.ModifyItem;
import nashi.NRPG.listeners.EventListener.Casting;

public class SkillAPI {
	public static HashMap<UUID, Long> Silence = new HashMap<UUID, Long>();

	public static void setSilenceTime(Player player, long ms) {
		Silence.put(player.getUniqueId(), System.currentTimeMillis() + ms);
	}

	public static PlayerData getPlayerData(Player player) {
		return Players.Data.get(player);
	}

	public static boolean inSkillMode(Player player) {
		return EventListener.skillmode.get(player);
	}

	public static Casting getCast(Player player) {
		if (EventListener.cast.containsKey(player)) {
			return EventListener.cast.get(player);
		}
		return null;
	}

	public static void setPlayerMaxAttackRange(Player player, double range) {
		ModifyItem.edit.put(player, range);
	}

	public static void resetPlayerMaxAttackRange(Player player, double range) {
		if (ModifyItem.edit.containsKey(player)) {
			ModifyItem.edit.remove(player);
		}
	}

	public static void damage(LivingEntity e, Player player, double amount) {
		ModifyItem.edit.put(player, Double.MAX_VALUE);
		e.damage(amount + player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue(), player);
		ModifyItem.edit.remove(player);
	}

	public static long getRemainingSilenceTime(Player player) {
		return Silence.get(player.getUniqueId()) - System.currentTimeMillis();
	}

	public static boolean costMP(Player player, double MP) {
		return Players.Data.get(player).costMP(MP);
	}

	private static final Set<Material> containerTypes = EnumSet.of(Material.CHEST, Material.DROPPER, Material.HOPPER,
			Material.DISPENSER, Material.TRAPPED_CHEST, Material.BREWING_STAND, Material.FURNACE,
			Material.BLAST_FURNACE, Material.CHEST_MINECART, Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX,
			Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX,
			Material.GREEN_SHULKER_BOX, Material.ENDER_CHEST, Material.BARREL, Material.BEACON, Material.JUKEBOX,
			Material.NOTE_BLOCK, Material.COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART, Material.CHAIN_COMMAND_BLOCK,
			Material.ITEM_FRAME);

	public static boolean isContainer(Material mat) {
		return containerTypes.contains(mat);
	}

	public static List<LivingEntity> getEntityFromLoc(Location loc, int range) {
		List<LivingEntity> near = loc.getWorld().getLivingEntities();
		ArrayList<LivingEntity> l = new ArrayList<LivingEntity>();
		for (LivingEntity target : near) {
			if ((target.getLocation().distance(loc) <= range) && (!(target instanceof ArmorStand))) {
				l.add(target);
			}
		}
		return l;
	}

	public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2, World w) {
		List<Block> blocks = new ArrayList<Block>();

		int minx = Math.min(loc1.getBlockX(), loc2.getBlockX()), miny = Math.min(loc1.getBlockY(), loc2.getBlockY()),
				minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ()),
				maxx = Math.max(loc1.getBlockX(), loc2.getBlockX()),
				maxy = Math.max(loc1.getBlockY(), loc2.getBlockY()),
				maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		for (int x = minx; x <= maxx; x++) {
			for (int y = miny; y <= maxy; y++) {
				for (int z = minz; z <= maxz; z++) {
					Block b = w.getBlockAt(x, y, z);
					if (b.getType() != Material.AIR) {
						blocks.add(b);
					}
				}
			}
		}

		return blocks;
	}

	/**
	 * 用於計算拋物線的重力加速度，必須為負數。
	 */
	private static final double G = -9.8;
	/**
	 * 拋物線運動時的速度加快倍數，數字越大，速度越快。
	 */
	private static final double SPEED = 1;
	/**
	 * 變更運動方向的週期。
	 */
	private static final long PERIOD = 1;

	/**
	 * 將目標推離玩家。
	 *
	 * @param player   玩家
	 * @param target   要推離的目標
	 * @param distance 離地到落地的距離
	 * @param rad      起飛角度
	 */
	public static void push(Player player, Entity target, double distance, double rad) {
		Location playerLocation = player.getLocation();
		Location targetLocation = target.getLocation();

		Vector vector = getDirection(playerLocation, targetLocation);

		move(target, vector, distance, rad);
	}

	public static void push(Location loc, Entity target, double distance, double rad) {
		Location targetLocation = target.getLocation();

		Vector vector = getDirection(loc, targetLocation);

		move(target, vector, distance, rad);
	}

	/**
	 * 將目標拉進玩家。
	 *
	 * @param player   玩家
	 * @param target   要拉進的目標
	 * @param distance 離地到落地的距離
	 * @param rad      起飛角度
	 */
	public static void pull(Player player, Entity target, double distance, double rad) {
		Location playerLocation = player.getLocation();
		Location targetLocation = target.getLocation();

		Vector vector = getDirection(targetLocation, playerLocation);

		move(target, vector, distance, rad);
	}

	public static void pull(Location loc, Entity target, double distance, double rad) {
		Location targetLocation = target.getLocation();

		Vector vector = getDirection(targetLocation, loc);

		move(target, vector, distance, rad);
	}

	/**
	 * 取得某個點朝向另一個點的方向。
	 */
	private static Vector getDirection(Location from, Location to) {
		return new Vector(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ());
	}

	/**
	 * 將目標朝向指定方向以指定弧度拋出。
	 *
	 * @param target   目標
	 * @param vector   方向
	 * @param distance 起飛到落地的距離
	 * @param rad      弧度（單位是 rad）
	 */
	private static void move(Entity target, Vector vector, double distance, double rad) {

		double v = Math.sqrt((G / (-Math.tan(rad) * Math.cos(rad) * Math.cos(rad))) * distance / 2);
		double vx = v * Math.cos(rad);

		double coefficient1 = (G / (2 * v * v * Math.cos(rad) * Math.cos(rad)));
		double coefficient2 = Math.tan(rad);

		vector.normalize().setY(0);

		new BukkitRunnable() {

			private Location last = new Location(target.getWorld(), 0, 0, 0);
			double now = 0;

			@Override
			public void run() {
				double x = vx / 20 * now;
				if (x > distance) {
					this.cancel();
					return;
				}
				double y = coefficient1 * x * x + coefficient2 * x;

				Location end = vector.clone().multiply(x).setY(y).toLocation(target.getWorld());
				Vector move = getDirection(last, end);
				try {
					target.setVelocity(move);
				} catch (Exception e) {
					this.cancel();
				}
				last = end;

				now += PERIOD * SPEED;

			}

		}.runTaskTimer(Main.getPlugin(), 0, PERIOD);

	}

	public static Vector getRightDirection(Location l) {
		Vector direction = l.getDirection().normalize();
		return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
	}

	public static Vector getLeftDirection(Location l) {
		Vector direction = l.getDirection().normalize();
		return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
	}

	public static LivingEntity getEntityInLineOfSight(Player player, double range) {
		ArrayList<Entity> entities = (ArrayList<Entity>) player.getNearbyEntities(range, range, range);
		ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight((Set<Material>) null, (int) range);
		ArrayList<Location> sight = new ArrayList<Location>();
		for (int i = 0; i < sightBlock.size(); i++) {
			Block b = sightBlock.get(i);
			if (!Utils.transparentBlocks.contains(b.getType())) {
				break;
			}
			sight.add(sightBlock.get(i).getLocation());
		}
		for (int i = 0; i < sight.size(); i++) {
			for (int k = 0; k < entities.size(); k++) {
				if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3) {
					if (Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5) {
						if (Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3) {
							if (entities.get(k) instanceof LivingEntity && (!(entities.get(k) instanceof ArmorStand))) {
								return (LivingEntity) entities.get(k);
							}
						}
					}
				}
			}
		}
		return null;
	}

	public static List<LivingEntity> getNearEntity(Player player, int range) {
		List<LivingEntity> near = player.getLocation().getWorld().getLivingEntities();
		ArrayList<LivingEntity> l = new ArrayList<LivingEntity>();
		for (LivingEntity target : near) {
			if (target.getLocation().distance(player.getLocation()) <= range && target != player
					&& (!(target instanceof ArmorStand))) {
				l.add(target);
			}
		}
		return l;
	}

	public static List<LivingEntity> getNumbersTargetsFromList(List<LivingEntity> List, int maxtargets) {
		if (List.size() <= maxtargets) {
			return List;
		} else {
			int i;
			List<LivingEntity> l = new ArrayList<LivingEntity>();
			for (i = 0; i <= maxtargets; i++) {
				LivingEntity le = List.get(i);
				l.add(le);
			}
			return l;
		}
	}

	public static double getSkillInfo(Player player, String skillname, String Info) {
		YamlConfiguration pf = Players.getYaml(player);
		return pf.getDouble("Skills." + skillname + ".Custom." + Info);
	}

	public static double getPublicSkillInfo(String skillname, String Info) {
		YamlConfiguration f = Players.skillsetting;
		return f.getDouble(skillname + "." + Info);
	}

	public static boolean getSkillPublicBoolean(String skillname, String Info) {
		YamlConfiguration f = Players.skillsetting;
		return f.getBoolean(skillname + "." + Info);
	}

	public static Location BlockLocPlayerLook(Player player, int maxdistance) {
		Block block = player.getTargetBlock((Set<Material>) null, maxdistance);
		Location bl = block.getLocation();
		return bl;
	}

	public static long getRemainingCooldown(Player player, String skillname) {
		if (Skill.cooldowns.containsKey(player.getUniqueId() + "." + skillname)) {
			return Skill.cooldowns.get(player.getUniqueId() + "." + skillname) - System.currentTimeMillis();
		} else {
			return 0;
		}
	}

	public static Vector getPlayerLookDir(Player player) {
		return player.getLocation().getDirection();
	}

	public static void setCooldown(Player player, String skillname, long CD) {
		Skill.cooldowns.put(player.getUniqueId() + "." + skillname, System.currentTimeMillis() + CD);
	}

	public static Plugin plugin() {
		return Main.getPlugin();
	}

}