package io.anuke.novix.ui;

import static io.anuke.novix.Var.core;
import static io.anuke.ucore.UCore.s;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

import io.anuke.novix.ui.DialogClasses.BaseDialog;

public class SettingsMenu extends BaseDialog{
	
	public SettingsMenu(){
		super("Settings");
		
		addTitleSeperator();
		
		Table table = getContentTable();

		table.add().height(20).row();

		VisTextButton back = new VisTextButton("Back");
		back.add(new Image(VisUI.getSkin().getDrawable("icon-arrow-left"))).size(40 * s).center();

		back.getCells().reverse();
		back.getLabelCell().padRight(40f * s);

		getButtonsTable().add(back).width(Gdx.graphics.getWidth()).height(60 * s);
		setObject(back, false);
	}
	
	public void addScrollSetting(final String name, int min, int max){
		final VisLabel label = new VisLabel(name + ": " + core.prefs.getInteger(name));
		final VisSlider slider = new VisSlider(min, max, 1, false);
		slider.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				label.setText(name + ": " + slider.getValue());
				core.prefs.put(name, (int)slider.getValue());
			}
		});
		DialogClasses.scaleSlider(slider);
		slider.setValue(core.prefs.getInteger(name));
		Table table = getContentTable();
		table.top().left().add(label).align(Align.left);
		table.row();
		table.add(slider).width(200 * s).padBottom(40f*s);
		table.row();
	}
	
	public void addPercentScrollSetting(final String name){
		final VisLabel label = new VisLabel();
		final VisSlider slider = new VisSlider(0f, 2f, 0.01f, false){
			public float getPrefHeight(){
				return 50*s;
			}
		};
		DialogClasses.scaleSlider(slider);
		slider.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				label.setText(name + ": [SKY]" + (int)(slider.getValue()*100) + "%");
				core.prefs.put(convert(name), slider.getValue());
			}
		});
		slider.setValue(core.prefs.getFloat(convert(name)));
		slider.fire(new ChangeListener.ChangeEvent());
		Table table = getContentTable();
		table.top().left().add(label).align(Align.left);
		table.row();
		table.add(slider).width(300 * s);
		table.row();
		table.add(new Separator()).colspan(1).padTop(10*s).padBottom(5*s).growX().row();
	}

	public void addCheckSetting(final String name, boolean value){
		final VisLabel label = new VisLabel(name);
		final VisCheckBox box = new VisCheckBox("", core.prefs.getBoolean(convert(name)));
		
		Table row = new VisTable();
		row.left();
		
		box.getImageStackCell().size(40 * s);
		box.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				core.prefs.put(convert(name), box.isChecked());
				label.setText(name + ": " + (box.isChecked() ? "[CORAL]On" : "[PURPLE]Off"));
			}
		});
		box.fire(new ChangeListener.ChangeEvent());
		
		Table table = getContentTable();
		row.top().left().add(label).align(Align.left);
		row.add(box).padLeft(150*s);
		
		table.add(row).padTop(5*s).padBottom(5*s).align(Align.left);
		table.row();
		table.add(new Separator()).colspan(1).padTop(10*s).growX().row();
	}
	
	public void addButton(String name, ChangeListener listener){
		VisTextButton button = new VisTextButton(name);
		button.addListener(listener);
		Table table = getContentTable();
		table.top().left().add(button).size(200*s, 60*s).padTop(40*s).align(Align.left);
		table.add().align(Align.left);
	}
	
	public VisDialog show(Stage stage){
		super.show(stage);
		int i = Gdx.app.getType() == ApplicationType.Desktop ? 0 : 1;
		setSize(stage.getWidth(), stage.getHeight()-i);
		setY(i);
		return this;
	}
	
	private String convert(String name){
		return name.replace(" ", "").toLowerCase();
	}
	
	public void result(Object o){
		core.prefs.save();
	}
}	
