package nashi.NRPG.Commands;

import nashi.NRPG.Players.Players;
import nashi.NRPG.Skills.GUI;
import nashi.NRPG.Skills.SkillName;
import nashi.NRPG.listeners.EventListener;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SkillSlotCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlable, String[] args) {
		Player player;
		if (args.length == 0) {
			if (sender instanceof Player) {
				player = (Player) sender;
				if (!EventListener.skillmode.get(player)) {
					GUI.SlotGUI(player, 0);
				} else {
					player.sendMessage(ChatColor.RED + "請先離開施法模式");
				}
			}

			return true;
		} else if (args[0] == null) {
			return false;
		} else {
			if (sender instanceof Player) {
				player = (Player) sender;
				int slot = Integer.parseInt(args[0]) - 1;
				if (slot < 0 || slot > 8) {
					player.sendMessage(ChatColor.RED + "請輸入1~9的數字");
					return true;
				}

				YamlConfiguration y = Players.getYaml(player);

				try {
					String skname = SkillName.get(args[1]);
					boolean have = false;
					if (y.getStringList("UniqeSkills").contains(skname)) {
						have = true;
					}

					if (have) {
						y.set("Slot." + slot, skname);
						player.sendMessage(ChatColor.GREEN + "你已將" + skname + "綁定至欄位" + (slot + 1));
					} else {
						player.sendMessage(ChatColor.RED + "技能不存在,請確認是否擁有此技能或名稱是否正確");
					}
				} catch (ArrayIndexOutOfBoundsException var10) {
					y.set("Slot." + slot, (Object) null);
					player.sendMessage(ChatColor.GREEN + "你已將欄位" + (slot + 1) + "清空");
				}
			}

			return true;
		}
	}
}