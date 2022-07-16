package de.fruitfly.retrotech.demo;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RetroTechDemo extends Game {
	SpriteBatch batch;
	Texture img;
	public static Map map;
	public static EditorTopView editorTopView;
	public static Editor3DView editor3DView;
	public static Player player;
	@Override
	public void create () {
		T.init();
		R.init();
		System.out.println(System.getProperty("user.dir"));
		FileHandle mf = Gdx.files.getFileHandle("./maps/test.map", FileType.Absolute);
		//FileHandle mf = Gdx.files.getFileHandle("/Users/daniel/Dropbox/Dev/Java/Games/RetroTech/Project/android/assets/maps/test.map", FileType.Absolute);

		if (mf.exists()) {
			map = MapFile.load(mf);
		}
		else {
			map = new Map();
		}
		editorTopView = new EditorTopView(this);
		editor3DView = new Editor3DView(this);
		player = new Player();
		player.getPosition().set(0.0f, -10.0f, 3.0f);

		setScreen(editorTopView);
	}
}
