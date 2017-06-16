package io.anuke.novix.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.function.ActionProvider;
import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.event.Touchable;
import io.anuke.ucore.scene.ui.layout.Table;

public class FloatingMenu extends Table{
	private Table title, content;
	
	static ActionProvider showAction = ()->{
		return Actions.sequence(Actions.alpha(0f), Actions.alpha(1f, 0.3f, Interpolation.fade));
	};
	
	static ActionProvider hideAction = ()->{
		return Actions.sequence(Actions.alpha(0f, 0.3f, Interpolation.fade));
	};
	
	public FloatingMenu(String text){
		setFillParent(true);
		setTouchable(Touchable.enabled);
		background(DrawContext.skin.newDrawable("white", new Color(0, 0, 0, 0.6f)));
		
		title = new Table();
		
		title.add(text).center();
		
		add(title).growX();
		
		row();
		
		add(content).grow();
	}
	
	public Table content(){
		return content;
	}
	
	public Table title(){
		return title;
	}
	
	public void show(){
		addAction(showAction.get());
		DrawContext.scene.add(this);
	}
	
	public void hide(){
		addAction(Actions.sequence(hideAction.get(), Actions.removeActor()));
	}
}
