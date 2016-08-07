package net.pixelstatic.pixeleditor.modules;

import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.pixeleditor.tools.TutorialStage;
import net.pixelstatic.utils.modules.Module;


public class Tutorial extends Module<PixelEditor>{
	TutorialStage stage = TutorialStage.first;
	
	@Override
	public void update(){
		stage.draw(Core.i.stage.getBatch());
	}
}
