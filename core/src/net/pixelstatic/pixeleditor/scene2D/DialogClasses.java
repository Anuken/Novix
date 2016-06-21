package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.graphics.Filter;
import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.utils.MiscUtils;
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

			widthfield.setText(GUI.gui.drawgrid.canvas.width() + "");
			heightfield.setText(GUI.gui.drawgrid.canvas.height() + "");

			TextFieldDialogListener.add(widthfield, true, 3);
			TextFieldDialogListener.add(heightfield, true, 3);
			
			getContentTable().add(new VisLabel("Width: ")).padLeft(50 * s).padTop(40 * s);
			getContentTable().add(widthfield).size(140, 40).padRight(50 * s).padTop(40 * s);

			getContentTable().row();

			getContentTable().add(new VisLabel("Height: ")).padLeft(50 * s).padTop(40 * s).padBottom(40f * s);
			getContentTable().add(heightfield).size(140, 40).padRight(50 * s).padTop(40 * s).padBottom(40f * s);

			getContentTable().row();
			
			addButtons();
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
	
	public static class FlipDialog extends MenuDialog{
		VisCheckBox hbox, vbox;
		
		public FlipDialog(){
			super("Flip Image");
			

			vbox = new VisCheckBox("Flip Vertically");
			hbox = new VisCheckBox("Flip Horizontally");
			
			new ButtonGroup<VisCheckBox>(hbox, vbox);
			
			vbox.setChecked(true);
			
			hbox.getImageStackCell().size(40*s);
			vbox.getImageStackCell().size(40*s);
			
			Table table = getContentTable();
			
			table.add(vbox).align(Align.left).row();
			table.add(hbox).align(Align.left).padTop(10*s).padBottom(10*s);
			
			
			addButtons();
			
		}
		
		public void result(){
			Pixmap pixmap = Filter.flip.apply(GUI.gui.drawgrid.canvas.pixmap, vbox.isChecked());
			GUI.gui.drawgrid.canvas.drawPixmap(pixmap);
			pixmap.dispose();
		}
	}
	
	public static class RotateDialog extends MenuDialog{
		public VisSlider slider;
		
		public RotateDialog(){
			super("Rotate Image");
			
			final VisLabel label = new VisLabel("Rotation: 0.0");
			
			final RotatedImage rotimage = new RotatedImage();
			/*
			Stack stack = new Stack();
			
			final Image image = new Image(GUI.gui.drawgrid.canvas.texture);
			final AlphaImage alpha = new AlphaImage(GUI.gui.drawgrid.canvas.width(), GUI.gui.drawgrid.canvas.height());

			stack.add(alpha);
			stack.add(image);
			*/
			
			slider = new VisSlider(0, 360, 0.001f, false);
			
			slider.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					label.setText("Rotation: " + MiscUtils.limit(slider.getValue() + "", 5));
					rotimage.setImageRotation(slider.getValue());
				}
			});
			
			getContentTable().add(rotimage).size(200, 200).row();
			rotimage.getImage().setOrigin(100, 100);
			getContentTable().add(label).align(Align.left).padTop(20).row();
			getContentTable().add(slider).expand().fill().padBottom(30).padTop(5);
			
			addButtons();
			
		}
		
		public void result(){
			Pixmap pixmap = Filter.rotate.apply(GUI.gui.drawgrid.canvas.pixmap, slider.getValue());
			GUI.gui.drawgrid.canvas.drawPixmap(pixmap);
			pixmap.dispose();
		}
	}
	
	
	public static abstract class MenuDialog extends VisDialog{
		
		public MenuDialog(String title){
			super(title, "dialog");
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
