package io.anuke.novix.ui;

import static io.anuke.ucore.UCore.s;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;

import io.anuke.novix.Var;
import io.anuke.novix.ui.DialogClasses.MenuDialog;

public class TutorialDialog extends MenuDialog{
	
	public TutorialDialog(){
		super("Tutorial");
		
		setup();
	}
	
	public void result(){
		Var.tutorial.begin();
	}
	
	private void setup(){
		VisLabel header = new VisLabel("Welcome to Novix!");
		LabelStyle style = new LabelStyle(header.getStyle());
		style.font = VisUI.getSkin().getFont("large-font");
		style.fontColor = Color.CORAL;
		header.setStyle(style);

		getContentTable().add(header).pad(20 * s).row();

		VisImage image = new VisImage("icon");

		getContentTable().add(image).size(image.getPrefWidth() * s, image.getPrefHeight() * s).row();

		getContentTable().add("Would you like to take the tutorial?").pad(20 * s);
		
		addTitleSeperator();
	}
}
