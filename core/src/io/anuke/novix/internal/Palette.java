package io.anuke.novix.internal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.TimeUtils;

import io.anuke.novix.handlers.Palettes;

public class Palette implements Comparable<Palette>{
	public Color[] colors;
	public String name;
	public String id;
	public long time;
	
	public Palette(String name, Color...colors){
		this.name = name;
		this.colors = colors;
		this.id = Palettes.genID();
		time = TimeUtils.millis();
	}
	
	private Palette(){}
	
	public Palette(Palette other){
		this.name = other.name;
		this.id = Palettes.genID();
		
		colors = new Color[other.colors.length];
		for(int i = 0; i < other.colors.length; i ++){
			colors[i] = other.colors[i].cpy();
		}
		
		time = TimeUtils.millis();
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
		return time > other.time ? -1 : time < other.time ? 1 : 0;
	}
}
