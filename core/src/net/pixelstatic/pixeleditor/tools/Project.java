package net.pixelstatic.pixeleditor.tools;

import net.pixelstatic.utils.MiscUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class Project implements Disposable{
	public String name;
	public Texture cachedTexture;
	private Pixmap cachedPixmap;
	public final FileHandle file;
	
	public Project(FileHandle file){
		this.file = file;
		name = file.nameWithoutExtension();
		Gdx.app.log("pedebugging", "Creating new project: \"" + name + "\"");
		reloadTexture();
	}
	
	public Pixmap getCachedPixmap(){
		if(cachedPixmap == null || (Boolean)MiscUtils.getPrivate(cachedPixmap, "disposed")){
			reloadTexture();
		}
		
		return cachedPixmap;
	}
	
	public void reloadTexture(){
		Gdx.app.log("pedebugging", "Project \"" + name + "\": reloading texture. ");
		
		if(cachedTexture != null) cachedTexture.dispose();
		cachedTexture = new Texture(file);
		cachedTexture.getTextureData().prepare();
		cachedPixmap = cachedTexture.getTextureData().consumePixmap();
	}
	
	public void dispose(){
		cachedTexture.dispose();
		cachedPixmap.dispose();
	}
}
