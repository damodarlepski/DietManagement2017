package com.example.trzeszczu.dietmanagement2017;

import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.content.Context;
import android.widget.Filter;

public class MyAdapter<T> extends ArrayAdapter<String> implements Filterable{
    public MyAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null) {
                filterResults.count = getCount();
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                notifyDataSetChanged();
            }
        }
    };
}
