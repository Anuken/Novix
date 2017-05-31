package io.anuke.novix.element;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.scene.ui.ImageButton;

public class ColorBox extends ImageButton{
	
	public ColorBox(Color color){
		super("white", "toggle");
		getImage().setColor(color);
		getImageCell().grow().pad(-4);
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
	}
}
