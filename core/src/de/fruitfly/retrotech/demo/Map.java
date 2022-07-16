package de.fruitfly.retrotech.demo;

import java.util.LinkedList;
import java.util.List;

public class Map {
	private Vector2i spawn = new Vector2i();
	private List<Sector> sectors = new LinkedList<Sector>();
	
	public List<Sector> getSectors() {
		return sectors;
	}

	public Vector2i getSpawn() {
		return spawn;
	}
}
