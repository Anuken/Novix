package io.anuke.novix;

import com.badlogic.gdx.Gdx;

import io.anuke.ucore.modules.ModuleCore;

public class Novix extends ModuleCore {
	
	@Override
	public void init(){
		module(Vars.control = new Control());
		module(Vars.ui = new UI());
		module(Vars.drawing = new Drawing());
	}
	
	public static void log(Object o){
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		String name = e.getFileName().replace(".java", "");
		
		Gdx.app.log("[" + name + "::" + e.getMethodName() + "]", "" + o);
	}
	
}
