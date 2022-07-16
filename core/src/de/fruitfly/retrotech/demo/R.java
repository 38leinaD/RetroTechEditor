package de.fruitfly.retrotech.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class R {
	public static ShapeRenderer shapes;
	public static SurfaceRenderer imr;
	public static DebugRenderer2D debug2D;
	public static BitmapFont font, smallFont;
	public static SpriteBatch batch;
	public static int frameCounter = 0;
	public static void init() {
		shapes = new ShapeRenderer();
		font = new BitmapFont(Gdx.files.internal("fonts/visitor20.fnt"));
		font.setColor(Color.WHITE);
		smallFont = new BitmapFont(Gdx.files.internal("fonts/visitor10.fnt"));
		smallFont.setColor(Color.WHITE);
		batch = new SpriteBatch();
		
		debug2D = new DebugRenderer2D();
		imr = new SurfaceRenderer();
	}
}
