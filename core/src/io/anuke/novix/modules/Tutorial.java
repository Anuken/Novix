package io.anuke.novix.modules;

import static io.anuke.novix.Var.*;
import static io.anuke.ucore.UCore.s;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import io.anuke.novix.Novix;
import io.anuke.novix.Var;
import io.anuke.novix.tools.TutorialStage;
import io.anuke.novix.ui.DialogClasses;
import io.anuke.ucore.graphics.ShapeUtils;
import io.anuke.ucore.modules.Module;

public class Tutorial extends Module<Novix>{
	private boolean active = false;
	private TutorialStage stage = TutorialStage.values()[0];
	private TutorialStage laststage = null;
	private float shadespeed = 0.05f;
	{
		stage.trans = 0;
	}

	@Override
	public void update(){
		
		ShapeUtils.thickness = 4;
		if(active){
			for(Rectangle rect : TutorialStage.cliprects)
				rect.set(0, 0, 0, 0);

			batch.begin();

			if(stage.trans < 1f){
				if(laststage != null){
					laststage.trans -= shadespeed;
					if(laststage.trans < 0) laststage.trans = 0f;
					laststage.draw(batch);
				}
				stage.trans += shadespeed;
				if(stage.trans > 1f) stage.trans = 1f;
				Gdx.graphics.requestRendering();

			}

			stage.draw(batch);

			if(stage.next){
				stage.end();
				laststage = stage;
				stage = TutorialStage.values()[stage.ordinal() + 1];
				stage.trans = 0f;
			}

			batch.end();
		}else{
			if(stage.trans > 0){
				Gdx.graphics.requestRendering();
				batch.begin();
				stage.draw(batch);
				batch.end();
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
		reset();
		active = true;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button){
		if(active){
			stage.tap(x, Gdx.graphics.getHeight() - y);
			if(x > Gdx.graphics.getWidth() - 50*s && (Gdx.graphics.getHeight() - y) < 30 * s){
				active = false;
				new DialogClasses.ConfirmDialog("Confirm", "Are you sure you want to\nexit the tutorial?"){
					boolean confirming;
					
					public void result(){
						confirming = true;
						close();
					}
					
					public void cancel(){
						close();
					}
					
					public void close(){
						super.close();
						if(confirming){
							end();
							if(core.projectsShown()){
								if(!core.colorMenuCollapsed()) core.collapseColorMenu();
								if(!core.toolMenuCollapsed()) core.collapseToolMenu();
								core.hideProjects();
							}
						}else{
							active = true;
						}
					}
					
					public void hide(){}
				}.show(Var.stage);
				
				
			}
		}
		return inRect(x, y);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		return inRect(screenX, screenY);
	}

	public boolean inRect(int screenX, int screenY){
		if( !active) return false;
		for(Rectangle rect : TutorialStage.cliprects){
			if(rect.contains(screenX, Gdx.graphics.getHeight() - screenY)) return true;
		}
		return false;
	}
}
