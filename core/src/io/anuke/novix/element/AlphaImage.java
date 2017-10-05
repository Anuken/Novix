package io.anuke.novix.element;

import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;

import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.scene.Element;

public class AlphaImage extends Element{
	//private float imageWidth, imageHeight;
	private float u, v;
	
	public AlphaImage(){
		
		Core.skin.getAtlas().getRegion("alpha").getTexture().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
	}
	
	public AlphaImage(float u, float v){
		this();
		this.u = u;
		this.v = v;
	}
	
	@Override
	public void draw(Batch batch, float alpha){
		
		Draw.alpha(alpha);
		
		Draw.batch().draw(Draw.region("alpha").getTexture(), 
				x, y, width, height, 0, 0, u, v);
		
		Draw.color();
	}
	
	public void setTileUV(float u, float v){
		this.u = u;
		this.v = v;
	}
}
