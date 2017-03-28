package io.anuke.novix.ui;

import static io.anuke.ucore.UCore.s;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.anuke.novix.modules.Core;
import io.anuke.novix.scene2D.*;
import io.anuke.utools.MiscUtils;
import io.anuke.utools.SceneUtils;

public class ColorTable extends VisTable{
	private ColorBox[] boxes;
	private ColorWidget picker;
	private VisTable colortable;
	private PaletteMenu palettemenu;
	private SmoothCollapsibleWidget collapser;
	private CollapseButton collapsebutton;
	private int paletteColor;
	
	public ColorTable(final Core c){
		background("button-window-bg");
		
		palettemenu = new PaletteMenu(c);

		picker = new ColorWidget(){
			public void onColorChanged(){
				if(c.colormenu != null)
				updateSelectedColor(picker.getSelectedColor());
			}
		};
		
		colortable = new VisTable();
		colortable.setName("colortable");
		colortable.setFillParent(true);
		c.stage.addActor(colortable);

		colortable.top().left();
		
		collapsebutton = new CollapseButton();
		collapsebutton.setName("colorcollapsebutton");
		collapsebutton.flip();

		collapser = new SmoothCollapsibleWidget(this);
		c.stage.addActor(collapser);
		
		collapsebutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				if(!collapser.isCollapsed()){
					picker.setSelectedColor(picker.getSelectedColor());
					c.tool().onColorChange(c.selectedColor(), c.drawgrid.canvas);
				}
				collapser.setCollapsed(!collapser.isCollapsed());
				collapsebutton.flip();

				if(!c.toolMenuCollapsed() && event != null){
					c.toolmenu.collapse();
				}
			}
		});
		
		updateColorMenu();

		VisTextButton palettebutton = new VisTextButton("Palettes...");
		palettebutton.setName("palettebutton");
		
		SceneUtils.addIconToButton(palettebutton, new VisImage("icon-palette"), 40*s);
		palettebutton.getLabelCell().expand(false, false);

		palettebutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				openPaletteMenu();
			}
		});
		add().grow().row();
		
		picker.pack();
		Cell<?> cell = add(picker).expand().fill().padBottom(10f * s).padTop(0f).padBottom(20 * s);
		row();
		center().add(palettebutton).align(Align.center).padBottom(10f * s).height(60 * s).growX();
		collapser.setY(Core.i.toolmenu.getButton().getTop());
		collapser.toBack();
		collapser.resetY();
		Vector2 pos = picker.localToStageCoordinates(new Vector2());
		float tpad = (Gdx.graphics.getHeight() - (pos.y + picker.getPrefHeight() + 90 * s)
				- collapsebutton.getPrefHeight() - 65 * s) / 2;

		cell.padTop(Gdx.graphics.getHeight() - (pos.y + picker.getPrefHeight()) - collapsebutton.getPrefHeight());
		cell.padBottom(tpad / 2);
		picker.getPadCell().padTop(tpad / 2);
		picker.invalidateHierarchy();
		pack();
		collapser.setCollapsed(true, false);
		setupBoxColors();
	}
	
	public float getBoxBorder(){
		return boxes[0].getBorderThickness();
	}
	
	public void collapse(){
		((ClickListener) collapsebutton.getListeners().get(2)).clicked(null, 0, 0);
	}
	
	public boolean collapsed(){
		return collapser.isCollapsed();
	}
	
	public void openPaletteMenu(){
		palettemenu.update();
		palettemenu.show(Core.i.stage);
	}
	
	public void setSelectedColor(int color){
		((ClickListener)Core.i.colormenu.boxes[color].getListeners().get(0)).clicked(null, 0, 0);
	}
	
	public void addRecentColor(Color color){
		picker.addRecentColor(color);
	}
	
	public Color getSelectedColor(){
		return Core.i.getCurrentPalette().colors[paletteColor];
	}
	
	public void resetPaletteColor(){
		paletteColor = 0;
	}
	
	public ColorBox[] getRecentColors(){
		return picker.getRecentColors();
	}
	
	public int getPaletteColor(){
		return paletteColor;
	}
	
	public ColorBox getSelectedBox(){
		return boxes[paletteColor];
	}
	
	public void updateColorMenu(){
		colortable.clear();

		int maxcolorsize = (int) (65 * s);
		int mincolorsize = (int) (30 * s);

		int colorsize = Gdx.graphics.getWidth() / Core.i.getCurrentPalette().size() - MiscUtils.densityScale(3);

		int perow = 0; // colors per row

		colorsize = Math.min(maxcolorsize, colorsize);

		if(colorsize < mincolorsize){
			colorsize = mincolorsize;
			perow = Gdx.graphics.getWidth() / colorsize;
		}

		colortable.add(collapsebutton).expandX().fillX()
				.colspan((perow == 0 ? Core.i.getCurrentPalette().size() : perow) + 2).height(50f * s);
		collapsebutton.setZIndex(collapser.getZIndex() + 10);

		colortable.row();

		colortable.add().growX();

		boxes = new ColorBox[Core.i.getCurrentPalette().size()];

		for(int i = 0; i < Core.i.getCurrentPalette().size(); i++){
			final int index = i;
			final ColorBox box = new ColorBox();

			boxes[i] = box;
			colortable.add(box).size(colorsize);

			box.setColor(Core.i.getCurrentPalette().colors[i]);

			box.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					picker.addRecentColor(boxes[paletteColor].getColor().cpy());
					boxes[paletteColor].selected = false;
					paletteColor = index;
					Core.i.prefs.put("palettecolor", paletteColor);
					box.selected = true;
					box.toFront();
					setSelectedColor(box.getColor());
				}
			});

			if(perow != 0 && i % perow == perow - 1){
				colortable.add().growX();
				colortable.row();
				colortable.add().growX();
			}
		}

		if(perow == 0)
			colortable.add().growX();
	}
	
	public void updateSelectedColor(Color color){
		boxes[paletteColor].setColor(color.cpy());
		Core.i.getCurrentPalette().colors[paletteColor] = color.cpy();
		Core.i.toolmenu.updateColor(color.cpy());
		Core.i.updateToolColor();
	}

	public void setSelectedColor(Color color){
		updateSelectedColor(color);
		picker.setSelectedColor(color);
		Core.i.updateToolColor();
	}

	public void setupBoxColors(){
		paletteColor = Core.i.prefs.getInteger("palettecolor", 0);
		for(ColorBox box : boxes)
			box.getColor().a = 1f;

		if(paletteColor > boxes.length)
			paletteColor = 0;

		picker.setRecentColors(boxes);
		boxes[paletteColor].selected = true;
		boxes[paletteColor].toFront();
		picker.setSelectedColor(Core.i.getCurrentPalette().colors[paletteColor]);
	}
	
	public float getPrefWidth(){
		return Gdx.graphics.getWidth();
	}
}
