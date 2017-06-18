package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

import io.anuke.novix.Novix;
import io.anuke.novix.Vars;
import io.anuke.novix.element.FloatingMenu;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.function.Listenable;
import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.builders.build;
import io.anuke.ucore.scene.builders.table;
import io.anuke.ucore.scene.ui.Button;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.TextButton;
import io.anuke.ucore.scene.ui.layout.Table;

public class BottomSlider extends Table{
	private float duration = 0.18f;
	private Table content;
	protected LayerDisplay display;
	
	public BottomSlider(){
		setup();
		
		setY(-content.getHeight());
	}
	
	private void addMenus(Table table){
		table.pad(0);
		
		table.defaults().growX().height(50);
		
		table.addButton("Menu", ()->{
			Vars.ui.showProjectMenu();
		}, b->{
			b.addImage("icon-menu").size(32);
			b.getCells().reverse();
		});
		
		menu(table, "Image", "image")
		.add("Resize", "icon-resize", "Change the canvas size.", ()->{
			
		})
		.add("Crop", "icon-crop", "Cut out part of the image.", ()->{
			
		})
		.add("Clear", "icon-clear", "Clear the image.", ()->{
	
		})
		.add("Color Fill", "icon-clear", "Fill the image with one color.", ()->{
	
		})
		.add("Symmetry", "icon-symmetry", "Configure symmetry.", ()->{
	
		});
		
		menu(table, "Filters", "filter")
		.add("Colorize", "icon-colorize", "Configure image huge, brightness\nand saturation.", ()->{
			
		})
		.add("Invert", "icon-invert", "Invert the image color.", ()->{
			
		})
		.add("Replace", "icon-replace", "Replace a color with another.", ()->{
	
		})
		.add("Contrast", "icon-filter", "Change image contrast.", ()->{
	
		})
		.add("Outline", "icon-outline", "Add an outline around image contents.", ()->{
	
		})
		.add("Erase Color", "icon-erasecolor", "Erase a color from the image.", ()->{
			
		});
		menu(table, "Edit", "edit")
		.add("Flip", "icon-flip", "Flip the image.", ()->{
			
		})
		.add("Rotate", "icon-rotate", "Rotate the image.", ()->{
			
		})
		.add("Scale", "icon-scale", "Scale the image.", ()->{
	
		})
		.add("Shift", "icon-shift", "Move the image.", ()->{
	
		});
		
		menu(table, "File", "file")
		.add("Export", "icon-export", "Export the image as a PNG.", ()->{
			
		})
		.add("Open", "icon-open", "Load an image file into this project.", ()->{
			FileChooser.open("Open Image", f->{
				Novix.log("Image opened: " + f);
			});
		});
		//
		
		//menu(table, "");
	}
	
	private void button(Table table, String name, String description, String icon, Listenable clicked){
		
	}
	
	private void button(Table table, String name, String description, Listenable clicked){
		
	}
	
	private MenuBuilder menu(Table table, String name, String icon){
		TextButton button = new TextButton(name);
		Image image = new Image(Draw.getPatch("icon-"+icon));
		button.add(image).size(32);
		button.getCells().reverse();
		
		table.add(button);
		
		return new MenuBuilder(name, button);
	}
	
	private void setup(){
		bottom().left();
		
		display = new LayerDisplay();
		
		build.begin(this);
		
		new table("button"){{
			atop();
			aleft();
			
			Table table = new Table();
			table.background("button");
			addMenus(table);
			
			add(table).padTop(-get().getPadTop()).padLeft(-get().getPadLeft()).growX()
			.padRight(-get().getPadRight());
			
			row();
			
			Table extra = new Table();
			
			add(extra).left();
			
			extra.add(display).left().pad(10);
			
			content = get();
		}}.expandX().fillX();
		
		build.end();
		
		pack();
	}
	
	void slide(boolean up){
		addAction(Actions.moveBy(0, up ? content.getHeight() : -content.getHeight(), duration, Interpolation.fade));
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		
		setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	class MenuBuilder{
		private FloatingMenu menu;
		
		public MenuBuilder(String title, Button bind){
			menu = new FloatingMenu(title);
			
			bind.clicked(()->{
				menu.show();
			});
		}
		
		public MenuBuilder add(String name, String icon, String text, Listenable clicked){
			menu.addMenuItem(name, icon, text, ()->{
				clicked.listen();
				menu.hide();
			});
			return this;
		}
	}
}
