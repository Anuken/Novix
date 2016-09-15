package io.anuke.pixeleditor.tools;

import io.anuke.utils.SceneUtils;
import io.anuke.utils.android.AndroidKeyboard;
import io.anuke.utils.android.TextFieldDialogListener;
import io.anuke.utils.android.AndroidKeyboard.AndroidKeyboardListener;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.widget.VisTextField;

public class DialogKeyboardMoveListener implements AndroidKeyboardListener{
	HashMap<Actor, Float> moved = new HashMap<Actor, Float>();

	void moveActor(final int height, boolean extra){
		Focusable focus = FocusManager.getFocusedWidget();

		if(focus == null){
			if(extra){
				Gdx.app.postRunnable(new Runnable(){
					public void run(){
						moveActor(height, false);
					}
				});
			}
			return;
		}

		Actor actor = (Actor)focus;

		if( !(actor instanceof VisTextField)) return;

		VisTextField field = (VisTextField)actor;

		for(EventListener listener : field.getListeners()){
			if(listener instanceof TextFieldDialogListener) return;
		}

		Actor parent = SceneUtils.getTopParent(field);

		float keypadding = 30;

		//float parenty = parent.getY();
		float actory = field.localToStageCoordinates(new Vector2(0, 0)).y;
		float keyheight = AndroidKeyboard.getCurrentKeyboardHeight() + keypadding;

		if(height > 0){
			moveActorDown(parent);
		}

		if(actory < keyheight){
			float diff = keyheight - actory;
			moveActorUp(parent, diff);
		}
	}

	@Override
	public void onSizeChange(int height){
		moveActor(height, true);
	}

	void moveActorUp(Actor actor, float move){
		actor.addAction(Actions.moveBy(0, move, 0.1f));
		if(moved.containsKey(actor)){
			moved.put(actor, moved.get(actor) + move);
		}else{
			moved.put(actor, move);
		}
	}

	void moveActorDown(Actor actor){
		if(moved.containsKey(actor)){
			float move = moved.get(actor);
			actor.addAction(Actions.moveBy(0, -move, 0.1f));
			moved.remove(actor);
		}
	}
}
