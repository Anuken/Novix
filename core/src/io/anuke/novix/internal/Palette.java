package io.anuke.novix.internal;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novix.handlers.Palettes;

public class Palette implements Comparable<Palette>{
	public Color[] colors;
	public String name;
	public String id;
	
	public Palette(String name, Color...colors){
		this.name = name;
		this.colors = colors;
		this.id = Palettes.genID();
	}
	
	private Palette(){}
	
	public Palette(Palette other){
		this.name = other.name;
		this.id = Palettes.genID();
		
		colors = new Color[other.colors.length];
		for(int i = 0; i < other.colors.length; i ++){
			colors[i] = other.colors[i].cpy();
		}
	}

	public void resize(int size){
		Color[] ncolors = new Color[size];
		
		for(int i = 0; i < ncolors.length; i ++){
			if(i >= colors.length)
				ncolors[i] = Color.WHITE;
			else
				ncolors[i] = colors[i];
		}
		
		colors = ncolors;
	}

	@Override
	public int compareTo(Palette other){
		return 0;
	}
}
