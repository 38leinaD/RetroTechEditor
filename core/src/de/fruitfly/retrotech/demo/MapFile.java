package de.fruitfly.retrotech.demo;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;

class MSector {
	public int wallPtr, numWalls;
	public int floorHeight;
	public int ceilHeight;
}

class MWall {
	public Vector2i start;
	public int nextWall;
	public int portal;
	
	public Wall wall;
}

public class MapFile {
	public static void save(Map map, FileHandle fh) {
		fh.writeString("", false);
		int wallCount = 0;
		List<Wall> wallList = new LinkedList<Wall>();
		for (Sector sector : map.getSectors()) {
			fh.writeString("SECTOR\n", true);
			fh.writeString(" WALLSTART=" + wallCount + "\n", true);
			int sectorWallCount = 0;
			for (Wall wallLoop : sector.wallLoops) {
				for (Wall wall : wallLoop) {
					sectorWallCount++;
					wallList.add(wall);
				}
			}
			wallCount += sectorWallCount;
			fh.writeString(" WALLCOUNT=" + sectorWallCount + "\n", true);
			fh.writeString(" CEILHEIGHT=" + sector.ceilHeight + "\n", true);
			fh.writeString(" FLOORHEIGHT=" + sector.floorHeight + "\n", true);
		}
		
		fh.writeString("\n", true);

		for (Wall wall : wallList) {
			fh.writeString("WALL\n", true);
			fh.writeString(" START=" + wall.start.x + "," + wall.start.y + "\n", true);
			fh.writeString(" NEXTWALL=" + wallList.indexOf(wall.end) + "\n", true);
			fh.writeString(" PORTAL=" + "-1" + "\n", true);
		}
		fh.writeString("\n", true);
	}
	
	public static Map load(FileHandle fh) {
		String str = fh.readString();
		Map map = new Map();
		String[] lines = str.split("\n");
		
		List<MSector> sectors = new LinkedList<MSector>();
		List<MWall> walls = new LinkedList<MWall>();
		
		for (int i=0; i<lines.length; i++) {
			String line = lines[i];
			if (line.startsWith("SECTOR")) {
				MSector sector = new MSector();
				sectors.add(sector);
				while (i+1<lines.length && lines[i+1].startsWith(" ")) {
					line = lines[++i];
					if (line.startsWith(" WALLSTART")) {
						sector.wallPtr = Integer.parseInt(line.split("=")[1]);
					}
					else if (line.startsWith(" WALLCOUNT")) {
						sector.numWalls = Integer.parseInt(line.split("=")[1]);
					}
					else if (line.startsWith(" CEILHEIGHT")) {
						sector.ceilHeight = Integer.parseInt(line.split("=")[1]);
					}
					else if (line.startsWith(" FLOORHEIGHT")) {
						sector.floorHeight = Integer.parseInt(line.split("=")[1]);
					}
				}
			}
			else if (line.startsWith("WALL")) {
				MWall wall = new MWall();
				walls.add(wall);
				while (i+1<lines.length && lines[i+1].startsWith(" ")) {
					line = lines[++i];
					if (line.startsWith(" START")) {
						String[] coords = line.split("=")[1].split(",");
						wall.start = new Vector2i(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
					}
					else if (line.startsWith(" NEXTWALL")) {
						wall.nextWall = Integer.parseInt(line.split("=")[1]);
					}
					else if (line.startsWith(" PORTAL")) {
						wall.portal = Integer.parseInt(line.split("=")[1]);
					}
				}
			}
		}
		
		for (int i=0; i<walls.size(); i++) {
			MWall mwall = walls.get(i);
			mwall.wall = new Wall(null, mwall.start.x, mwall.start.y);
		}
		
		for (int i=0; i<walls.size(); i++) {
			MWall mwall = walls.get(i);
			mwall.wall.end = walls.get(mwall.nextWall).wall;
		}
		
		for (MSector msector : sectors) {
			Sector sector = new Sector();
			sector.floorHeight = msector.floorHeight;
			sector.ceilHeight = msector.ceilHeight;
			List<Wall> wallLoop = new LinkedList<Wall>();
			for (int i=msector.wallPtr; i<msector.wallPtr+msector.numWalls; i++) {
				MWall mwall = walls.get(i);
				mwall.wall.sector = sector;
				wallLoop.add(mwall.wall);
				if (mwall.nextWall < i) {
					// loop closed				
					sector.wallLoops.add(mwall.wall);
				}
			}
			map.getSectors().add(sector);
		}
		
		return map;
	}
}
