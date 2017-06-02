package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.builders.build;
import io.anuke.ucore.scene.builders.button;
import io.anuke.ucore.scene.builders.table;
import io.anuke.ucore.scene.ui.layout.Table;

public class TopSlider extends Table{
	float duration = 0.18f;
	Table content;
	
	public TopSlider(){
		setup();
	}
	
	@Override
	public Table padTop(float top){
		super.padTop(top);
		setY(Gdx.graphics.getHeight()-top*2);
		
		return this;
	}
	
	void setup(){
		bottom().left();
		
		build.begin(this);
		
		new table("button"){{
			atop();
			aleft();
			
			new button("asdf", ()->{
				
			}).fillX().padTop(100).padBottom(100);
			
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
