package net.pixelstatic.pixeleditor;

import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.utils.AndroidKeyboard;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
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
}
