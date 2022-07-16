package de.fruitfly.retrotech.demo;

public class Vector2i {
	public int x, y;
	public Vector2i() {};
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static float dist(Vector2i v1, Vector2i v2) {
		return (float) Math.sqrt((v1.x-v2.x)*(v1.x-v2.x) + (v1.y-v2.y)*(v1.y-v2.y));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2i other = (Vector2i) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Vector2i [x=" + x + ", y=" + y + "]";
	}
}
