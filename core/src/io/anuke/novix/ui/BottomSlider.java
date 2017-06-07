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
	protected LayerDisplay display;
	
	public BottomSlider(){
		setup();
		
		setY(-content.getHeight());
	}
	
	private void addMenus(Table table){
		table.pad(0);
		
		table.defaults().growX().height(50);
		
		table.addButton("Menu", ()->{
			
		}, b->{
			b.addImage("icon-menu").size(32);
			b.getCells().reverse();
		});
		
		menu(table, "Image", "image");
		menu(table, "Filters", "filter");
		menu(table, "Edit", "edit");
		menu(table, "File", "file");
		//
		
		//menu(table, "");
	}
	
	private void button(Table table, String name, String description, String icon, Listenable clicked){
		
	}
	
	private void button(Table table, String name, String description, Listenable clicked){
		
	}
	
	private void menu(Table table, String name, String icon, Object... objects){
		TextButton button = new TextButton(name);
		Image image = new Image(Draw.getPatch("icon-"+icon));
		button.add(image).size(32);
		button.getCells().reverse();
		
		table.add(button);
	}
	
	private void setup(){
		bottom().left();
		
		display = new LayerDisplay();
		
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
			
			Table extra = new Table();
			
			add(extra).left();
			
			extra.add(display).left().pad(10);
			
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
