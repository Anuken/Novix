package io.anuke.pixeleditor.managers;

import static io.anuke.pixeleditor.modules.Core.s;
import io.anuke.pixeleditor.modules.Core;
import io.anuke.pixeleditor.tools.Tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class GestureManager implements GestureListener{
	private Core main;
	private float touchy;
	private Vector2 vector = new Vector2();
	private final float flingvelocity = 1300, swipevelocity = 1300;

	public GestureManager(Core main){
		this.main = main;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button){
		touchy = Gdx.graphics.getHeight() - y;
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button){
		return false;
	}

	@Override
	public boolean longPress(float x, float y){
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button){
		if(Core.i.getCurrentDialog() != null || 
				(!Core.i.drawgrid.input.checkRange((int)touchy) && Core.i.tool == Tool.zoom && !(!Core.i.colorMenuCollapsed() || !Core.i.toolMenuCollapsed()))) return false;
		
		float swipevelocity = this.swipevelocity*s;
		float flingvelocity = this.flingvelocity*s;
		
		float tooltop = main.toolmenu.localToStageCoordinates(vector.set(0, 0)).y + main.toolmenu.getHeight();
		float colortop = main.pickertable.localToStageCoordinates(vector.set(0, 0)).y;

		if(Math.abs(velocityX) > swipevelocity && Math.abs(velocityY) < swipevelocity && MathUtils.isEqual(touchy, Gdx.graphics.getHeight() / 2, 250 * s)){
			if( !main.colorMenuCollapsed() && velocityX < -swipevelocity) main.collapseColorMenu(); //swipe left, close color menu
			if(main.colorMenuCollapsed() && velocityX > swipevelocity){ //swipe right, open color menu
				if(!main.toolMenuCollapsed()) main.collapseToolMenu(); //close tool menu if open
				main.collapseColorMenu();
			}
			
		}else if( !main.colorMenuCollapsed() && touchy < colortop + 320 * s && velocityY < -flingvelocity){ // swipe up from the top, collapse color menu
			main.collapseColorMenu();
		}else if(main.toolMenuCollapsed() && touchy < Gdx.graphics.getHeight() / 3 && velocityY < -flingvelocity){
			main.collapseToolMenu();
		}
		
		if( !main.toolMenuCollapsed() && touchy > tooltop - 60 * s && velocityY > flingvelocity){
			main.collapseToolMenu();
		}else if(main.toolMenuCollapsed() && main.colorMenuCollapsed() && touchy > Gdx.graphics.getWidth() / 3f * 2 && velocityY > flingvelocity){
			Gdx.app.error("pedebugging", "collapsing other menu");
			main.collapseColorMenu();
		}

		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY){
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button){
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance){
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2){
		return false;
	}

	@Override
	public void pinchStop(){
		// TODO Auto-generated method stub
		
	}
}
