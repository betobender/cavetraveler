package com.cave.traveler.engine.Graphics;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;

public class TextBox extends Drawing {

	public class TextBoxContent {
		private boolean modified;
		private String content = "";
		private Paint paint;
		private float[] position;

		public TextBoxContent(String content, Paint paint, float[] position) {
			this.setContent(content);
			this.setPaint(paint);
			this.setPosition(position);
			this.modified = true;
		}

		public void paint(Canvas canvas) {
			canvas.drawText(content, position[0], position[1], paint);
			modified = false;
		}

		public String getContent() {
			this.modified = true;
			return content;
		}

		public void setContent(String content) {
			this.modified = (this.content.equals(content) == false);
			this.content = content;
		}
		
		public void setColor(int color) {
			if(this.paint.getColor() != color) {
				this.paint.setColor(color);
				this.modified = true;
			}
		}

		public Paint getPaint() {
			this.modified = true;
			return paint;
		}

		public void setPaint(Paint paint) {
			this.paint = paint;
			this.modified = true;
		}

		public float[] getPosition() {
			this.modified = true;
			return position;
		}

		public void setPosition(float[] position) {
			this.modified = true;
			this.position = position;
		}

		public boolean isModified() {
			return modified;
		}
	}

	public static Paint createPaint(Typeface typeFace, int size, int color, Paint.Align align) {
		Paint paint = new Paint();
		paint.setColor(color);
		if (typeFace != null)
			paint.setTypeface(typeFace);
		paint.setTextSize(size);
		paint.setAntiAlias(true);
		paint.setTextAlign(align);
		return paint;
	}

	private List<TextBoxContent> contents = new LinkedList<TextBoxContent>();

	public TextBox(int bitmapWidth, int bitmapHeight, float modelWidth, float modelHeight) {
		super(bitmapWidth, bitmapHeight, modelWidth, modelHeight);
	}

	@Override
	public void draw(TextureMap textureMap, Shaders shaders) {
		boolean modified = false;
		for (TextBoxContent content : contents)
			modified |= content.isModified();
	
		if (true == modified) {
			Canvas canvas = getCanvas();
			canvas.drawColor(0x00000000, Mode.SRC);
			for (TextBoxContent content : contents)
				content.paint(canvas);
		}
		
		reloadTexture();
		super.draw(textureMap, shaders);
	}

	public TextBoxContent addContent(String content, Paint paint, float[] position) {
		TextBoxContent c = new TextBoxContent(content, paint, position);
		contents.add(c);
		return c;
	}

}
