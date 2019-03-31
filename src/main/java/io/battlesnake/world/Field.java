package io.battlesnake.world;

import java.util.Objects;

public class Field implements Cloneable{

	private int x;
	private int y;
	
	public Field(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void add(Field toAdd) {
		this.x += toAdd.x;
		this.y += toAdd.y;
	}
	
	public int distanceTo(Field other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
	
	@Override
	public Field clone() {
		
		try {
			return (Field) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field that = (Field) o;
        return x == that.x &&
                y == that.y;
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
    	if (x == 0 && y == 1)
			return "down";
		else if (x == 1 && y == 0)
			return "right";
		else if (x == -1 && y == 0)
			return "left";
		else if (x == 0 && y == -1)
			return "up";
		else
			return "error";	
    }
}
