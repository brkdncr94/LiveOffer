package cs310.brkdncr.liveoffer_deneme2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by brkdn on 28/04/2016.
 */
public class OfferListAdapter extends ArrayAdapter<Offer> {

    public OfferListAdapter(Context ctx, List<Offer> offers){

        super(ctx,android.R.layout.simple_list_item_1,offers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder  = null;

        if(row == null){
            LayoutInflater inflater =((Activity) getContext()).getLayoutInflater();
            row = inflater.inflate(R.layout.row_layout, parent, false);

            holder = new ViewHolder();

            holder.offerTitle = (TextView)row.findViewById(R.id.offerTitle);
            holder.companyName = (TextView)row.findViewById(R.id.companyName);

            row.setTag(holder);
        }

        holder = (ViewHolder)row.getTag();
        holder.offerTitle.setText(getItem(position).getTitle());
        holder.companyName.setText(getItem(position).getCompanyName());

        return row;
    }

    class ViewHolder{
        TextView offerTitle;
        TextView companyName;

    }
}
