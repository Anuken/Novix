package io.anuke.novix.internal;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import io.anuke.novix.Vars;

public class Project implements Disposable, Comparable<Project>{
	public String id;
	public String name;
	public int layers = 1;
	public long lastloadtime;
	
	public transient Texture[] cachedTextures;
	
	public Project(String name, int layers, String id){
		this.name = name;
		this.id = id;
	}
	
	private Project(){}
	
	public void reloadTextures(){
		if(cachedTextures != null)
			dispose();
		
		cachedTextures = new Texture[layers];
		
		for(int i = 0; i < layers; i ++){
			cachedTextures[i] = new Texture(getFiles()[i]);
		}
	}
	
	public FileHandle[] getFiles(){
		FileHandle[] files = new FileHandle[layers];
		
		for(int i = 0; i < layers; i ++){
			files[i] = Vars.projectDirectory.child(id +"-"+i+ ".png");
		}
		
		return files;
	}

	public FileHandle[] getBackupFiles(){
		FileHandle[] files = new FileHandle[layers];
		
		for(int i = 0; i < layers; i ++){
			files[i] = Vars.projectDirectory.child(id +"-"+i+ "-backup.png");
		}
		
		return files;
	}
	
	@Override
	public int compareTo(Project other){
		if(other.lastloadtime == lastloadtime) return 0;
		return other.lastloadtime > lastloadtime ? 1 : -1;
	}
	
	@Override
	public void dispose(){
		for(Texture texture : cachedTextures)
			texture.dispose();
	}
}
