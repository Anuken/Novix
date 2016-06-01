package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.utils.graphics.Textures;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.kotcrab.vis.ui.VisUI;

public class ColorBox extends Widget{

	public void draw(Batch batch, float parentAlpha){
		int a = 3;
		int b = 0;

		if(selected()) b = 4;

		batch.setColor( !selected() ? Color.DARK_GRAY : Color.CORAL);
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX() - b, getY() - b, getWidth() + b * 2, getHeight() + b * 2);
		
		batch.setColor(Color.WHITE);
		
		batch.draw(Textures.get("alpha"), getX() + a, getY() + a, getWidth() - a * 2, getHeight() - a * 2, 0, 0, 2, 2);
		
		batch.setColor(getColor());
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX() + a, getY() + a, getWidth() - a * 2, getHeight() - a * 2);
	}

	public boolean selected(){
		return GUI.gui.selected == this;
	}
}
