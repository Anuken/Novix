package io.anuke.novix.ui;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novix.Vars;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.scene.ui.layout.Table;

public class TopMenu extends Table{
	private ColorDisplay display;
	private TopSlider slider;
	
	public TopMenu(){
		setFillParent(true);
		setup();
	}
	
	public void updateDisplay(Color[] colors){
		display.update(colors);
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
		
		addIButton("icon-menu", 34, ()->{
			Vars.ui.showProjectMenu();
		}).size(60);
		addIButton("icon-down", 48, ()->{
			Vars.ui.showToolMenu();
		}).growX().height(60);
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
