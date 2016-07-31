package net.pixelstatic.pixeleditor.managers;

import net.pixelstatic.pixeleditor.modules.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class GestureManager implements GestureListener{
	private Main main;
	private boolean nearToolMenu = false, nearColorMenu;
	
	public GestureManager(Main main){
		this.main = main;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button){
		float tooltop = main.toolmenu.localToStageCoordinates(new Vector2()).y + main.toolmenu.getHeight();
		float colortop = main.toolmenu.localToStageCoordinates(new Vector2()).y + main.toolmenu.getHeight();
		
		nearToolMenu = !main.toolMenuCollapsed() && Gdx.graphics.getHeight() - y > tooltop - 60;
		nearColorMenu = !main.colorMenuCollapsed() && Gdx.graphics.getHeight() - y > tooltop - 60;
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button){
		System.out.println(nearToolMenu);
		if(nearToolMenu && velocityY > 400){
			main.collapseToolMenu();
		}
		
		System.out.printf("Fling: %f, %f\n", velocityX, velocityY);
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button){
		
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2){
		
		return false;
	}
}
