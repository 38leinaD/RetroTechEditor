package de.fruitfly.retrotech.demo;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;
import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

public class Tesselator extends GLUtessellatorCallbackAdapter  {

	private GLUtessellator tesselator;
	
	private int primitiveType;
	private float[] primitiveVertices = new float[1000];
	private int primtiveVertexCount;
	
	private float[] tesselationTris = new float[1000];
	private int tesselationTriVertexCount;
	
	public Tesselator() {
		// Create a new tessellation object
		tesselator = GLU.gluNewTess();
		// Set callback functions

		tesselator.gluTessCallback(GLU.GLU_TESS_VERTEX, this);
		tesselator.gluTessCallback(GLU.GLU_TESS_BEGIN, this);
		tesselator.gluTessCallback(GLU.GLU_TESS_END, this);
		tesselator.gluTessCallback(GLU.GLU_TESS_COMBINE, this);
		
		tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_POSITIVE);
	}
/*
	void setWindingRule(int windingRule) {
		// Set the winding rule
		tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, windingRule);
	}
*/
	void tesselateSector(Sector s) {
		tesselationTriVertexCount = 0; 
		tesselator.gluTessBeginPolygon(null);
		for (Wall wallLoop : s.wallLoops) {
			tesselator.gluTessBeginContour();
			for (Wall w : wallLoop) {
				double[] vertexData = new double[] { w.start.x, w.start.y, 0.0, s.floorColor.r, s.floorColor.g, s.floorColor.b }; 
				tesselator.gluTessVertex(vertexData, 0, vertexData);
			}
			tesselator.gluTessEndContour();
		}
		/*
		for (Sector ss : s.subSectors) {
			tesselator.gluTessBeginContour();
			for (int i=0; i<ss.walls.size(); i++) {
				Wall w = ss.walls.get(ss.walls.size()-1-i);
				double[] vertexData = new double[] { w.start.x, w.start.y, 0.0, ss.floorColor.r, ss.floorColor.g, ss.floorColor.b }; 
				tesselator.gluTessVertex(vertexData, 0, vertexData);
			}
			tesselator.gluTessEndContour();
		}
		*/
		tesselator.gluTessEndPolygon();
		
		//s.vbo = new VertexBufferObject(true, tesselationTriCount*3, VertexAttribute.Position());
		//s.vbo.setVertices(tesselationTris, 0, tesselationTriCount*3*3);
		s.fbo = Arrays.copyOf(tesselationTris, tesselationTriVertexCount*3);
	}

	// tesselator.gluDeleteTess();

	private Color c;

	public void begin(int type) {
	// GL_TRIANGLE, GL_TRIANGLE_FAN, GL_TRIANGLE_STRIP
		primitiveType = type;
		primtiveVertexCount = 0;
		if (primitiveType == GL20.GL_TRIANGLES) {
			c = Color.RED;
			//System.out.println("Produces GL_TRIANGLES");
		}
		else if (primitiveType == GL20.GL_TRIANGLE_FAN) {
			c = Color.GREEN;
			//System.out.println("Produces GL_TRIANGLE_FAN");
		}
		else if (primitiveType == GL20.GL_TRIANGLE_STRIP) {
			c = Color.BLUE;
			//System.out.println("Produces GL_TRIANGLE_STRIP");
		}
		else {
			new RuntimeException("Unkown type " + primitiveType);
		}
		//R.imr.begin(EditorTopView.cam.combined, type);
	}
	
	public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
		/*for (int i=0;i<outData.length;i++) {
		double[] combined = new double[6];
		combined[0] = coords[0];
		combined[1] = coords[1];
		combined[2] = coords[2];
		combined[3] = 1;
		combined[4] = 1;
		combined[5] = 1;
		outData[i] = new VertexData(combined);
		}
		// vertex[0] = coords[0];
		// vertex[1] = coords[1];
		// vertex[2] = coords[2];
		//
		// for (int i = 3; i < 6; i++)
		// {
		// vertex[i] = weight[0] * vertex_data[0][i] +
		// indent indweight[1] * vertex_data[1][i] +
		// indent indweight[2] * vertex_data[2][i] +
		// indent indweight[3] * vertex_data[3][i];
		// }
		//
		// *dataOut = vertex;
		*/
			new RuntimeException("combine called");
	}

	public void vertex(Object vertexData) {
		double[] vd = (double[]) vertexData;
		//R.imr.color((float)vd[3], (float)vd[4], (float)vd[5], 1.0f);
		//R.imr.color(c.r, c.g, c.b, 1.0f);
		//R.imr.vertex((float)vd[0], (float)vd[1], (float)vd[2]);
		
		float x = (float)vd[0];
		float y = (float)vd[1];
		float z = (float)vd[2];
		
		primitiveVertices[primtiveVertexCount*3 + 0] = x;
		primitiveVertices[primtiveVertexCount*3 + 1] = y;
		primitiveVertices[primtiveVertexCount*3 + 2] = z;
		
		primtiveVertexCount++;
	}
	
	public void end() {
		//R.imr.end();
		
		if (primitiveType == GL20.GL_TRIANGLES) {
			for (int i=0; i<primtiveVertexCount; i++) {
				tesselationTris[tesselationTriVertexCount*3 + 0] = primitiveVertices[i*3 + 0];
				tesselationTris[tesselationTriVertexCount*3 + 1] = primitiveVertices[i*3 + 1];
				tesselationTris[tesselationTriVertexCount*3 + 2] = primitiveVertices[i*3 + 2];
				tesselationTriVertexCount++;
			}
		}
		else if (primitiveType == GL20.GL_TRIANGLE_FAN) {
			for (int i=2; i<primtiveVertexCount; i++) {
				tesselationTris[tesselationTriVertexCount*3 + 0] = primitiveVertices[0];
				tesselationTris[tesselationTriVertexCount*3 + 1] = primitiveVertices[1];
				tesselationTris[tesselationTriVertexCount*3 + 2] = primitiveVertices[2];
				tesselationTriVertexCount++;
				
				tesselationTris[tesselationTriVertexCount*3 + 0] = primitiveVertices[(i-1)*3 + 0];
				tesselationTris[tesselationTriVertexCount*3 + 1] = primitiveVertices[(i-1)*3 + 1];
				tesselationTris[tesselationTriVertexCount*3 + 2] = primitiveVertices[(i-1)*3 + 2];
				tesselationTriVertexCount++;
				
				tesselationTris[tesselationTriVertexCount*3 + 0] = primitiveVertices[i*3 + 0];
				tesselationTris[tesselationTriVertexCount*3 + 1] = primitiveVertices[i*3 + 1];
				tesselationTris[tesselationTriVertexCount*3 + 2] = primitiveVertices[i*3 + 2];
				tesselationTriVertexCount++;
			}
		}
		else if (primitiveType == GL20.GL_TRIANGLE_STRIP) {
			for (int i=0; i<primtiveVertexCount-2; i++) {
				int a, b;
				if (i%2==0) {
					a = i;
					b = i+1;
				}
				else {
					a = i+1;
					b = i;
				}
				
				tesselationTris[tesselationTriVertexCount*3 + 0] = primitiveVertices[a*3 + 0];
				tesselationTris[tesselationTriVertexCount*3 + 1] = primitiveVertices[a*3 + 1];
				tesselationTris[tesselationTriVertexCount*3 + 2] = primitiveVertices[a*3 + 2];
				tesselationTriVertexCount++;
				
				tesselationTris[tesselationTriVertexCount*3 + 0] = primitiveVertices[b*3 + 0];
				tesselationTris[tesselationTriVertexCount*3 + 1] = primitiveVertices[b*3 + 1];
				tesselationTris[tesselationTriVertexCount*3 + 2] = primitiveVertices[b*3 + 2];
				tesselationTriVertexCount++;
				
				tesselationTris[tesselationTriVertexCount*3 + 0] = primitiveVertices[(i+2)*3 + 0];
				tesselationTris[tesselationTriVertexCount*3 + 1] = primitiveVertices[(i+2)*3 + 1];
				tesselationTris[tesselationTriVertexCount*3 + 2] = primitiveVertices[(i+2)*3 + 2];
				tesselationTriVertexCount++;
			}
		}
	}
	
	@Override
	public void error(int errnum) {
		throw new RuntimeException("tesselation error " + errnum);
	}

	@Override
	public void errorData(int errnum, Object polygonData) {
		throw new RuntimeException("tesselation error " + errnum + " with data " + polygonData);
	}

}
