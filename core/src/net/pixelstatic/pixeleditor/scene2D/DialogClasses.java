package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.graphics.Filter;
import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.graphics.PixmapUtils;
import net.pixelstatic.utils.graphics.Textures;
import net.pixelstatic.utils.scene2D.AndroidColorPicker;
import net.pixelstatic.utils.scene2D.ColorBox;
import net.pixelstatic.utils.scene2D.TextFieldDialogListener;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.*;

public class DialogClasses{
	static float s = GUI.s;

	public static class SizeDialog extends MenuDialog{
		VisTextField widthfield, heightfield;

		public SizeDialog(String title){
			super(title);

			widthfield = new VisTextField();
			heightfield = new VisTextField();
			widthfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());
			heightfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());

			widthfield.setText((GUI.gui.drawgrid.canvas.width()) + "");
			heightfield.setText(GUI.gui.drawgrid.canvas.height() + "");

			TextFieldDialogListener.add(widthfield, 1, 3);
			TextFieldDialogListener.add(heightfield, 1, 3);

			getContentTable().add(new VisLabel("Width: ")).padLeft(50 * s).padTop(40 * s);
			getContentTable().add(widthfield).size(140, 40).padRight(50 * s).padTop(40 * s);

			getContentTable().row();

			getContentTable().add(new VisLabel("Height: ")).padLeft(50 * s).padTop(40 * s).padBottom(40f * s);
			getContentTable().add(heightfield).size(140, 40).padRight(50 * s).padTop(40 * s).padBottom(40f * s);

			getContentTable().row();

		}

		public void result(){
			try{
				int width = Integer.parseInt(widthfield.getText());
				int height = Integer.parseInt(heightfield.getText());
				result(width, height);
			}catch(Exception e){
				e.printStackTrace();
				Dialogs.showDetailsDialog(getStage(), "An exception has occured.", "Error", e.getClass().getSimpleName() + ": " + (e.getMessage() == null ? "" : e.getMessage()));
			}
		}

		public void result(int width, int height){

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
					updatePreview();
				}
			});

			getContentTable().add(label).align(Align.left).row();;
			getContentTable().add(slider).expand().fill();

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

			from = new ColorBox(GUI.gui.selectedColor());
			to = new ColorBox();

			final AndroidColorPicker picker = new AndroidColorPicker(false){
				public void onColorChanged(){
					selected.setColor(getSelectedColor());
					updatePreview();
				}
			};
			picker.setRecentColors(GUI.gui.apicker.getRecentColors());

			final VisDialog dialog = new VisDialog("Choose Color", "dialog");
			dialog.getContentTable().add(picker).expand().fill();

			VisTextButton button = new VisTextButton("OK");

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
					dialog.show(GUI.gui.stage);
				}
			};

			from.addSelectListener();
			to.addSelectListener();

			from.addListener(listener);
			to.addListener(listener);

			Table table = new VisTable();

			getContentTable().add(table).expand().fill();

			VisImageButton pickfrom = new VisImageButton(new TextureRegionDrawable(new TextureRegion(Textures.get("icon-pick"))));
			VisImageButton pickto = new VisImageButton(new TextureRegionDrawable(new TextureRegion(Textures.get("icon-pick"))));

			pickfrom.getImageCell().size(60 * s);

			pickto.getImageCell().size(60 * s);

			table.add(from).size(70 * s).pad(10 * s);

			Image image = new Image((Textures.get("icon-arrow-right")));

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

					updatePreview();
				}
			};

			hslider.addListener(listener);
			sslider.addListener(listener);
			bslider.addListener(listener);

			hslider.setValue(180f);
			sslider.setValue(50f);
			bslider.setValue(50f);

			getContentTable().add(hlabel).align(Align.left).padTop(5).row();
			getContentTable().add(hslider).expand().fill().row();

			getContentTable().add(slabel).align(Align.left).padTop(5).row();
			getContentTable().add(sslider).expand().fill().row();

			getContentTable().add(blabel).align(Align.left).padTop(5).row();
			getContentTable().add(bslider).expand().fill();

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

			table.add(vbox).align(Align.left).row();
			table.add(hbox).align(Align.left).padTop(10 * s).padBottom(10 * s);
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

			float ratio = (float)sourcePixmap().getWidth() / sourcePixmap().getHeight();

			float isize = 400;
			float width = isize, height = isize / ratio;
			if(height > width){
				height = isize;
				width = isize * ratio;
			}
			float sidePad = (isize - width) / 2f, topPad = (isize - height) / 2f;
			getContentTable().add(preview).size(width, height).padTop(3 + topPad).padBottom(topPad).padLeft(sidePad + 2).padRight(sidePad + 2).row();

		}

		abstract Object[] getArgs();

		public void updatePreview(){
			filter.apply(sourcePixmap(), pixmap(), getArgs());
			preview.image.updateTexture();
		}

		public final void result(){
			filter.apply(sourcePixmap(), pixmap(), getArgs());
			GUI.gui.drawgrid.canvas.drawPixmap(pixmap());
			pixmap().dispose();
		}

		public Pixmap sourcePixmap(){
			return GUI.gui.drawgrid.canvas.pixmap;
		}

		public Pixmap pixmap(){
			return preview.image.pixmap;
		}
	}

	public static class SymmetryDialog extends MenuDialog{
		VisCheckBox hbox, vbox;

		public SymmetryDialog(){
			super("Edit Symmetry");

			vbox = new VisCheckBox("Vertical Symmetry", GUI.gui.drawgrid.vSymmetry);
			hbox = new VisCheckBox("Horizontal Symmetry", GUI.gui.drawgrid.hSymmetry);

			ChangeListener listener = new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					GUI.gui.drawgrid.hSymmetry = hbox.isChecked();
					GUI.gui.drawgrid.vSymmetry = vbox.isChecked();
				}
			};

			hbox.addListener(listener);
			vbox.addListener(listener);

			hbox.getImageStackCell().size(40 * s);
			vbox.getImageStackCell().size(40 * s);

			Table table = getContentTable();

			table.add(vbox).align(Align.left).row();
			table.add(hbox).align(Align.left).padTop(10 * s).padBottom(10 * s);
		}

		Object[] getArgs(){
			return null;
		}
	}

	public static class ScaleDialog extends MenuDialog{
		VisTextField widthfield, heightfield, xscalefield, yscalefield;

		public ScaleDialog(){
			super("Scale Image");

			final float aspectRatio = (float)GUI.gui.drawgrid.canvas.width() / GUI.gui.drawgrid.canvas.height();

			widthfield = new VisTextField(GUI.gui.drawgrid.canvas.width() + "");
			heightfield = new VisTextField(GUI.gui.drawgrid.canvas.height() + "");

			TextFieldDialogListener.add(widthfield, 1, 3);
			TextFieldDialogListener.add(heightfield, 1, 3);

			xscalefield = new VisTextField("1.0");
			yscalefield = new VisTextField("1.0");

			TextFieldDialogListener.add(xscalefield, 2, 3);
			TextFieldDialogListener.add(yscalefield, 2, 3);

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

					int value = Integer.parseInt(field.getText());

					if(box.isChecked()){
						if(field == widthfield){
							heightfield.setText((int)(value / aspectRatio) + "");
						}else{
							widthfield.setText((int)(value * aspectRatio) + "");
						}
					}

					float xscl = (float)Integer.parseInt(widthfield.getText()) / GUI.gui.drawgrid.canvas.width();
					float yscl = (float)Integer.parseInt(heightfield.getText()) / GUI.gui.drawgrid.canvas.height();

					xscalefield.setText(xscl + "");
					yscalefield.setText(yscl + "");

				}
			};

			ChangeListener scaleClickListener = new ChangeListener(){
				public void changed(ChangeEvent event, Actor actor){
					VisTextField field = (VisTextField)actor;
					
					System.out.println("change.");
					float value = Float.parseFloat(field.getText());

					if(box.isChecked()){
						if(field == xscalefield){
							yscalefield.setText((value / aspectRatio) + "");
						}else{
							xscalefield.setText((value * aspectRatio) + "");
						}
					}

					int width = (int)(Float.parseFloat(xscalefield.getText()) * GUI.gui.drawgrid.canvas.width());
					int height = (int)(Float.parseFloat(yscalefield.getText()) * GUI.gui.drawgrid.canvas.height());

					widthfield.setText(width + "");
					heightfield.setText(height + "");
				}
			};

			widthfield.addListener(sizeClickListener);
			heightfield.addListener(sizeClickListener);

			yscalefield.addListener(scaleClickListener);
			xscalefield.addListener(scaleClickListener);

			Table table = getContentTable();

			float width = 130, height = 50, pad = 20, right = 80f;

			table.add().height(30f);
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
			table.add(box).colspan(4).padTop(15f);

			table.row();
			table.add().height(30f);

			/*
				
				final VisTextField scalefield = new VisTextField();
				scalefield.setTouchable(Touchable.disabled);
				
				
				scalefield.addListener(new ChangeListener(){
					@Override
					public void changed(ChangeEvent event, Actor actor){
						float scl = Float.parseFloat(scalefield.getText());
						int nwidth = (int)(scl*GUI.gui.drawgrid.canvas.width());
						int nheight = (int)(scl*GUI.gui.drawgrid.canvas.height());
					}
				});
				
				scalefield.setText("1.0");
				
				scalefield.fire(new ChangeListener.ChangeEvent());
				
				VisImageButton upbutton = new VisImageButton(Textures.getDrawable("icon-plus"));
				VisImageButton downbutton = new VisImageButton(Textures.getDrawable("icon-minus"));
				
				upbutton.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						
					}
				});
				
				upbutton.getImageCell().size(40f);
				downbutton.getImageCell().size(40f);
				
				float fieldheight = 40;
				
				TextFieldDialogListener.add(scalefield);
				getContentTable().add(new VisLabel("Scale: ")).padRight(0f).padTop(40f*s);
				getContentTable().add(scalefield).size(180*s, fieldheight).padTop(40f*s).padLeft(0);
				
				getContentTable().add(downbutton).spaceLeft(5f).padTop(40f*s).size(fieldheight);
				getContentTable().add(upbutton).spaceLeft(5f).padTop(40f*s).size(fieldheight).row();
				
				float tpad = 20;
				
				getContentTable().add(new VisLabel("Width: ")).colspan(1).padTop(tpad * s);
				getContentTable().add(widthfield).height(fieldheight).colspan(2).expandX().fillX().padRight(50 * s).padTop(tpad * s);

				getContentTable().row();

				getContentTable().add(new VisLabel("Height: ")).colspan(1).padTop(tpad * s).padBottom(40f * s);
				getContentTable().add(heightfield).height(fieldheight).colspan(2).expandX().fillX().padRight(50 * s).padTop(tpad * s).padBottom(40f * s);

				
				
				
				//spinner = new Spinner("Scale: ", new SimpleFloatSpinnerModel(1, 0.1f, 10));
				//((Cell<VisTextField>)MiscUtils.getPrivate(spinner, "textFieldCell")).size(100f);
			
				//getContentTable().add(spinner).pad(10f);
				 * 
				 */
		}

		public void result(){
			try{
				float xscale = Float.parseFloat(xscalefield.getText());
				float yscale = Float.parseFloat(yscalefield.getText());

				PixelCanvas canvas = new PixelCanvas(PixmapUtils.scale(GUI.gui.drawgrid.canvas.pixmap, xscale, yscale));

				GUI.gui.drawgrid.setCanvas(canvas);
				GUI.gui.updateToolColor();
			}catch(Exception e){
				e.printStackTrace();
				Dialogs.showDetailsDialog(getStage(), "An exception has occured.", "Error", e.getClass().getSimpleName() + ": " + (e.getMessage() == null ? "" : e.getMessage()));
			}
		}
	}

	public static abstract class MenuDialog extends VisDialog{

		public MenuDialog(String title){
			super(title, "dialog");
			addButtons();
		}

		void addButtons(){
			Button cancel = new VisTextButton("Cancel");
			Button ok = new VisTextButton("OK");

			setObject(ok, true);
			setObject(cancel, false);

			getButtonsTable().add(cancel).size(130 * s, 60 * s);
			getButtonsTable().add(ok).size(130 * s, 60 * s);
			addCloseButton();
		}

		protected void result(Object object){
			if((Boolean)object == true) result();

		}

		public void result(){

		}
	}
}
