package io.anuke.novix.dialogs;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novix.Vars;
import io.anuke.novix.element.ColorBox;
import io.anuke.novix.element.FloatingMenu;
import io.anuke.novix.filter.Filter;
import io.anuke.novix.internal.Layer;
import io.anuke.ucore.scene.style.Drawable;
import io.anuke.ucore.scene.ui.ColorPicker;
import io.anuke.ucore.scene.ui.Slider;
import io.anuke.ucore.scene.ui.layout.Table;

public class FilterDialogs{
	
	public static class FilterMenu extends FloatingMenu{
		Table buttons = new Table();
		Table tweaks = new Table();
		Object[] args;
		Filter filter;

		public FilterMenu(Filter filter, String text) {
			super(false, text);
			
			this.filter = filter;
			
			background((Drawable)null);
			
			shown(()->{
				updateImage();
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
				apply();
			});
			
			tweaks.background("button");
			
			content.bottom().add(tweaks).growX();
			tweaks.pad(10);
			
			row();
			add(buttons).growX().fillY();
			
			tweaks.setVisible(()->tweaks.getChildren().size != 0);
		}
		
		public Filter getFilter(){
			return filter;
		}
		
		void updateArgs(){}
		
		void updateImage(){
			updateArgs();
			Layer[] layers = Vars.drawing.getLayers();
			for(Layer layer : layers){
				layer.applyPreviewFilter(filter, args);
			}
		}
		
		void apply(){
			Layer[] layers = Vars.drawing.getLayers();
			for(Layer layer : layers){
				layer.applyFilter();
			}
		}
	}
	
	public static final FilterMenu
	
	colorize = new FilterMenu(Filter.colorize, "Colorize"){
		Slider h, s, b;
		{
			tweaks.left();
			
			tweaks.add(()->"Hue: " + (int)h.getValue()).left();
			tweaks.row();
			h = tweaks.addSlider(0, 360f, 1f, 180, value->{
				updateImage();
			}).growX().get();
			
			tweaks.row();
			
			tweaks.add(()->"Saturation: " + (int)s.getValue()).left();
			tweaks.row();
			s = tweaks.addSlider(0, 100f, 1f, 50, value->{
				updateImage();
			}).growX().get();
			
			tweaks.row();
			
			tweaks.add(()->"Brightness: " + (int)b.getValue()).left();
			tweaks.row();
			b = tweaks.addSlider(0, 100f, 1f, 50, value->{
				updateImage();
			}).growX().get();
			
			tweaks.row();
		}
		
		public void updateArgs(){
			args = new Object[]{h.getValue()/360f, s.getValue()/100f, b.getValue()/100f};
		}
	},
	invert = new FilterMenu(Filter.invert, "Invert"){
		{
			//nice
		}
	},
	contrast = new FilterMenu(Filter.contrast, "Contrast"){
		Slider slider;
		{
			tweaks.add(()->"Contrast: " + (int)slider.getValue()).left();
			tweaks.row();
			
			slider = tweaks.addSlider(-50, 50, 1f, 0, value->{
				updateImage();
			}).growX().get();
		}
		
		public void updateArgs(){
			args = new Object[]{slider.getValue()/50f};
		}
	},
	outline = new FilterMenu(Filter.outline, "Outline"){
		ColorBox box;
		{
			box = new ColorBox(Color.WHITE, false);
			box.clicked(()->{
				picker.setColor(box.getImageColor());
				picker.show();
				picker.hidden(()->{
					updateImage();
				});
			});
			
			tweaks.add("Outline color:").left();
			tweaks.row();
			tweaks.add(box).size(60);
		}
		
		public void updateArgs(){
			args = new Object[]{box.getImageColor()};
		}
	},
	coloralpha = new FilterMenu(Filter.colorToAlpha, "Erase Color"){
		ColorBox box;
		{
			box = new ColorBox(Color.WHITE, false);
			box.clicked(()->{
				picker.setColor(box.getImageColor());
				picker.show();
				picker.hidden(()->{
					updateImage();
				});
			});
			
			tweaks.add("Target color:").left();
			tweaks.row();
			tweaks.add(box).size(60);
		}
		
		public void updateArgs(){
			args = new Object[]{box.getImageColor()};
		}
	},
	replace = new FilterMenu(Filter.replace, "Replace"){
		ColorBox from, to;
		{
			from = new ColorBox(Color.WHITE, false);
			from.clicked(()->{
				picker.setColor(from.getImageColor());
				picker.show();
				picker.hidden(()->{
					updateImage();
				});
			});
			
			to = new ColorBox(Color.WHITE, false);
			to.clicked(()->{
				picker.setColor(to.getImageColor());
				picker.show();
				picker.hidden(()->{
					updateImage();
				});
			});
			
			tweaks.add("Colors:").colspan(3).padBottom(10);
			tweaks.row();
			tweaks.add(from).size(60);
			tweaks.addImage("icon-arrow-right").size(42).padRight(20).padLeft(20);
			tweaks.add(to).size(60);
		}
		
		public void updateArgs(){
			args = new Object[]{from.getImageColor(), to.getImageColor()};
		}
	};
	
	private static FloatingMenu picker = new FloatingMenu(false, "Choose Color"){
		ColorPicker colorp;
		Color last;
		{
			colorp = new ColorPicker();
			content.add(colorp);
			
			row();
			
			Table buttons = new Table();
			
			buttons.defaults().size(166, 56).pad(8);
			
			buttons.addButton("Cancel", ()->{
				colorp.getColor().set(last);
				hide();
			});
			
			buttons.addButton("OK", ()->{
				hide();
			});
			
			content.row();
			
			content.add(buttons).padTop(32).growX();
		}
		
		@Override
		public void setColor(Color color){
			last = color.cpy();
			colorp.setColor(color);
		}
	};
}
