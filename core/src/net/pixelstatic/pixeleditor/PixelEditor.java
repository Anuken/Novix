package net.pixelstatic.pixeleditor;

import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.pixeleditor.modules.Input;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.modules.ModuleController;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.DetailsDialog;

public class PixelEditor extends ModuleController<PixelEditor>{
	
	@Override
	public void init(){
		addModule(Input.class);
		addModule(GUI.class);
	}
	
	@Override
	public void render(){
		try{
			super.render();
			if(Gdx.graphics.getFrameId() == 5)throw new RuntimeException("Random error!");
		}catch (Exception e){
			DetailsDialog dialog = Dialogs.showDetailsDialog(((GUI)getModule(GUI.class)).stage, "An exception has occured.", "Error", 
					""+e.getClass().getSimpleName() + (e.getMessage()  == null ? "" : ": " + 
			e.getMessage()) + "\nSource: " + MiscUtils.getLineNumber());
			dialog.setCopyDetailsButtonVisible(false);
		}
	}
}
