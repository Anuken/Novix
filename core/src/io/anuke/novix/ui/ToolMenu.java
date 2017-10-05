package io.anuke.novix.ui;

import io.anuke.novix.element.FloatingMenu;
import io.anuke.novix.internal.NovixEvent.ColorChange;
import io.anuke.ucore.core.Events;

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
			Events.fire(ColorChange.class, color);
		});

		content.top().left();

		picker.padLeft(12);
		picker.padRight(12);
		
		content.add(list).growX();

	}

}
