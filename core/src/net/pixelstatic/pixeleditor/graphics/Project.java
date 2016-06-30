package net.pixelstatic.pixeleditor.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Project{
	public String name;
	public Texture cachedTexture;
	private Pixmap cachedPixmap;
	public final FileHandle file;
	
	public Project(FileHandle file, String name){
		this.name = name;
		this.file = file;
		reloadTexture();
	}
	
	public Pixmap getCachedPixmap(){
		return cachedPixmap;
	}
	
	public void reloadTexture(){
		if(cachedTexture != null)cachedTexture.dispose();
		cachedTexture = new Texture(file);
		cachedTexture.getTextureData().prepare();
		cachedPixmap = cachedTexture.getTextureData().consumePixmap();
	}
}
