package nashi.NRPG.listeners;

import edited.com.codingforcookies.armorequip.AfterArmorEquipEvent;
import edited.com.codingforcookies.armorequip.ArmorType;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import nashi.NRPG.Main;
import nashi.NRPG.API.SkillAPI;
import nashi.NRPG.API.Utils;
import nashi.NRPG.API.Utils.Status;
import nashi.NRPG.CustomEvents.CastEvent;
import nashi.NRPG.CustomEvents.CastStartEvent;
import nashi.NRPG.CustomEvents.SkillItemGenerateEvent;
import nashi.NRPG.Players.Players;
import nashi.NRPG.Players.Players.PlayerData;
import nashi.NRPG.Players.Races.Race;
import nashi.NRPG.Skills.Cast;
import nashi.NRPG.Skills.GUI;
import nashi.NRPG.Skills.Magic;
import nashi.NRPG.Skills.Skill;
import nashi.NRPG.Skills.SkillName;
import nashi.NRPG.Skills.Cast.Abracadabra;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class EventListener implements Listener {
	public static HashMap<UUID, Long> scooldowns = new HashMap<UUID, Long>();
	public static HashMap<Player, Boolean> skillmode = new HashMap<Player, Boolean>();
	private static DecimalFormat df = new DecimalFormat("#.##");
	public static HashMap<Player, ItemStack[]> backup = new HashMap<Player, ItemStack[]>();
	public static HashMap<Player, Long> castCD = new HashMap<Player, Long>();
	public static HashMap<Player, Casting> cast = new HashMap<Player, Casting>();
	public static long CD = 500L;
	public static HashMap<Player, Long> refresh = new HashMap<Player, Long>();

	@EventHandler
	public void move(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!refresh.containsKey(player)) {
			PlayerData pd = Players.Data.get(player);
			Players.recalcData(player, pd);
			refresh.put(player, System.currentTimeMillis() + 10000);
			return;
		}
		if (refresh.get(player) < System.currentTimeMillis()) {
			PlayerData pd = Players.Data.get(player);
			Players.recalcData(player, pd);
		}
	}

	@EventHandler
	public void caststart(CastEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player player = e.getPlayer();
		PlayerInventory inv = player.getInventory();
		ItemStack item = inv.getItemInMainHand();
		Casting cast = e.getCast();
		Abracadabra a = cast.getAbracadabra();
		Set<Integer> s = a.getPos();
		if (!cast.validPos()) {
			return;
		}
		if (cast.getSuccessTime() < 1) {
			cast.setWand(item);
			int i = ModifyItem.getItemCastMod(item);
			if (castmod.containsKey(player)) {
				i += castmod.get(player);
			}
			cast.setModSuccess(i);
		}
		if (!cast.getWand().equals(item)) {
			cast.setModSuccess(0);
		}
		if (s.contains(cast.getPos())) {
			cast.success();
			player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.0f, 3.0f);
			if (cast.getSuccessTime() + cast.getSuccessMod() >= cast.getNeededTimes()) {
				castCD.put(player, System.currentTimeMillis() + CD);
				ItemStack n = inv.getItemInOffHand();
				ItemMeta m = n.getItemMeta();
				PersistentDataContainer pdc = m.getPersistentDataContainer();
				if (pdc.has(ModifyItem.CastHelp, PersistentDataType.BYTE)) {
					inv.setItemInOffHand(null);
				}
				EventListener.cast.remove(player);
				Skill.cast(cast.getSkillName(), player, false);
			}
		} else {
			int t = cast.getSuccessTime();
			String skname = cast.getSkillName();
			Location loc = player.getLocation();
			loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, (float) t, 3.0f);
			loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 2);
			double cost = SkillAPI.getPublicSkillInfo(skname, "CostMP");
			SkillAPI.costMP(player, cost);
			double damage = cost * Utils.manaDMG * ((0.0 + t) / cast.getNeededTimes());
			SkillAPI.damage((LivingEntity) player, (Player) player, damage);
			for (LivingEntity ent : SkillAPI.getNearEntity((Player) player, (int) ((int) Math.pow(cost, 0.5)))) {
				SkillAPI.damage((LivingEntity) ent, (Player) player, damage);
			}
			ItemStack n = inv.getItemInOffHand();
			ItemMeta m = n.getItemMeta();
			try {
				PersistentDataContainer pdc = m.getPersistentDataContainer();
				if (pdc.has(ModifyItem.CastHelp, PersistentDataType.BYTE)) {
					inv.setItemInOffHand(null);
				}
			} catch (Exception pdc) {
				// empty catch block
			}
			castCD.put(player, System.currentTimeMillis() + CD);
			EventListener.cast.remove(player);
			return;
		}
	}

	@EventHandler
	public void close(InventoryCloseEvent e) {
		Player player = (Player) e.getPlayer();
		PlayerData pd = Players.Data.get(player);
		Players.recalcData(player, pd);

	}

	@EventHandler
	public void equip(AfterArmorEquipEvent e) {
		Player player = e.getPlayer();
		PlayerData pd = Players.Data.get(player);
		Players.recalcData(player, pd);
	}

	@EventHandler
	public void logout(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		Players.unload((Player) player);
		if (backup.containsKey(player)) {
			PlayerInventory inv = player.getInventory();
			ItemStack[] back = backup.get(player).clone();
			int n = back.length;
			for (int n2 = 0; n2 < n; n2++) {
				ItemStack item = back[n2];
				inv.setItem(n2, item);
			}
			backup.remove(player);
		}
		if (ModifyItem.edit.containsKey(player)) {
			ModifyItem.edit.remove(player);
		}
	}

	@EventHandler
	public void login(PlayerLoginEvent e) {
		Player player = e.getPlayer();
		Players.loadYaml(player);
		skillmode.put(player, false);
		Magic.check(player);
	}

	@EventHandler
	public void Join(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		Main.setPlayerScoreboard(player);
		YamlConfiguration y = Players.getYaml(player);
		y.set("ID", player.getName());
		int a = y.getInt("Scale");
		if (a > 0) {
			player.setHealthScale(a);
		} else {
			player.setHealthScale(20.0);
		}
		PlayerData pd = Players.Data.get(player);
		Players.recalcData(player, pd);
		if (ModifyItem.edit.containsKey(player)) {
			ModifyItem.edit.remove(player);
		}
	}

	@EventHandler
	public void respawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		if (backup.containsKey(player)) {
			PlayerInventory inv = player.getInventory();
			ItemStack[] back = backup.get(player).clone();
			int n = back.length;
			for (int n2 = 0; n2 < n; n2++) {
				ItemStack item = back[n2];
				inv.setItem(n2, item);
			}
			backup.remove(player);
			skillmode.put(player, false);
		}
		Players.PlayerData pdata = Players.Data.get(player);
		Players.recalcData(player, pdata);
	}

	public static void setData(Player player, Players.PlayerData pdata) {
		if (skillmode.get(player)) {
			double d;
			if (extra.containsKey(player)) {
				d = extra.get(player);
			} else {
				d = 0;
			}
			player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
					.setBaseValue(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() + d);
			player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(pdata.getMaxHealth());
			player.setWalkSpeed(pdata.getSpeed());
			if (pdata.getCurrentMP() > pdata.getMaxMana()) {
				pdata.setCurrentMP(pdata.getMaxMana());
			}
		} else {
			if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() != pdata.getMaxHealth()) {
				player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(pdata.getMaxHealth());
			}
			player.setWalkSpeed(pdata.getSpeed());
			if (pdata.getCurrentMP() > pdata.getMaxMana()) {
				pdata.setCurrentMP(pdata.getMaxMana());
			}
		}
	}

	@EventHandler
	public void check(InventoryClickEvent e) {
		if (e.isCancelled()) {
			return;
		}
		try {
			if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
				ItemStack i = e.getCurrentItem();
				if (i == null) {
					return;
				}
				ItemStack c = e.getCursor();
				if (e.getSlotType() == InventoryType.SlotType.ARMOR && Utils.isNotDrop(i)) {
					if (c == null || c.getType() == Material.AIR) {
						e.setCancelled(true);
						return;
					}
					int slot = e.getSlot();
					ArmorType at = ArmorType.matchType(c);
					if (at == null) {
						return;
					}
					if (at.equals(ArmorType.BOOTS) && slot != 36) {
						e.setCancelled(true);
						return;
					}
					if (at.equals(ArmorType.LEGGINGS) && slot != 37) {
						e.setCancelled(true);
						return;
					}
					if (at.equals(ArmorType.CHESTPLATE) && slot != 38) {
						e.setCancelled(true);
						return;
					}
					if (at.equals(ArmorType.HELMET) && slot != 39) {
						e.setCancelled(true);
						return;
					}
					e.setCurrentItem(null);
				}
			}
		} catch (Exception i) {
			// empty catch block
		}
	}

	@EventHandler
	public void SkillEditPageGUI(InventoryClickEvent e) {
		if (!e.getView().getTitle().contains("技能調整")) {
			return;
		}
		e.setCancelled(true);
		Player player = (Player) e.getWhoClicked();
		if (e.getClick() == ClickType.valueOf("SWAP_OFFHAND")) {
			player.closeInventory();
			ItemStack item = player.getInventory().getItemInOffHand().clone();
			new BukkitRunnable() {
				public void run() {
					player.getInventory().setItemInOffHand(item);
				}
			}.runTaskLater(SkillAPI.plugin(), 1L);
			return;
		}
		if (e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT) {
			return;
		}
		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}
		if (!item.hasItemMeta()) {
			return;
		}
		ItemMeta im = item.getItemMeta();
		if (!im.hasDisplayName()) {
			return;
		}
		if (ChatColor.stripColor(im.getDisplayName()).equals("下一頁")) {

			GUI.EditGUI(player, (GUI.editpage.get(player) + 1));
		} else {
			String skillname = SkillName.get(ChatColor.stripColor(im.getDisplayName()));
			if (skillname == null) {
				return;
			}
			GUI.openSkillEditGUI(player, skillname);
		}
	}

	@EventHandler
	public void SlotEditClose(InventoryCloseEvent e) {
		if (!e.getView().getTitle().contains("技能欄位設定")) {
			return;
		}
		e.getView().setCursor(null);
		Player player = (Player) e.getPlayer();
		Inventory inv = e.getInventory();
		String[] skills = new String[9];
		int i = 0;
		while (i < 9) {
			ItemStack item = inv.getItem(i);
			if (item != null && item.hasItemMeta()) {
				ItemMeta im = item.getItemMeta();
				skills[i] = SkillName.get(ChatColor.stripColor(im.getDisplayName()));
			}
			++i;
		}
		YamlConfiguration y = Players.getYaml(player);
		int slot = 0;
		while (slot < 9) {
			String skname = skills[slot];
			y.set("Slot." + slot, skname);
			++slot;
		}
		PlayerInventory pi = player.getInventory();
		int i2 = 0;
		while (i2 < pi.getSize()) {
			ItemStack item = pi.getItem(i2);
			if (Utils.isNotDrop(item)) {
				pi.setItem(i2, null);
			}
			++i2;
		}
	}

	@EventHandler
	public void SlotPageGUI(InventoryClickEvent e) {
		if (!e.getView().getTitle().contains("技能欄位設定")) {
			return;
		}
		Player player = (Player) e.getWhoClicked();
		if (e.getClick() == ClickType.valueOf("SWAP_OFFHAND")) {
			e.setCancelled(true);
			player.closeInventory();
			ItemStack item = player.getInventory().getItemInOffHand().clone();
			new BukkitRunnable() {
				public void run() {
					player.getInventory().setItemInOffHand(item);
				}
			}.runTaskLater(SkillAPI.plugin(), 1L);
			return;
		}
		if (e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT) {
			e.setCancelled(true);
			return;
		}
		if (e.getRawSlot() < 0 || e.getRawSlot() > 53) {
			e.setCancelled(true);
			return;
		}
		try {
			if (e.getClickedInventory().equals(player.getInventory())) {
				e.setCancelled(true);
				return;
			}
			ItemStack item = e.getCurrentItem();
			if (item == null) {
				return;
			}
			if (!item.hasItemMeta()) {
				e.setCancelled(true);
				return;
			}
			ItemMeta im = item.getItemMeta();
			if (!im.hasDisplayName()) {
				e.setCancelled(true);
				return;
			}
			if (ChatColor.stripColor(im.getDisplayName()).equals("下一頁")) {
				e.setCancelled(true);
				GUI.SlotGUI((Player) player, (GUI.editpage.get(player) + 1));
				return;
			}
			if (SkillName.get(ChatColor.stripColor(im.getDisplayName())) == null) {
				e.setCancelled(true);
			}
		} catch (Exception e1) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void SkillEditGUI(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if ((skillmode.get(player) || cast.containsKey(player)) && !e.getView().getTitle().contains("技能設定")
				&& !e.getView().getTitle().contains("技能調整")) {
			e.setCancelled(true);
			return;
		}
		if (!e.getView().getTitle().contains("技能設定")) {
			return;
		}
		if (e.getClick() == ClickType.valueOf("SWAP_OFFHAND")) {
			e.setCancelled(true);
			player.closeInventory();
			ItemStack item = player.getInventory().getItemInOffHand().clone();
			new BukkitRunnable() {
				public void run() {
					player.getInventory().setItemInOffHand(item);
				}
			}.runTaskLater(SkillAPI.plugin(), 1L);
			return;
		}
		if (e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT && e.getClick() != ClickType.SHIFT_LEFT
				&& e.getClick() != ClickType.SHIFT_RIGHT) {
			e.setCancelled(true);
			return;
		}
		e.setCancelled(true);
		String skillName = e.getView().getTitle().split("-")[1];
		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}
		YamlConfiguration yml = Players.getYaml((Player) player);
		ItemMeta im = item.getItemMeta();
		boolean increase = im.getDisplayName().contains("提升");
		String var = ChatColor.stripColor(im.getDisplayName()).split(":")[1];
		double max = yml.getDouble("Skills." + skillName + ".Max." + var);
		if (e.isShiftClick()) {
			if (increase) {
				if (e.isLeftClick()) {
					double mod = Double
							.parseDouble(df.format(yml.getDouble("Skills." + skillName + ".Custom." + var) + 0.1));
					if (mod > max) {
						mod = max;
					}
					yml.set("Skills." + skillName + ".Custom." + var, mod);
				} else {
					double mod = Double
							.parseDouble(df.format(yml.getDouble("Skills." + skillName + ".Custom." + var) + 10));
					if (mod > max) {
						mod = max;
					}
					yml.set("Skills." + skillName + ".Custom." + var, mod);
				}
			} else if (e.isLeftClick()) {
				double mod = Double
						.parseDouble(df.format(yml.getDouble("Skills." + skillName + ".Custom." + var) - 0.1));
				if (mod < 0.0) {
					mod = 0.0;
				}
				yml.set("Skills." + skillName + ".Custom." + var, mod);
			} else {
				double mod = Double
						.parseDouble(df.format(yml.getDouble("Skills." + skillName + ".Custom." + var) - 10));
				if (mod < 0.0) {
					mod = 0.0;
				}
				yml.set("Skills." + skillName + ".Custom." + var, mod);
			}
		} else if (increase) {
			if (e.isLeftClick()) {
				double mod = Double
						.parseDouble(df.format(yml.getDouble("Skills." + skillName + ".Custom." + var) + 0.01));
				if (mod > max) {
					mod = max;
				}
				yml.set("Skills." + skillName + ".Custom." + var, mod);
			} else {
				double mod = Double.parseDouble(df.format(yml.getDouble("Skills." + skillName + ".Custom." + var) + 1));
				if (mod > max) {
					mod = max;
				}
				yml.set("Skills." + skillName + ".Custom." + var, mod);
			}
		} else if (e.isLeftClick()) {
			double mod = Double.parseDouble(df.format(yml.getDouble("Skills." + skillName + ".Custom." + var) - 0.01));
			if (mod < 0.0) {
				mod = 0.0;
			}
			yml.set("Skills." + skillName + ".Custom." + var, mod);
		} else {
			double mod = Double.parseDouble(df.format(yml.getDouble("Skills." + skillName + ".Custom." + var) - 1));
			if (mod < 0.0) {
				mod = 0.0;
			}
			yml.set("Skills." + skillName + ".Custom." + var, mod);
		}
		GUI.openSkillEditGUI((Player) player, skillName);
	}

	@EventHandler
	public void die(PlayerDeathEvent e) {
		List<ItemStack> drops = e.getDrops();
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (ItemStack item : drops) {
			if (!Utils.isNotDrop(item)) {
				items.add(item);
			}
		}
		drops.clear();
		drops.addAll(items);
		Player player = e.getEntity();
		if (Utils.deathpenworld.contains(player.getWorld())) {
			PlayerData pd = Players.Data.get(player);
			Status status = pd.getStatus();
			if (Utils.Penalty_MakeUp) {
				YamlConfiguration yml = Players.getYaml(player);
				try {
					for (String skill : yml.getConfigurationSection("Skills").getKeys(false)) {
						if (yml.getBoolean("Skills." + skill + ".isMakeUp")) {
							yml.set("Skills." + skill, null);
						}
					}
				} catch (Exception e1) {

				}
			}
			if (Utils.Penalty_LVL) {
				status.setLvL(1);
			}
			if (Utils.Penalty_Status) {
				Status stat = Utils.getStatusFromInt(player.getUniqueId().hashCode(), status.getLvL());
				status = stat;
				Utils.setPlayerStatus(player, stat);
				Race race = pd.getRace();
				Players.Data.put(player, pd);
				if (!Utils.Penalty_Race) {
					pd = new PlayerData(player, race, stat, pd.getCurrentMP());
				} else {
					pd = new PlayerData(player, stat);
				}
			} else if (Utils.Penalty_Race) {
				Utils.setPlayerStatus(player, status);
				pd = new PlayerData(player, status);
				Players.Data.put(player, pd);
			}
		}
	}

	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		if (cast.containsKey(player) || skillmode.get(player).booleanValue()) {
			e.setCancelled(true);
		}
		if (Utils.isNotDrop(e.getItemDrop().getItemStack())) {
			e.setCancelled(true);
		}
	}

	public static HashMap<Player, Double> extra = new HashMap<Player, Double>();
	public static HashMap<Player, Integer> castmod = new HashMap<Player, Integer>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void SkillMode(PlayerSwapHandItemsEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player player = e.getPlayer();
		e.setCancelled(true);
		YamlConfiguration y = Players.getYaml(player);
		if (skillmode.get(player)) {// from skill mode
			PlayerInventory inv = player.getInventory();
			ItemStack[] back = backup.get(player).clone();
			int n = back.length;
			for (int n2 = 0; n2 < n; n2++) {
				ItemStack item = back[n2];
				inv.setItem(n2, item);
			}
			double d;
			if (extra.containsKey(player)) {
				d = extra.get(player);
			} else {
				d = 0;
			}
			backup.remove(player);
			skillmode.put(player, false);
			player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
					.setBaseValue(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() - d);
			extra.remove(player);
			castmod.remove(player);
		} else {// to skillmode
			PlayerInventory inv = player.getInventory();
			ItemStack[] back = new ItemStack[9];
			for (int i = 0; i < 9; i++) {
				back[i] = inv.getItem(i);
			}
			backup.put(player, back);
			double d = 0;
			int c = ModifyItem.getItemCastMod(inv.getItemInMainHand());
			try {
				for (AttributeModifier a : inv.getItemInMainHand().getItemMeta()
						.getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE)) {
					d += a.getAmount();
				}
			} catch (Exception e1) {
			}
			extra.put(player, d);
			castmod.put(player, c);
			player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
					.setBaseValue(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() + d);
			int i = 0;
			while (i < 9) {
				try {
					String sk = y.getString("Slot." + i);
					ItemStack item = Utils.getSkillItem(sk, player);
					SkillItemGenerateEvent event = new SkillItemGenerateEvent(player, item, sk);
					Bukkit.getPluginManager().callEvent(event);
					inv.setItem(i, item);
				} catch (NullPointerException e1) {
					ItemStack item = new ItemStack(Material.BARRIER);
					ItemMeta im = item.getItemMeta();
					PersistentDataContainer pdc = im.getPersistentDataContainer();
					pdc.set(AllAboutDamage.key, PersistentDataType.STRING, "ItemsNotDrop");
					item.setItemMeta(im);
					inv.setItem(i, item);
				}
				++i;
			}
			skillmode.put(player, true);
		}
	}

	public static void sendTitle(Player player, String title, int time1, int time2, int time3) {
		IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + title + "\"}");
		PacketPlayOutTitle titleT = new PacketPlayOutTitle(EnumTitleAction.ACTIONBAR, chatTitle);
		PacketPlayOutTitle length = new PacketPlayOutTitle(time1, time2, time3);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(titleT);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
	}

	public static void playercast(final Player player, final Casting cast, long speed, long limit) {
		CastStartEvent ce = new CastStartEvent(player, cast);
		Bukkit.getPluginManager().callEvent(ce);
		if (ce.isCancelled()) {
			return;
		}
		EventListener.cast.put(player, cast);
		final String a = cast.getAbracadabra().getString();
		sendTitle(player, Cast.getDisplay(a, cast.getPos()), 5, 5, 5);
		/*
		 * player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
		 * TextComponent.fromLegacyText(Cast.getDisplay(a, cast.getPos())));
		 */
		final long breaktime = System.currentTimeMillis() + limit;
		new BukkitRunnable() {

			public void run() {
				if (!EventListener.cast.containsKey(player)) {
					this.cancel();
					return;
				}
				if (System.currentTimeMillis() > breaktime) {
					int t = cast.getSuccessTime();
					String skname = cast.getSkillName();
					double cost = SkillAPI.getPublicSkillInfo(skname, "CostMP");
					SkillAPI.costMP((Player) player, cost);
					double damage = cost * Utils.manaDMG * ((0.0 + t) / cast.getNeededTimes());
					Location loc = player.getLocation();
					loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, (float) t, 3.0f);
					loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 2);
					SkillAPI.damage(player, player, damage);
					for (LivingEntity ent : SkillAPI.getNearEntity(player, ((int) Math.pow(cost, 0.5)))) {
						SkillAPI.damage(ent, player, damage);
					}
					EventListener.cast.remove(player);
					EventListener.castCD.put(player, System.currentTimeMillis() + EventListener.CD);
					this.cancel();
					return;
				}
				cast.nextPos();
				sendTitle(player, Cast.getDisplay(a, cast.getPos()), 5, 5, 5);
				/*
				 * player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
				 * TextComponent.fromLegacyText(Cast.getDisplay(a, cast.getPos())));
				 */
			}
		}.runTaskTimer(Main.getPlugin(), speed, speed);
	}

	@EventHandler
	public void place(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		if (cast.containsKey(player) || skillmode.get(player).booleanValue()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void itemframe(PlayerInteractEntityEvent event) {

		Player player = event.getPlayer();

		Entity e = event.getRightClicked();

		if (e instanceof ItemFrame) {

			if (Utils.isNotDrop(player.getInventory().getItemInMainHand())) {
				event.setCancelled(true);
			}

		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void interact(PlayerInteractEvent event) {
		ItemStack item;
		Player player = event.getPlayer();
		if ((item = event.getItem()) != null) {
			if (!item.hasItemMeta()) {
				return;
			}
			ItemMeta im = item.getItemMeta();
			if (im.isUnbreakable()) {
				String skillname = ModifyItem.getSkillBookSkill(item);
				if (skillname != null) {
					event.setCancelled(true);
					YamlConfiguration y = Players.getYaml((Player) player);
					int slot = y.getInt("UniqueSkillSlots");
					if (slot > y.getInt("UsedSlots")) {
						if (!Magic.learnSkill((Player) player, skillname)) {
							player.sendMessage(ChatColor.RED + "你已經擁有此技能");
							return;
						}
						ItemStack book = player.getInventory().getItemInMainHand();
						book.setAmount(book.getAmount() - 1);
						player.sendMessage(ChatColor.GREEN + "你已學習了" + skillname);
					} else {
						player.sendMessage(ChatColor.RED + "請確認你是否擁有足夠的欄位");
					}
					return;
				}
				PersistentDataContainer pdc = im.getPersistentDataContainer();
				if (pdc.has(Utils.key, PersistentDataType.STRING)) {
					event.setCancelled(true);
					double hp = 0.0;
					double mp = 0.0;
					Players.PlayerData pd = Players.Data.get(player);
					pd.addCurrentMP(mp += ModifyItem.getPotionMP(item));
					double chp = player.getHealth();
					double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
					if (chp + (hp += ModifyItem.getPotionHP(item)) < 0.0) {
						player.setHealth(0.0);
					} else if (chp + hp < max) {
						player.setHealth(chp + hp);
					} else {
						player.setHealth(max);
					}
					int amount = item.getAmount();
					item.setAmount(amount - 1);
					return;
				}
			}
		}
		if (cast.containsKey(player)) {
			if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				return;
			}
			event.setCancelled(true);
			Casting c = cast.get(player);
			CastEvent ce = new CastEvent(player, c);
			Bukkit.getPluginManager().callEvent((Event) ce);
			return;
		}
		if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		long cooldowntime = 100L;
		if (scooldowns.containsKey(player.getUniqueId())
				&& System.currentTimeMillis() - scooldowns.get(player.getUniqueId()) <= cooldowntime) {
			return;
		}
		if (skillmode.get(player)) {
			if (castCD.containsKey(player) && castCD.get(player) > System.currentTimeMillis()) {
				return;
			}
			if (skillmode.get(player)) {
				event.setCancelled(true);
				scooldowns.put(player.getUniqueId(), System.currentTimeMillis());
				int slot = player.getInventory().getHeldItemSlot();
				YamlConfiguration y = Players.getYaml(player);
				try {
					String sk = y.getString("Slot." + slot);
					if (sk.length() < 1) {
						return;
					}
					double cost = SkillAPI.getPublicSkillInfo(sk, "CostMP");
					Players.PlayerData pd = Players.Data.get(player);
					if (pd.getCurrentMP() - cost < 0.0) {
						player.sendMessage(ChatColor.RED + "魔力不足");
						return;
					}
					if (SkillAPI.getPublicSkillInfo(sk, "Times") < 1.0) {
						Skill.cast(sk, player, false);
					} else {
						long time = SkillAPI.getRemainingCooldown((Player) player, sk);
						if (time > 0L) {
							player.sendMessage(ChatColor.RED + "技能冷卻時間剩餘:" + time + " 毫秒");
							return;
						}
						Cast.Abracadabra abra = Cast
								.getabracadabra(((int) SkillAPI.getPublicSkillInfo(sk, "Difficulty")));
						EventListener.playercast(player,
								new Casting(sk, abra, (int) SkillAPI.getPublicSkillInfo(sk, "Times")),
								(Cast.castspeed.get(player)), Cast.timelimit);
					}
				} catch (NullPointerException sk) {
					// empty catch block
				}
			}
		}

	}

	public static class Casting {
		private int success = 0;
		private String skillname;
		private Cast.Abracadabra abracadabra;
		private int pos;
		private boolean d;
		private int needed;
		private boolean s;
		private int add = 0;
		private ItemStack item = null;

		public Casting(String skillname, Cast.Abracadabra abracadabra, int needed) {
			this.skillname = skillname;
			this.abracadabra = abracadabra;
			this.pos = 1;
			this.d = true;
			this.needed = needed;
			this.s = true;
		}

		public int getSuccessMod() {
			return this.add;
		}

		public int getNeededTimes() {
			return this.needed;
		}

		public void setModSuccess(int mod) {
			this.add = mod;
		}

		public ItemStack getWand() {
			return this.item;
		}

		public void setWand(ItemStack wand) {
			this.item = wand;
		}

		public boolean validPos() {
			return !this.s;
		}

		public void nextPos() {
			this.s = false;
			if (this.pos < 20 && this.d) {
				++this.pos;
			} else if (this.pos == 20 || this.pos == 1) {
				d = !d;
				this.pos = this.d ? ++this.pos : --this.pos;
			} else {
				--this.pos;
			}
		}

		public int getPos() {
			return this.pos - 1;
		}

		public Cast.Abracadabra getAbracadabra() {
			return this.abracadabra;
		}

		public int getSuccessTime() {
			return this.success;
		}

		public String getSkillName() {
			return this.skillname;
		}

		public void success() {
			this.s = true;
			++this.success;
		}

		public void addSuccessTime(int i) {
			this.success += i;
		}
	}

}