package io.anuke.novix.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Palette implements Comparable<Palette>{
	public long time;
	public Color[] colors;
	public String name, id;
	
	public Palette(String name, String id, int size, boolean auto){
		this.name = name;
		this.id = id;
		colors = new Color[size];
		time = TimeUtils.millis();
		if(auto){
			genColors();
		}else{
			for(int i = 0; i < size; i ++)
				colors[i] = Color.BLACK.cpy();
		}
		//
	}
	
	public Palette(){}
	
	public int size(){
		return colors.length;
	}
	
	private void genColors(){
		int clen = MathUtils.random(1, 4);
		int ci = 0;
		Color current = null;
		for(int i = 0; i < colors.length; i ++){
			if(current == null){
				current = new Color(MathUtils.random(0.5f), MathUtils.random(0.5f), MathUtils.random(0.5f), 1f);
			}else{
				float b = MathUtils.random(0.1f);
				current = new Color(current).add(b,b,b,0f).add(MathUtils.random(0.2f), MathUtils.random(0.2f), MathUtils.random(0.2f), 0f);
				current.clamp();
			}
			
			colors[i] = current;
			ci ++;
			if(ci > clen){
				current = null;
				ci = 0;
			}
		}
	}
	
	public int compareTo(Palette other){
		if(other.time == time) return 0;
		return other.time > time ? -1 : 1;
	}
}
