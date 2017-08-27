package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

import io.anuke.novix.element.FloatingMenu;
import io.anuke.ucore.function.Listenable;
import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.builders.build;
import io.anuke.ucore.scene.builders.table;
import io.anuke.ucore.scene.ui.Button;
import io.anuke.ucore.scene.ui.Slider;
import io.anuke.ucore.scene.ui.layout.Table;

public class BottomSlider extends Table{
	private float duration = 0.18f;
	private Table content;
	private Slider sizeslider;
	//private Bar alphabar;
	
	protected LayerDisplay display;
	
	public BottomSlider(){
		setup();
		
		setY(-content.getHeight());
	}
	
	private void setup(){
		bottom().left();
		
		display = new LayerDisplay();
		sizeslider = new Slider(0, 10, 1, false);
		
		build.begin(this);
		
		new table("button"){{
			atop();
			aleft();
			
			Table extra = new Table();
			extra.pad(8);
			
			add(extra).left().grow();
			
			extra.add(()->"Brush size: " + (int)sizeslider.getValue()).left();
			extra.row();
			extra.add(sizeslider).growX();
			
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
	
	class MenuBuilder{
		private FloatingMenu menu;
		
		public MenuBuilder(String title, Button bind){
			menu = new FloatingMenu(title);
			
			bind.clicked(()->{
				menu.show();
			});
		}
		
		public MenuBuilder add(String name, String icon, String text, Listenable clicked){
			menu.addMenuItem(name, icon, text, ()->{
				clicked.listen();
				menu.hide();
			});
			return this;
		}
	}
}
