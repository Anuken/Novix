package io.anuke.novix.ui;

import io.anuke.novix.Vars;
import io.anuke.novix.element.LayerImage;
import io.anuke.novix.internal.Layer;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.scene.ui.ButtonGroup;
import io.anuke.ucore.scene.ui.ImageButton;
import io.anuke.ucore.scene.ui.TextButton;
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
		
		ButtonGroup group = new ButtonGroup();
		
		for(int i = 0; i < layers.length; i ++){
			int index = i;
			
			
			//tabs.defaults().size(50).top();
			
			TextButton lbutton = new TextButton("asdf", "toggle");
			lbutton.clicked(()->{
				Vars.drawing.setLayer(index);
			});
			
			group.add(lbutton);
			
			tabs.add(lbutton).height(50);
			
			lbutton.setChecked(Vars.drawing.getLayer(i) == Vars.drawing.getLayer());
			
			ImageButton visible = new ImageButton("icon-invisible", "toggle");
			visible.resizeImage(30);
			
			visible.getStyle().imageChecked = DrawContext.skin.getDrawable("icon-visible");
			visible.setChecked(true);
			visible.clicked(()->{
				Vars.drawing.getLayers()[index].visible = visible.isChecked();
			});
			
			tabs.add(visible).size(50);
			
			tabs.row();
		}
		
	}
}
