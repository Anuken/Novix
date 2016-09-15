package io.anuke.pixeleditor.managers;

import io.anuke.pixeleditor.modules.Core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PrefsManager{
	private Core core;
	//private ObjectMap<String, Object> map = new ObjectMap<String, Object>();
	private Preferences prefs;
	
	public PrefsManager(Core core){
		this.core = core;
		prefs = Gdx.app.getPreferences("pixeleditor");
	}
	
	public void put(String name, boolean value){
		prefs.putBoolean(name, value);
		//map.put(name, value);
		
		if(name.equals("grid"))
		core.toolmenu.getGridButton().setChecked(value);
	}
	
	public void put(String name, String value){
		prefs.putString(name, value);
		//map.put(name, value);
	}
	
	public void put(String name, float value){
		prefs.putFloat(name, value);
		//map.put(name, value);
	}
	
	public void put(String name, int value){
		prefs.putInteger(name, value);
		//map.put(name, value);
	}
	
	public void put(String name, long value){
		prefs.putLong(name, value);
		//map.put(name, value);
	}
	
	public boolean getBoolean(String name){
		return getBoolean(name, false);
	}
	
	public boolean getBoolean(String name, boolean val){
		return prefs.getBoolean(name, val);
	}
	
	public float getFloat(String name){
		return getFloat(name, 0);
	}
	
	public float getFloat(String name, float def){
		return prefs.getFloat(name, def);
	}
	
	public int getInteger(String name){
		return getInteger(name, 0);
	}
	
	public int getInteger(String name, int i){
		return prefs.getInteger(name, i);
	}
	
	public String getString(String name){
		return getString(name, null);
	}
	
	public String getString(String name, String def){
		return prefs.getString(name, def);
	}
	
	public long getLong(String name){
		return prefs.getLong(name);
	}
	
	public void save(){
		prefs.flush();
	}
}
