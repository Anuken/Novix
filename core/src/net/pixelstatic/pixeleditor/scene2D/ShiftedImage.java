package net.pixelstatic.pixeleditor.scene2D;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ShiftedImage extends Image{
	int offsetx, offsety;
	private Texture texture;
	
	public ShiftedImage(Texture tex){
		super(tex);
		texture = tex;
	}
	
	public void draw(Batch batch, float alpha){
		float pscalex = (float)getWidth() / texture.getWidth();
		float pscaley = (float)getHeight() / texture.getHeight();
		
		setPosition(getX() + offsetx*pscalex, getY() + offsety*pscaley);
		super.draw(batch, alpha);
		
	}
}
