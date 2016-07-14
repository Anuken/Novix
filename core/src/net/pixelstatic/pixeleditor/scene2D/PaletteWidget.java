package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.graphics.Palette;
import net.pixelstatic.utils.graphics.Hue;
import net.pixelstatic.utils.graphics.Textures;
import net.pixelstatic.utils.scene2D.ColorBox;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;

public class PaletteWidget extends VisTable{
	public final Palette palette;
	public final boolean selected;
	public VisImageButton extrabutton;

	public PaletteWidget(Palette palette, boolean selected){
		//super(VisUI.getSkin());
		this.palette = palette;
		this.selected = selected;
		setup();
	}
	
	public void draw(Batch batch, float parentAlpha){
		super.draw(batch, parentAlpha);
		if(selected){
			batch.setColor(1,1,1,parentAlpha);
			getSkin().getDrawable("border").draw(batch, getX(), getY(), getWidth(), getHeight());
		}
	}

	private void setup(){
		float maxsize = 25;
		background("button");
		setColor(Hue.lightness(0.87f));

		top().left();

		VisLabel label = new VisLabel(palette.name);
		label.setColor(Color.LIGHT_GRAY);

		add(label).align(Align.left);
		
		extrabutton = new VisImageButton(Textures.getDrawable("icon-dots"));
		extrabutton.setStyle(new VisImageButtonStyle(extrabutton.getStyle()));
		extrabutton.getStyle().down = extrabutton.getStyle().up;
		extrabutton.getStyle().over = extrabutton.getStyle().up;
		extrabutton.getStyle().imageDown = Textures.getDrawable("icon-dots-down");
		extrabutton.setColor(getColor());
		extrabutton.getImageCell().size(40);
		
		extrabutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				
			}
		});
		
		add(extrabutton).size(42).align(Align.topRight).padRight(0);
		
		
		row();

		top().left().add(generatePaletteTable(maxsize, getPrefWidth(), palette.colors)).grow().colspan(2);
	}
	
	public void addExtraButtonListener(EventListener listener){
		extrabutton.addListener(listener);
	}

	public static Table generatePaletteTable(float size, float width, Color[] colors){
		VisTable table = new VisTable();

		ColorBox[] boxes = new ColorBox[colors.length];
		for(int i = 0;i < boxes.length;i ++)
			boxes[i] = new ColorBox(colors[i]);

		float rowsize = width / boxes.length;

		int perow = (int)(width / size);

		table.top().left();

		if(rowsize < size){ // this means another row is needed
			for(int i = 0;i < boxes.length;i ++){
				table.add(boxes[i]).size(size);
				if(i == perow - 1) table.row();
			}
		}else{ //otherwise, put it in one row
			for(int i = 0;i < boxes.length;i ++){
				table.add(boxes[i]).size(size);
			}

			//add blank cells
			for(int i = 0;i < perow - boxes.length;i ++){
				table.add().size(size);
			}
		}
		return table;
	}

	@Override
	public float getPrefWidth(){
		return 220;
	}

	@Override
	public float getPrefHeight(){
		return 105;
	}

}
