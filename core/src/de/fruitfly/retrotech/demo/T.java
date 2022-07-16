package de.fruitfly.retrotech.demo;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class T {
	public static Texture tex;
	
	public static Map<String, TextureRegion> texMap = new HashMap<String, TextureRegion>(); 
	
	public static void init() {
		tex = new Texture(Gdx.files.internal("textures.png"));
		
		TextureRegion t;
		
		t = new TextureRegion(tex, 512, 512, 32, 32);
		t.flip(false, true);
		texMap.put("wall", t);
		
		t = new TextureRegion(tex, 544, 512, 32, 32);
		t.flip(false, true);
		texMap.put("floor", t);
		
		t = new TextureRegion(tex, 576, 512, 32, 32);
		t.flip(false, true);
		texMap.put("ceil", t);
	}
}
