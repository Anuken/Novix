package io.anuke.pixeleditor.tools;

import io.anuke.pixeleditor.modules.Core;
import io.anuke.utils.MiscUtils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class Project implements Disposable, Comparable<Project>{
	public String name;
	public long lastloadtime;
	public long id;
	public transient Texture cachedTexture;
	private transient Pixmap cachedPixmap;
	
	public Project(String name, long id){
		this.name = name;
		this.id = id;
		reloadTexture();
	}
	
	public Project(){}
	
	public Pixmap getCachedPixmap(){
		if(cachedPixmap == null || (Boolean)MiscUtils.getPrivate(cachedPixmap, "disposed")){
			reloadTexture();
		}
		
		return cachedPixmap;
	}
	
	public void reloadTexture(){
		//Gdx.app.log("pedebugging", "Project \"" + name + "\": reloading texture. ");
		
		if(cachedTexture != null) cachedTexture.dispose();
		cachedTexture = new Texture(getFile());
		cachedTexture.getTextureData().prepare();
		cachedPixmap = cachedTexture.getTextureData().consumePixmap();
	}
	
	public FileHandle getFile(){
		return Core.i.projectmanager.getFile(id);
	}
	
	public void dispose(){
		cachedTexture.dispose();
		cachedPixmap.dispose();
	}
	
	
	public int compareTo(Project other){
		if(other.lastloadtime == lastloadtime) return 0;
		return other.lastloadtime > lastloadtime ? 1 : -1;
	}
}
