package net.pixelstatic.pixeleditor.managers;

import net.pixelstatic.pixeleditor.graphics.Palette;
import net.pixelstatic.pixeleditor.modules.Core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class PaletteManager{
	private Core main;
	private Json json = new Json();
	private Palette currentPalette;
	private ObjectMap<String, Palette> palettes = new ObjectMap<String, Palette>();
	
	public PaletteManager(Core main){
		this.main = main;
	}
	
	public Palette getCurrentPalette(){
		return currentPalette;
	}
	
	public void setCurrentPalette(Palette palette){
		currentPalette = palette;
	}
	
	public Iterable<Palette> getPalettes(){
		return palettes.values();
	}
	
	public void removePalette(Palette palette){
		palettes.remove(palette.name);
	}
	
	public void addPalette(Palette palette){
		palettes.put(palette.name, palette);
	}
	
	public void savePalettes(){
		String string = json.toJson(palettes);
		main.paletteDirectory.writeString(string, false);
	}

	@SuppressWarnings("unchecked")
	public void loadPalettes(){
		try{
			palettes = json.fromJson(ObjectMap.class, main.paletteDirectory);

			String name = main.prefs.getString("lastpalette");
			if(name != null){
				currentPalette = palettes.get(name);
			}

			Gdx.app.log("pedebugging", "Palettes loaded.");
		}catch(Exception e){
			e.printStackTrace();
			Gdx.app.error("pedebugging", "Palette file nonexistant or corrupt.");
		}

		if(currentPalette == null){
			if( !palettes.containsKey("Untitled")){
				currentPalette = new Palette("Untitled", 8);
				palettes.put("Untitled", currentPalette);
			}else{
				currentPalette = palettes.get("Untitled");
			}
		}
	}
}
