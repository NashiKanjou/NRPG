package nashi.NRPG.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import nashi.NRPG.Players.Players;
import nashi.NRPG.Skills.Cast;

public class CastCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlable, String[] args) {
		if (args.length == 0) {
			Commands.sendHelp(sender);
			return true;
		} else {
			if (args[0] != null) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					int speed = Integer.parseInt(args[0]);
					YamlConfiguration y = Players.getYaml(player);
					if (speed > 0 && speed <= 20) {
						y.set("CastSpeed", speed);
						Cast.castspeed.put(player, speed);
						player.sendMessage(ChatColor.GREEN +"你已將詠唱間隔設定為 " + speed + " ticks");
					} else {
						player.sendMessage(ChatColor.RED +"請輸入1~20的數字");
					}
				}
				return true;
			}
		}
		return false;
	}

}
