package io.anuke.novix.ui;

import io.anuke.novix.Novix;
import io.anuke.novix.Vars;
import io.anuke.novix.dialogs.FilterDialogs;
import io.anuke.novix.element.FloatingMenu;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.function.Listenable;
import io.anuke.ucore.scene.ui.Button;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.TextButton;
import io.anuke.ucore.scene.ui.layout.Table;

public class ListTable extends Table{
	
	public ListTable(){
		setup();
	}
	
	void setup(){
		pad(0);
		
		defaults().growX().height(60);
		
		menu("Image", "image")
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
		
		menu("Filters", "filter")
		.add("Colorize", "icon-colorize", "Configure image huge, brightness\nand saturation.", ()->{
			FilterDialogs.colorize.show();
		})
		.add("Invert", "icon-invert", "Invert the image color.", ()->{
			FilterDialogs.invert.show();
		})
		.add("Replace", "icon-replace", "Replace a color with another.", ()->{
			FilterDialogs.replace.show();
		})
		.add("Contrast", "icon-filter", "Change image contrast.", ()->{
			FilterDialogs.contrast.show();
		})
		.add("Outline", "icon-outline", "Add an outline around image contents.", ()->{
			FilterDialogs.outline.show();
		})
		.add("Erase Color", "icon-erasecolor", "Erase a color from the image.", ()->{
			FilterDialogs.coloralpha.show();
		});
		menu("Edit", "edit")
		.add("Flip", "icon-flip", "Flip the image.", ()->{
			
		})
		.add("Rotate", "icon-rotate", "Rotate the image.", ()->{
			
		})
		.add("Scale", "icon-scale", "Scale the image.", ()->{
	
		})
		.add("Shift", "icon-shift", "Move the image.", ()->{
	
		});
		
		menu("File", "file")
		.add("Export", "icon-export", "Export the image as a PNG.", ()->{
			
		})
		.add("Open", "icon-open", "Load an image file into this project.", ()->{
			FileChooser.open("Open Image", f->{
				Vars.drawing.loadImage(f);
				Novix.log("Image opened: " + f);
			});
		});
	}
	
	private MenuBuilder menu(String name, String icon){
		TextButton button = new TextButton(name);
		Image image = new Image(Draw.getPatch("icon-"+icon));
		button.add(image).size(32);
		button.getCells().reverse();
		
		add(button);
		
		return new MenuBuilder(name, button);
	}
	
	class MenuBuilder{
		private FloatingMenu menu;
		
		public MenuBuilder(String title, Button bind){
			menu = new FloatingMenu(title);
			
			bind.clicked(()->{
				menu.show();
				Vars.ui.hideToolMenu();
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
