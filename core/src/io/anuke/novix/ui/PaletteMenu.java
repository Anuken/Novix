package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import io.anuke.novix.Vars;
import io.anuke.novix.dialogs.PaletteDialogs;
import io.anuke.novix.element.ColorBox;
import io.anuke.novix.element.FloatingMenu;
import io.anuke.novix.internal.Palette;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.scene.ui.ScrollPane;
import io.anuke.ucore.scene.ui.layout.Table;

public class PaletteMenu extends FloatingMenu{
	Table mainTable = new Table();
	float maxWidth = 400;
	
	public PaletteMenu(){
		super("Palettes");
		
		ScrollPane pane = new ScrollPane(mainTable);
		
		content.add(pane);
	}
	
	void rebuild(){
		mainTable.clearChildren();
		
		Array<Palette> list = Vars.control.palettes().getPalettes();
		
		for(Palette palette : list){
			
			mainTable.add(new PaletteTable(palette)).width(maxWidth);
			
			mainTable.row();
		}
		
		mainTable.addCenteredImageTextButton("New Palette", "icon-plus", 30, ()->{
			
		}).growX().height(46).padTop(8);
	}
	
	public void show(){
		super.show();
		rebuild();
	}
	
	class PaletteTable extends Table{
		
		public PaletteTable(Palette palette){
			background("button");
			
			pad(14);
			
			Table title = new Table();
			
			title.add(palette.name);
			
			title.add().growX();
			
			title.addIButton("dots", "icon-dots", 40, ()->{
				Table c = PaletteDialogs.editPalette.content();
				
				PaletteDialogs.editPalette.getCell(c).padLeft(0).padBottom(0);
				
				PaletteDialogs.editPalette.pack();
				
				float padl = (Gdx.input.getX()) - c.getX(), padb = (Graphics.mouse().y) - c.getY();
				
				PaletteDialogs.editPalette.getCell(c).padLeft(padl).padBottom(padb);
				
				PaletteDialogs.editPalette.show();
			}).size(40);
			
			title.getChildren().peek().setColor(Color.BLACK);
			
			add(title).growX();
			
			row();
			
			Table table = generatePaletteTable(36, maxWidth-28, palette.colors);
			
			add(table).fill();
		}
	}
	
	Table generatePaletteTable(float size, float width, Color[] colors){
		Table table = new Table();

		ColorBox[] boxes = new ColorBox[colors.length];
		for(int i = 0;i < boxes.length;i ++)
			boxes[i] = new ColorBox(colors[i]);

		float rowsize = width / boxes.length;

		int perow = (int)(width / size);

		table.top().left();

		if(rowsize < size){ // this means another row is needed
			for(int i = 0;i < boxes.length;i ++){
				table.add(boxes[i]).size(size);
				if((i%perow) == perow - 1) table.row();
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
		table.row();
		table.add().height(5);
		return table;
	}
}
