package io.anuke.novix.element;

import com.badlogic.gdx.math.Interpolation;

import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.function.ActionProvider;
import io.anuke.ucore.function.Listenable;
import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.event.Touchable;
import io.anuke.ucore.scene.ui.ImageButton;
import io.anuke.ucore.scene.ui.layout.Table;

public class PopupDialog extends Table{
	private Table content;
	
	static ActionProvider showAction = ()->{
		return Actions.sequence(Actions.alpha(0f), Actions.alpha(1f, 0.3f, Interpolation.fade));
	};
	
	static ActionProvider hideAction = ()->{
		return Actions.sequence(Actions.alpha(0f, 0.3f, Interpolation.fade));
	};
	
	public PopupDialog(){
		setTouchable(Touchable.enabled);
		setFillParent(true);
		
		tapped(()->{
			hide();
		});
		
		content = new Table();
		
		add(content);
	}
	
	public PopupDialog addItem(String text, String icon, float isize, Listenable clicked){
		
		ImageButton button = new ImageButton(icon);
		button.resizeImage(isize);
		button.add(text).left().bottom();
		button.left();
		button.getImageCell().left().padRight(4);
		content.add(button);
		content.row();
		
		return this;
	}
	
	public Table content(){
		return content;
	}
	
	public void show(){
		pack();
		addAction(showAction.get());
		DrawContext.scene.add(this);
	}
	
	public void hide(){
		addAction(Actions.sequence(hideAction.get(), Actions.removeActor()));
	}
}
