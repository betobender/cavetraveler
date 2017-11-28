package com.cave.traveler;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.renderscript.Font.Style;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ScoreListViewAdapter extends ArrayAdapter<ScoreListViewItem> {

	private Context context;
	private int textViewResourceId;
	private List<ScoreListViewItem> objects;

	public ScoreListViewAdapter(Context context, int textViewResourceId, List<ScoreListViewItem> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (null == view) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(textViewResourceId, null);
		}

		if (objects.get(position) != null && position > 0) {
			
			int color = 0xFFFFFFFF;
			int style = Typeface.NORMAL;
			if(objects.get(position).isMyself()) {
				color = 0xFFFFFF00;
				style = Typeface.BOLD;
			}
			
			TextView text1 = (TextView) view.findViewById(R.id.listViewText1);
			text1.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"), style);
			text1.setText("" + objects.get(position).getPosition());
			text1.setTextColor(color);

			TextView text2 = (TextView) view.findViewById(R.id.listViewText2);
			text2.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"), style);
			text2.setText("" + objects.get(position).getScore());
			text2.setTextColor(color);

			TextView text3 = (TextView) view.findViewById(R.id.listViewText3);
			text3.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"), style);
			text3.setText("" + objects.get(position).getDistance());
			text3.setTextColor(color);

			TextView text4 = (TextView) view.findViewById(R.id.listViewText4);
			text4.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"), style);
			text4.setText(objects.get(position).getPlayer());
			text4.setTextColor(color);
			
		} else if (objects.get(position) != null && position == 0) {
			int color = 0xFF8080FF;
			
			TextView text1 = (TextView) view.findViewById(R.id.listViewText1);
			text1.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"), Typeface.BOLD);
			text1.setText("Position");
			text1.setTextColor(color);

			TextView text2 = (TextView) view.findViewById(R.id.listViewText2);
			text2.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"), Typeface.BOLD);
			text2.setText("Score");
			text2.setTextColor(color);

			TextView text3 = (TextView) view.findViewById(R.id.listViewText3);
			text3.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"), Typeface.BOLD);
			text3.setText("Distance");
			text3.setTextColor(color);

			TextView text4 = (TextView) view.findViewById(R.id.listViewText4);
			text4.setTypeface(ResourceFactory.getInstance().createFontFromAssets("fonts/AstronBoy.ttf"), Typeface.BOLD);
			text4.setText("Player");
			text4.setTextColor(color);
		}

		return view;
	}

}
