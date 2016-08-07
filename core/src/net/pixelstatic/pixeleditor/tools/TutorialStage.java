package net.pixelstatic.pixeleditor.tools;

import com.badlogic.gdx.graphics.g2d.Batch;

public enum TutorialStage{
	first {
		@Override
		public void draw(Batch batch){
			
		}
	};
	public abstract void draw(Batch batch);
}
