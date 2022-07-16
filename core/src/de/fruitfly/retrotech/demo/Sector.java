package de.fruitfly.retrotech.demo;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;

public class Sector {
	public Color floorColor;
	public float[] fbo;
	public List<Wall> wallLoops = new LinkedList<Wall>();
	public Sector() {
		floorColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f);
	}
	public int floorHeight, ceilHeight;
	public int frameCounter;
}
