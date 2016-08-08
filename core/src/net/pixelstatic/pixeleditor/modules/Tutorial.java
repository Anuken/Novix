package net.pixelstatic.pixeleditor.modules;

import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.pixeleditor.tools.TutorialStage;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.Gdx;


public class Tutorial extends Module<PixelEditor>{
	TutorialStage stage = TutorialStage.colors;
	TutorialStage laststage = null;
	float shadespeed = 0.05f;
	
	@Override
	public void update(){
		TutorialStage.cliprect.set(0,0,0,0);
		TutorialStage.cliprect2.set(0,0,0,0);
		
		Core.i.stage.getBatch().begin();
		
		if(stage.trans < 1f){
			laststage.trans -= shadespeed * Gdx.graphics.getDeltaTime()*60f;
			if(laststage.trans < 0)laststage.trans = 0f;
			stage.trans += shadespeed * Gdx.graphics.getDeltaTime()*60f;
			Gdx.graphics.requestRendering();
			laststage.draw(Core.i.stage.getBatch());
		}
		
		stage.draw(Core.i.stage.getBatch());
		
		if(stage.next){
			laststage = stage;
			stage = TutorialStage.values()[stage.ordinal()+1];
			stage.trans = 0f;
		}
		
		Core.i.stage.getBatch().end();
	}
	
	public void reset(){
		for(TutorialStage stage : TutorialStage.values()){
			stage.next = false;
			stage.trans = 1f;
		}
		stage = TutorialStage.values()[0];
	}
	
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		return inRect(screenX, screenY);
	}
	
	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		return inRect(screenX, screenY);
	}	
	
	public boolean inRect(int screenX, int screenY){
		return TutorialStage.cliprect.contains(screenX, Gdx.graphics.getHeight() - screenY) ||  TutorialStage.cliprect2.contains(screenX, Gdx.graphics.getHeight() - screenY);
	}
}
