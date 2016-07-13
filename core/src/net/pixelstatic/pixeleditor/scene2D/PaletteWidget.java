package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.graphics.Palette;
import net.pixelstatic.utils.graphics.Hue;
import net.pixelstatic.utils.graphics.Textures;
import net.pixelstatic.utils.scene2D.ColorBox;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class PaletteWidget extends Button{
	public final Palette palette;

	public PaletteWidget(Palette palette, boolean selected){
		super(VisUI.getSkin());
		this.palette = palette;
		setup(selected);
	}

	private void setup(boolean selected){
		float maxsize = 25;

		setColor(Hue.lightness(0.87f));

		top().left();

		VisLabel label = new VisLabel(palette.name);
		label.setColor(Color.LIGHT_GRAY);

		add(label).align(Align.topLeft);
		
		VisImageButton extrabutton = new VisImageButton(Textures.getDrawable("icon-rename"));
		extrabutton.getImageCell().size(40);
		
		add(extrabutton).size(42);
		
		
		row();

		top().left().add(generatePaletteTable(maxsize, getPrefWidth(), palette.colors)).grow().colspan(2);
		
		

		if(selected){
			Image image = new Image(getSkin().getDrawable("border"));

			image.setFillParent(true);
			addActor(image);
		}
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
		return 80;
	}

}
