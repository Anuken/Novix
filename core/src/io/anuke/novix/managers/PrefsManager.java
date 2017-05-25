package io.anuke.novix.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.ObjectMap;

public class PrefsManager{
	private Preferences prefs;
	private ObjectMap<String, Object> defaults = new ObjectMap();
	
	public PrefsManager(){
		prefs = Gdx.app.getPreferences("io.anuke.novix");
		
		registerDefaults();
	}
	
	private void registerDefaults(){
		defaults.put("grid", true);
		defaults.put("gestures", true);
		defaults.put("cursormode", true);
		defaults.put("cursorsize", 1f);
		defaults.put("cursorspeed", 1f);
		
		defaults.put("hsymmetry", false);
		defaults.put("vsymmetry", false);
		
		defaults.put("tutorial", false);
		
		defaults.put("brushsize", 1);
		defaults.put("opacity", 1f);
		defaults.put("alpha", 1f);
		
		defaults.put("palettecolor", 0);
		defaults.put("genpalettes", true);
		
		defaults.put("lock", false);
		
		defaults.put("lastpalette", null);
		defaults.put("lastproject", -1L);
	}
	
	private Object get(String name){
		if(!defaults.containsKey(name)) throw new RuntimeException("Setting \""+name+"\" does not exist.");
		return defaults.get(name);
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
		return prefs.getBoolean(name, (Boolean)get(name));
	}
	
	public float getFloat(String name){
		return prefs.getFloat(name, (Float)get(name));
	}
	
	public int getInteger(String name){
		return prefs.getInteger(name, (Integer)get(name));
	}
	
	public String getString(String name){
		return prefs.getString(name, (String)get(name));
	}
	public long getLong(String name){
		return prefs.getLong(name, (Long)get(name));
	}
	
	public void save(){
		prefs.flush();
	}
}
