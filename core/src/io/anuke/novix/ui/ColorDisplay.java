package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import io.anuke.novix.element.ColorBox;
import io.anuke.novix.internal.NovixEvent.ColorChange;
import io.anuke.novix.internal.NovixEvent.ColorPick;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Events;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.scene.ui.ButtonGroup;
import io.anuke.ucore.scene.ui.layout.Table;

public class ColorDisplay extends Table{
	private int selected;
	private ColorBox[] boxes;
	private ButtonGroup<ColorBox> group;
	
	public ColorDisplay(){
		Events.on(ColorChange.class, color->{
			boxes[selected].setImageColor(color);
		});
		
		Events.on(ColorPick.class, color->{
			color.a = 1f;
			int index = -1;
			
			for(int i = 0; i < boxes.length; i ++){
				if(Hue.approximate(color, boxes[i].getImageColor(), 0.005f)){
					index = i;
					break;
				}
			}
			
			if(index == -1){
				Events.fire(ColorChange.class, color);
			}else{
				selected = index;
				boxes[selected].setChecked(true);
				Events.fire(ColorChange.class, boxes[selected].getImageColor());
			}
		});
	}
	
	@Override
	public void draw(Batch batch, float alpha){
		super.draw(batch, alpha);
		
		ColorBox box = boxes[selected];
		
		Draw.color("title");
		float margin = 6;
		Draw.patch("box-select", getX() + box.getX() - margin, getY() + box.getY() - margin, 
				box.getWidth() + margin*2, box.getHeight() + margin*2);
		Draw.color();
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
		
		int maxcolorsize = 54;
		int mincolorsize = 40;

		int colorsize = Gdx.graphics.getWidth() / colors.length;

		int perow = 0; // colors per row

		colorsize = Math.min(maxcolorsize, colorsize);

		if(colorsize < mincolorsize){
			colorsize = mincolorsize;
			perow = Gdx.graphics.getWidth() / colorsize;
		}

		add().growX();
		
		group = new ButtonGroup<>();

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
