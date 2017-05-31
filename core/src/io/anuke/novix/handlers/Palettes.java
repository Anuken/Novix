package io.anuke.novix.handlers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import io.anuke.novix.internal.Palette;

public class Palettes{
	private Palette current;
	
	public Palettes(){
		
	}
	
	public void load(){
		current = new Palette("Test Palette", genColors(32));
	}
	
	public Palette current(){
		return current;
	}
	
	private Color[] genColors(int length){
		Color[] colors = new Color[length];
		
		int clen = MathUtils.random(1, 4);
		int ci = 0;
		Color current = null;
		for(int i = 0; i < length; i ++){
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
		
		return colors;
	}
	
	public static long genID(){
		return MathUtils.random(Long.MAX_VALUE - 1);
	}
}
