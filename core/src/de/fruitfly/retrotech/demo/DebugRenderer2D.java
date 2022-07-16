package de.fruitfly.retrotech.demo;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;

public class DebugRenderer2D {
	private interface DeferedRenderCall {
		public void execute();
	}
	
	private List<DeferedRenderCall> queue = new LinkedList<DeferedRenderCall>();
	
	public void line(final float x1, final float y1, final float x2, final float y2, final Color c1, final Color c2) {
		queue.add(new DeferedRenderCall() {
			@Override
			public void execute() {
				R.shapes.line(x1, y1, x2, y2, c1, c2);
			}
		});
	}
	
	public void vector(final float x1, final float y1, final float dx, final float dy, final Color c1, final Color c2) {
		queue.add(new DeferedRenderCall() {
			@Override
			public void execute() {
				System.out.println(x1 + " " + y1 + " " + (x1+dx) + " " + (y1+dy));
				R.shapes.line(x1, y1, x1+dx, y1+dy, c1, c2);
			}
		});
	}
	
	public void draw(Matrix4 projection, Matrix4 view) {
		R.shapes.setProjectionMatrix(projection);
		R.shapes.setTransformMatrix(view);
		R.shapes.setAutoShapeType(true);
		R.shapes.begin();
		for (DeferedRenderCall call : queue) {
			call.execute();
		}
		queue.clear();
		R.shapes.end();
	}
}
