package io.anuke.novix.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.math.Interpolation;

import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.function.ActionProvider;
import io.anuke.ucore.function.Listenable;
import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.event.Touchable;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.TextButton;
import io.anuke.ucore.scene.ui.layout.Table;

public class FloatingMenu extends Table{
	protected Table title, content;
	
	static ActionProvider showAction = ()->{
		return Actions.sequence(Actions.alpha(0f), Actions.alpha(1f, 0.3f, Interpolation.fade));
	};
	
	static ActionProvider hideAction = ()->{
		return Actions.sequence(Actions.alpha(0f, 0.3f, Interpolation.fade));
	};
	
	public FloatingMenu(String text){
		this(true, text);
	}
	
	public FloatingMenu(boolean backbutton, String text){
		setFillParent(true);
		setTouchable(Touchable.enabled);
		background(DrawContext.skin.newDrawable("white", new Color(0, 0, 0, 0.9f)));
		
		content = new Table();
		//content.background("button");
		
		title = new Table();
		title.background("button").pad(8);
		
		title.add(text, Colors.get("title")).center();
		title.row();
		add(title).growX();
		row();
		add(content).grow();
		row();
		
		if(backbutton)
		addCenteredImageTextButton("Back", "icon-arrow-left", 40, ()->{
			hide();
		}).growX().height(62).pad(10);
	}
	
	public Table content(){
		return content;
	}
	
	public Table title(){
		return title;
	}
	
	public void addMenuItem(String text, String icon, String detail, Listenable clicked){
		TextButton button = new TextButton(text, "invisible");
		button.clicked(clicked);
		
		button.left();
		
		Image image = new Image(icon);
		button.clearChildren();
		button.add(image).left().size(40).padRight(6);
		
		Table table = new Table();
		table.add(button.getLabel()).left();
		table.row();
		table.add(detail, Colors.get("shading"));
		
		button.add(table);
		
		button.pad(16);
		
		content.add(button).growX();
		content.row();
	}
	
	public void show(){
		addAction(showAction.get());
		DrawContext.scene.add(this);
	}
	
	public void hide(){
		addAction(Actions.sequence(hideAction.get(), Actions.removeActor()));
	}
}
