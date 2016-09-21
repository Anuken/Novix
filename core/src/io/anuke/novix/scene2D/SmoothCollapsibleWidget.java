package io.anuke.novix.scene2D;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * A VisUI CollapsibleWidget with a smooth animation. Yes, I had to copy all the
 * code, because private variables suck.
 * 
 * @author Anuken
 */
public class SmoothCollapsibleWidget extends WidgetGroup {

	private Table table;

	private CollapseAction collapseAction = new CollapseAction();

	private boolean collapsed;
	private boolean actionRunning, up;

	private float currentHeight, firsty = 9998;

	public SmoothCollapsibleWidget(Table table) {
		this(table, true);
	}

	public SmoothCollapsibleWidget(Table table, boolean up) {
		this.table = table;
		this.up = up;
		if(up) currentHeight = table.getPrefHeight();
		updateTouchable();
		addActor(table);
	}
	
	public Table getTable(){
		return table;
	}
	
	public float getDone(){
		return currentHeight / table.getPrefHeight();
	}

	public void setCollapsed(boolean collapse, boolean withAnimation) {
		this.collapsed = collapse;
		updateTouchable();

		if (table == null)
			return;

		actionRunning = true;

		if (withAnimation) {
			addAction(collapseAction);
		} else {
			if (collapse) {
				currentHeight = 0;
				collapsed = true;
			} else {
				currentHeight = table.getPrefHeight();
				collapsed = false;
			}

			actionRunning = false;
			invalidateHierarchy();
		}
	}

	public void setCollapsed(boolean collapse) {
		setCollapsed(collapse, true);
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	private void updateTouchable() {
		if (collapsed)
			setTouchable(Touchable.disabled);
		else
			setTouchable(Touchable.enabled);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		
		float y = getY();
		setY(y + (up ? -1 : 1) * currentHeight - (up ? -1 : 1) * table.getPrefHeight());
	

		// Gdx.app.error("asdsadsaddadsad", firsty + "");
		if (currentHeight > 1) {
			batch.flush();
			//boolean clipEnabled = clipBegin(getX(), firsty, getWidth(), currentHeight);

			super.draw(batch, parentAlpha);

			batch.flush();
			//if (clipEnabled)
			//	clipEnd();
		}
		setY(y);
	}

	@Override
	public void layout() {
		if (table == null)
			return;

		if (firsty >= 9997)
			firsty = getY();

		table.setBounds(0, 0, table.getPrefWidth(), table.getPrefHeight());
		
		
		
		if (actionRunning == false) {
			if (collapsed)
				currentHeight = 0;
			else
				currentHeight = table.getPrefHeight();
		}
	}

	@Override
	public float getPrefWidth() {
		return table == null ? 0 : table.getPrefWidth();
	}

	@Override
	public float getPrefHeight() {
		if (table == null)
			return 0;

		if (actionRunning == false) {
			if (collapsed)
				return 0;
			else
				return currentHeight;
		}

		return currentHeight;
	}

	public void setTable(Table table) {
		this.table = table;
		clearChildren();
		addActor(table);
	}
	
	public void resetY(){
		firsty = getY();
	}

	@Override
	protected void childrenChanged() {
		super.childrenChanged();
		if (getChildren().size > 1)
			throw new GdxRuntimeException("Only one actor can be added to CollapsibleWidget");
	}

	private class CollapseAction extends Action {
		@Override
		public boolean act(float delta) {
			if (collapsed) {
				currentHeight = Interpolation.bounceIn.apply(currentHeight, 0, up ? 0.2f : 0.2f);
				currentHeight -= delta * 400f;
				if (currentHeight <= 1f) {
					currentHeight = 0;
					collapsed = true;
					actionRunning = false;
				}
			} else {
				currentHeight = Interpolation.bounceIn.apply(currentHeight, table.getPrefHeight(), 0.4f);
				currentHeight += delta * 400f;

				if (currentHeight > table.getPrefHeight() - 1f) {
					currentHeight = table.getPrefHeight();
					collapsed = false;
					actionRunning = false;
				}
			}

			invalidateHierarchy();
			return !actionRunning;
		}
	}
}
