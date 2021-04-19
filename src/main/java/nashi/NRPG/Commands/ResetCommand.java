package nashi.NRPG.Commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import nashi.NRPG.API.SkillAPI;
import nashi.NRPG.Players.Players;

public class ResetCommand implements CommandExecutor {
	private static Set<Player> reset = new HashSet<Player>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlable, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "如果確定要重設資料請在30秒內輸入/reset confirm");
				reset.add(player);
				sec(player);
			} else if (args[0].equalsIgnoreCase("confirm")) {
				if (reset.contains(player)) {
					reset.remove(player);
					Players.delete(player);
					Players.delete(player);// bug 需要reset兩次來生效..
					player.sendMessage(ChatColor.GOLD + "你已重設你的資料");
				}
			}

			return true;
		}
		return false;
	}

	private void sec(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				reset.remove(player);
			}
		}.runTaskLater(SkillAPI.plugin(), 600);
	}
}
