package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

import io.anuke.novix.Vars;
import io.anuke.novix.element.ColorBar;
import io.anuke.novix.internal.NovixEvent.AlphaChange;
import io.anuke.ucore.core.Events;
import io.anuke.ucore.core.Settings;
import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.builders.build;
import io.anuke.ucore.scene.builders.table;
import io.anuke.ucore.scene.ui.ImageButton;
import io.anuke.ucore.scene.ui.Slider;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.scene.utils.Elements;

public class BottomSlider extends Table{
	private float duration = 0.18f;
	private Table content;
	private Slider sizeslider;
	private ColorBar alphabar;
	private ImageButton grid, drawmode;
	
	private LayerDisplay display;
	
	public BottomSlider(){
		setup();
		
		setY(-content.getHeight());
	}
	
	private void setup(){
		bottom().left();
		
		display = new LayerDisplay();
		sizeslider = new Slider(1, 10, 1, false);
		alphabar = new ColorBar(Color.CLEAR, Color.YELLOW);
		
		alphabar.setValue(Settings.getFloat("alpha"));
		Events.fire(AlphaChange.class, alphabar.getValue());
		
		sizeslider.setValue(Settings.getInt("brushsize"));
		
		sizeslider.changed(()->{
			Settings.putInt("brushsize", (int)sizeslider.getValue());
			Settings.save();
		});
		
		alphabar.changed(()->{
			Settings.putFloat("alpha", alphabar.getValue());
			Events.fire(AlphaChange.class, alphabar.getValue());
		});
		
		alphabar.update(()->{
			alphabar.setColors(Color.CLEAR, Vars.ui.top().getSelectedColor());
		});
		
		build.begin(this);
		
		new table("button"){{
			atop();
			aleft();
			
			Table extra = new Table();
			extra.pad(10);
			
			add(extra).left().grow();
			
			extra.add(()->"Brush size: " + (int)sizeslider.getValue()).left();
			extra.row();
			extra.add(sizeslider).growX();
			extra.row();
			extra.add(()->"Alpha: " + String.format("%.2f", alphabar.getValue())).left();
			extra.row();
			extra.add(alphabar).height(40).padTop(10).padBottom(8).padRight(4).growX();
			
			Table buttons = new Table();
			add(buttons);
			
			grid = Elements.newImageButton("toggle", "icon-grid", 48, ()->{
				Settings.putBool("grid", grid.isChecked());
			});
			
			drawmode = Elements.newImageButton("toggle", "icon-cursor", 48, ()->{
				Settings.putBool("cursormode", drawmode.isChecked());
			});
			
			grid.setChecked(Settings.getBool("grid"));
			drawmode.setChecked(Settings.getBool("cursormode"));
			
			buttons.add(grid).size(74).pad(2);
			buttons.row();
			buttons.add(drawmode).size(74).pad(2);
			
			content = get();
		}}.expandX().fillX();
		
		build.end();
		
		pack();
	}
	
	void slide(boolean up){
		addAction(Actions.moveBy(0, up ? content.getHeight() : -content.getHeight(), duration, Interpolation.pow5Out));
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		
		setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
}
