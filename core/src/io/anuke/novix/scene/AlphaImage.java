package io.anuke.novix.scene;




import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.anuke.ucore.graphics.Textures;

public class AlphaImage extends Actor{
	private float imageWidth, imageHeight;
	
	public AlphaImage(float w, float h){
		this.imageWidth = w;
		this.imageHeight = h;
	}

	public void draw(Batch batch, float alpha){
		batch.setColor(1, 1, 1, alpha);
		
		batch.draw(Textures.get("alpha"), getX(), getY(), getWidth(), getHeight(), 0, 0, imageWidth, imageHeight);
	}
	
	public void setImageSize(int w, int h){
		this.imageWidth = w;
		this.imageHeight = h;
	}
}
