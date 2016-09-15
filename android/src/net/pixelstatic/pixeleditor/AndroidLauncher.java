package net.pixelstatic.pixeleditor;

import io.anuke.pixeleditor.PixelEditor;
import io.anuke.utils.android.AndroidKeyboard;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.*;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication{
	private View rootView;
	private int width, height;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;

		rootView = this.getWindow().getDecorView().getRootView();
		Rect rect = new Rect();
		rootView.getWindowVisibleDisplayFrame(rect);
		width = rect.width();
		height = rect.height();

		rootView.addOnLayoutChangeListener(new OnLayoutChangeListener(){

			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom){

				Rect rect = new Rect();
				rootView.getWindowVisibleDisplayFrame(rect);

				if( !(width == rect.width() && height == rect.height())){
					AndroidKeyboard.onResize(rect.width(), rect.height(), rect.width() - width, rect.height() - height);
					width = rect.width();
					height = rect.height();
				}
			}

		});

		initialize(new PixelEditor(), config);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		super.onWindowFocusChanged(hasFocus);
		// When the window loses focus (e.g. the action overflow is shown),
		// cancel any pending hide action. When the window gains focus,
		// hide the system UI.
		if(hasFocus){
			delayedHide(300);
		}else{
			mHideHandler.removeMessages(0);
		}
	}

	@SuppressLint("NewApi")
	private void hideSystemUI(){
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	@SuppressLint("NewApi")
	private void showSystemUI(){
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	@SuppressLint("HandlerLeak")
	private final Handler mHideHandler = new Handler(){
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg){
			hideSystemUI();
		}
	};

	private void delayedHide(int delayMillis){
		mHideHandler.removeMessages(0);
		mHideHandler.sendEmptyMessageDelayed(0, delayMillis);
	}
}
