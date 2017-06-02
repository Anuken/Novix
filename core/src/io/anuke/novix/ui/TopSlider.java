package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.builders.build;
import io.anuke.ucore.scene.builders.button;
import io.anuke.ucore.scene.builders.table;
import io.anuke.ucore.scene.ui.layout.Table;

public class TopSlider extends Table{
	private float duration = 0.18f;
	private Table content;
	private ColorPicker picker;
	
	public TopSlider(){
		setup();
	}
	
	@Override
	public Table padTop(float top){
		super.padTop(top);
		setY(content.getHeight());
		
		return this;
	}
	
	void setup(){
		picker = new ColorPicker();
		top().left();
		
		build.begin(this);
		
		new table("button"){{
			atop();
			aleft();
			get().padTop(64);
			
			picker.padLeft(12);
			picker.padRight(12);
			add(picker).growX().padTop(12);
			
			row();
			
			new button("Palettes...", ()->{
				
			}).growX().height(60).padBottom(12).padLeft(4).padRight(4);
			
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
