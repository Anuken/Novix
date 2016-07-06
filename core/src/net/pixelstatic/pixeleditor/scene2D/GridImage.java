package net.pixelstatic.pixeleditor.scene2D;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.VisUI;

public class GridImage extends Actor{
	private int imageWidth, imageHeight;
	
	public GridImage(int w, int h){
		this.imageWidth = w;
		this.imageHeight = h;
	}

	public void draw(Batch batch, float alpha){
		TextureRegion blank = VisUI.getSkin().getAtlas().findRegion("white");
		
		float xspace = (getWidth() / imageWidth);
		float yspace = (getHeight() / imageHeight);
		float s = 1f;
		
		for(int x = 0; x <= imageWidth; x ++){
			batch.draw(blank, (int)(getX() + xspace * x - s), getY() - s, 2, getHeight()+ (x == imageWidth ? 1: 0));
		}
		
		for(int y = 0; y <= imageHeight; y ++){
			batch.draw(blank, getX() - s, (int)(getY() + y * yspace - s), getWidth(), 2);
		}
	}
	
	public void setImageSize(int w, int h){
		this.imageWidth = w;
		this.imageHeight = h;
	}
}
