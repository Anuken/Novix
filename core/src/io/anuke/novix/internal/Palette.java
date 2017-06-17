package io.anuke.novix.internal;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novix.handlers.Palettes;

public class Palette{
	public Color[] colors;
	public String name;
	public String id;
	
	public Palette(String name, Color...colors){
		this.name = name;
		this.colors = colors;
		this.id = Palettes.genID();
	}
	
	private Palette(){}
}
