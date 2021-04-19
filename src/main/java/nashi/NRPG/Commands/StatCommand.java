package nashi.NRPG.Commands;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import nashi.NRPG.API.Utils;
import nashi.NRPG.Players.Players;
import nashi.NRPG.Players.Players.PlayerData;
import nashi.NRPG.Skills.SkillName;

public class StatCommand implements CommandExecutor {
	public static DecimalFormat df = new DecimalFormat("#.##");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlable, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;
			YamlConfiguration y = Players.getYaml(player);
			PlayerData pdata = Players.Data.get(player);
			player.sendMessage(ChatColor.GREEN + "詠唱間隔: " + y.getInt("CastSpeed"));

			player.sendMessage(ChatColor.GREEN + "種族: " + ChatColor.GOLD + pdata.getRace().getDisplayName());
			int slot = y.getInt("UniqueSkillSlots");
			if (slot < 0) {
				slot = 0;
			}
			player.sendMessage(ChatColor.GREEN + "等級: " + ChatColor.GOLD + pdata.getStatus().getLvL());
			player.sendMessage(ChatColor.GOLD + "能力值");
			player.sendMessage(ChatColor.GREEN + "血量倍率: " + ChatColor.GOLD + df.format(pdata.getTotalStatusHP()*pdata.getStatus().getLvL()));
			player.sendMessage(ChatColor.GREEN + "魔力倍率: " + ChatColor.GOLD + df.format(pdata.getTotalStatusMP()*pdata.getStatus().getLvL()));
			player.sendMessage(ChatColor.GREEN + "攻擊倍率: " + ChatColor.GOLD + df.format(pdata.getDamage()));
			player.sendMessage(ChatColor.GREEN + "遠程倍率: " + ChatColor.GOLD + df.format(pdata.getRangeDamage()));
			player.sendMessage(ChatColor.GREEN + "技能倍率: " + ChatColor.GOLD + df.format(pdata.getSkillDamage()));
			player.sendMessage(ChatColor.GREEN + "迴避率/爆擊率: " + ChatColor.GOLD + df.format(pdata.getTotalStatusLUK()*Utils.LUKAVD)+"% / "+df.format(pdata.getTotalStatusLUK()*Utils.LUKCRIT)+"%");
			player.sendMessage(ChatColor.GREEN + "速度: " + ChatColor.GOLD + df.format(pdata.getTotalStatusSpeed()*Utils.SPDSpeed+Utils.basespd));
			player.sendMessage(ChatColor.GREEN + "裝甲倍率: " + ChatColor.GOLD + df.format(pdata.getTotalStatusArmor()));
			player.sendMessage(
					ChatColor.GREEN + "抗性倍率: " + ChatColor.GOLD + df.format(pdata.getTotalStatusArmorToughness()));
			player.sendMessage(ChatColor.GREEN + "額外技能槽數量: " + ChatColor.GOLD + slot);
			player.sendMessage(ChatColor.GREEN + "技能列表: ");
			for (String str : y.getStringList("UniqeSkills")) {
				player.sendMessage(ChatColor.YELLOW + "- " + ChatColor.LIGHT_PURPLE + SkillName.get(str)
						+ ChatColor.GREEN + " " + Players.skillsetting.getString(SkillName.get(str) + ".Description"));
			}
			return true;
		}
		return false;
	}

}
