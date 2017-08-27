package io.anuke.novix.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.event.InputEvent;
import io.anuke.ucore.scene.event.InputListener;
import io.anuke.ucore.scene.utils.ChangeListener;
import io.anuke.ucore.util.Mathf;

public class ColorBar extends Element{
	private Texture texture;
	private Color from = new Color();
	private Color to = new Color();
	private float selection = 0.5f;
	private AlphaImage image;
	
	public ColorBar(Texture texture){
		this.texture = texture;
		
		image = new AlphaImage();
		
		addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {return true;}
			
			public void touchDragged (InputEvent event, float x, float y, int pointer) {
				selection = x/width;
				selection = Mathf.clamp(selection);
				ColorBar.this.fire(new ChangeListener.ChangeEvent());
			}
		});
	}
	
	public ColorBar(Color from, Color to){
		this(null);
		this.from = from;
		this.to = to;
	}
	
	public void setColors(Color from, Color to){
		this.from = from;
		this.to = to;
	}
	
	public float getValue(){
		return selection;
	}
	
	public void setValue(float value){
		selection = value;
	}
	
	@Override
	public void draw(){
		Draw.alpha(alpha);
		
		patch("button", -6);
		
		image.setBounds(x, y, width, height);
		image.setTileUV(width/10, height/10);
		image.draw(DrawContext.batch, alpha);
		
		if(texture == null){
			Draw.gradient(from, to, alpha, x, y, width, height);
		}else{
			Draw.tint(getColor());
			Draw.batch().draw(texture, x, y, width, height);
		}
		
		float nw = 20;
		float nh = 62;
		Draw.tint(Color.WHITE);
		Draw.patch("slider-knob", x-nw/2+selection*width, y-(nh-height)/2f, nw, nh);
		Draw.color();
		
	}
}
