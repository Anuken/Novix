package io.anuke.novix.android;


import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTextField;

import android.app.Activity;
import android.text.InputType;
import io.anuke.novix.android.AndroidTextFieldDialog.TextPromptListener;

public class TextFieldDialogListener extends ClickListener{
	private VisTextField field;
	private int type;
	private int max;

	public static void add(VisTextField field, int type, int max){
		field.addListener(new TextFieldDialogListener(field, type, max));
		field.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Gdx.input.setOnscreenKeyboardVisible(false);
				return false;
			}
		});
	}

	public static void add(VisTextField field){
		add(field, 0, 15);
	}

	//type - 0 is text, 1 is numbers, 2 is decimals
	public TextFieldDialogListener(VisTextField field, int type, int max){
		this.field = field;
		this.type = type;
		this.max = max;
	}

	public void clicked(final InputEvent event, float x, float y){
		
		if(Gdx.app.getType() == ApplicationType.Desktop) return;
		
		AndroidTextFieldDialog dialog = new AndroidTextFieldDialog((Activity)Gdx.app);

		dialog.setTextPromptListener(new TextPromptListener(){
			public void confirm(String text){
				field.clearText();
				field.appendText(text);
				field.fire(new ChangeListener.ChangeEvent());
				Gdx.graphics.requestRendering();
			}
		});

		if(type == 0){
			dialog.setInputType(InputType.TYPE_CLASS_TEXT);
		}else if(type == 1){
			dialog.setInputType(InputType.TYPE_CLASS_NUMBER);
		}else if(type == 2){
			dialog.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}

		dialog.setConfirmButtonLabel("OK").setText(field.getText());
		dialog.setCancelButtonLabel("Cancel");
		dialog.setMaxLength(max);
		dialog.show();
		event.cancel();

	}
}
