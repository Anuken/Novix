package net.pixelstatic.pixeleditor.scene2D;

import static net.pixelstatic.pixeleditor.modules.Core.s;
import net.pixelstatic.gdxutils.graphics.PixmapUtils;
import net.pixelstatic.pixeleditor.graphics.Filter;
import net.pixelstatic.pixeleditor.modules.Core;
import net.pixelstatic.pixeleditor.tools.PixelCanvas;
import net.pixelstatic.pixeleditor.tools.Project;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.scene2D.AndroidColorPicker;
import net.pixelstatic.utils.scene2D.AndroidFileChooser;
import net.pixelstatic.utils.scene2D.ColorBox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;

public class DialogClasses{

	public static class SizeDialog extends MenuDialog{
		VisTextField widthfield, heightfield;

		public SizeDialog(String title){
			super(title);

			widthfield = new VisTextField();
			heightfield = new VisTextField();
			widthfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());
			heightfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());

			widthfield.setText((Core.i.drawgrid.canvas.width()) + "");
			heightfield.setText(Core.i.drawgrid.canvas.height() + "");

			float twidth = 160 * s, theight = 40 * s;

			getContentTable().add(new VisLabel("Width: ")).padLeft(50 * s).padTop(40 * s);
			getContentTable().add(widthfield).size(twidth, theight).padRight(50 * s).padTop(40 * s);

			getContentTable().row();

			getContentTable().add(new VisLabel("Height: ")).padLeft(50 * s).padTop(40 * s).padBottom(40f * s);
			getContentTable().add(heightfield).size(twidth, theight).padRight(50 * s).padTop(40 * s).padBottom(40f * s);

			getContentTable().row();
			
			ChangeListener listener = new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					ok.setDisabled(widthfield.getText().replace("0", "").isEmpty() || heightfield.getText().replace("0", "").isEmpty());
				}
			};
			widthfield.addListener(listener);
			heightfield.addListener(listener);

		}

		public void result(){
			try{
				int width = Integer.parseInt(widthfield.getText());
				int height = Integer.parseInt(heightfield.getText());
				result(width, height);
			}catch(Exception e){
				e.printStackTrace();
				showError(getStage(), "Image error!", e);
			}
		}

		public void result(int width, int height){

		}

	}

	public static class NamedSizeDialog extends MenuDialog{
		VisTextField widthfield, heightfield, namefield;

		public NamedSizeDialog(String title){
			super(title);

			widthfield = new VisTextField();
			heightfield = new VisTextField();
			namefield = new VisTextField();

			widthfield.setText((Core.i.drawgrid.canvas.width()) + "");
			heightfield.setText(Core.i.drawgrid.canvas.height() + "");

			float twidth = 160 * s, theight = 40 * s;

			getContentTable().add(new VisLabel("Name: ")).padLeft(50 * s).padTop(40 * s);
			getContentTable().add(namefield).size(twidth, theight).padRight(50 * s).padTop(40 * s);

			getContentTable().row();

			getContentTable().add(new VisLabel("Width: ")).padLeft(50 * s).padTop(40 * s);
			getContentTable().add(widthfield).size(twidth, theight).padRight(50 * s).padTop(40 * s);

			getContentTable().row();

			getContentTable().add(new VisLabel("Height: ")).padLeft(50 * s).padTop(40 * s).padBottom(40f * s);
			getContentTable().add(heightfield).size(twidth, theight).padRight(50 * s).padTop(40 * s).padBottom(40f * s);

			getContentTable().row();

			ChangeListener oklistener = new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					ok.setDisabled(widthfield.getText().isEmpty() || heightfield.getText().isEmpty() || namefield.getText().replace(" ", "").isEmpty());
				}
			};

			widthfield.addListener(oklistener);
			heightfield.addListener(oklistener);
			namefield.addListener(oklistener);
			widthfield.fire(new ChangeListener.ChangeEvent());

		}

		public void result(){
			try{
				int width = Integer.parseInt(widthfield.getText());
				int height = Integer.parseInt(heightfield.getText());
				result(namefield.getText(), width, height);
			}catch(Exception e){
				e.printStackTrace();
				showError(getStage(), "Image error!", e);
			}
		}

		public void result(String name, int width, int height){

		}

	}

	public static class ContrastDialog extends FilterDialog{
		VisSlider slider;

		public ContrastDialog(){
			super(Filter.contrast, "Change Image Contrast");

			final VisLabel label = new VisLabel("Contrast: 0");

			slider = new VisSlider( -50f, 50, 1f, false);

			slider.setValue(0f);

			slider.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					label.setText("Contrast: " + slider.getValue());
				}
			});

			addSliderChangeListener(slider);

			getContentTable().add(label).align(Align.left).padTop(15f * s).row();
			getContentTable().add(slider).expand().fill().padBottom(30 * s);

			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{slider.getValue() / 50f};
		}
	}

	public static class ReplaceDialog extends FilterDialog{
		ColorBox from, to, selected;

		public ReplaceDialog(){
			super(Filter.replace, "Replace Colors");

			from = new ColorBox(Core.i.selectedColor());
			to = new ColorBox();

			final AndroidColorPicker picker = new AndroidColorPicker(false){
				public void onColorChanged(){
					selected.setColor(getSelectedColor());
				}
			};
			picker.setRecentColors(Core.i.apicker.getRecentColors());

			final VisDialog dialog = new VisDialog("Choose Color", "dialog");
			dialog.getContentTable().add(picker).expand().fill();

			VisTextButton button = new VisTextButton("OK");

			button.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					updatePreview();
				}
			});

			dialog.getButtonsTable().add(button).size(320 * s, 70 * s).pad(5f * s);
			dialog.setObject(button, true);

			VisImageButton closeButton = new VisImageButton("close-window");
			dialog.getTitleTable().add(closeButton).padRight( -getPadRight() + 0.7f);
			closeButton.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					dialog.hide();
				}
			});

			ClickListener listener = new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					selected = (ColorBox)event.getTarget();
					picker.setSelectedColor(event.getTarget().getColor());
					dialog.show(Core.i.stage);
				}
			};

			from.addSelectListener();
			to.addSelectListener();

			from.addListener(listener);
			to.addListener(listener);

			Table table = new VisTable();

			getContentTable().add(table).expand().fill();

			VisImageButton pickfrom = new VisImageButton(VisUI.getSkin().getDrawable("icon-pick"));
			VisImageButton pickto = new VisImageButton(VisUI.getSkin().getDrawable("icon-pick"));

			pickfrom.getImageCell().size(60 * s);

			pickto.getImageCell().size(60 * s);

			table.add(from).size(70 * s).pad(10 * s);

			Image image = new Image(VisUI.getSkin().getDrawable("icon-arrow-right"));

			table.add(image).size(60 * s).pad(5 * s);

			table.add(to).size(70 * s).pad(10 * s);

			/*
			VisLabel label = new VisLabel("Pick");
			label.setAlignment(Align.center);
			
			
			table.row();
			table.add(pickfrom).size(70*s).pad(4f);
			table.add().size(60*s).align(Align.center);
			table.add(pickto).size(70*s).pad(4f);
			*/

			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{from.getColor(), to.getColor()};
		}
	}

	public static class ColorAlphaDialog extends FilterDialog{
		ColorBox selected;

		public ColorAlphaDialog(Filter filter, String name, String colorname){
			super(filter, name);
			setup(colorname);
		}

		public ColorAlphaDialog(){
			this(Filter.colorToAlpha, "Color to Alpha", "Color:");
		}

		private void setup(String colorname){

			selected = new ColorBox(Core.i.selectedColor());

			selected.addSelectListener();

			final AndroidColorPicker picker = new AndroidColorPicker(false){
				public void onColorChanged(){
					selected.setColor(getSelectedColor());
				}
			};
			picker.setRecentColors(Core.i.apicker.getRecentColors());

			final VisDialog dialog = new VisDialog("Choose Color", "dialog");
			dialog.getContentTable().add(picker).expand().fill();

			VisTextButton button = new VisTextButton("OK");

			button.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					updatePreview();
				}
			});

			dialog.getButtonsTable().add(button).size(320 * s, 70 * s).pad(5f * s);
			dialog.setObject(button, true);

			VisImageButton closeButton = new VisImageButton("close-window");
			dialog.getTitleTable().add(closeButton).padRight( -getPadRight() + 0.7f);
			closeButton.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					dialog.hide();
				}
			});

			ClickListener listener = new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					selected = (ColorBox)event.getTarget();
					picker.setSelectedColor(event.getTarget().getColor());
					dialog.show(Core.i.stage);
				}
			};

			selected.addListener(listener);

			Table table = new VisTable();

			getContentTable().add(new VisLabel(colorname)).padTop(15f * s).row();

			getContentTable().add(table).expand().fill();

			table.add(selected).size(70 * s).pad(5 * s);

			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{selected.getColor()};
		}
	}

	public static class OutlineDialog extends ColorAlphaDialog{
		public OutlineDialog(){
			super(Filter.outline, "Add Outline", "Outline Color:");
		}
	}

	public static class ColorizeDialog extends FilterDialog{
		VisSlider hslider;
		VisSlider sslider;
		VisSlider bslider;

		public ColorizeDialog(){
			super(Filter.colorize, "Colorize Image");

			hslider = new VisSlider(0, 360, 1f, false);
			sslider = new VisSlider(0, 100, 1f, false);
			bslider = new VisSlider(0, 100, 1f, false);

			final VisLabel hlabel = new VisLabel("Hue:"), slabel = new VisLabel("Saturation:"), blabel = new VisLabel("Brightness:");

			ChangeListener listener = new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					hlabel.setText("Hue: " + hslider.getValue());
					slabel.setText("Saturation: " + sslider.getValue());
					blabel.setText("Brightness: " + bslider.getValue());
				}
			};

			hslider.addListener(listener);
			sslider.addListener(listener);
			bslider.addListener(listener);

			addSliderChangeListener(hslider, sslider, bslider);

			hslider.setValue(180f);
			sslider.setValue(50f);
			bslider.setValue(50f);

			getContentTable().add(hlabel).align(Align.left).padTop(10 * s).row();
			getContentTable().add(hslider).expand().fill().row();

			getContentTable().add(slabel).align(Align.left).padTop(5 * s).row();
			getContentTable().add(sslider).expand().fill().row();

			getContentTable().add(blabel).align(Align.left).padTop(5 * s).row();
			getContentTable().add(bslider).expand().fill().padBottom(30 * s);

			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{hslider.getValue() / 360f, sslider.getValue() / 100f, bslider.getValue() / 100f};
		}
	}

	public static class InvertDialog extends FilterDialog{

		public InvertDialog(){
			super(Filter.invert, "Invert Image");

			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{};
		}
	}

	public static class FlipDialog extends FilterDialog{
		VisCheckBox hbox, vbox;

		public FlipDialog(){
			super(Filter.flip, "Flip Image");

			vbox = new VisCheckBox("Flip Vertically");
			hbox = new VisCheckBox("Flip Horizontally");

			new ButtonGroup<VisCheckBox>(hbox, vbox);

			vbox.setChecked(true);

			ChangeListener listener = new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					updatePreview();
				}
			};

			hbox.addListener(listener);
			vbox.addListener(listener);

			hbox.getImageStackCell().size(40 * s);
			vbox.getImageStackCell().size(40 * s);

			Table table = getContentTable();

			table.add(vbox).align(Align.left).padTop(25 * s).padLeft(40f * s).row();
			table.add(hbox).align(Align.left).padTop(25 * s).padLeft(40f * s).padBottom(25 * s);
			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{vbox.isChecked()};
		}
	}

	public static class RotateDialog extends FilterDialog{
		VisSlider slider;

		public RotateDialog(){
			super(Filter.rotate, "Rotate Image");

			final VisLabel label = new VisLabel("Rotation: 0.0");

			slider = new VisSlider(0, 360, 5f, false);

			slider.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					label.setText("Rotation: " + MiscUtils.limit(slider.getValue() + "", 5));
					updatePreview();
				}
			});

			getContentTable().add(label).align(Align.left).padTop(20 * s).row();
			getContentTable().add(slider).expand().fill().padBottom(30 * s).padTop(5 * s);
			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{slider.getValue()};
		}
	}

	public abstract static class FilterDialog extends MenuDialog{
		private Filter filter;
		ImagePreview preview;

		public FilterDialog(Filter filter, String title){
			super(title);
			this.filter = filter;
			preview = new ImagePreview(PixmapUtils.copy(sourcePixmap()));

			Cell<?> cell = getContentTable().add(preview);

			resizeImageCell(cell);

			getContentTable().row();
		}

		abstract Object[] getArgs();

		public void updatePreview(){
			filter.apply(sourcePixmap(), pixmap(), getArgs());
			preview.image.updateTexture();
		}

		public final void result(){
			filter.apply(sourcePixmap(), pixmap(), getArgs());
			Core.i.drawgrid.canvas.drawPixmap(pixmap());
			pixmap().dispose();
		}

		public Pixmap sourcePixmap(){
			return Core.i.drawgrid.canvas.pixmap;
		}

		public Pixmap pixmap(){
			return preview.image.pixmap;
		}

		void addSliderChangeListener(VisSlider...sliders){
			EventListener listener = null;

			if(Core.i.isImageLarge()){
				listener = new InputListener(){
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
						return true;
					}

					public void touchUp(InputEvent event, float x, float y, int pointer, int button){
						updatePreview();
					}
				};
			}else{
				listener = new ChangeListener(){
					public void changed(ChangeEvent event, Actor actor){
						updatePreview();
					}
				};
			}

			for(VisSlider slider : sliders)
				slider.addListener(listener);
		}
	}

	public static class SymmetryDialog extends MenuDialog{
		VisCheckBox hbox, vbox;

		public SymmetryDialog(){
			super("Edit Symmetry");

			vbox = new VisCheckBox("Vertical Symmetry", Core.i.drawgrid.vSymmetry);
			hbox = new VisCheckBox("Horizontal Symmetry", Core.i.drawgrid.hSymmetry);

			hbox.getImageStackCell().size(40 * s);
			vbox.getImageStackCell().size(40 * s);

			Table table = getContentTable();

			table.add(vbox).align(Align.left).row();
			table.add(hbox).align(Align.left).padTop(10 * s).padBottom(10 * s);
		}
		
		public void result(){
			Core.i.drawgrid.hSymmetry = hbox.isChecked();
			Core.i.drawgrid.vSymmetry = vbox.isChecked();
		}

		Object[] getArgs(){
			return null;
		}
	}

	public static class ExportScaledDialog extends MenuDialog{
		VisTextField field;
		VisTextField directory;

		public ExportScaledDialog(){
			super("Export Scaled Image");

			field = new VisTextField("1");
			field.setTextFieldFilter(new FloatFilter());

			VisTextButton button = new VisTextButton("...");

			directory = new VisTextField("");
			directory.setTouchable(Touchable.disabled);

			button.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					new AndroidFileChooser(AndroidFileChooser.imageFilter, false){
						public void fileSelected(FileHandle file){
							directory.setText(file.file().getAbsolutePath());
							MiscUtils.moveTextToSide(directory);
						}
					}.show(getStage());
				}
			});

			ChangeListener oklistener = new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					ok.setDisabled(directory.getText().isEmpty() || field.getText().isEmpty() || field.getText().replace("0", "").replace(".", "").isEmpty());
				}
			};

			field.addListener(oklistener);
			directory.addListener(oklistener);

			field.fire(new ChangeListener.ChangeEvent());
			directory.fire(new ChangeListener.ChangeEvent());

			float sidepad = 20 * s;

			float height = 45 * s;

			getContentTable().add(new VisLabel("File:")).padTop(15 * s).padLeft(sidepad);
			getContentTable().add(directory).size(150 * s, 50 * s).padTop(15 * s);
			getContentTable().add(button).size(50 * s).padTop(15 * s).padRight(sidepad);

			getContentTable().row();

			getContentTable().add(new VisLabel("Scale:")).padTop(15 * s).padBottom(30 * s).padLeft(sidepad);
			getContentTable().add(field).grow().height(height).padTop(15 * s).padBottom(30 * s).colspan(2).padRight(sidepad);
		}

		public void result(){
			exportPixmap(PixmapUtils.scale(Core.i.drawgrid.canvas.pixmap, Float.parseFloat(field.getText())), Gdx.files.absolute(directory.getText()));
		}
	}

	public static class OpenProjectFileDialog extends MenuDialog{
		VisTextField field;
		VisTextField directory;

		public OpenProjectFileDialog(){
			super("Open Project File");

			field = new VisTextField("");

			VisTextButton button = new VisTextButton("...");

			directory = new VisTextField("");
			directory.setTouchable(Touchable.disabled);

			button.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					new AndroidFileChooser(AndroidFileChooser.imageFilter, true){
						public void fileSelected(FileHandle file){
							directory.setText(file.file().getAbsolutePath());
							MiscUtils.moveTextToSide(directory);
							directory.fire(new ChangeListener.ChangeEvent());
						}
					}.show(getStage());
				}
			});

			ChangeListener oklistener = new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					ok.setDisabled(directory.getText().isEmpty() || field.getText().isEmpty());
				}
			};

			field.addListener(oklistener);
			directory.addListener(oklistener);

			field.fire(new ChangeListener.ChangeEvent());
			directory.fire(new ChangeListener.ChangeEvent());

			float sidepad = 20 * s;

			float height = 45 * s;

			getContentTable().add(new VisLabel("File:")).padTop(15 * s).padLeft(sidepad);
			getContentTable().add(directory).size(150 * s, 50 * s).padTop(15 * s);
			getContentTable().add(button).size(50 * s).padTop(15 * s).padRight(sidepad);

			getContentTable().row();

			getContentTable().add(new VisLabel("Name:")).padTop(15 * s).padBottom(30 * s).padLeft(sidepad);
			getContentTable().add(field).grow().height(height).padTop(15 * s).padBottom(30 * s).colspan(2).padRight(sidepad);
		}

		public void result(){
			FileHandle file = Gdx.files.absolute(directory.getText());
			FileHandle to = Core.i.projectDirectory.child(field.getText() + ".png");
			file.copyTo(to);
			Project project = Core.i.projectmanager.loadProject(to);
			Core.i.projectmanager.openProject(project);
			//exportPixmap(PixmapUtils.scale(Main.i.drawgrid.canvas.pixmap, Float.parseFloat(field.getText())), Gdx.files.absolute(directory.getText()));
		}
	}

	public static class ScaleDialog extends MenuDialog{
		VisTextField widthfield, heightfield, xscalefield, yscalefield;

		public ScaleDialog(){
			super("Scale Image");

			final float aspectRatio = (float)Core.i.drawgrid.canvas.width() / Core.i.drawgrid.canvas.height();

			widthfield = new VisTextField(Core.i.drawgrid.canvas.width() + "");
			heightfield = new VisTextField(Core.i.drawgrid.canvas.height() + "");

			widthfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());
			heightfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());

			xscalefield = new VisTextField("1");
			yscalefield = new VisTextField("1");

			xscalefield.setTextFieldFilter(new FloatFilter());
			yscalefield.setTextFieldFilter(new FloatFilter());

			final VisCheckBox box = new VisCheckBox("Keep Aspect Ratio", true);

			box.getImageStackCell().size(40);

			box.addListener(new ChangeListener(){
				public void changed(ChangeEvent event, Actor actor){
					heightfield.setDisabled(box.isChecked());
					yscalefield.setDisabled(box.isChecked());
				}
			});

			box.fire(new ChangeListener.ChangeEvent());

			ChangeListener sizeClickListener = new ChangeListener(){
				public void changed(ChangeEvent event, Actor actor){
					VisTextField field = (VisTextField)actor;
					checkOkStatus(widthfield, heightfield, xscalefield, yscalefield);
					if(field.getText().isEmpty() || field.getText().equals(".")) return;

					int value = Integer.parseInt(field.getText());

					if(box.isChecked()){
						if(field == widthfield){
							heightfield.setText((int)(value / aspectRatio) + "");
						}else{
							widthfield.setText((int)(value * aspectRatio) + "");
						}
					}

					float xscl = (float)Integer.parseInt(widthfield.getText()) / Core.i.drawgrid.canvas.width();
					float yscl = (float)Integer.parseInt(heightfield.getText()) / Core.i.drawgrid.canvas.height();

					xscalefield.setText(MiscUtils.displayFloat(xscl));
					yscalefield.setText(MiscUtils.displayFloat(yscl));
					
					checkOkStatus(widthfield, heightfield, xscalefield, yscalefield);
				}
			};

			ChangeListener scaleClickListener = new ChangeListener(){
				public void changed(ChangeEvent event, Actor actor){
					VisTextField field = (VisTextField)actor;
					checkOkStatus(widthfield, heightfield, xscalefield, yscalefield);
					if(field.getText().isEmpty() || field.getText().equals(".")) return;

					float value = Float.parseFloat(field.getText());

					if(box.isChecked()){
						if(field == xscalefield){
							yscalefield.setText(MiscUtils.displayFloat(value));
						}else{
							xscalefield.setText(MiscUtils.displayFloat(value));
						}
					}

					int width = (int)(Float.parseFloat(xscalefield.getText()) * Core.i.drawgrid.canvas.width());
					int height = (int)(Float.parseFloat(yscalefield.getText()) * Core.i.drawgrid.canvas.height());

					widthfield.setText(width + "");
					heightfield.setText(height + "");
					checkOkStatus(widthfield, heightfield, xscalefield, yscalefield);
				}
			};

			widthfield.addListener(sizeClickListener);
			heightfield.addListener(sizeClickListener);

			yscalefield.addListener(scaleClickListener);
			xscalefield.addListener(scaleClickListener);

			Table table = getContentTable();

			float width = 135 * s, height = 55 * s, pad = 30 * s, right = 80f * s;

			table.add().height(30f * s);
			table.row();

			table.add(new VisLabel("Size: "));
			table.add(widthfield).size(width, height);
			table.add(new VisLabel("x"));
			table.add(heightfield).size(width, height).padRight(right);

			table.row();

			table.add(new VisLabel("Scale: ")).padTop(pad);
			table.add(xscalefield).size(width, height).padTop(pad);
			table.add(new VisLabel("x")).padTop(pad);
			table.add(yscalefield).size(width, height).padTop(pad).padRight(right);

			table.row();
			table.add(box).colspan(4).padTop(15f * s);

			table.row();
			table.add().height(30f * s);
		}

		public void result(){
			try{
				float xscale = Float.parseFloat(xscalefield.getText());
				float yscale = Float.parseFloat(yscalefield.getText());

				PixelCanvas canvas = new PixelCanvas(PixmapUtils.scale(Core.i.drawgrid.canvas.pixmap, xscale, yscale));

				Core.i.drawgrid.setCanvas(canvas);
				Core.i.updateToolColor();
			}catch(Exception e){
				e.printStackTrace();
				showError(getStage(), e);
			}
		}
	}

	public static class ShiftDialog extends MenuDialog{
		ShiftImagePreview preview;

		public ShiftDialog(){
			super("Shift Image");

			preview = new ShiftImagePreview();

			VisTable table = new VisTable();
			table.setClip(true);
			table.add(preview).grow().pad(2);

			Cell<?> cell = getContentTable().add(table);

			resizeImageCell(cell);

			getContentTable().row();
		}

		static class ShiftController extends Group{
			VisImage left, right, up, down;

			public ShiftController(){
				left = new VisImage(VisUI.getSkin().getDrawable("icon-arrow-left"));
				right = new VisImage(VisUI.getSkin().getDrawable("icon-arrow-right"));
				up = new VisImage(VisUI.getSkin().getDrawable("icon-arrow-up"));
				down = new VisImage(VisUI.getSkin().getDrawable("icon-arrow-down"));

				final Color color = Color.PURPLE;

				up.setColor(color);
				down.setColor(color);
				left.setColor(color);
				right.setColor(color);

				float size = 70;

				left.setSize(size, size);
				right.setSize(size, size);
				up.setSize(size, size);
				down.setSize(size, size);

				InputListener colorlistener = new InputListener(){
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
						event.getTarget().setColor(Color.CORAL);
						return true;
					}

					public void touchUp(InputEvent event, float x, float y, int pointer, int button){
						event.getTarget().setColor(color);
					}
				};

				up.addListener(colorlistener);
				down.addListener(colorlistener);
				left.addListener(colorlistener);
				right.addListener(colorlistener);

				up.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						shifted(0, 1);
					}
				});
				down.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						shifted(0, -1);
					}
				});
				left.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						shifted( -1, 0);
					}
				});
				right.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						shifted(1, 0);
					}
				});

				addActor(left);
				addActor(right);
				addActor(up);
				addActor(down);
			}

			public void draw(Batch batch, float alpha){
				super.draw(batch, alpha);
				float centerx = getX() + getWidth() / 2f;
				float centery = getY() + getHeight() / 2f;

				float hwidth = getWidth() / 2.5f;
				float hheight = getHeight() / 2.5f;

				up.setPosition(centerx, centery + hheight, Align.center);
				down.setPosition(centerx, centery - hheight, Align.center);
				left.setPosition(centerx - hwidth, centery, Align.center);
				right.setPosition(centerx + hwidth, centery, Align.center);

			}

			public void shifted(int x, int y){

			}
		}

		//note: call back, d

		class ShiftImagePreview extends Group{
			Stack stack;
			ShiftedImage image;
			ShiftController controller;

			public ShiftImagePreview(){
				stack = new Stack();

				AlphaImage alpha = new AlphaImage(Core.i.drawgrid.canvas.width(), Core.i.drawgrid.canvas.height());
				GridImage grid = new GridImage(Core.i.drawgrid.canvas.width(), Core.i.drawgrid.canvas.height());
				image = new ShiftedImage(Core.i.drawgrid.canvas.texture);
				controller = new ShiftController(){
					public void shifted(int x, int y){
						image.offsetx += x;
						image.offsety += y;
					}
				};

				stack.add(alpha);
				stack.add(image);

				if(Core.i.prefs.getBoolean("grid")) stack.add(grid);

				stack.add(controller);

				addActor(stack);
			}

			public void draw(Batch batch, float alpha){
				super.draw(batch, alpha);
				stack.setBounds(0, 0, getWidth(), getHeight());

				Color color = Color.CORAL.cpy();
				color.a = alpha;
				batch.setColor(color);
				MiscUtils.drawBorder(batch, getX(), getY(), getWidth(), getHeight(), 2, 2);
				batch.setColor(Color.WHITE);
			}
		}

		public void result(){
			PixelCanvas canvas = Core.i.drawgrid.canvas;
			Pixmap temp = PixmapUtils.copy(canvas.pixmap);

			int offsetx = preview.image.offsetx, offsety = preview.image.offsety;

			Pixmap.setBlending(Blending.None);
			for(int x = 0;x < canvas.width();x ++){
				for(int y = 0;y < canvas.height();y ++){
					if(x >= canvas.width() + offsetx || y >= canvas.height() + offsety || x < offsetx || y < offsety){
						canvas.drawPixelBlendless(x, y, 0);
					}

					canvas.drawPixelBlendless(x + offsetx, y + offsety, temp.getPixel(x, temp.getHeight() - 1 - y));
				}
			}
			Pixmap.setBlending(Blending.SourceOver);

			canvas.updateAndPush();
			preview.image.offsetx = 0;
			preview.image.offsety = 0;
		}
	}

	public static class CropDialog extends MenuDialog{
		CropImagePreview preview;

		public CropDialog(){
			super("Crop Image");
			Cell<?> cell = getContentTable().add((preview = new CropImagePreview()));

			resizeImageCell(cell);

			getContentTable().row();
		}

		public void result(){
			int x = 0, y = 0, x2 = 0, y2 = 0;

			x = Math.min(preview.controller.selx1, preview.controller.selx2);
			x2 = Math.max(preview.controller.selx1, preview.controller.selx2);
			y = Math.min(preview.controller.sely1, preview.controller.sely2);
			y2 = Math.max(preview.controller.sely1, preview.controller.sely2);

			PixelCanvas canvas = new PixelCanvas(PixmapUtils.crop(Core.i.drawgrid.canvas.pixmap, x, y, x2 - x, y2 - y));

			Core.i.drawgrid.setCanvas(canvas);
			Core.i.updateToolColor();
		}

		static class CropImagePreview extends ImagePreview{
			CropController controller;

			public CropImagePreview(){
				super(Core.i.drawgrid.canvas.pixmap);
				stack.add((controller = new CropController(this)));

			}

		}

		static class CropController extends Actor{
			private float xscale, yscale;
			Vector2[] points = {new Vector2(), new Vector2(), new Vector2(), new Vector2(), new Vector2(),
					new Vector2(), new Vector2(), new Vector2()};
			int selx1, sely1, selx2, sely2;
			CropImagePreview preview;
			CropPoint[] croppoints = new CropPoint[10];

			class CropPoint{
				int point;
				int pointer;

				public CropPoint(int pointer){
					this.pointer = pointer;
					point = -1;
				}
			}

			public CropController(final CropImagePreview preview){
				this.preview = preview;

				for(int i = 0;i < 10;i ++)
					croppoints[i] = new CropPoint(i);

				int pwidth = preview.image.pixmap.getWidth(), pheight = preview.image.pixmap.getHeight();

				selx1 = pwidth / 2 - pwidth / 3;
				sely1 = pheight / 2 - pheight / 3;

				selx2 = pwidth / 2 + pwidth / 3;
				sely2 = pheight / 2 + pheight / 3;

				addListener(new InputListener(){

					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){

						for(int i = 0;i < 8;i ++){
							if(points[i].dst(x, y) < 30){
								croppoints[pointer].point = i;
								touch(x, y, pointer);
								return true;
							}
						}

						return false;
					}

					public void touchUp(InputEvent event, float x, float y, int pointer, int button){
						croppoints[pointer].point = -1;
					}

					public void touchDragged(InputEvent event, float x, float y, int pointer){
						touch(x, y, pointer);
					}

					void touch(float x, float y, int pointer){
						CropPoint point = croppoints[pointer];

						points[point.point].set(x, y);

						//0 is bottom left, 1 is bottom right, 2 is  top left, 3 is top right
						// 4 is left, 5 is bottom, 6 is right, 7 is top

						Vector2 vector = points[point.point];

						vector.x = MiscUtils.clamp(vector.x, getX(), getX() + getWidth());
						vector.y = MiscUtils.clamp(vector.y, getY(), getY() + getHeight());

						if(point.point == 4){
							points[0].x = vector.x;
							points[2].x = vector.x;
						}else if(point.point == 5){
							points[0].y = vector.y;
							points[1].y = vector.y;
						}else if(point.point == 6){
							points[1].x = vector.x;
							points[3].x = vector.x;
						}else if(point.point == 7){
							points[3].y = vector.y;
							points[2].y = vector.y;
						}

						if(point.point == 1){
							points[0].y = vector.y;
							points[3].x = vector.x;
						}else if(point.point == 2){
							points[0].x = vector.x;
							points[3].y = vector.y;
						}

						selx1 = (int)((points[0].x - getX()) / xscale + 0.5f);
						sely1 = (int)((points[0].y - getY()) / yscale + 0.5f);
						selx2 = (int)((points[3].x - getX()) / xscale + 0.5f);
						sely2 = (int)((points[3].y - getY()) / yscale + 0.5f);
					}
				});
			}

			public void draw(Batch batch, float alpha){
				xscale = preview.getWidth() / preview.image.pixmap.getWidth();
				yscale = preview.getHeight() / preview.image.pixmap.getHeight();
				updatePoints();

				TextureRegion region = VisUI.getSkin().getAtlas().findRegion("white");

				int s = 1;

				if(selx1 > selx2 && sely1 > sely2) s = -1;

				batch.setColor(0, 0, 0, 0.5f);
				MiscUtils.setBatchAlpha(batch, alpha);

				MiscUtils.drawMasked(batch, region, getX(), getY(), getWidth(), getHeight(), getX() + selx1 * xscale, getY() + sely1 * yscale, (selx2 - selx1) * xscale, (sely2 - sely1) * yscale);

				batch.setColor(Color.PURPLE);
				MiscUtils.setBatchAlpha(batch, alpha);

				MiscUtils.drawBorder(batch, getX() + selx1 * xscale, getY() + sely1 * yscale, (selx2 - selx1) * xscale, (sely2 - sely1) * yscale, s * 4, s * 2);

				Color color = Color.CORAL;
				Color select = Color.PURPLE;

				int size = 40;

				for(int i = 0;i < 8;i ++){
					boolean selected = false;

					for(CropPoint point : croppoints){
						if(point.point == i){
							selected = true;
							break;
						}
					}

					batch.setColor(selected ? select : color);
					MiscUtils.setBatchAlpha(batch, alpha);
					batch.draw(region, points[i].x - size / 2, points[i].y - size / 2, size, size);

				}
			}

			void updatePoints(){
				float width = (selx2 - selx1) * xscale, height = (sely2 - sely1) * yscale;

				//0 is bottom left, 1 is bottom right, 2 is  top left, 3 is top right
				// 4 is left, 5 is bottom, 6 is right, 7 is top
				points[0].set(getX() + selx1 * xscale, getY() + sely1 * yscale);
				points[1].set(getX() + selx1 * xscale + width, getY() + sely1 * yscale);
				points[2].set(getX() + selx1 * xscale, getY() + sely1 * yscale + height);
				points[3].set(getX() + selx1 * xscale + width, getY() + sely1 * yscale + height);
				points[4].set(points[0].cpy().add(points[2]).scl(0.5f));
				points[5].set(points[0].cpy().add(points[1]).scl(0.5f));
				points[6].set(points[1].cpy().add(points[3]).scl(0.5f));
				points[7].set(points[3].cpy().add(points[2]).scl(0.5f));
			}
		}
	}

	public static class ClearDialog extends MenuDialog{
		public ClearDialog(){
			super("Confirm Clear Image");

			VisLabel label = new VisLabel("Are you sure you want\nto clear the image?");

			label.setAlignment(Align.center);
			getContentTable().center().add(label).pad(40 * s).align(Align.center);
		}

		public void result(){
			PixelCanvas canvas = Core.i.drawgrid.canvas;
			float alpha = canvas.getAlpha();

			canvas.setAlpha(1f);

			for(int x = 0;x < canvas.width();x ++){
				for(int y = 0;y < canvas.height();y ++){
					canvas.erasePixelFullAlpha(x, y);
				}
			}
			canvas.pushActions();
			canvas.updateTexture();

			canvas.setAlpha(alpha);
		}
	}

	public static class InputDialog extends MenuDialog{
		protected VisTextField textfield;

		public InputDialog(String title, String fieldtext, String text){
			super(title);

			textfield = new VisTextField(fieldtext);
			getContentTable().center().add(new VisLabel(text));
			getContentTable().center().add(textfield).pad(20 * s).padLeft(0f);
		}

		public final void result(){
			result(textfield.getText());
		}

		public void result(String string){

		}
	}

	public static class NumberInputDialog extends MenuDialog{
		protected VisTextField numberfield;

		public NumberInputDialog(String title, String fieldtext, String text){
			super(title);

			numberfield = new VisTextField(fieldtext);
			numberfield.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
			getContentTable().center().add(new VisLabel(text));
			getContentTable().center().add(numberfield).pad(20 * s).padLeft(0f);
		}

		public final void result(){
			if( !numberfield.getText().isEmpty()) result(Integer.parseInt(numberfield.getText()));
		}

		public void result(int i){

		}
	}
	
	public static class InfoDialog extends MenuDialog{
		public InfoDialog(String title, String info){
			super(title);
			getButtonsTable().clearChildren();
			getButtonsTable().add(ok).size(130 * s, 60 * s).padBottom(3*s);
			ok.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					hide();
				}
			});
			
			VisLabel label = new VisLabel(info);
			label.setAlignment(Align.center);
			label.setWrap(true);
			getContentTable().add(label).width(400f).padTop(20*s).padBottom(20*s);
		}

	}
	
	public static class ErrorDialog extends MenuDialog{
		public ErrorDialog(String info, String extra){
			super("Error");
			getButtonsTable().clear();
			getButtonsTable().add(ok).size(130 * s, 60 * s).padBottom(3*s);
			ok.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					hide();
				}
			});
			
			VisLabel label = new VisLabel(info);
			label.setAlignment(Align.center);
			label.setWrap(true);
			
			VisLabel extralabel = new VisLabel(extra);
			extralabel.setColor(Color.RED);
			extralabel.setAlignment(Align.center);
			extralabel.setWrap(true);
			getContentTable().add(label).width(400f).padTop(20*s).padBottom(5*s).row();
			getContentTable().add(extralabel).width(400f).padTop(5*s).padBottom(20*s);
		}
	}
	
	public static void showInfo(Stage stage, String info){
		new InfoDialog("Info", info).show(stage);
	}
	
	public static void showError(Stage stage, String info, String details){
		new ErrorDialog(info, details).show(stage);
		/*
		VisDialog dialog = new VisDialog("Error", "dialog");
		dialog.getTitleLabel().setColor(Color.RED);
		dialog.addCloseButton();
		dialog.getContentTable().add(new VisLabel(info)).padTop(20*s).padBottom(20*s);
		
		dialog.getContentTable().row();
		
		VisLabel error = new VisLabel(details);
		error.setWrap(true);
		error.setColor(Hue.blend(Color.GRAY, Color.RED, 0.5f));
		error.setAlignment(Align.center);
		dialog.getContentTable().center().add(error).align(Align.center).width(400f*s).padTop(10*s).padBottom(30*s);
		
		VisTextButton button = new VisTextButton("OK");
		dialog.getButtonsTable().add(button).size(120*s, 60*s);
		dialog.setObject(button, true);
		dialog.show(stage);
		*/
	}
	
	public static void showError(Stage stage, String info){
		new ErrorDialog(info, "").show(stage);
		/*
		VisDialog dialog = new VisDialog("Error", "dialog");
		dialog.getTitleLabel().setColor(Color.RED);
		dialog.addCloseButton();
		dialog.getContentTable().add(new VisLabel(info)).padTop(20*MiscUtils.densityScale()).padBottom(20*MiscUtils.densityScale());
			
		VisTextButton button = new VisTextButton("OK");
		dialog.getButtonsTable().add(button).size(120*MiscUtils.densityScale(), 60*MiscUtils.densityScale());
		dialog.setObject(button, true);
		dialog.show(stage);
		*/
	}
	
	public static void showError(Stage stage, Exception e){
		showError(stage, "Failed to write image!", e);
	}
	
	public static void showError(Stage stage, String title, Exception e){
		showError(stage, title, convertToString(e));
	}

	public static class ConfirmDialog extends MenuDialog{

		public ConfirmDialog(String title, String text){
			super(title);

			VisLabel label = new VisLabel(text);
			getContentTable().center().add(label).pad(20 * s);
		}
	}

	public static abstract class MenuDialog extends BaseDialog{
		protected VisTextButton ok;
		protected VisTextButton cancel;

		public MenuDialog(String title){
			super(title);
			addCloseButton();
			addButtons();
		}

		void addButtons(){
			cancel = new VisTextButton("Cancel");
			ok = new VisTextButton("OK");

			setObject(ok, true);
			setObject(cancel, false);

			getButtonsTable().add(cancel).size(130 * s, 60 * s);
			getButtonsTable().add(ok).size(130 * s, 60 * s);

		}

		protected void result(Object object){
			if((Boolean)object == true) 
				result();
			else
				cancel();
		}
		
		public void cancel(){
			
		}

		public void result(){

		}

		public void checkOkStatus(VisTextField...fields){

			for(VisTextField field : fields)
				if(field.getText().replace("0", "").isEmpty()){
					ok.setDisabled(true);
					return;
				}
			ok.setDisabled(false);
		}
	}
	
	public static abstract class BaseDialog extends VisDialog{

		public BaseDialog(String title){
			super(title, "dialog");
			setMovable(false);
			pad((s-1f)*10f);
		}

		
		public void addTitleSeperator(){
			getTitleTable().row();
			getTitleTable().add(new Separator()).expandX().fillX().padTop(3 * s).padBottom(3*s);
		}
		
		@Override
		public void addCloseButton () {
			Label titleLabel = getTitleLabel();
			Table titleTable = getTitleTable();

			VisImageButton closeButton = new VisImageButton("close-window");
			closeButton.getImageCell().size(40*s);
			titleTable.add(closeButton).padRight(-getPadRight() + 0.7f).size(50*s);
			closeButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					close();
				}
			});
			closeButton.addListener(new ClickListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					event.cancel();
					return true;
				}
			});

			if (titleLabel.getLabelAlign() == Align.center && titleTable.getChildren().size == 2)
				titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2);
		}

		public void hide(){
			super.hide();
			Gdx.input.setOnscreenKeyboardVisible(false);
		}

		public void close(){
			super.close();
			Gdx.input.setOnscreenKeyboardVisible(false);
		}
	}
	
	
	private static String convertToString(Exception e){
		String extra = "";
		
		if(e.getCause() != null){
			extra = convertToString(e.getCause());
		}else{
			extra = convertToString(e);
		}
		return extra;
	}
	
	private static String convertToString(Throwable e){
		String string = "";
		if(e.getMessage().toLowerCase().contains("permission denied")){
			string = "Error: Permission denied.";
		}else{
			string = e.getClass().getSimpleName() + ": " + e.getMessage();
		}
		
		return string;
	}

	public static void exportPixmap(Pixmap pixmap, FileHandle file){
		try{
			if( !file.extension().equalsIgnoreCase("png")) file = file.parent().child(file.nameWithoutExtension() + ".png");
			PixmapIO.writePNG(file, Core.i.drawgrid.canvas.pixmap);
			showInfo(Core.i.stage, "Image exported to " + file + ".");
		}catch(Exception e){
			e.printStackTrace();
			showError(Core.i.stage, e);
		}
	}

	static Cell<? extends Actor> resizeImageCell(Cell<? extends Actor> cell){
		float ratio = Core.i.drawgrid.canvas.width() / Core.i.drawgrid.canvas.height();

		float isize = 400;
		float width = isize, height = isize / ratio;
		if(height > width){
			height = isize;
			width = isize * ratio;
		}
		float sidePad = (isize - width) / 2f, topPad = (isize - height) / 2f;

		return cell.size(width, height).padTop(3 + topPad).padBottom(topPad).padLeft(sidePad + 2).padRight(sidePad + 2);
	}

	static class FloatFilter implements VisTextField.TextFieldFilter{
		@Override
		public boolean acceptChar(VisTextField textField, char c){

			return Character.isDigit(c) || (c == '.' && !textField.getText().contains("."));
		}
	}
}
