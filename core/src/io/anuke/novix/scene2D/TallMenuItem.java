package io.anuke.novix.scene2D;

import io.anuke.novix.modules.Core;

import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.MenuItem;

public class TallMenuItem extends MenuItem{

	public TallMenuItem(String text, ChangeListener listener){
		super(text, listener);
	}

	public float getPrefHeight(){
		return super.getPrefHeight() * 1.4f*Core.s;
	}
}
