package io.anuke.pixeleditor;

import io.anuke.pixeleditor.modules.Core;
import io.anuke.pixeleditor.modules.Input;
import io.anuke.pixeleditor.modules.Tutorial;
import net.pixelstatic.gdxutils.modules.ModuleController;

public class PixelEditor extends ModuleController<PixelEditor>{
	
	@Override
	public void init(){
		addModule(Input.class);
		addModule(Core.class);
		addModule(Tutorial.class);
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
