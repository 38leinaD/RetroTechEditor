package de.fruitfly.retrotech.demo;

import java.util.logging.LogManager;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

// winding check: http://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order
public class EditorTopView extends InputAdapter implements Screen {

	private Game game;
	
	public static OrthographicCamera cam;
	private Matrix4 proj = new Matrix4();

	private long messageTimer;
	private String message;
	private Tesselator tess;
	
	private int gridSize = 16;
	
	private Wall selectedWall;
	private Vector2i selectedVertex;
	private Sector selectedSector;
	
	public EditorTopView(Game game) {
		cam = new OrthographicCamera();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.game = game;
		E.sectorOps = new SectorOps(RetroTechDemo.map);
		this.tess = new Tesselator();
	}
	
	public void showMessage(String message) {
		this.message = message;
		this.messageTimer = TimeUtils.millis() + 2000;
	}
	
	private void update(float delta) {
		long millies = TimeUtils.millis();
		if (message != null && messageTimer < millies) {
			message = null;
		}
	}
	
	@Override
	public void render(float delta) {
		update(delta);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glFrontFace(GL20.GL_CCW);
		Gdx.gl.glCullFace(GL20.GL_BACK);
		
		R.shapes.setProjectionMatrix(cam.projection);
		R.shapes.setTransformMatrix(cam.view);
		
		R.shapes.begin(ShapeType.Line);
		
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		
		int cx = Gdx.input.getX();
		int cy = Gdx.input.getY();
		
		int cwx, cwy;
		Vector3 screenCoords = new Vector3(cx, cy, 0.0f);
		Vector3 worldCoords = cam.unproject(screenCoords);
		cwx = (int) worldCoords.x;
		cwy = (int) worldCoords.y;
		
		for (int i=-h/2-(gridSize-h/2%gridSize); i<=h/2; i+=gridSize) {
			if (i==0) {
				R.shapes.setColor(0.6f, 0.6f, 0.6f, 1.0f);
			}
			else if (i%100==0) {
				R.shapes.setColor(0.4f, 0.4f, 0.4f, 1.0f);
			}
			else {
				R.shapes.setColor(0.2f, 0.2f, 0.2f, 1.0f);
			}
			R.shapes.line(-w/2, i, w/2, i);
		}
		
		for (int i=-w/2-(gridSize-w/2%gridSize); i<=w/2; i+=gridSize) {
			if (i==0) {
				R.shapes.setColor(0.6f, 0.6f, 0.6f, 1.0f);
			}
			else if (i%100==0) {
				R.shapes.setColor(0.4f, 0.4f, 0.4f, 1.0f);
			}
			else {
				R.shapes.setColor(0.2f, 0.2f, 0.2f, 1.0f);
			}
			R.shapes.line(i,-h/2, i, h/2);
		}
		R.shapes.end();
		
		for (Sector s : RetroTechDemo.map.getSectors()) {
			tess.tesselateSector(s);
		/*	
			R.imr.begin(cam.combined, GL20.GL_TRIANGLES);
			int numTris = s.fbo.length/9;
			for (int i=0; i<numTris; i++) {
				for (int j=0; j<3; j++) {
					R.imr.vertex(s.fbo[i*9+j*3+0], s.fbo[i*9+j*3+1], s.fbo[i*9+j*3+2]);
				}
			}
			R.imr.end();
			*/
			renderSector(s);
		}
		
		if (E.sectorOps.buildingSector != null) {
			renderSector(E.sectorOps.buildingSector);
		}
		
		R.shapes.begin(ShapeType.Line);

		Player player = RetroTechDemo.player;
		Vector3 dir = new Vector3(-MathUtils.sin(player.getYaw()), MathUtils.cos(player.getYaw()), MathUtils.sin(player.getPitch())).scl(20.0f);
		R.shapes.setColor(Color.RED);
		R.shapes.circle(player.getPosition().x, player.getPosition().y, 10.0f);
		R.shapes.line(player.getPosition().x, player.getPosition().y, player.getPosition().x + dir.x, player.getPosition().y + dir.y);
		R.shapes.end();

		renderLabels();
		
		R.debug2D.draw(cam.projection, cam.view);
		
		proj.setToOrtho2D(0.0f, 0.0f, w, h);
		R.shapes.setProjectionMatrix(proj);
		R.shapes.setTransformMatrix(new Matrix4().idt());
		R.shapes.begin(ShapeType.Filled);
		R.shapes.setColor(0.3f, 0.3f, 0.3f, 1.0f);
		R.shapes.rect(0.0f, 0.0f, w, 50.0f);
		
		R.shapes.end();
		
		Gdx.gl.glFrontFace(GL20.GL_CW);
		R.batch.setProjectionMatrix(proj);
		R.batch.setTransformMatrix(new Matrix4().idt());
		R.batch.begin();
		if (message != null) {
			R.font.draw(R.batch, message, 10.0f, 45.0f);
		}
		else {
			R.font.draw(R.batch, "x: " + cwx, 10.0f, 45.0f);
			R.font.draw(R.batch, "y: " + cwy, 100.0f, 45.0f);
		}
		R.font.draw(R.batch, "RetroTech by fruitfly", w-250, 25.0f);
		
		R.batch.end();
	}

	private void renderLabels() {
		R.batch.setProjectionMatrix(cam.projection);
		R.batch.setTransformMatrix(cam.view);
		for (Sector s : RetroTechDemo.map.getSectors()) {
			for (Wall wallLoop : s.wallLoops) {
				int i=0;
				for (Wall wall : wallLoop) {
					renderLabel(""+i++, wall.start.x, wall.start.y, Color.RED);
				}
			}
		}
	}

	private void renderSector(Sector s) {
		R.shapes.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		R.shapes.begin(ShapeType.Line);
		float intensity = 0.75f + 0.25f*MathUtils.sin((float) ((TimeUtils.millis() % 1000) / 1000.0f * 2*Math.PI));

		for (Wall wallLoop : s.wallLoops) {
			for (Wall wall : wallLoop) {
				if (wall.start.equals(selectedVertex)) {
					R.shapes.setColor(intensity, intensity, intensity, 1.0f);
				}
				else {
					R.shapes.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
				R.shapes.rect(wall.start.x-2, wall.start.y-2, 5, 5);
				Wall nw = wall.end;
				if (nw != null) {
					
					float lineIntensity = 1.0f;
					if (selectedWall == wall) {
						lineIntensity = intensity;
					}
					if (wall.portal != null) {
						R.shapes.setColor(lineIntensity, 0.0f, 0.0f, 1.0f);
					}
					else {
						R.shapes.setColor(lineIntensity, lineIntensity, lineIntensity, 1.0f);
					}
					R.shapes.line(wall.start.x, wall.start.y, nw.start.x, nw.start.y);
				}
			}
		}
		R.shapes.end();
	}
	
	private void renderLabel(String str, int x, int y, Color c) {
		TextBounds bounds = R.smallFont.getMultiLineBounds(str);
		float padding = 3.0f;
		
		R.shapes.setColor(c.r, c.g, c.b, c.a);
		R.shapes.begin(ShapeType.Filled);
		R.shapes.triangle(x, y,
				x+10.0f, y-10.0f,
				x+10.0f, y+10.0f);
		
		R.shapes.rect(x+10.0f, y-10.0f-Math.max(bounds.height + 2*padding, 20.0f)+20.0f, Math.max(bounds.width + 2*padding, 20.0f), Math.max(bounds.height + 2*padding, 20.0f));
		R.shapes.end();

		Gdx.gl.glFrontFace(GL20.GL_CW);
		R.batch.begin();
		R.smallFont.drawMultiLine(R.batch, str, x + 10.0f + padding, y + 10.0f - padding);
		R.batch.end();
		Gdx.gl.glFrontFace(GL20.GL_CCW);
	}

	private void screen2world(Vector2i sc, Vector2i wc) {
		Vector3 screenCoords = new Vector3(sc.x, sc.y, 0.0f);
		Vector3 worldCoords = cam.unproject(screenCoords);
		float wcx = worldCoords.x;
		float wcy = worldCoords.y;
		
		wcx = Math.round(wcx/gridSize)*gridSize;
		wcy = Math.round(wcy/gridSize)*gridSize;
		
		wc.x = (int) wcx;
		wc.y = (int) wcy;
	}
	
	private void world2screen(Vector2i wc, Vector2i sc) {
		
	}
	
	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = width;
		cam.viewportHeight = height;
		cam.update();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean keyDown(int keycode) {
		Vector2i screenCoords = new Vector2i(Gdx.input.getX(), Gdx.input.getY());
		Vector2i worldCoords = new Vector2i();
		screen2world(screenCoords, worldCoords);
		
		if (keycode == Keys.SPACE) {
			E.sectorOps.buildSector(worldCoords.x, worldCoords.y);
			return true;
		}
		else if (keycode == Keys.T) {
			Sector s = E.sectorOps.findContaingSector(worldCoords.x, worldCoords.y);
			if (s == null) {
				showMessage("Move mouse cursor over sector to select.");
				return true;
			}
			E.sectorOps.deleteSector(s);
			return true;
		}
		else if (keycode == Keys.BACKSPACE) {
			E.sectorOps.unbuildSectorVertex();
			return true;
		}
		else if (keycode == Keys.PLUS) {
			if (gridSize > 1) {
				gridSize /= 2;
			}
			else {
				showMessage("minimum grid size reached.");
			}
			return true;
		}
		else if (keycode == Keys.ENTER) {
			game.setScreen(RetroTechDemo.editor3DView);
		}
		else if (keycode == Keys.MINUS) {
			if (gridSize < 64) {
				gridSize *= 2;
			}
			else {
				showMessage("maximum grid size reached.");
			}
			return true;
		}
		else if (keycode == Keys.I) {
			if (selectedWall != null) {
				E.sectorOps.splitWall(selectedWall);
			}
		}
		else if (Gdx.input.isKeyPressed(Keys.ALT_LEFT) && keycode == Keys.S) {
			MapFile.save(RetroTechDemo.map, Gdx.files.getFileHandle("C:/Users/daniel.platz/Dropbox/Dev/Java/Games/RetroTech/Project/android/assets/maps/test.map", FileType.Absolute));
			//MapFile.save(RetroTechDemo.map, Gdx.files.getFileHandle("~/Dropbox/Dev/Java/Games/RetroTech/Project/android/assets/maps/test.map", FileType.Absolute));
			showMessage("Saved map.");
		}
		else if (Gdx.input.isKeyPressed(Keys.ALT_LEFT) && keycode == Keys.D) {
			selectedSector = E.sectorOps.findContaingSector(worldCoords.x, worldCoords.y);
		}
		return false;
	}

	private Vector2i draggedVertex = null;
	private int dragPointer;
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Vector2i screenCoords = new Vector2i(Gdx.input.getX(), Gdx.input.getY());
		Vector2i worldCoords = new Vector2i();
		screen2world(screenCoords, worldCoords);

		if (E.sectorOps.buildingSector != null) {
			E.sectorOps.updateBuildWall(worldCoords.x, worldCoords.y);
		}

		selectedVertex = E.sectorOps.findClosestVertex(worldCoords.x, worldCoords.y);
		selectedWall = E.sectorOps.findClosestWall(worldCoords.x, worldCoords.y);
		
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		draggedVertex = selectedVertex;
		dragPointer = pointer;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		draggedVertex = null;
		return true;
	}
	
	

	@Override
	public boolean scrolled(int amount) {
		if (cam.zoom + amount > 0 && cam.zoom + amount < 4) {
			cam.zoom += amount;
			cam.update();
		}
		else {
			showMessage("min/max zoom level reached.");
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (Gdx.input.isButtonPressed(0)) {
			Vector2i worldCoords = new Vector2i();
			screen2world(new Vector2i(screenX, screenY), worldCoords);
			
			E.sectorOps.updateVertexPosition(draggedVertex, worldCoords.x, worldCoords.y);
			//draggedVertex.x = worldCoords.x;
			//draggedVertex.y = worldCoords.y;
		}
		else if (Gdx.input.isButtonPressed(1)) {
			int dx = Gdx.input.getDeltaX(pointer);
			int dy = Gdx.input.getDeltaY(pointer);
			float dxw = dx*(float)cam.zoom;
			float dyw = dy*(float)cam.zoom;
			cam.position.x -= dxw;
			cam.position.y += dyw;
			cam.update();
		}
		
		return true;
	}
	
	
}
