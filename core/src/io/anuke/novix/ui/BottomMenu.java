package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Colors;

import io.anuke.novix.Vars;
import io.anuke.novix.internal.Tool;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.scene.ui.ButtonGroup;
import io.anuke.ucore.scene.ui.ImageButton;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.scene.utils.Elements;

public class BottomMenu extends Table{
	private FlipButton flip;
	private BottomSlider slider;

	public BottomMenu(){
		setFillParent(true);
		setup();
	}
	
	public void toggle(){
		flip.flip();
		slider.slide(!flip.flipped());
	}
	
	public void updateLayerDisplay(){
		slider.display.updateImage();
	}
	
	private void setup(){
		slider = new BottomSlider();
		slider.setVisible(()-> isVisible());
		DrawContext.scene.add(slider);
		
		bottom().left();
		
		int amount = Tool.values().length + 3;
		
		flip = new FlipButton(true);
		flip.getImage().setColor(Colors.get("accent"));
		
		flip.clicked(()->{
			slider.slide(!flip.flipped());
			
			if(Vars.ui.top().open()){
				Vars.ui.top().toggle();
			}
		});
		
		//add(flip).colspan(amount).height(60).growX();
		
		row();
		
		float size = Gdx.graphics.getWidth() / (amount);
		
		ButtonGroup<ImageButton> group = new ButtonGroup<>();
		
		defaults().expandX().size(size+0.5f);
		
		for(int i = 0 ; i < Tool.values().length; i ++){
			Tool tool = Tool.values()[i];
			ImageButton button = new ImageButton("icon-" + tool.name(), "toggle");
			button.resizeImage(48);
			button.clicked(()->{
				Vars.control.setTool(tool);
			});
			group.add(button);
			add(button);
		}
		
		add(Elements.newImageButton("icon-undo", 48, ()->{
			Vars.drawing.undo();
		}));
		
		add(Elements.newImageButton("icon-redo", 48, ()->{
			Vars.drawing.redo();
		}));
		
		add(flip);
		
		//add(Elements.newImageButton("icon-up", 48, Colors.get("title"), ()->{
			
		//}));
		
		pack();
		slider.padBottom(getHeight());
	}
	
	public boolean open(){
		return !flip.flipped();
	}
}
