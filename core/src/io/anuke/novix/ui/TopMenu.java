package io.anuke.novix.ui;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.scene.ui.layout.Table;

public class TopMenu extends Table{
	FlipButton flip;
	ColorDisplay display;
	boolean open = true;
	
	public TopMenu(){
		setFillParent(true);
		setup();
	}
	
	public boolean open(){
		return open;
	}
	
	public void updateDisplay(Color[] colors){
		display.update(colors);
	}
	
	private void setup(){
		display = new ColorDisplay();
		top();
		
		flip = new FlipButton(false);
		add(flip).growX().height(60);
		row();
		add(display);
	}
}
