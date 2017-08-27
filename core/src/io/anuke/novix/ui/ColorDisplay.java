package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import io.anuke.novix.Vars;
import io.anuke.novix.element.ColorBox;
import io.anuke.ucore.scene.ui.ButtonGroup;
import io.anuke.ucore.scene.ui.layout.Table;

public class ColorDisplay extends Table{
	private int selected;
	private ColorBox[] boxes;
	
	public void setColor(int index, Color color){
		boxes[index].setImageColor(color);
	}
	
	public int getSelected(){
		return selected;
	}
	
	public Color getSelectedColor(){
		return boxes[selected].getImageColor();
	}
	
	public void update(Color[] colors){
		boxes = new ColorBox[colors.length];
		
		clear();

		int maxcolorsize = 57;
		int mincolorsize = 40;

		int colorsize = Gdx.graphics.getWidth() / colors.length;

		int perow = 0; // colors per row

		colorsize = Math.min(maxcolorsize, colorsize);

		if(colorsize < mincolorsize){
			colorsize = mincolorsize;
			perow = Gdx.graphics.getWidth() / colorsize;
		}

		add().growX();
		
		ButtonGroup<ColorBox> group = new ButtonGroup<>();

		for(int i = 0; i < colors.length; i++){
			int index = i;
			
			Color color = colors[i];
			ColorBox box = new ColorBox(colors[i]);
			group.add(box);
			
			add(box).size(colorsize);
			
			box.clicked(()->{
				Vars.drawing.getLayer().setColor(color.cpy());
				Vars.ui.top().slider().updateColor(color);
				selected = index;
			});
			
			if(i == 0){
				Vars.drawing.getLayer().setColor(color.cpy());
				Vars.ui.top().slider().updateColor(color);
			}

			if(perow != 0 && i % perow == perow - 1){
				add().growX();
				row();
				add().growX();
			}
			
			boxes[i] = box;
		}

		if(perow == 0)
			add().growX();
	}
	
}
