package net.pixelstatic.pixeleditor.modules;

import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.pixeleditor.tools.TutorialStage;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class Tutorial extends Module<PixelEditor>{
	private boolean active = true;
	private TutorialStage stage = TutorialStage.canvas;
	private TutorialStage laststage = null;
	private float shadespeed = 0.05f;

	@Override
	public void update(){
		if(active){
			for(Rectangle rect : TutorialStage.cliprects)
				rect.set(0, 0, 0, 0);

			Core.i.stage.getBatch().begin();

			if(stage.trans < 1f){
				laststage.trans -= shadespeed * Gdx.graphics.getDeltaTime() * 60f;
				if(laststage.trans < 0) laststage.trans = 0f;
				stage.trans += shadespeed * Gdx.graphics.getDeltaTime() * 60f;
				if(stage.trans > 1f) stage.trans = 1f;
				Gdx.graphics.requestRendering();
				
				laststage.draw(Core.i.stage.getBatch());
				
			}

			stage.draw(Core.i.stage.getBatch());

			if(stage.next){
				stage.end();
				laststage = stage;
				stage = TutorialStage.values()[stage.ordinal() + 1];
				stage.trans = 0f;
			}

			Core.i.stage.getBatch().end();
		}else{

			if(stage.trans > 0){
				Gdx.graphics.requestRendering();
				Core.i.stage.getBatch().begin();
				stage.draw(Core.i.stage.getBatch());
				Core.i.stage.getBatch().end();
				stage.trans -= shadespeed;
			}
		}
	}

	public void end(){
		active = false;
	}

	private void reset(){
		for(TutorialStage stage : TutorialStage.values()){
			stage.next = false;
			stage.trans = 1f;
		}
		stage = TutorialStage.values()[0];
		laststage = null;
		active = false;
	}

	public void begin(){
		if(active) reset();
		active = true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		stage.tap(screenX, Gdx.graphics.getHeight() - screenY);
		return inRect(screenX, screenY);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		return inRect(screenX, screenY);
	}

	public boolean inRect(int screenX, int screenY){
		for(Rectangle rect : TutorialStage.cliprects){
			if(rect.contains(screenX, Gdx.graphics.getHeight() - screenY)) return true;
		}
		return false;
	}
}
