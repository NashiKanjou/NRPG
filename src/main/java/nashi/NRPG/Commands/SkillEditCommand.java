package nashi.NRPG.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import nashi.NRPG.Players.Players;
import nashi.NRPG.Skills.GUI;
import nashi.NRPG.Skills.SkillName;

public class SkillEditCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlable, String[] args) {
		if (args.length == 0) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				GUI.EditGUI(player, 0);
			}
			return true;
		} else {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				String skname = SkillName.get(args[0]);
				YamlConfiguration y = Players.getYaml(player);
				if (y.getStringList("UniqeSkills").contains(skname)) {
					try {
						if (y.getConfigurationSection(("Skills." + skname + ".Custom")).getKeys(false).size() > 0) {
							GUI.openSkillEditGUI(player, skname);
						} else {
							player.sendMessage(ChatColor.RED + "請確認技能是否有可自定的數值");
						}
					} catch (Exception e) {
						player.sendMessage(ChatColor.RED + "請確認技能是否有可自定的數值");
					}
				} else {
					player.sendMessage(ChatColor.RED + "技能不存在,請確認是否擁有此技能或名稱是否正確");
				}
			}
			return true;

		}
	}
}
