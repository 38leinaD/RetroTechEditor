package de.fruitfly.retrotech.demo;

import com.badlogic.gdx.math.Vector3;

public class Player {
	private Vector3 position = new Vector3();
	private float yaw, pitch;
	
	public float getYaw() {
		return yaw;
	}
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	public float getPitch() {
		return pitch;
	}
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	public Vector3 getPosition() {
		return position;
	}
}
