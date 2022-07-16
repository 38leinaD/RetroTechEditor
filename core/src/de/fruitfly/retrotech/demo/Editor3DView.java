package de.fruitfly.retrotech.demo;

import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Editor3DView extends InputAdapter implements Screen {

	private Game game;
	private PerspectiveCamera viewCam;

	public Editor3DView(Game game) {
		this.game = game;
		this.viewCam = new PerspectiveCamera(75.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.viewCam.near = 0.1f;
		this.viewCam.far = 1000.0f;
	}
	
	@Override
	public void render(float delta) {
		R.frameCounter++;
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		float dx = Gdx.input.getDeltaX()/(float)Gdx.graphics.getWidth();
		float dz = Gdx.input.getDeltaY()/(float)Gdx.graphics.getHeight();
		
		Player player = RetroTechDemo.player;
		
		player.setPitch(player.getPitch() - dz);
		player.setYaw(player.getYaw() - dx);

		Vector3 dir = new Vector3(-MathUtils.sin(player.getYaw()), MathUtils.cos(player.getYaw()), MathUtils.sin(player.getPitch()));
		Vector3 side = new Vector3(dir).crs(0.0f, 0.0f, 1.0f).nor();
		
		if (Gdx.input.isKeyPressed(Keys.W)) {
			player.getPosition().add(dir.x* 2.0f, dir.y* 2.0f, dir.z* 2.0f);
		}
		else if (Gdx.input.isKeyPressed(Keys.S)) {
			player.getPosition().add(dir.x*-2.0f, dir.y*-2.0f, dir.z*-2.0f);
		}
		
		if (Gdx.input.isKeyPressed(Keys.D)) {
			player.getPosition().add(side.x* 2.0f, side.y* 2.0f, side.z* 2.0f);
		}
		else if (Gdx.input.isKeyPressed(Keys.A)) {
			player.getPosition().add(side.x*-2.0f, side.y*-2.0f, side.z*-2.0f);
		}

		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glFrontFace(GL20.GL_CCW);
		Gdx.gl.glCullFace(GL20.GL_BACK);
		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
		viewCam.position.set(player.getPosition());

		viewCam.up.set(0.0f, 0.0f, 1.0f);
		viewCam.direction.set(dir);
		viewCam.update();
		
		/*
		R.shapes.setProjectionMatrix(viewCam.projection);
		R.shapes.setTransformMatrix(viewCam.view);
		
		R.shapes.begin(ShapeType.Filled);
		R.shapes.setColor(Color.RED);
		R.shapes.rect(0.0f, 0.0f, 10.0f, 10.0f);
		R.shapes.end();
		*/
		
		T.tex.bind(0);
		
		for (Sector sector : RetroTechDemo.map.getSectors()) {
			renderSector(sector);
		}
	}

	public void renderSector(Sector sector) {
		// floor
		R.imr.begin(viewCam.combined, GL20.GL_TRIANGLES, T.texMap.get("floor"));
		renderFloorPlane(sector);
		R.imr.end();
		
		// ceil
		R.imr.begin(viewCam.combined, GL20.GL_TRIANGLES, T.texMap.get("ceil"));
		int numTris = sector.fbo.length/9;
		for (int i=0; i<numTris; i++) {
			for (int j=2; j>=0; j--) {
				float x = sector.fbo[i*9+j*3+0];
				float y = sector.fbo[i*9+j*3+1];
				float z = sector.fbo[i*9+j*3+2];
				R.imr.texCoord(x/16.0f, y/16.0f);
				R.imr.vertex(x, y, z + sector.ceilHeight);
			}
		}
		R.imr.end();

		
		for (Wall wallLoop : sector.wallLoops) {
			for (Wall w : wallLoop) {
				Wall nw = w.end;
				
				Vector2i p1 = w.start;
				Vector2i p2 = nw.start;
				
				R.imr.begin(viewCam.combined, GL20.GL_TRIANGLES, T.texMap.get("wall"));

				
				if (w.portal == null) {
					renderWall(p1.x, p1.y, sector.floorHeight, p2.x, p2.y, sector.ceilHeight);
				}
				else {
					Wall portal = w.portal;
					Sector portalSector = portal.sector;
					if (portalSector.floorHeight > sector.floorHeight) {
						renderWall(p1.x, p1.y, sector.floorHeight, p2.x, p2.y, portalSector.floorHeight);
					}
					if (portalSector.ceilHeight < sector.ceilHeight) {
						renderWall(p1.x, p1.y, portalSector.ceilHeight, p2.x, p2.y, sector.ceilHeight);
					}
				}
				R.imr.end();

			}
		}
		
	}
	
	private void renderFloorPlane(Sector sector) {
		int numTris = sector.fbo.length/9;
		for (int i=0; i<numTris; i++) {
			for (int j=0; j<3; j++) {
				float x = sector.fbo[i*9+j*3+0];
				float y = sector.fbo[i*9+j*3+1];
				float z = sector.fbo[i*9+j*3+2];
				R.imr.texCoord(x/16.0f, y/16.0f);
				R.imr.vertex(x, y, z + sector.floorHeight);
			}
		}
	}
	
	private void renderCeilPlane(Sector sector) {
		int numTris = sector.fbo.length/9;
		for (int i=0; i<numTris; i++) {
			for (int j=2; j>=0; j--) {
				float x = sector.fbo[i*9+j*3+0];
				float y = sector.fbo[i*9+j*3+1];
				float z = sector.fbo[i*9+j*3+2];
				R.imr.texCoord(x/16.0f, y/16.0f);
				R.imr.vertex(x, y, z + sector.floorHeight);
			}
		}
	}
	
	private void renderWall(float x1, float y1, float z1, float x2, float y2, float z2) {
		float vOrigin = 0.0f;
		float vAxis = (z2-z1)/16.0f;
		float uOrigin = 0.0f;
		float uAxis = (float) (Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1))/16.0f);
		R.imr.color(Color.WHITE);
		R.imr.texCoord(uOrigin, vOrigin);
		R.imr.vertex(x2, y2, z1);
		//R.imr.color(Color.CYAN);
		R.imr.texCoord(uOrigin + uAxis, vOrigin);
		R.imr.vertex(x1, y1, z1);
		//R.imr.color(Color.CYAN);
		R.imr.texCoord(uOrigin, vOrigin + vAxis);
		R.imr.vertex(x2, y2, z2);
		
		//R.imr.color(Color.CYAN);
		R.imr.texCoord(uOrigin, vOrigin + vAxis);
		R.imr.vertex(x2, y2, z2);
		//R.imr.color(Color.CYAN);
		R.imr.texCoord(uOrigin + uAxis, vOrigin);
		R.imr.vertex(x1, y1, z1);
		//R.imr.color(Color.CYAN);
		R.imr.texCoord(uOrigin + uAxis, vOrigin + vAxis);
		R.imr.vertex(x1, y1, z2);
		
		/*
		 R.imr.color(Color.WHITE);
		R.imr.texCoord(tex.getU(), tex.getV());
		R.imr.vertex(x2, y2, z1);
		//R.imr.color(Color.CYAN);
		R.imr.texCoord(tex.getU2(), tex.getV());
		R.imr.vertex(x1, y1, z1);
		//R.imr.color(Color.CYAN);
		R.imr.texCoord(tex.getU(), tex.getV2());
		R.imr.vertex(x2, y2, z2);
		
		//R.imr.color(Color.CYAN);
		R.imr.texCoord(tex.getU(), tex.getV2());
		R.imr.vertex(x2, y2, z2);
		//R.imr.color(Color.CYAN);
		R.imr.texCoord(tex.getU2(), tex.getV());
		R.imr.vertex(x1, y1, z1);
		//R.imr.color(Color.CYAN);
		R.imr.texCoord(tex.getU2(), tex.getV2());
		R.imr.vertex(x1, y1, z2);
		 */
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCursorCatched(true);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		Gdx.input.setCursorCatched(false);
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
		if (keycode == Keys.ENTER) {
			game.setScreen(RetroTechDemo.editorTopView);
		}
		return true;
	}
}
