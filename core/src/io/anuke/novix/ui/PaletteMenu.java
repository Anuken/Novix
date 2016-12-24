package io.anuke.novix.ui;


import static io.anuke.ucore.UCore.s;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;

import io.anuke.novix.graphics.Palette;
import io.anuke.novix.modules.Core;
import io.anuke.novix.scene2D.PaletteWidget;
import io.anuke.novix.scene2D.TallMenuItem;
import io.anuke.novix.ui.DialogClasses.BaseDialog;
import io.anuke.utils.SceneUtils;
import io.anuke.utils.SceneUtils.TextFieldEmptyListener;

public class PaletteMenu extends BaseDialog{
	private Core main;
	private PaletteWidget currentWidget = null;

	public PaletteMenu(Core main){
		super("Palettes");
		this.main = main;
		setMovable(false);
		addCloseButton();
		setStage(main.stage);
		
	}

	public void update(){
		float scrolly = getContentTable().getChildren().size == 0 ? 0 : ((ScrollPane)getContentTable().getChildren().first()).getScrollPercentY();

		getContentTable().clearChildren();
		getButtonsTable().clearChildren();

		class PaletteListener extends ClickListener{
			PaletteWidget widget;
			Palette palette;

			public PaletteListener(PaletteWidget palette){
				widget = palette;
				this.palette = widget.palette;
			}

			public void clicked(InputEvent event, float x, float y){
				PopupMenu menu = new PopupMenu();
				menu.addItem(new TallMenuItem("Resize", new ChangeListener(){
					public void changed(ChangeEvent event, Actor actor){
						new DialogClasses.NumberInputDialog("Resize Palette", palette.size() + "", "Size: "){
							public void result(int size){
								Color[] newcolors = new Color[size];

								Arrays.fill(newcolors, Color.WHITE.cpy());

								for(int i = 0;i < size && i < palette.size();i ++){
									newcolors[i] = palette.colors[i];
								}

								palette.colors = newcolors;

								update();
								main.updateColorMenu();
							}
						}.show(getStage());
					}
				}));
				menu.addItem(new TallMenuItem("Rename", new ChangeListener(){
					public void changed(ChangeEvent event, Actor actor){
						new DialogClasses.InputDialog("Rename Palette", palette.name, "Name: "){
							public void result(String string){
								palette.name = string;
								update();
							}
						}.show(getStage());
					}
				}));
				menu.addItem(new TallMenuItem("Delete", new ChangeListener(){
					public void changed(ChangeEvent event, Actor actor){
						if(widget != currentWidget){
							new DialogClasses.ConfirmDialog("Delete Palette", "Are you sure you want\nto delete this palette?"){
								public void result(){
									main.palettemanager.removePalette(palette);
									update();
								}
							}.show(getStage());
						}else{
							DialogClasses.showInfo(getStage(), "You cannot delete the\npalette you are using!");
						}
					}
				}));

				Vector2 coords = widget.extrabutton.localToStageCoordinates(new Vector2());
				menu.showMenu(getStage(), coords.x - menu.getWidth() + widget.extrabutton.getWidth(), coords.y);
			}
		}

		VisTable palettetable = new VisTable();

		final VisScrollPane pane = new VisScrollPane(palettetable);
		pane.setFadeScrollBars(false);
		pane.setOverscroll(false, false);

		getContentTable().add(pane).left().grow().maxHeight(Gdx.graphics.getHeight() / 2);

		for(final Palette palette : main.palettemanager.getPalettes()){
			final PaletteWidget widget = new PaletteWidget(palette, palette == main.getCurrentPalette());
			if(main.getCurrentPalette() == palette) currentWidget = widget;

			widget.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					//delay action to make sure the isOver() check works properly
					
					
					if( !widget.extrabutton.isPressed() && currentWidget != widget){

						currentWidget.setSelected(false);
						currentWidget = widget;

						widget.setSelected(true);
						main.setPalette(palette);
					}

				}
			});

			widget.addExtraButtonListener(new PaletteListener(widget));

			palettetable.add(widget).padBottom(6 * s);
			palettetable.row();
		}

		VisTextButton backbutton = new VisTextButton("Back");

		backbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				hide();
			}
		});

		VisTextButton addpalettebutton = new VisTextButton("New Palette");

		addpalettebutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				new DialogClasses.InputDialog("New Palette", "", "Name:"){
					protected VisTextField numberfield;
					protected VisCheckBox box;

					{
						numberfield = new VisTextField("8");
						numberfield.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
						box = new VisCheckBox("Auto-Generate", main.prefs.getBoolean("genpalettes", true));
						box.getImageStackCell().size(40*s);
						
						getContentTable().center();
						getContentTable().row();

						getContentTable().add(new VisLabel("Size:")).padTop(0f).padBottom(20f * s);
						getContentTable().add(numberfield).pad(20 * s).padBottom(20*s).padLeft(0f).padTop(0).size(160*s, 40*s).row();
						
						getContentTable().add(box).colspan(2).padBottom(25*s);

						new TextFieldEmptyListener(ok, textfield, numberfield);
					}

					public void result(String string){
						if(Integer.parseInt(numberfield.getText()) > 32){
							DialogClasses.showInfo(getStage(), "A palette may not have\nmore than 32 colors.");
							return;
						}
						main.palettemanager.addPalette(new Palette(string, main.palettemanager.generatePaletteID(), Integer.parseInt(numberfield.getText()), box.isChecked()));
						update();
						main.prefs.put("genpalettes", box.isChecked());
					}

				}.show(getStage());
			}
		});

		SceneUtils.addIconToButton(addpalettebutton, new Image(VisUI.getSkin().getDrawable("icon-plus")), 40*s);
		SceneUtils.addIconToButton(backbutton, new Image(VisUI.getSkin().getDrawable("icon-arrow-left")), 40*s);

		getButtonsTable().add(backbutton).size(150 * s, 50 * s).padRight(s);
		getButtonsTable().add(addpalettebutton).size(200 * s, 50 * s);

		pack();

		main.stage.setScrollFocus(pane);

		pane.setSmoothScrolling(false);
		pane.setScrollPercentY(scrolly);

		pane.addAction(Actions.sequence(Actions.delay(0.01f), new Action(){
			@Override
			public boolean act(float delta){
				pane.setSmoothScrolling(true);
				return true;
			}
		}));

		centerWindow();
	}

	public VisDialog show(Stage stage){
		super.show(stage);
		stage.setScrollFocus(getContentTable().getChildren().first());
		return this;
	}
}
