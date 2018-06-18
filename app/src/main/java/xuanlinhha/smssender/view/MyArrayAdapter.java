package xuanlinhha.smssender.view;

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
    private final List<Receiver> receivers;

    public MyArrayAdapter(@NonNull Context context, @NonNull List<Receiver> receivers) {
        super(context, -1, receivers);
        this.receivers = receivers;
        this.context = context;
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
        // change the icon for Windows and iPhone
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
