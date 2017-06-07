package io.anuke.novix.ui;

import io.anuke.novix.Vars;
import io.anuke.novix.element.LayerImage;
import io.anuke.novix.internal.Layer;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.scene.ui.ImageButton;
import io.anuke.ucore.scene.ui.layout.Stack;
import io.anuke.ucore.scene.ui.layout.Table;

public class LayerDisplay extends Table{
	Stack images;
	Table tabs;
	int selected = 0;
	
	public LayerDisplay(){
		images = new Stack();
		tabs = new Table();
		
		left();
		
		add(images).size(160);
		add(tabs);
	}
	
	public void updateImage(){
		images.clear();
		tabs.clear();
		
		Layer[] layers = Vars.drawing.getLayers();
		
		for(int i = 0 ; i < layers.length; i ++){
			LayerImage image = new LayerImage(layers[i].getTexture());
			images.add(image);
		}
		
		tabs.top();
		
		for(int i = 0; i < layers.length; i ++){
			int index = i;
			
			
			tabs.defaults().size(50).top();
			
			tabs.addButton("asdf", ()->{
				Vars.drawing.setLayer(index);
			});
			
			ImageButton button = new ImageButton("icon-invisible", "toggle");
			button.getStyle().imageChecked = DrawContext.skin.getDrawable("icon-visible");
			button.setChecked(true);
			button.clicked(()->{
				Vars.drawing.getLayers()[index].visible = button.isChecked();
			});
			
			tabs.add(button);
			
			tabs.row();
		}
	}
}
