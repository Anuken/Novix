package io.anuke.novix.ui;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.scene.ui.layout.Table;

public class TopMenu extends Table{
	FlipButton flip;
	ColorDisplay display;
	TopSlider slider;
	
	public TopMenu(){
		setFillParent(true);
		setup();
	}
	
	public boolean open(){
		return !flip.flipped();
	}
	
	public void updateDisplay(Color[] colors){
		display.update(colors);
	}
	
	private void setup(){
		display = new ColorDisplay();
		slider = new TopSlider();
		DrawContext.scene.add(slider);
		top();
		
		flip = new FlipButton(false);
		flip.clicked(()->{
			slider.slide(!flip.flipped());
		});
		
		add(flip).growX().height(60);
		row();
		add(display);
		
		slider.padTop(flip.getHeight());
	}
}
