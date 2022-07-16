package de.fruitfly.retrotech.demo;

public class M {
	public static boolean isBetween(float x1, float x2, float x) {
		if (x1 > x2) {
			return x2 <= x && x <= x1;
		}
		else {
			return x1 <= x && x <= x2;
		}
	}
}
