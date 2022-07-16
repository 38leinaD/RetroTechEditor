package de.fruitfly.retrotech.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class SectorOps {
	private Map map;
	protected Sector buildingSector;

	public SectorOps(Map map) {
		this.map = map;
	}
	
	
	public void buildSector(int x, int y) {
		if (buildingSector == null) {
			buildingSector = new Sector();
			
			buildingSector.floorHeight = 0;
			buildingSector.ceilHeight = 16;
			
			Wall w = new Wall(buildingSector, x, y);
			buildingSector.wallLoops.add(w);
			
			Wall zw = new Wall(buildingSector, x, y);
			w.end = zw;
		}
		else {
			boolean closeSector = false;
			for (Wall w : buildingSector.wallLoops.get(0)) {
				if (w.end == null) continue;
				if (w.start.x == x && w.start.y == y) {
					closeSector = true;
					break;
				}
			}
			
			if (!closeSector) {
				// check if user wants to e.g. split an existing sector
				Wall startWall = buildingSector.wallLoops.get(0);
				List<Wall> existingWallsThatStartAtNewSectorStart = getWallsStartingAtVertex(startWall.start.x, startWall.start.y);
				if (existingWallsThatStartAtNewSectorStart.size() > 0) {
					existingWallsThatStartAtNewSectorStart = new ArrayList<Wall>(existingWallsThatStartAtNewSectorStart);
					
					List<Wall> existingWallsThatStartAtNewSectorEnd = getWallsStartingAtVertex(x, y);
					
					if (existingWallsThatStartAtNewSectorEnd.size() > 0) {
						Wall w1 = null, w2 = null;
						outerLoop:
						for (Wall ws : existingWallsThatStartAtNewSectorStart) {
							for (Wall we : existingWallsThatStartAtNewSectorEnd) {
								if (ws.isPartOfSameLoopAs(we)) {
									w1 = ws;
									w2 = we;
									break outerLoop;
								}
							}
						}
						
						if (w1 != null) {
							assert w2 != null;
							
							System.out.println("MATCH");
							
							splitWallLoop(w1, w2, buildingSector.wallLoops.get(0));
							buildingSector = null;
							return;
						}
					}
				}
			}
			
			Wall lastWall = null;
			Wall secondLastWall = null;

			for (Wall w : buildingSector.wallLoops.get(0)) {
				secondLastWall = lastWall;
				lastWall = w;
			}
			
			if (closeSector) {
				secondLastWall.end = buildingSector.wallLoops.get(0);
				
				map.getSectors().add(buildingSector);
				buildingSector = null;
			}
			else {
				Wall w = new Wall(buildingSector, x, y);
				lastWall.end = w;
			}
		}
	}
	
	private void splitWallLoop(Wall w1, Wall w2, Wall splittingLoop) {
		Wall w1e = getEndingWall(w1);
		Wall w2e = getEndingWall(w2);
		Sector sector = w1.sector;
		
		System.out.println("=== BEFORE === ");
		for (Wall w : splittingLoop) {
			System.out.println(w);
		}
		
		Wall splittingLoopFlipped = duplicateWallLoop(splittingLoop);
		System.out.println("=== DUP === ");
		for (Wall w : splittingLoopFlipped) {
			System.out.println(w);
		}
		
		splittingLoopFlipped = flipWallLoop(splittingLoopFlipped);
		System.out.println("=== FLIP === ");
		for (Wall w : splittingLoopFlipped) {
			System.out.println(w);
		}
				
		w1e.end = splittingLoop;
		
		Wall lastWall = null;
		Wall secondLastWall = null;

		int numPortals = 0;
		for (Wall w : splittingLoop) {
			secondLastWall = lastWall;
			lastWall = w;
			numPortals++;
		}
		numPortals--;
		
		secondLastWall.end = w2;
		
		w2.sector.wallLoops.remove(0);
		w2.sector.wallLoops.add(w2);
		
		w2e.end = splittingLoopFlipped;
		for (Wall w : splittingLoopFlipped) {
			secondLastWall = lastWall;
			lastWall = w;
		}
		
		secondLastWall.end = w1;
		
		Sector newSector = new Sector();
		for (Wall w : w1) {
			w.sector = newSector;
		}
		newSector.wallLoops.add(w1);
		map.getSectors().add(newSector);
		
		newSector.floorHeight = sector.floorHeight+15;
		newSector.ceilHeight = sector.ceilHeight-15;
		
		// connect portals
		int i=0;
		for (Wall w : splittingLoop) {
			if (i++>=numPortals) break;
			connectPortals(w);
		}
	}


	private void connectPortals(Wall w) {
		List<Wall> ending = getWallsStartingAtVertex(w.end.start.x, w.end.start.y);
		for (Wall end : ending) {
			if (end.end.start.equals(w.start)) {
				w.portal = end;
				end.portal = w;
				break;
			}
		}
	}


	public void unbuildSectorVertex() {
		if (buildingSector != null) {
			Wall lastWall = null;
			Wall secondLastWall = null;

			for (Wall w : buildingSector.wallLoops.get(0)) {
				secondLastWall = lastWall;
				lastWall = w;
			}
			
			if (secondLastWall != null) {
				secondLastWall.end = null;
			}
			else {
				buildingSector = null;
			}
		}
	}
	
	public void updateBuildWall(int x, int y) {
		if (buildingSector != null) {
			Wall lastWall = null;
			for (Wall w : buildingSector.wallLoops.get(0)) {
				lastWall = w;
			}
			lastWall.start.x = x;
			lastWall.start.y = y;
		}
	}
	
	public Vector2i findClosestVertex(int x, int y) {
		Vector2i point = new Vector2i(x, y);
		float minDistance = Float.POSITIVE_INFINITY;
		Vector2i minDistanceVertex = null;
		for (Sector s : RetroTechDemo.map.getSectors()) {
			for (Wall wallLoop : s.wallLoops) {
				int i=0;
				for (Wall wall : wallLoop) {
					Vector2i vertex = wall.start;
					float dist = Vector2i.dist(point, vertex);
					if (minDistanceVertex != null) {
						if (dist < minDistance) {
							minDistanceVertex = vertex;
							minDistance = dist;
						}
					}
					else {
						minDistanceVertex = vertex;
						minDistance = dist;
					}
					i++;
				}
			}
		}
		return minDistanceVertex;
	}
	
	public Wall findClosestWall(int x, int y) {
		float minDistance = Float.POSITIVE_INFINITY;
		Wall minDistanceWall = null;
		for (Sector s : RetroTechDemo.map.getSectors()) {
			for (Wall wallLoop : s.wallLoops) {
				for (Wall w : wallLoop) {
					Vector2 wallDir = new Vector2(w.end.start.x - w.start.x, w.end.start.y - w.start.y);
					Vector2 wallDirNorm = new Vector2(wallDir).nor();
					Vector2 pointDir = new Vector2(x - w.start.x, y - w.start.y);
					float alpha = pointDir.dot(wallDirNorm)/wallDir.len();
					float minDistanceOfCurrentWall;
					if (alpha <= 0.0f) {
						minDistanceOfCurrentWall = (float) Math.sqrt((w.start.x - x)*(w.start.x - x) + (w.start.y - y)*(w.start.y - y));
					}
					else if (alpha >= 1.0f) {
						minDistanceOfCurrentWall = (float) Math.sqrt((w.end.start.x - x)*(w.end.start.x - x) + (w.end.start.y - y)*(w.end.start.y - y));
					}
					else {
						//http://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
						float x1 = w.start.x;
						float y1 = w.start.y;
						float x2 = w.end.start.x;
						float y2 = w.end.start.y;
						float x0 = x;
						float y0 = y;
						minDistanceOfCurrentWall = (float) (Math.abs((y2-y1)*x0-(x2-x1)*y0+x2*y1-y2*x1)/Math.sqrt((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1)));
					}
					
					if (minDistanceOfCurrentWall < minDistance) {
						minDistanceWall = w;
						minDistance = minDistanceOfCurrentWall;
					}
				}
			}
		}
		return minDistanceWall;
	}

	public void splitWall(Wall wall) {
		float hx = wall.start.x + (wall.end.start.x - wall.start.x)/2.0f;
		float hy = wall.start.y + (wall.end.start.y - wall.start.y)/2.0f;
		Wall newWall = new Wall(wall.sector, (int)hx, (int)hy);
		newWall.end = wall.end;
		wall.end = newWall;
		
		// TODO: portals
	}
	
	public Sector findContaingSector(int x, int y) {
		for (Sector sector : map.getSectors()) {
			if (isPointInSector(sector, x, y)) {
				return sector;
			}
		}
		return null;
	}
	
	public boolean isPointInSector(Sector sector, int x, int y) {
		for (int i=0; i<sector.wallLoops.size(); i++) {
			Wall loop = sector.wallLoops.get(i);
			if (i==0) {
				if (!isPointInLoop(loop, x, y)) {
					return false;
				}
			}
			else {
				if (isPointInLoop(loop, x, y)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isPointInLoop(Wall loop, int x, int y) {
		int intersections=0;
		for (Wall w : loop) {
			Vector2i v1 = w.start;
			Vector2i v2 = w.end.start;
			
			if (M.isBetween(v1.y, v2.y, y) && v1.y != v2.y) {
				float xi = (y-v1.y)*(v2.x-v1.x)/(float)(v2.y-v1.y)+v1.x;
				if (M.isBetween(v1.x, v2.x, xi) && xi>=x) {
					intersections++;
				}
			}
		}
		return intersections%2==1;
	}
	
	/*
	public Sector findContaingSector(int x, int y) {
		Vector2i point = new Vector2i(x, y);
		float minDistance = Float.POSITIVE_INFINITY;
		Sector minSector = null;
		for (Sector s : RetroTechDemo.map.getSectors()) {
			for (List<Wall> wallLoop : s.walls) {
				int i=0;
				for (Wall wall : wallLoop) {
					Vector2i vertex = wall.start;
					float dist = Vector2i.dist(point, vertex);
					if (minSector != null) {
						if (dist < minDistance) {
							minSector = s;
							minDistance = dist;
						}
					}
					else {
						minSector = s;
						minDistance = dist;
					}
					i++;
				}
			}
		}
		return minSector;
	}*/
	
	public void makeSubsector(Sector sector) {
		
	}

	public void updateVertexPosition(Vector2i draggedVertex, int x, int y) {
		List<Wall> affectedWalls = getWallsStartingAtVertex(draggedVertex.x, draggedVertex.y);
		for (Wall w : affectedWalls) {
			w.start.x = x;
			w.start.y = y;
		}
	}
	
	private List<Wall> _wallsResult = new ArrayList<Wall>(10);
	public List<Wall> getWallsStartingAtVertex(int x, int y) {
		_wallsResult.clear();
		for (Sector s : RetroTechDemo.map.getSectors()) {
			for (Wall wallLoop : s.wallLoops) {
				for (Wall w : wallLoop) {
					if (w.start.x == x && w.start.y == y) {
						_wallsResult.add(w);
					}
				}
			}
		}
		return _wallsResult;
	}
	
	// Get wall that ends at this wall's starting point.
	public Wall getEndingWall(Wall wall) {
		Iterator<Wall> it = wall.iterator();
		Wall end = null;
		while (it.hasNext()) {
			end = it.next();
		}
		return end;
	}
	
	public Wall duplicateWallLoop(Wall wallLoop) {
		Wall cur = null;
		Wall start = null;
		for (Wall w : wallLoop) {
			if (cur == null) {
				start = cur = new Wall(null, w.start.x, w.start.y);
				cur.portal = w.portal;
			}
			else {
				Wall next = new Wall(null, w.start.x, w.start.y);
				cur.end = next;
				cur = next;
				next.portal = w.portal;
			}
		}
		return start;
	}
	
	public Wall flipWallLoop(Wall wallLoop) {
		Wall pw = null;
		List<Wall> itList = new LinkedList<Wall>();
		for (Wall w : wallLoop) {
			itList.add(w);
		}
		
		for (Wall w : itList) {
			if (pw != null) w.portal = pw.portal;
			w.end = pw;
			pw = w;
		}
		
		return pw;
	}
	
	public void deleteSector(Sector s) {
		map.getSectors().remove(s);
	}
}
