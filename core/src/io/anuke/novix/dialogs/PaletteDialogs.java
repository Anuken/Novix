package io.anuke.novix.dialogs;

import io.anuke.novix.element.PopupDialog;

public class PaletteDialogs{
	public static final PopupDialog editPalette = new PopupDialog(){{
		content().defaults().size(190, 50);
		
		addItem("Rename", "icon-rename", 28, ()->{
			
		});
		addItem("Resize", "icon-resize", 28, ()->{
			
		});
		addItem("Copy", "icon-copy", 28, ()->{

		});
		addItem("Delete", "icon-trash", 28, ()->{
	
		});
	}};
			
}
