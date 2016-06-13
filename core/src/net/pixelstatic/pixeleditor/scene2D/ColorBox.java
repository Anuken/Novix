package net.pixelstatic.pixeleditor.scene2D;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.kotcrab.vis.ui.VisUI;

import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.graphics.Textures;

public class ColorBox extends Widget{

	public void draw(Batch batch, float parentAlpha){
		int grayborder = MiscUtils.densityScale(3);
		float border = 0;

		if(selected()) border = (int)getWidth()/7;

		batch.setColor( !selected() ? Color.DARK_GRAY : Color.CORAL);
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX() - border, getY() - border, getWidth() + border * 2, getHeight() + border * 2);
		
		batch.setColor(Color.WHITE);
		
		batch.draw(Textures.get("alpha"), getX() + grayborder, getY() + grayborder, getWidth() - grayborder * 2, getHeight() - grayborder * 2, 0, 0, 2, 2);
		
		batch.setColor(getColor());
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX() + grayborder, getY() + grayborder, getWidth() - grayborder * 2, getHeight() - grayborder * 2);
	}

	public boolean selected(){
		return GUI.gui.colorbox == this;
	}
}
