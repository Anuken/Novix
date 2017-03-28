package io.anuke.novix;

import com.badlogic.gdx.Gdx;

import io.anuke.novix.modules.Core;
import io.anuke.novix.modules.Input;
import io.anuke.novix.modules.Tutorial;
import io.anuke.ucore.modules.ModuleController;

public class Novix extends ModuleController<Novix>{
	
	@Override
	public void init(){
		addModule(Input.class);
		addModule(Core.class);
		addModule(Tutorial.class);
	}
	
	public static void log(Object o){
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		String name = e.getFileName().replace(".java", "");
		
		Gdx.app.log("[" + name + "::" + e.getMethodName() + "]", "" + o);
	}
}
