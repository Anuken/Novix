package net.pixelstatic.pixeleditor.scene2D;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;

public class CollapseButton extends VisImageButton{
	Drawable up = VisUI.getSkin().getDrawable("icon-up");
	Drawable down = VisUI.getSkin().getDrawable("icon-down");

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
