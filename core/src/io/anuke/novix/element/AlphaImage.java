package io.anuke.novix.element;

import com.badlogic.gdx.graphics.g2d.Batch;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.scene.Element;

public class AlphaImage extends Element{
	private float imageWidth, imageHeight;
	
	public AlphaImage(float w, float h){
		this.imageWidth = w;
		this.imageHeight = h;
	}
	
	@Override
	public void draw(Batch batch, float alpha){
		Draw.alpha(alpha);
		
		Draw.region("alpha").setU2(imageWidth);
		Draw.region("alpha").setV2(imageHeight);
		Draw.crect("alpha", getX(), getY(), getWidth(), getHeight());
		
		Draw.color();
	}
	
	public void setImageSize(int w, int h){
		this.imageWidth = w;
		this.imageHeight = h;
	}
}
