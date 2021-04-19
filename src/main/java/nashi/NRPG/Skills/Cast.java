package nashi.NRPG.Skills;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Cast {
	public static long timelimit;
	public static final int length = 20;
	public static final String logo = "✡";
	private static final String normal = "-";
	public static final ChatColor wrong = ChatColor.YELLOW;
	public static final ChatColor right = ChatColor.AQUA;
	public static final ChatColor current = ChatColor.LIGHT_PURPLE;

	public static HashMap<Player, Integer> castspeed = new HashMap<Player, Integer>();

	public static class Abracadabra {
		private String str;
		private Set<Integer> pos;

		public Abracadabra(String str, Set<Integer> set) {
			this.str = str;
			this.pos = set;
		}

		public String getString() {
			return this.str;
		}

		public Set<Integer> getPos() {
			return this.pos;
		}
	}

	public static Abracadabra getabracadabra(int num) {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();
		int l = 0;
		Set<Integer> pos = new HashSet<Integer>();
		while (l < length) {
			l++;
			sb.append(normal);
		}
		l = 0;
		while (l < num) {
			int s = rand.nextInt(length);
			if (!pos.contains(s)) {
				pos.add(s);
				l++;
			} else {
				while (pos.contains(s)) {
					s = (s + 1) % length;
				}
				pos.add(s);
				l++;
			}
		}
		for (int i : pos) {
			sb.replace(i, i + 1, logo);
		}
		Abracadabra a = new Abracadabra(sb.toString(), pos);
		return a;
	}

	public static String getDisplay(String str, int pos) {
		str = str.replaceAll(logo, right + logo).replaceAll(normal, wrong + normal);
		String s = str.substring(0, pos * 3) + current;
		try {
			s += str.substring(pos * 3 + 2, str.length());
		} catch (Exception e) {

		}
		return s;
	}
}
