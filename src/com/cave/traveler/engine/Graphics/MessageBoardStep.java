package com.cave.traveler.engine.Graphics;

public class MessageBoardStep {
	
	public MessageBoardStep(String[] text, int color, int background, float duration, float fromAlpha, float toAlpha, Mode mode) {
		super();
		this.text = text;
		this.color = color;
		this.background = background;
		this.duration = duration;
		this.fromAlpha = fromAlpha;
		this.toAlpha = toAlpha;
		this.mode = mode;
	}

	public String text[];
	public int color;
	public int background;
	public float duration;
	public float fromAlpha;
	public float toAlpha;
	public Mode mode;

	public enum Mode {
		BackgroundOnly, TwoSideText
	}
}
