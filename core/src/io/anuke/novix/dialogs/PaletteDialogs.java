package io.anuke.novix.dialogs;

import static io.anuke.novix.Vars.*;

import io.anuke.novix.element.FloatingMenu;
import io.anuke.novix.element.PopupDialog;
import io.anuke.novix.internal.Palette;
import io.anuke.ucore.scene.ui.TextField;
import io.anuke.ucore.scene.ui.TextField.TextFieldFilter;
import io.anuke.ucore.util.Strings;

public class PaletteDialogs{
	public static Palette palette;
	
	public static final PopupDialog editPalette = new PopupDialog(){
		{
			content().defaults().size(190, 40);
			
			addItem("Rename", "icon-rename", 28, () -> {
				renamePalette.show();
			});
			addItem("Resize", "icon-resize", 28, () -> {
				resizePalette.show();
			});
			addItem("Copy", "icon-copy", 28, () -> {
				Palette copy = new Palette(palette);
				control.palettes().addPalette(copy);
				rebuild();
				
			});
			addItem("Delete", "icon-trash", 28, () -> {
				confirmDelete.show();
			});
		}
	};
	
	public static final FloatingMenu renamePalette = new FloatingMenu("Rename Palette"){
		TextField name;
		{
			name = new TextField("");
			content.add("New palette name:").padBottom(8);
			content.row();
			content.add(name).size(220, 46);
			
			content.row();
			
			content.addButton("Rename", ()->{
				palette.name = (name.getText());
				control.palettes().save();
				rebuild();
				hide();
			}).padTop(60).size(260, 58);
		}
		
		public void show(){
			super.show();
			name.setText(palette.name);
		}
	},
			
	resizePalette = new FloatingMenu("Resize Palette"){
		TextField size;
		{
			size = new TextField("");
			size.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
			
			content.add("New palette size:").colspan(3).padBottom(8);
			content.row();
			content.add(size).size(220, 46);
			content.addButton("+", ()->{
				add(1);
			}).size(46);
			content.addButton("-", ()->{
				add(-1);
			}).size(46);
			
			content.row();
			
			content.addButton("Resize", ()->{
				palette.resize(Strings.parseInt(size.getText()));
				control.palettes().save();
				hide();
				rebuild();
			}, b->{
				b.setDisabled(()->!Strings.canParsePostiveInt(size.getText()));
			}).padTop(60).colspan(3).size(260, 58);
		}
		
		void add(int amount){
			if(!Strings.canParsePostiveInt(size.getText())) return;
			
			int current = Strings.parseInt(size.getText());
			
			current += amount;
			
			if(current > 0)
				size.setText(current + "");
		}
		
		public void show(){
			super.show();
			size.setText(palette.colors.length + "");
		}
	}, 
	
	confirmDelete = new BaseDialogs.ConfirmDialog("Confirm Delete", "Are you sure you want to delete this palette?", ()->{
		control.palettes().removePalette(palette);
		rebuild();
	}),
	
	newPalette = new FloatingMenu("New Palette"){
		String size = "16", name = "";
		
		{
			content.add("Name: ").left();
			
			content.addField("", s->{
				name = s;
			}).size(180, 44);
			
			content.row();
			
			content.add("Size: ").padTop(40).left();
			
			content.addField("16", s->{
				size = s;
			}).size(180, 44).padTop(40);
			
			content.row();
			
			content.addButton("Create", () -> {
				int size = Integer.parseInt(this.size);
				
				Palette palette = new Palette(name, control.palettes().genColors(size));
				
				control.palettes().addPalette(palette);
				rebuild();
				hide();
			}, b->{
				b.setDisabled(()->{
					return !(Strings.canParsePostiveInt(size) && !name.trim().isEmpty());
				});
			}).colspan(2).padTop(60).fillX().height(60).width(360);
		}
	};
	
	private static void rebuild(){
		ui.updatePaletteMenu();
	}

}
