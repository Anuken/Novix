package net.pixelstatic.pixeleditor.graphics;

import com.badlogic.gdx.graphics.Color;

public class Palette{
	public Color[] colors;
	public String name;
	
	public Palette(String name, int size){
		this.name = name;
		colors = new Color[size];
		for(int i = 0; i < size; i ++)
			colors[i] = Color.WHITE.cpy();
	}
	
	public Palette(){
		colors = null;
		name = null;
	}
	
	public int size(){
		return colors.length;
	}
}
