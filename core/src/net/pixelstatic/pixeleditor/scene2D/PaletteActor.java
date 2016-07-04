package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.graphics.Palette;
import net.pixelstatic.utils.scene2D.ColorBox;

import com.kotcrab.vis.ui.widget.VisTable;

public class PaletteActor extends VisTable{
	private final Palette palette;
	
	public PaletteActor(Palette palette){
		this.palette = palette;
		setup();
	}
	
	private void setup(){
		float maxsize = 25;
		
		ColorBox[] boxes = new ColorBox[palette.size()];
	}
	
	@Override
	public float getPrefWidth(){
		return 200;
	}
	
	@Override
	public float getPrefHeight(){
		return 80;
	}
	
}
