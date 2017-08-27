package io.anuke.novix.ui;

import io.anuke.novix.Vars;
import io.anuke.novix.element.FloatingMenu;

public class ToolMenu extends FloatingMenu{
	private ColorPicker picker;
	private PaletteMenu palettemenu;
	private ListTable list;

	public ToolMenu() {
		super(true, "");
		setup();
	}

	private void setup(){
		title.remove();
		
		list = new ListTable();
		
		palettemenu = new PaletteMenu();
		
		picker = new ColorPicker();
		picker.colorChanged(color -> {
			Vars.ui.top().setSelectedColor(color.cpy());
			Vars.drawing.getLayer().setColor(color);
		});

		content.top().left();

		picker.padLeft(12);
		picker.padRight(12);
		//content.add(picker).growX().padTop(12);

		//content.row();

		//content.addCenteredImageTextButton("Palettes...", "icon-palette", 42, () -> {
		//	palettemenu.show();
		//}).growX().height(60).padBottom(12).padLeft(4).padRight(4);
		
		//content.row();
		
		content.add(list).growX();

	}

}
