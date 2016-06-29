package net.pixelstatic.pixeleditor.graphics;

import com.badlogic.gdx.files.FileHandle;

public class Project{
	public String name;
	public final FileHandle file;
	
	public Project(FileHandle file, String name){
		this.name = name;
		this.file = file;
	}
}
