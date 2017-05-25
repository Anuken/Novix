package io.anuke.novix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import io.anuke.novix.modules.*;

public class Var{
	public static final int largeImageSize = 100 * 100;
	public static final Color clearcolor = Color.valueOf("12161b");
	
	public static final FileHandle paletteFile = Gdx.files.local("palettes.json");
	public static final FileHandle projectFile = Gdx.files.local("projects.json");
	public static final FileHandle projectDirectory = Gdx.files.absolute(Gdx.files.getExternalStoragePath()).child("NovixProjects");
	
	public static Core core;
	public static Tutorial tutorial;
	public static Input input;
	public static Drawing drawing;
	
	public static Stage stage;
	public static Batch batch;
	
	public static void load(Core core){
		Var.core = core;
		stage = core.stage();
		batch = stage.getBatch();
	}
}
