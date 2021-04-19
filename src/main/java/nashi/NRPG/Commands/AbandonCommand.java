package nashi.NRPG.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import nashi.NRPG.Players.Players;
import nashi.NRPG.Skills.Skill;
import nashi.NRPG.Skills.SkillName;

public class AbandonCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlable, String[] args) {
		if (args.length == 0) {
			Commands.sendHelp(sender);
			return true;
		} else {
			if (args[0] != null) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (!player.hasPermission("nrpg.abandon")) {
						player.sendMessage(ChatColor.RED + "你沒有權限使用此指令");
						return true;
					}
					String sk = args[0];
					String skillname = SkillName.get(sk);
					if (Skill.GeneralSkills.contains(skillname)) {
						player.sendMessage(ChatColor.RED + "你無法放棄通用技能");
						return true;
					}
					if (Players.Data.get(player).getRace().getSkillList().contains(skillname)) {
						player.sendMessage(ChatColor.RED + "你無法放棄種族技能");
						return true;
					}
					YamlConfiguration y = Players.getYaml(player);
					boolean have = false;
					List<String> skl = y.getStringList("UniqeSkills");
					if (skl.contains(skillname)) {
						have = true;
					}
					if (have) {
						skl.remove(skillname);
						y.set("UniqeSkills", skl);
						y.set("UsedSlots", y.getInt("UsedSlots") - 1);
						for(int i = 1;i<=9;i++) {
							try {
								if(y.getString("Slot." + i).equals(skillname)) {
									y.set("Slot."+i, null);
								}
							}catch(Exception e) {}
						}
						player.sendMessage(ChatColor.GREEN + "你已放棄技能- " + skillname);
					} else {
						player.sendMessage(ChatColor.RED + "技能不存在,請確認是否擁有此技能或名稱是否正確");
					}

					return true;
				}
			}
		}
		return false;
	}

}
