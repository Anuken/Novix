package net.pixelstatic.pixeleditor.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Project{
	public final Texture texture;
	public final Pixmap pixmap;
	public final String name;
	public final FileHandle file;
	
	public Project(FileHandle file, String name){
		this.name = name;
		this.file = file;
		texture = new Texture(file);
		texture.getTextureData().prepare();
		pixmap = texture.getTextureData().consumePixmap();
	}
	
	public int getWidth(){
		return texture.getWidth();
	}
	
	public int getHeight(){
		return texture.getHeight();
	}
}
