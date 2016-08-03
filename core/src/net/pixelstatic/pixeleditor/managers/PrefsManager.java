package net.pixelstatic.pixeleditor.managers;

import net.pixelstatic.pixeleditor.modules.Core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PrefsManager{
	private Core core;
	private Preferences prefs;
	
	public PrefsManager(Core core){
		this.core = core;
		prefs = Gdx.app.getPreferences("pixeleditor");
	}
	
	public void put(String name, boolean bool){
		prefs.putBoolean(name, bool);
		
		if(name.equals("grid"))
		core.gridbutton.setChecked(bool);
	}
	
	public void put(String name, String value){
		prefs.putString(name, value);
	}
	
	public void put(String name, float val){
		prefs.putFloat(name, val);
	}
	
	public void put(String name, int i){
		prefs.putInteger(name, i);
	}
	
	public boolean getBoolean(String name){
		return prefs.getBoolean(name);
	}
	
	public boolean getBoolean(String name, boolean val){
		return prefs.getBoolean(name, val);
	}
	
	public float getFloat(String name){
		return prefs.getFloat(name);
	}
	
	public float getFloat(String name, float def){
		return prefs.getFloat(name, def);
	}
	
	public int getInteger(String name){
		return prefs.getInteger(name);
	}
	
	public int getInteger(String name, int i){
		return prefs.getInteger(name, i);
	}
	
	public String getString(String name){
		return prefs.getString(name);
	}
	
	public String getString(String name, String def){
		return prefs.getString(name, def);
	}
	
	public void save(){
		prefs.flush();
	}
}
