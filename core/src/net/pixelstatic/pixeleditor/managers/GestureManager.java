package net.pixelstatic.pixeleditor.managers;

import net.pixelstatic.pixeleditor.modules.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class GestureManager implements GestureListener{
	private Main main;
	private float touchy;
	private Vector2 vector = new Vector2();
	private final float flingvelocity = 400;
	
	public GestureManager(Main main){
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
		if(Main.i.getCurrentDialog() != null) return false;
		
		float tooltop = main.toolmenu.localToStageCoordinates(vector.set(0,0)).y + main.toolmenu.getHeight();
		float colortop = main.pickertable.localToStageCoordinates(vector.set(0,0)).y;
		
		if(!main.toolMenuCollapsed() && touchy > tooltop - 60 && velocityY > flingvelocity){
			main.collapseToolMenu();
		}
		
		if(!main.colorMenuCollapsed() && touchy < colortop + 320 && velocityY < flingvelocity){
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
}
