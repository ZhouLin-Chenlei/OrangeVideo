package com.community.yuequ.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.community.yuequ.R;
import com.community.yuequ.modle.RProgramDetail;

import java.util.List;

public class ProgramAdapter extends BaseAdapter {
	private List<RProgramDetail> items;
	private int selsect = 0;
	private LayoutInflater mInflater;
	private Context context;
	public ProgramAdapter(Context context,List<RProgramDetail> items) {
		this.mInflater = LayoutInflater.from(context);
		this.items = items;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (items == null)
			return 0;
		else
			return items.size();
	}

	@Override
	public Object getItem(int position) {
		if(position < getCount())
			return items.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.selectplayerapater,parent,false);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		RProgramDetail newItem = items.get(position);
		holder.title.setText(newItem.name);
		if (selsect == position) {
			holder.title.setBackgroundResource(R.color.selectplay_selected);
			
		} else {
			holder.title.setBackgroundResource(R.color.info_panel_bg);
		}
		
		return convertView;
	}
	public final class ViewHolder {
		public TextView title;
	}
	public void setSelection(int position) {
		if(position < 0)
			position = 0;
		if(position<getCount()){
			selsect = position;
			notifyDataSetChanged();
		}
		
	}
}
