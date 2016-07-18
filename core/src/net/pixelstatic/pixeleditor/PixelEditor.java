package net.pixelstatic.pixeleditor;

import net.pixelstatic.pixeleditor.modules.Main;
import net.pixelstatic.pixeleditor.modules.Input;
import net.pixelstatic.utils.modules.ModuleController;

public class PixelEditor extends ModuleController<PixelEditor>{
	
	@Override
	public void init(){
		addModule(Input.class);
		addModule(Main.class);
	}
	/*
	@Override
	public void render(){
		try{
			super.render();
		}catch (Exception e){
			DetailsDialog dialog = Dialogs.showDetailsDialog(((GUI)getModule(GUI.class)).stage, "An exception has occured.", "Error", 
					""+e.getClass().getSimpleName() + (e.getMessage()  == null ? "" : ": " + 
			e.getMessage()) + "\nSource: " + MiscUtils.getLineNumber());
			dialog.setCopyDetailsButtonVisible(false);
		}
	}
	*/
}
