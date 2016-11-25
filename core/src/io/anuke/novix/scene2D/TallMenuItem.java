package io.anuke.novix.scene2D;


import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.MenuItem;

import io.anuke.ucore.UCore;

public class TallMenuItem extends MenuItem{

	public TallMenuItem(String text, ChangeListener listener){
		super(text, listener);
	}

	public float getPrefHeight(){
		return super.getPrefHeight() * 1.4f*UCore.s;
	}
}
