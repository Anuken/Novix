package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;

import io.anuke.novix.Vars;
import io.anuke.novix.internal.Tool;
import io.anuke.ucore.scene.ui.ButtonGroup;
import io.anuke.ucore.scene.ui.ImageButton;
import io.anuke.ucore.scene.ui.layout.Table;

public class BottomMenu extends Table{
	FlipButton flip;
	boolean open = true;

	public BottomMenu(){
		setFillParent(true);
		setup();
	}
	
	private void setup(){
		bottom().left();
		
		flip = new FlipButton(true);
		
		add(flip).colspan(Tool.values().length).height(60).growX();
		
		row();
		
		float size = Gdx.graphics.getWidth() / (Tool.values().length);
		
		ButtonGroup<ImageButton> group = new ButtonGroup<>();
		
		for(int i = 0 ; i < Tool.values().length; i ++){
			Tool tool = Tool.values()[i];
			ImageButton button = new ImageButton("icon-" + tool.name(), "toggle");
			button.resizeImage(48);
			button.clicked(()->{
				Vars.control.setTool(tool);
			});
			group.add(button);
			add(button).size(size+0.5f).expandX();
		}
	}
	
	public boolean open(){
		return open;
	}
}
