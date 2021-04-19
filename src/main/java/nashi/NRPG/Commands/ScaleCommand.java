package nashi.NRPG.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import nashi.NRPG.Players.Players;

public class ScaleCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlable, String[] args) {
		if (args.length == 0) {
			Commands.sendHelp(sender);
			return true;
		} else {
			if (args[0] != null) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					int i = Integer.parseInt(args[0]);
					YamlConfiguration y = Players.getYaml(player);
					y.set("Scale", i);
					if (i < 1) {
						sender.sendMessage(ChatColor.RED + "請輸入大於1的數字");
						return true;
					}
					player.setHealthScale(i);
					return true;
				}
			}
		}
		return false;
	}
}
