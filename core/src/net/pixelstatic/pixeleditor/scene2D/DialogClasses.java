package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.graphics.Filter;
import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.graphics.PixmapUtils;
import net.pixelstatic.utils.scene2D.TextFieldDialogListener;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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

			widthfield.setText((GUI.gui.drawgrid.canvas.width())+ "");
			heightfield.setText(GUI.gui.drawgrid.canvas.height() + "");

			TextFieldDialogListener.add(widthfield, true, 3);
			TextFieldDialogListener.add(heightfield, true, 3);

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
	
	public static class DesaturateDialog extends FilterDialog{
		
		public DesaturateDialog(){
			super(Filter.desaturate, "Rotate Image");
		}

		@Override
		Object[] getArgs(){
			return new Object[]{};
		}
	}
	
	public static class ReplaceDialog extends FilterDialog{
		
		public ReplaceDialog(){
			super(Filter.replace, "Rotate Image");
			
			updatePreview();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{};
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
			
			VisLabel hlabel = new VisLabel("Hue:"), slabel = new VisLabel("Saturation:"), blabel = new VisLabel("Brightness:");
			
			
			getContentTable().add(hlabel).align(Align.left).padTop(5).row();
			getContentTable().add(hslider).expand().fill().row();

			getContentTable().add(slabel).align(Align.left).padTop(5).row();
			getContentTable().add(sslider).expand().fill().row();

			getContentTable().add(blabel).align(Align.left).padTop(5).row();
			getContentTable().add(bslider).expand().fill();
		}

		@Override
		Object[] getArgs(){
			return new Object[]{};
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
			float width = isize, height = isize/ratio;
			if(height > width){
				height = isize;
				width = isize*ratio;
			}
			float sidePad = (isize - width)/2f, topPad = (isize-height)/2f;
			getContentTable().add(preview).size(width, height).padTop(3 + topPad).padBottom(topPad).padLeft(sidePad+2).padRight(sidePad+2).row();
			
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
