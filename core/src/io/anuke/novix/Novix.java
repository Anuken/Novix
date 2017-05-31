package io.anuke.novix;

import com.badlogic.gdx.Gdx;

import io.anuke.ucore.modules.Core;

public class Novix extends Core {
	
	@Override
	public void init(){
		add(Vars.control = new Control());
		add(Vars.ui = new UI());
		add(Vars.drawing = new Drawing());
	}
	
	public static void log(Object o){
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		String name = e.getFileName().replace(".java", "");
		
		Gdx.app.log("[" + name + "::" + e.getMethodName() + "]", "" + o);
	}
	
}
