package io.anuke.novix.managers;


import static io.anuke.novix.Var.*;
import static io.anuke.ucore.UCore.s;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.anuke.novix.Novix;
import io.anuke.novix.tools.Tool;

public class GestureManager extends GestureAdapter{
	private float touchy;
	private Vector2 vector = new Vector2();
	private final float flingvelocity = 1300, swipevelocity = 1300;

	@Override
	public boolean touchDown(float x, float y, int pointer, int button){
		touchy = Gdx.graphics.getHeight() - y;
		return false;
	}
	
	@Override
	public boolean fling(float velocityX, float velocityY, int button){
		Actor toolmenu = stage.getRoot().findActor("toolmenu");
		
		if(!core.prefs.getBoolean("gestures", true) || core.getCurrentDialog() != null || 
				(!drawing.hasMouse(touchy) && core.tool() == Tool.zoom 
				&& !(!core.colorMenuCollapsed() || !core.toolMenuCollapsed()))) return false;
		
		float swipevelocity = this.swipevelocity*s;
		float flingvelocity = this.flingvelocity*s;
		
		float tooltop = toolmenu.localToStageCoordinates(vector.set(0, 0)).y + toolmenu.getHeight();
		float colortop = toolmenu.localToStageCoordinates(vector.set(0, 0)).y;

		if(Math.abs(velocityX) > swipevelocity && Math.abs(velocityY) < swipevelocity && MathUtils.isEqual(touchy, Gdx.graphics.getHeight() / 2, 250 * s)){
			if( !core.colorMenuCollapsed() && velocityX < -swipevelocity) core.collapseColorMenu(); //swipe left, close color menu
			if(core.colorMenuCollapsed() && velocityX > swipevelocity){ //swipe right, open color menu
				if(!core.toolMenuCollapsed()) core.collapseToolMenu(); //close tool menu if open
				core.collapseColorMenu();
			}
			
		}else if( !core.colorMenuCollapsed() && touchy < colortop + 320 * s && velocityY < -flingvelocity){ // swipe up from the top, collapse color menu
			core.collapseColorMenu();
		}else if(core.toolMenuCollapsed() && touchy < Gdx.graphics.getHeight() / 3 && velocityY < -flingvelocity){
			core.collapseToolMenu();
		}
		
		if( !core.toolMenuCollapsed() && touchy > tooltop - 60 * s && velocityY > flingvelocity){
			core.collapseToolMenu();
		}else if(core.toolMenuCollapsed() && core.colorMenuCollapsed() && touchy > Gdx.graphics.getWidth() / 3f * 2 && velocityY > flingvelocity){
			Novix.log("collapsing other menu");
			core.collapseColorMenu();
		}

		return false;
	}
}
