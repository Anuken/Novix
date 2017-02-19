package io.anuke.novix.android;

import com.badlogic.gdx.Gdx;

public class AndroidKeyboard{
	static private AndroidKeyboardListener listener;
	static private int currentheight;
	
	public static void setListener(AndroidKeyboardListener listener){
		AndroidKeyboard.listener = listener;
	}
	
	public static void onResize(int width, int height, int changewidth, int changeheight){
		currentheight = height;
		if(listener != null){
			listener.onSizeChange(changeheight);
		}
	}
	
	public static int getCurrentKeyboardHeight(){
		return Gdx.graphics.getHeight() - currentheight;
	}
	
	public interface AndroidKeyboardListener{
		public void onSizeChange(int nheight);
	}
}
