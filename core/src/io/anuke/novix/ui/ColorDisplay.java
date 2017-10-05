package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import io.anuke.novix.element.ColorBox;
import io.anuke.novix.internal.NovixEvent.ColorChange;
import io.anuke.ucore.core.Events;
import io.anuke.ucore.scene.ui.ButtonGroup;
import io.anuke.ucore.scene.ui.layout.Table;

public class ColorDisplay extends Table{
	private int selected;
	private ColorBox[] boxes;
	
	public ColorDisplay(){
		Events.on(ColorChange.class, color->{
			boxes[selected].setImageColor(color);
		});
	}
	
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
		selected = 0;
		
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
			ColorBox box = new ColorBox(color);
			boxes[i] = box;
			group.add(box);
			
			add(box).size(colorsize);
			
			box.clicked(()->{
				selected = index;
				Events.fire(ColorChange.class, color);
			});
			
			if(i == selected){
				Events.fire(ColorChange.class, color);
			}

			if(perow != 0 && i % perow == perow - 1){
				add().growX();
				row();
				add().growX();
			}
			
		}

		if(perow == 0)
			add().growX();
	}
	
}
