package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.gdxutils.graphics.Textures;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;

public class CollapseButton extends VisImageButton{
	Drawable up = new TextureRegionDrawable(new TextureRegion(Textures.get("icon-up")));
	Drawable down = new TextureRegionDrawable(new TextureRegion(Textures.get("icon-down")));

	public CollapseButton(){
		super("default");
		setStyle(new VisImageButtonStyle(getStyle()));
		this.getImageCell().size(getHeight());
		getStyle().up = VisUI.getSkin().getDrawable("button");
		set(up);
	}

	public void flip(){
		if(getStyle().imageUp == up){
			set(down);
		}else{
			set(up);
		}
	}

	private void set(Drawable d){
		getStyle().imageUp = d;
	}
}
