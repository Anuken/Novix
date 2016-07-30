package net.pixelstatic.pixeleditor.scene2D;



import net.pixelstatic.gdxutils.graphics.Textures;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AlphaImage extends Actor{
	private int imageWidth, imageHeight;
	
	public AlphaImage(int w, int h){
		this.imageWidth = w;
		this.imageHeight = h;
	}

	public void draw(Batch batch, float alpha){
		batch.setColor(1, 1, 1, alpha);
		
		batch.draw(Textures.get("alpha"), getX(), getY(), getWidth(), getHeight(), 0, 0, imageWidth/4, imageHeight/4);
	}
	
	public void setImageSize(int w, int h){
		this.imageWidth = w;
		this.imageHeight = h;
	}
}
