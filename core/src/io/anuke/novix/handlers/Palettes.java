package io.anuke.novix.handlers;

import static io.anuke.novix.Vars.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;

import io.anuke.novix.Novix;
import io.anuke.novix.internal.Palette;
import io.anuke.ucore.core.Settings;

public class Palettes{
	private Palette current;
	private ObjectMap<String, Palette> palettes = new ObjectMap<String, Palette>();
	private Array<Palette> palettesort = new Array<Palette>();
	
	public Palettes(){
		
	}
	
	public Palette current(){
		return current;
	}
	
	public void setSelected(Palette palette){
		this.current = palette;
		current.time = TimeUtils.millis();
	}
	
	public Array<Palette> getPalettes(){
		palettesort.clear();
		for(Palette p : palettes.values())
			palettesort.add(p);
		
		palettesort.sort();
		
		return palettesort;
	}
	
	public void removePalette(Palette palette){
		palettes.remove(palette.id);
	}

	public void addPalette(Palette palette){
		palettes.put(palette.id, palette);
	}

	public void save(){
		try{
			String string = json.toJson(palettes);
			paletteFile.writeString(string, false);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void load(){
		try{
			palettes = json.fromJson(ObjectMap.class, paletteFile);
			
			String id = Settings.getString("lastpalette");
			if(id != null){
				current = palettes.get(id);
			}

			Novix.log("Palettes loaded.");
		}catch(Exception e){
			e.printStackTrace();
			Novix.log("Palette file nonexistant or corrupt.");
		}

		if(current == null){
			current = new Palette("Untitled", genColors(8));
			palettes.put(current.id, current);
			Settings.putString("lastpalette", current.id);
		}
	}
	
	public Color[] genColors(int length){
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
	
	public static String genID(){
		return MathUtils.random(Long.MAX_VALUE - 1) + "";
	}
}
