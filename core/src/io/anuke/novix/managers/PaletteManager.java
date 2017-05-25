package io.anuke.novix.managers;

import static io.anuke.novix.Var.*;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.novix.Novix;
import io.anuke.novix.graphics.Palette;

public class PaletteManager{
	private Json json = new Json();
	private Palette currentPalette;
	private ObjectMap<String, Palette> palettes = new ObjectMap<String, Palette>();
	private Array<Palette> palettesort = new Array<Palette>();

	public Palette getCurrentPalette(){
		return currentPalette;
	}

	public void setCurrentPalette(Palette palette){
		currentPalette = palette;
	}

	public Iterable<Palette> getPalettes(){
		palettesort.clear();
		for(Palette palette : palettes.values())
			palettesort.add(palette);
		palettesort.sort();
		return palettesort;
	}

	public void removePalette(Palette palette){
		palettes.remove(palette.id);
	}

	public void addPalette(Palette palette){
		palettes.put(palette.id, palette);
	}

	public void savePalettes(){
		try{
			String string = json.toJson(palettes);
			paletteFile.writeString(string, false);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void loadPalettes(){
		try{
			palettes = json.fromJson(ObjectMap.class, paletteFile);
			
			String id = core.prefs.getString("lastpalette");
			if(id != null){
				currentPalette = palettes.get(id);
			}

			Novix.log("Palettes loaded.");
		}catch(Exception e){
			e.printStackTrace();
			Novix.log("Palette file nonexistant or corrupt.");
		}

		if(currentPalette == null){
			currentPalette = new Palette("Untitled", generatePaletteID(), 8, true);
			palettes.put(currentPalette.id, currentPalette);
			core.prefs.put("lastpalette", currentPalette.id);
		}
	}

	public String generatePaletteID(){
		long id = MathUtils.random(Long.MAX_VALUE - 1);
		return id + "";
	}
}
