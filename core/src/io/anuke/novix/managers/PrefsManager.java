package io.anuke.novix.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import io.anuke.novix.modules.Core;

public class PrefsManager{
	private Core core;
	private Preferences prefs;
	
	public PrefsManager(Core core){
		this.core = core;
		prefs = Gdx.app.getPreferences("pixeleditor");
	}
	
	public void put(String name, boolean value){
		prefs.putBoolean(name, value);
	}
	
	public void put(String name, String value){
		prefs.putString(name, value);
	}
	
	public void put(String name, float value){
		prefs.putFloat(name, value);
	}
	
	public void put(String name, int value){
		prefs.putInteger(name, value);
	}
	
	public void put(String name, long value){
		prefs.putLong(name, value);
	}
	
	public boolean getBoolean(String name){
		return prefs.getBoolean(name, false);
	}
	
	public float getFloat(String name){
		return prefs.getFloat(name, 0);
	}
	
	public int getInteger(String name){
		return prefs.getInteger(name, 0);
	}
	
	public String getString(String name){
		return prefs.getString(name, null);
	}
	public long getLong(String name){
		return prefs.getLong(name);
	}
	
	public void save(){
		prefs.flush();
	}
}
