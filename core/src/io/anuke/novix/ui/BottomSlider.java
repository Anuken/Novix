package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.function.Listenable;
import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.builders.build;
import io.anuke.ucore.scene.builders.table;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.TextButton;
import io.anuke.ucore.scene.ui.layout.Table;

public class BottomSlider extends Table{
	private float duration = 0.18f;
	private Table content;
	
	public BottomSlider(){
		setup();
		
		setY(-content.getHeight());
	}
	
	private void addMenus(Table table){
		table.pad(0);
		
		table.defaults().growX();
		
		table.addButton("one", ()->{
			
		});
		
		table.addButton("two", ()->{
			
		});
		
		table.addButton("three", ()->{
			
		});
	}
	
	private void button(Table table, String name, String description, String icon, Listenable clicked){
		
	}
	
	private void button(Table table, String name, String description, Listenable clicked){
		
	}
	
	private void menu(Table table, String name, String icon, Object... objects){
		TextButton button = new TextButton(name);
		Image image = new Image(Draw.getPatch(name));
		button.add(image).size(48);
		button.getCells().reverse();
		
		table.add(button);
	}
	
	private void setup(){
		bottom().left();
		
		build.begin(this);
		
		new table("button"){{
			atop();
			aleft();
			
			Table table = new Table();
			table.background("button");
			addMenus(table);
			
			add(table).padTop(-get().getPadTop()).padLeft(-get().getPadLeft()).growX()
			.padRight(-get().getPadRight());
			
			row();
			
			add().size(200);
			
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
