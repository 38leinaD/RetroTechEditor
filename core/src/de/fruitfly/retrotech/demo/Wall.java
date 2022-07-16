package de.fruitfly.retrotech.demo;

import java.util.Iterator;

public class Wall implements Iterable<Wall> {
	public Vector2i start = new Vector2i();
	public Wall end;
	public Wall portal;
	public Sector sector;
	
	public Wall(Sector s, int x, int y) {
		this.start.x = x;
		this.start.y = y;
		this.sector = s;
	}

	@Override
	public String toString() {
		return "Wall [start=" + start + ", end=" + (end != null ? end.start : "?") + "]";
	}

	public boolean isPartOfSameLoopAs(Wall wall) {
		for (Wall w : this) {
			if (w == wall) return true;
		}
		return false;
	}
	
	@Override
	public Iterator<Wall> iterator() {
		return new Iterator<Wall>() {

			private Wall _start = Wall.this;
			private Wall _current = null;

			@Override
			public void remove() {
				throw new RuntimeException("Not implemented");
			}

			@Override
			public Wall next() {
				if (_current == null) {
					_current = _start;
				} else {
					_current = _current.end;
				}
				return _current;
			}

			@Override
			public boolean hasNext() {
				return _current == null || (_current.end != null && _current.end != _start);
			}
		};
	}
}
