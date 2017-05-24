package io.anuke.novix;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import io.anuke.novix.modules.*;

public class Var{
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
