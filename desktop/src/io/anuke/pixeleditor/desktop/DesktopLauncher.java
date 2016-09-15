package io.anuke.pixeleditor.desktop;

import io.anuke.pixeleditor.PixelEditor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 500;
		config.height = 900;
		new LwjglApplication(new PixelEditor(), config);
	}
}
