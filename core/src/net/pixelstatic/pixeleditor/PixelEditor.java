package net.pixelstatic.pixeleditor;

import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.pixeleditor.modules.Input;
import net.pixelstatic.utils.modules.ModuleController;

public class PixelEditor extends ModuleController<PixelEditor>{
	
	@Override
	public void init(){
		addModule(Input.class);
		addModule(GUI.class);
	}
}
