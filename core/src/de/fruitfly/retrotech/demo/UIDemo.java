package de.fruitfly.retrotech.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class UIDemo implements Screen {

	private Stage stage;
	private Table table;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
		table.debug(); // turn on all debug lines (table, cell, and widget)
		table.debugTable(); // turn on only table lines

		stage.draw();
		//Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		Skin skin = new Skin(Gdx.files.internal("ui/default/uiskin.json"));
		
		Label nameLabel = new Label("name", skin);
		TextField nameText = new TextField("bla", skin);
		Label addressLabel = new Label("Address:", skin);
		TextField addressText = new TextField("bli", skin);

		table = new Table();
		table.setFillParent(true);

		table.add(nameLabel);
		table.add(nameText).width(100);
		table.row();
		table.add(addressLabel);
		table.add(addressText).width(100);
		
		stage = new Stage();
		stage.addActor(table);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

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

}
