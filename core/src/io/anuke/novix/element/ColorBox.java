package io.anuke.novix.element;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.scene.ui.ImageButton;

public class ColorBox extends ImageButton{
	
	public ColorBox(Color color){
		super("white", "toggle");
		getImage().setColor(color);
		getImageCell().grow().pad(-4);
	}
	
	public ColorBox(Color color, boolean toggle){
		super("white", !toggle ? "default" : "toggle");
		getImage().setColor(color);
		getImageCell().grow().pad(-4);
	}
	
	public Color getImageColor(){
		return getImage().getColor();
	}
	
	public void setImageColor(Color color){
		getImage().setColor(color);
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
	}

	
}
