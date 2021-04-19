package nashi.NRPG.Skills;

import java.util.HashMap;
import java.util.HashSet;

public class SkillName {
	public static HashMap<String, String> skillname = new HashMap<String, String>();
	public static HashSet<String> SkillList = new HashSet<String>();

	public static void put(String Skillname) {
		skillname.put(Skillname.toLowerCase(), Skillname);
		if (!SkillList.contains(Skillname)) {
			SkillList.add(Skillname);
		}
	}

	public static String get(String Skillname) {
		return skillname.get(Skillname.toLowerCase());
	}
}
