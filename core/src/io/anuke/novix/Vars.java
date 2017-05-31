package io.anuke.novix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;

public class Vars{
	public static Control control;
	public static UI ui;
	public static Drawing drawing;
	
	public static final int largeImageSize = 100 * 100;
	public static final Color clearcolor = Color.valueOf("12161b");
	public static final Json json = new Json();
	
	public static final FileHandle paletteFile = Gdx.files.local("palettes.json");
	public static final FileHandle projectFile = Gdx.files.local("projects.json");
	public static final FileHandle projectDirectory = Gdx.files.absolute(Gdx.files.getExternalStoragePath()).child("NovixProjects");
}
