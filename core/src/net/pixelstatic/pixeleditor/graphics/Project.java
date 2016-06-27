package net.pixelstatic.pixeleditor.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class Project{
	public Texture texture;
	public final String name;
	public final FileHandle file;
	
	public Project(FileHandle file, String name){
		this.name = name;
		this.file = file;
		texture = new Texture(file);
	}
	
	public int getWidth(){
		return texture.getWidth();
	}
	
	public int getHeight(){
		return texture.getHeight();
	}
}
