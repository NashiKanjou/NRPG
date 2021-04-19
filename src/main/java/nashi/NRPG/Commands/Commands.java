package nashi.NRPG.Commands;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import nashi.NRPG.Main;
import nashi.NRPG.API.SkillAPI;
import nashi.NRPG.API.Utils;
import nashi.NRPG.Items.CreateItem;
import nashi.NRPG.Players.Players;
import nashi.NRPG.Skills.Cast;
import nashi.NRPG.Skills.Skill;
import nashi.NRPG.Skills.SkillItem;
import nashi.NRPG.Skills.SkillName;
import nashi.NRPG.listeners.ModifyItem;

public class Commands implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlable, String[] args) {
		if (args.length == 0) {
			sendHelp(sender);
			return true;
		} else {
			if (args[0].equalsIgnoreCase("key") && args.length >= 4) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					String key = args[1];
					String type = args[2];
					type = type.toUpperCase();
					String data = args[3];
					ItemStack item = player.getInventory().getItemInMainHand();
					ItemMeta im = item.getItemMeta();
					PersistentDataContainer pdc = im.getPersistentDataContainer();
					int c = 0;
					String[] sp = data.split(",");
					switch (type) {
					case "INTEGER":
						pdc.set(new NamespacedKey(Main.getPlugin(), key), PersistentDataType.INTEGER,
								Integer.parseInt(data));
						break;
					case "INTEGER_ARRAY":
						int[] ia = new int[data.length()];
						for (String str : sp) {
							ia[c] = Integer.parseInt(str);
							c++;
						}
						pdc.set(new NamespacedKey(Main.getPlugin(), key), PersistentDataType.INTEGER_ARRAY, ia);
						break;
					case "BYTE":
						pdc.set(new NamespacedKey(Main.getPlugin(), key), PersistentDataType.BYTE,
								Byte.parseByte(data));
						break;
					case "BYTE_ARRAY":
						byte[] ba = new byte[data.length()];
						for (String str : sp) {
							ba[c] = Byte.parseByte(str);
							c++;
						}
						pdc.set(new NamespacedKey(Main.getPlugin(), key), PersistentDataType.BYTE_ARRAY, ba);
						break;
					case "DOUBLE":
						pdc.set(new NamespacedKey(Main.getPlugin(), key), PersistentDataType.DOUBLE,
								Double.parseDouble(data));
						break;
					case "FLOAT":
						pdc.set(new NamespacedKey(Main.getPlugin(), key), PersistentDataType.FLOAT,
								Float.parseFloat(data));
						break;
					case "LONG":
						pdc.set(new NamespacedKey(Main.getPlugin(), key), PersistentDataType.LONG,
								Long.parseLong(data));
						break;
					case "LONG_ARRAY":
						long[] la = new long[data.length()];
						for (String str : sp) {
							la[c] = Long.parseLong(str);
							c++;
						}
						pdc.set(new NamespacedKey(Main.getPlugin(), key), PersistentDataType.LONG_ARRAY, la);
						break;
					case "SHORT":
						pdc.set(new NamespacedKey(Main.getPlugin(), key), PersistentDataType.SHORT,
								Short.parseShort(data));
						break;
					default:
						pdc.set(new NamespacedKey(Main.getPlugin(), key), PersistentDataType.STRING, data);
						break;
					}
					item.setItemMeta(im);
					return true;
				}

			}
			if (args[0].equalsIgnoreCase("potion")) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack i = player.getInventory().getItemInMainHand();
					if (i != null && i.getType() != Material.AIR) {
						ItemMeta im = i.getItemMeta();
						im.setUnbreakable(true);
						im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
						im.getPersistentDataContainer().set(Utils.key, PersistentDataType.STRING, "Consumables");
						i.setItemMeta(im);
						player.sendMessage("已修改道具");
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("skillitem")) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack i = player.getInventory().getItemInMainHand();
					if (i != null && i.getType() != Material.AIR) {
						SkillItem.setSkillItem(i, SkillName.get(args[1]), Integer.parseInt(args[2]),
								Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
						player.sendMessage("已修改道具");
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("modcast") && args[1] != null) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack item = player.getInventory().getItemInMainHand();
					if (item == null) {
						player.sendMessage("請手持想要修改的物品");
					}
					int mod = Integer.parseInt(args[1]);
					ModifyItem.setItemCastMod(item, mod);
					player.sendMessage("已將此道具的額外詠唱速度設為" + mod);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("mana") && args[1] != null) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack item = player.getInventory().getItemInMainHand();
					if (item == null) {
						player.sendMessage("請手持想要修改的物品");
					}
					int mod = Integer.parseInt(args[1]);
					ModifyItem.setItemMP(item, mod);
					player.sendMessage("裝備此道具時獲得的魔力修改為" + mod);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("mp") && args[1] != null) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack item = player.getInventory().getItemInMainHand();
					if (item == null) {
						player.sendMessage("請手持想要修改的物品");
					}
					int mod = Integer.parseInt(args[1]);
					ModifyItem.setPotionMP(item, mod);
					player.sendMessage("消耗此道具時獲得的魔力修改為" + mod);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("hp") && args[1] != null) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack item = player.getInventory().getItemInMainHand();
					if (item == null) {
						player.sendMessage("請手持想要修改的物品");
					}
					int mod = Integer.parseInt(args[1]);
					ModifyItem.setPotionHP(item, mod);
					player.sendMessage("消耗此道具時獲得的血量修改為" + mod);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("book") && args[1] != null) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					String sk = args[1];
					if (SkillName.get(sk) != null) {
						String skillname = SkillName.get(sk);
						ItemStack i = player.getInventory().getItemInMainHand();
						if (i != null && i.getType() != Material.AIR) {
							ItemMeta im = i.getItemMeta();
							im.setDisplayName(ChatColor.GOLD + "技能書" + ChatColor.AQUA + skillname);
							List<String> lore = new ArrayList<String>();
							lore.add(ChatColor.GREEN + "使用此書即可學習技能-" + ChatColor.LIGHT_PURPLE + skillname);
							lore.add(ChatColor.GREEN + "技能說明: ");
							lore.add(ChatColor.YELLOW + Players.skillsetting.getString(skillname + ".Description"));
							im.setLore(lore);
							im.setUnbreakable(true);
							im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
							i.setItemMeta(im);
							ModifyItem.setSkillBookSkill(i, skillname);
						} else {
							ItemStack a = new ItemStack(Material.ENCHANTED_BOOK);
							ItemMeta im = a.getItemMeta();
							im.setDisplayName(ChatColor.GOLD + "技能書" + ChatColor.AQUA + skillname);
							List<String> lore = new ArrayList<String>();
							lore.add(ChatColor.GREEN + "使用此書即可學習技能-" + ChatColor.LIGHT_PURPLE + skillname);
							lore.add(ChatColor.GREEN + "技能說明: ");
							lore.add(ChatColor.YELLOW + Players.skillsetting.getString(skillname + ".Description"));
							im.setLore(lore);
							im.setUnbreakable(true);
							im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
							a.setItemMeta(im);
							ModifyItem.setSkillBookSkill(a, skillname);
							player.getInventory().setItemInMainHand(a);
						}
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				SkillAPI.plugin().reloadConfig();
				Utils.init();
				return true;
			}
			if (args[0].equalsIgnoreCase("range") && args[1] != null) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack item = player.getInventory().getItemInMainHand();
					if (item == null) {
						player.sendMessage("請手持想要修改的物品");
					}
					double mod = Double.parseDouble(args[1]);
					ModifyItem.setItemRangeMod(item, mod);
					player.sendMessage("已將此道具的額外攻擊距離設為" + mod);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("forge") && args[1] != null) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack item = player.getInventory().getItemInMainHand();
					if (item == null) {
						player.sendMessage("請手持想要修改的物品");
					}
					CreateItem.setItemForgeSlot(item, Integer.parseInt(args[1]));
					CreateItem.setItemForgeTime(item, 0);

					player.sendMessage(
							"已將此道具設定為可鍛造物品, 欄位:" + CreateItem.getEquipmentSlotFromInt(Integer.parseInt(args[1])));
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("material") && args[1] != null) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				if (sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack item = player.getInventory().getItemInMainHand();
					if (item == null) {
						player.sendMessage("請手持想要修改的物品");
					}
					String tier = args[1];
					CreateItem.setItemForgeMaterialTier(item, tier);
					player.sendMessage("已將此道具設定為" + tier + "等級素材");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("unloadskill") && args[1] != null) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}
				try {
					String sk = args[1];
					Class<?> clazz = Skill.skills.get(sk).skill;
					Object s = clazz.getDeclaredConstructor().newInstance();
					Method skill = clazz.getMethod("unload");
					skill.invoke(s);
				} catch (Exception e) {

				}
			}
			if (args[0].equalsIgnoreCase("loadskill") && args[1] != null) {
				if (!sender.hasPermission("nrpg.op")) {
					sender.sendMessage("No permission");
					return true;
				}

				String sk = args[1];
				if (SkillName.get(sk) != null) {
					Class<?> clz = Skill.skills.get(SkillName.get(sk)).skill;
					try {
						Method set = clz.getMethod("unload");
						Object s = clz.getDeclaredConstructor().newInstance();
						set.invoke(s);
					} catch (Exception e) {
					}
				}
				File fileEntry = new File(
						SkillAPI.plugin().getDataFolder() + File.separator + "skills" + File.separator + sk + ".jar");
				try {
					File f = fileEntry;
					URL u = f.toURI().toURL();
					URLClassLoader child = new URLClassLoader(new URL[] { u }, Main.class.getClassLoader());
					Class<?> clazz = Class.forName(sk + "." + sk, true, child);
					Object s = clazz.getDeclaredConstructor().newInstance();
					try {
						Method set = clazz.getMethod("load");
						set.invoke(s);
					} catch (Exception e) {
					}
					try {
						if (SkillAPI.getSkillPublicBoolean(sk, "general")) {
							Skill.GeneralSkills.add(sk);
						}
						Method skill = clazz.getMethod("skill", Player.class);
						Skill.skills.put(sk, new Skill.Skills(skill, s, clazz));
						SkillName.put(sk);
					} catch (Exception e) {
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		return false;
	}

	public static void sendHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "NRPG指令幫助");
		sender.sendMessage(ChatColor.YELLOW + "/stat " + ChatColor.GREEN + "能力值");
		sender.sendMessage(ChatColor.YELLOW + "/skilledit " + ChatColor.GREEN + "調整技能參數");
		sender.sendMessage(ChatColor.YELLOW + "/cast 間隔 " + ChatColor.GREEN + "修改詠唱速度,越低越快");
		sender.sendMessage(ChatColor.YELLOW + "/slot " + ChatColor.GREEN + "將技能綁定至欄位上");
		sender.sendMessage(ChatColor.YELLOW + "/scale 格數 " + ChatColor.GREEN + "調整血量的最高格數顯示");
		sender.sendMessage(ChatColor.YELLOW + "/abandon 技能名稱 " + ChatColor.GREEN + "捨棄技能");
		sender.sendMessage(ChatColor.YELLOW + "/race " + ChatColor.GREEN + "種族相關指令");
		sender.sendMessage(ChatColor.YELLOW + "/reset " + ChatColor.GREEN + "重設玩家資料");
		sender.sendMessage(ChatColor.GOLD + "技能運作說明");
		sender.sendMessage(ChatColor.YELLOW + "切換雙手物品位置(快捷鍵F): " + ChatColor.GREEN + "施法模式");
		sender.sendMessage(ChatColor.YELLOW + "施法方式: " + ChatColor.GREEN + "Actionbar會顯示詠唱進度條,在跑到" + Cast.right
				+ Cast.logo + ChatColor.GREEN + "的時候點右鍵進行詠唱");
		sender.sendMessage(ChatColor.RED + "每個技能需要的成功次數不同");
		sender.sendMessage(ChatColor.RED + "詠唱時間為" + ChatColor.LIGHT_PURPLE + (Cast.timelimit / 1000) + ChatColor.RED
				+ "秒,時間過後則會判定失敗");
		sender.sendMessage(ChatColor.RED + "施法失敗會導致爆炸");
	}
}
