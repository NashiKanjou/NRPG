package nashi.NRPG.Skills;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.bukkit.entity.Player;

import nashi.NRPG.Main;
import nashi.NRPG.API.SkillAPI;
import nashi.NRPG.Skills.Skill.Skills;

public abstract class NewSkill {

	private static void load(File fileEntry) {
		try {
			File f = fileEntry;
			URL u = f.toURI().toURL();
			URLClassLoader child = new URLClassLoader(new URL[] { u }, Main.class.getClassLoader());
			Class<?> clazz = Class.forName(
					fileEntry.getName().replaceAll(".jar", "") + "." + fileEntry.getName().replaceAll(".jar", ""), true,
					child);
			Object s = clazz.getDeclaredConstructor().newInstance();
			try {
				Method set = clazz.getMethod("load");
				set.invoke(s);
			} catch (Exception e) {
			}
			try {
				String name = fileEntry.getName().replaceAll(".jar", "");
				if (SkillAPI.getSkillPublicBoolean(name, "general")) {
					Skill.GeneralSkills.add(name);
				}
				Method skill = clazz.getMethod("skill", Player.class);
				Skill.skills.put(name, new Skill.Skills(skill, s, clazz));
				SkillName.put(name);
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unloadAllSkills() {
		for (String key : Skill.skills.keySet()) {
			Skills skill = Skill.skills.get(key);
			Class<?> clazz = skill.skill;
			try {
				Object s = clazz.getDeclaredConstructor().newInstance();
				Method set = clazz.getMethod("unload");
				set.invoke(s);
			} catch (Exception e) {
			}
		}
	}

	private static void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				load(fileEntry);
			}
		}
	}

	public static void loadskill() {
		final File folder = new File(Main.getPlugin().getDataFolder() + File.separator + "skills");
		listFilesForFolder(folder);
	}
}
