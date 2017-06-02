package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.builders.build;
import io.anuke.ucore.scene.builders.table;
import io.anuke.ucore.scene.ui.layout.Table;

public class BottomSlider extends Table{
	private float duration = 0.18f;
	private Table content;
	
	public BottomSlider(){
		setup();
		
		setY(-content.getHeight());
	}
	
	void setup(){
		bottom().left();
		
		build.begin(this);
		
		new table("button"){{
			atop();
			aleft();
			
			
			
			content = get();
		}}.expandX().fillX();
		
		build.end();
		
		
		pack();
	}
	
	void slide(boolean up){
		addAction(Actions.moveBy(0, up ? content.getHeight() : -content.getHeight(), duration, Interpolation.fade));
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		
		setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
}
