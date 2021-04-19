package nashi.NRPG.Commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import nashi.NRPG.Players.Players;
import nashi.NRPG.Players.Races;
import nashi.NRPG.Players.Players.PlayerData;
import nashi.NRPG.Players.Races.Race;

public class RaceCommand implements CommandExecutor {
	public static Set<Player> enable = new HashSet<Player>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlable, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 0) {
			sendRaceHelp(player);
			return true;
		} else {
			if (args[0].equalsIgnoreCase("choose") && args[1] != null) {
				PlayerData pd = Players.Data.get(player);
				if (!pd.getRace().getName().equals(Races.defaultRace.getName())) {
					player.sendMessage(ChatColor.RED + "你已經選擇過種族了");
					return true;
				}
				String racename = args[1];
				Race race = Races.getRace(racename);
				if (race == null) {
					player.sendMessage(ChatColor.RED + "種族不存在,請確認名稱是否正確");
					return true;
				}
				YamlConfiguration y = Players.getYaml(player);
				pd.setRace(player, race);
				y.set("Race", race.getName());
				player.sendMessage(ChatColor.GREEN + "你已成為" + race.getDisplayName());

				return true;
			}
			if (args[0].equalsIgnoreCase("exterior")) {
				if (enable.contains(player)) {
					disable(player);
					player.sendMessage(ChatColor.RED + "種族外觀特徵已停用");
				} else {
					enable(player);
					player.sendMessage(ChatColor.GREEN + "種族外觀特徵已啟用");
				}
				return true;
			}
		}
		return false;
	}

	private static void sendRaceHelp(Player sender) {
		/*String able;
		if (enable.contains(sender)) {
			able = ChatColor.GREEN + "啟用";
		} else {
			able = ChatColor.RED + "停用";
		}
		sender.sendMessage(ChatColor.YELLOW + "種族外觀: " + able);
		sender.sendMessage(
				ChatColor.GREEN + "輸入" + ChatColor.YELLOW + "/race exterior " + ChatColor.GREEN + "即可啟用或停用種族外觀特徵");
*/
		sender.sendMessage(
				ChatColor.GREEN + "輸入" + ChatColor.YELLOW + "/race choose 種族英文名 " + ChatColor.GREEN + "即可選擇種族");
		sender.sendMessage(ChatColor.RED + "選擇種族後將無法更改");
		
		/*
		sender.sendMessage(ChatColor.GREEN + "種族列表:");
		Set<String> keys = Races.races.keySet();
		for (String key : keys) {
			Race race = Races.getRace(key);
			sender.sendMessage(ChatColor.GOLD + key + ChatColor.GRAY + ": " + ChatColor.YELLOW + race.getDisplayName()
					+ " " + ChatColor.LIGHT_PURPLE + race.getDescription());
		}
		*/
	}

	public static void disable(Player player) {
		YamlConfiguration y = Players.getYaml(player);
		y.set("Exterior", false);
		enable.remove(player);
	}

	public static void enable(Player player) {
		YamlConfiguration y = Players.getYaml(player);
		y.set("Exterior", true);
		enable.add(player);
	}

	public static boolean isEnable(Player player) {
		return enable.contains(player);
	}
}
