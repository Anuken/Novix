package io.anuke.novix.ui;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novix.Vars;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.scene.ui.layout.Table;

public class TopMenu extends Table{
	private FlipButton flip;
	private ColorDisplay display;
	private TopSlider slider;
	
	public TopMenu(){
		setFillParent(true);
		setup();
	}
	
	public boolean open(){
		return flip.flipped();
	}
	
	public void updateDisplay(Color[] colors){
		display.update(colors);
	}
	
	public void toggle(){
		flip.flip();
		slider.slide(!flip.flipped());
	}
	
	public int getSelectedColorIndex(){
		return display.getSelected();
	}
	
	public Color getSelectedColor(){
		return display.getSelectedColor();
	}
	
	public void setSelectedColor(Color color){
		display.setColor(display.getSelected(), color);
	}
	
	private void setup(){
		display = new ColorDisplay();
		slider = new TopSlider();
		Core.scene.add(slider);
		top();
		
		slider.setVisible(()-> isVisible());
		
		flip = new FlipButton(false);
		
		flip.clicked(()->{
			Vars.ui.showToolMenu();
			/*
			slider.slide(!flip.flipped());
			
			if(Vars.ui.bottom().open()){
				Vars.ui.bottom().toggle();
			}
			*/
		});
		
		addIButton("icon-menu", 34, ()->{
			Vars.ui.showProjectMenu();
		}).size(60);
		add(flip).growX().height(60);
		addIButton("icon-palette", 42, ()->{
			Vars.ui.showPaletteMenu();
		}).size(60);
		row();
		add(display).colspan(3);
		
		slider.padTop(60);
	}
	
	public TopSlider slider(){
		return slider;
	}
}
