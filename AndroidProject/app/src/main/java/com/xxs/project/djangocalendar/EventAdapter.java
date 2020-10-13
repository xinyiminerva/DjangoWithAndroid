package com.xxs.project.djangocalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.ramotion.foldingcell.FoldingCell;

import java.util.HashSet;
import java.util.List;

public class EventAdapter extends ArrayAdapter<EventModal> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();

    public EventAdapter(Context context, List<EventModal> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item for selected view
        EventModal item = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell_item, parent, false);
            // binding view parts to view holder
            viewHolder.title = cell.findViewById(R.id.item_event_title);
            viewHolder.foldcontent = cell.findViewById(R.id.event_content);
            viewHolder.contentTitle = cell.findViewById(R.id.content_event_title);
            viewHolder.content = cell.findViewById(R.id.content_event_content);
            viewHolder.expDate = cell.findViewById(R.id.content_event_exp_date);
            viewHolder.dueDate = cell.findViewById(R.id.content_event_due_date);
            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (null == item)
            return cell;

        // bind data from selected element to view through view holder
        viewHolder.title.setText(item.getFields().getEvent_title());
        viewHolder.contentTitle.setText(item.getFields().getEvent_title());
        viewHolder.foldcontent.setText(item.getFields().getEvent_text());
        viewHolder.content.setText(item.getFields().getEvent_text());
        viewHolder.expDate.setText(item.getFields().getExpected_date());
        viewHolder.dueDate.setText(item.getFields().getDue_date());
        return cell;
    }

    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    private static class ViewHolder {
        TextView title;
        TextView foldcontent;
        TextView contentTitle;
        TextView content;
        TextView expDate;
        TextView dueDate;

    }

}
