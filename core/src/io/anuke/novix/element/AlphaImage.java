package io.anuke.novix.element;

import com.badlogic.gdx.graphics.g2d.Batch;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.style.TiledDrawable;

public class AlphaImage extends Element{
	private float imageWidth, imageHeight;
	private TiledDrawable tile;
	
	public AlphaImage(float w, float h){
		this.imageWidth = w;
		this.imageHeight = h;
	}
	
	@Override
	public void draw(Batch batch, float alpha){
		if(tile == null){
			tile = new TiledDrawable(Draw.region("alpha"));
			tile.setTileSize(getWidth()/imageWidth, getHeight()/imageHeight);
		}
		
		Draw.alpha(alpha);
		
		tile.draw(batch, x, y, width, height);
		
		Draw.color();
	}
	
	public void setTileSize(float size){
		tile.setTileSize(size, size);
	}
	
	public void setImageSize(int w, int h){
		this.imageWidth = w;
		this.imageHeight = h;
	}
}
