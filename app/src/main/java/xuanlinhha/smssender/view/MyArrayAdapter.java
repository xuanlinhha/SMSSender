package xuanlinhha.smssender.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xuanlinhha.smssender.R;

public class MyArrayAdapter extends ArrayAdapter<Receiver> {
    private final Context context;
    private int resource;
    private List<Receiver> receivers;

    public MyArrayAdapter(@NonNull Context context, int resource, @NonNull List<Receiver> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.receivers = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Receiver receiver = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        ImageView status = (ImageView) rowView.findViewById(R.id.status);
        TextView noTxtView = (TextView) rowView.findViewById(R.id.no);
        noTxtView.setText(receiver.getNo());
        if (receiver.getStatus() == Receiver.Status.Fresh) {
            status.setImageResource(R.drawable.going);
        } else if (receiver.getStatus() == Receiver.Status.Success) {
            status.setImageResource(R.drawable.ok);
        } else {
            status.setImageResource(R.drawable.fail);
        }
        return rowView;
    }
}
