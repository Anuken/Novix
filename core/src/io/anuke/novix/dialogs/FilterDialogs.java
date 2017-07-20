package io.anuke.novix.dialogs;

import io.anuke.novix.Vars;
import io.anuke.novix.element.FloatingMenu;
import io.anuke.ucore.scene.style.Drawable;
import io.anuke.ucore.scene.ui.Slider;
import io.anuke.ucore.scene.ui.layout.Table;

public class FilterDialogs{
	
	public static class FilterMenu extends FloatingMenu{
		Table buttons = new Table();
		Table tweaks = new Table();

		public FilterMenu(String text) {
			super(false, text);
			
			background((Drawable)null);
			
			shown(()->{
				Vars.ui.setFilterMenu(this);
				Vars.ui.bottom().toggle();
			});
			
			hidden(()->{
				Vars.ui.setFilterMenu(null);
			});
			
			buttons.defaults().growX().height(60).pad(10);
			
			buttons.addButton("Cancel", ()->{
				hide();
			});
			
			buttons.addButton("Apply", ()->{
				hide();
			});
			
			tweaks.background("button");
			
			content.bottom().add(tweaks).growX();
			tweaks.pad(10);
			
			row();
			add(buttons).growX().fillY();
		}
	}
	
	public static final FilterMenu
	
	colorize = new FilterMenu("Colorize"){
		Slider h, s, v;
		{
			tweaks.left();
			
			tweaks.add(()->"Hue: " + (int)h.getValue()).left();
			tweaks.row();
			h = tweaks.addSlider(0, 360f, 1f, 180, value->{
				
			}).growX().get();
			
			tweaks.row();
			
			tweaks.add(()->"Saturation: " + (int)s.getValue()).left();
			tweaks.row();
			s = tweaks.addSlider(0, 100f, 1f, 50, value->{
				
			}).growX().get();
			
			tweaks.row();
			
			tweaks.add(()->"Brightness: " + (int)v.getValue()).left();
			tweaks.row();
			v = tweaks.addSlider(0, 100f, 1f, 50, value->{
				
			}).growX().get();
			
			tweaks.row();
		}
	};
}
