package io.anuke.novix.ui;

import io.anuke.novix.element.FloatingMenu;
import io.anuke.ucore.core.Settings;

public class SettingsMenu extends FloatingMenu{

	public SettingsMenu() {
		super("Settings");
		//TODO
		content.pad(15);
		
		checkSetting("Gestures", "gestures");
	}

	void checkSetting(String name, String prefName){
		content.top().left();
		
		content.addCheck("", 44, Settings.getBool(prefName), value -> {
			Settings.putBool(prefName, value);
			Settings.save();
		}).left().padRight(10f);
		
		content.add(() -> name + ": " + (Settings.getBool(prefName) ? "[title]On" : "[accent]Off"));
	}
}
