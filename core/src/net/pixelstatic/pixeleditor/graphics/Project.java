package net.pixelstatic.pixeleditor.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class Project{
	public String name;
	public Texture cachedTexture;
	public final FileHandle file;
	
	public Project(FileHandle file, String name){
		this.name = name;
		this.file = file;
	}
}
