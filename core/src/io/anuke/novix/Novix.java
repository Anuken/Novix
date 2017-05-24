package io.anuke.novix;

import com.badlogic.gdx.Gdx;

import io.anuke.novix.modules.*;
import io.anuke.ucore.modules.ModuleController;

public class Novix extends ModuleController<Novix>{
	
	@Override
	public void init(){
		addModule(Input.class);
		addModule(Drawing.class);
		addModule(Core.class);
		addModule(Tutorial.class);
		
		Var.input = getModule(Input.class);
		Var.tutorial = getModule(Tutorial.class);
		Var.drawing = getModule(Drawing.class);
	}
	
	public static void log(Object o){
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		String name = e.getFileName().replace(".java", "");
		
		Gdx.app.log("[" + name + "::" + e.getMethodName() + "]", "" + o);
	}
}
