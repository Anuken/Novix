package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import io.anuke.novix.element.ColorBox;
import io.anuke.ucore.scene.ui.ButtonGroup;
import io.anuke.ucore.scene.ui.layout.Table;

public class ColorDisplay extends Table{
	
	public void update(Color[] colors){
		clear();

		int maxcolorsize = 65;
		int mincolorsize = 30;

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
			ColorBox box = new ColorBox(colors[i]);
			group.add(box);
			
			add(box).size(colorsize);
			
			box.clicked(()->{
				
			});

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
