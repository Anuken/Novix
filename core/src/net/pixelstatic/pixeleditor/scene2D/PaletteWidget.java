package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.graphics.Palette;
import net.pixelstatic.utils.graphics.Hue;
import net.pixelstatic.utils.scene2D.ColorBox;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class PaletteWidget extends VisTable{
	private final Palette palette;
	
	public PaletteWidget(Palette palette){
		this.palette = palette;
		setup();
	}
	
	private void setup(){
		float maxsize = 25;
		
		ColorBox[] boxes = new ColorBox[palette.size()];
		for(int i = 0; i < boxes.length; i ++) boxes[i] = new ColorBox(palette.colors[i]);
		
		float rowsize = getPrefWidth() / boxes.length;
		
		int perow = (int)(getPrefWidth()/maxsize);
		
		background("button");
		setColor(Hue.lightness(0.87f));
		
		top().left();
		
		VisLabel label = new VisLabel(palette.name);
		label.setColor(Color.LIGHT_GRAY);
		add(label);
		row();
		
		if(rowsize < maxsize){ // this means another row is needed
			int extra = boxes.length - perow;
			for(int i = 0; i < boxes.length; i ++){
				add(boxes[i]).size(maxsize);
				if(i == perow-1) row();
			}
		}else{ //otherwise, put it in one row
			for(int i = 0; i < boxes.length; i ++){
				add(boxes[i]).size(maxsize);
			}
			for(int i = 0; i < perow - boxes.length; i ++){
				add().size(maxsize);
			}
		}
		
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
