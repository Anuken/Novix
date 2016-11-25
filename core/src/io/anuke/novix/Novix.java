package io.anuke.novix;

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
}
