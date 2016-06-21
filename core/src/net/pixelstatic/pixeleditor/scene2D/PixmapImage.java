package net.pixelstatic.pixeleditor.scene2D;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PixmapImage extends Actor{
	public final Pixmap pixmap;
	private Texture texture;
	
	public PixmapImage(Pixmap pixmap){
		this.pixmap = pixmap;
		texture = new Texture(pixmap);
	}
	
	public void draw(Batch batch, float alpha){
		batch.setColor(1,1,1,alpha);
		batch.draw(texture, getX(), getY(), getWidth(), getHeight());
	}
	
	public void updateTexture(){
		texture.draw(pixmap, 0, 0);
	}
	
}
