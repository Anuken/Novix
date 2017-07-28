package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import io.anuke.novix.Vars;
import io.anuke.novix.dialogs.PaletteDialogs;
import io.anuke.novix.element.ColorBox;
import io.anuke.novix.element.FloatingMenu;
import io.anuke.novix.internal.Palette;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.scene.event.Touchable;
import io.anuke.ucore.scene.ui.ImageButton;
import io.anuke.ucore.scene.ui.ScrollPane;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.scene.utils.ClickListener;

public class PaletteMenu extends FloatingMenu{
	Table mainTable = new Table();
	ScrollPane pane;
	float maxWidth = 400;
	
	public PaletteMenu(){
		super("Palettes");
		
		pane = new ScrollPane(mainTable);
		//pane.setFadeScrollBars(false);
		
		content.add(pane).growX();
		
		content.row();
		
		content.addCenteredImageTextButton("New Palette", "icon-plus", 30, ()->{
			PaletteDialogs.newPalette.show();
		}).width(maxWidth).height(46).padTop(10);
	}
	
	void rebuild(){
		mainTable.clearChildren();
		
		Array<Palette> list = Vars.control.palettes().getPalettes();
		
		for(Palette palette : list){
			
			mainTable.add(new PaletteTable(palette)).width(maxWidth).padTop(4);
			
			mainTable.row();
		}
	}
	
	public void show(){
		super.show();
		rebuild();
		DrawContext.scene.setScrollFocus(pane);
	}
	
	class PaletteTable extends Table{
		
		public PaletteTable(Palette palette){
			setTouchable(Touchable.enabled);
			
			background(Vars.control.palettes().current() == palette ? "button-select" : "button");
			
			pad(14);
			
			Table title = new Table();
			
			title.add(palette.name);
			
			title.add().growX();
			
			ImageButton button = title.addIButton("dots", "icon-dots", 40, ()->{
				
				PaletteDialogs.palette = palette;
				
				Table c = PaletteDialogs.editPalette.content();
				c.setPosition(Gdx.input.getX() - c.getPrefWidth()/2, Graphics.mouse().y-c.getPrefHeight()/2);
				
				PaletteDialogs.editPalette.show();
			}).size(40).get();
			
			title.getChildren().peek().setColor(Color.BLACK);
			
			add(title).growX();
			
			row();
			
			Table table = generatePaletteTable(36, maxWidth-28, palette.colors);
			
			add(table).fill();
			
			ClickListener click = button.clicked(null);
			
			clicked(()->{
				if(!click.isOver()){
					//TODO NO
					Vars.control.palettes().setSelected(palette);
					rebuild();
				}
			});
		}
	}
	
	Table generatePaletteTable(float size, float width, Color[] colors){
		Table table = new Table();

		ColorBox[] boxes = new ColorBox[colors.length];
		for(int i = 0;i < boxes.length;i ++){
			boxes[i] = new ColorBox(colors[i]);
			boxes[i].setTouchable(Touchable.disabled);
		}

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
