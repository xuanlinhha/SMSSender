package xuanlinhha.smssender;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xuanlinhha.smssender.view.MyArrayAdapter;
import xuanlinhha.smssender.view.Receiver;

public class SendActivity extends Activity {
    private static final String SENT = "SMS_SENT";
    private static final String DELIVERED = "SMS_DELIVERED";
    private LinearLayout textContainer;
    private String message;
    private ListView listView;
    private List<Receiver> receivers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        message = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE).trim();
        TextView tv = new TextView(this);
        tv.setText(message);
        textContainer = findViewById(R.id.container);
        textContainer.addView(tv);

        receivers = new ArrayList<>();
        for (String no : getIntent().getStringArrayListExtra(MainActivity.EXTRA_NOS)) {
            Receiver receiver = new Receiver();
            receiver.setNo(no);
            receiver.setStatus(Receiver.Status.Fresh);
            receivers.add(receiver);
        }
        MyArrayAdapter myAdapter = new MyArrayAdapter(this,
                R.layout.row_layout, receivers);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(myAdapter);
    }

    public void startSending(View view) {
        for (int i = 0; i < receivers.size(); i++) {
            smsSendMessage(i);
        }
    }

    private void smsSendMessage(final int i) {
        Receiver r = receivers.get(i);
        if (r.getStatus() == Receiver.Status.Delivered) {
            return;
        }
        final String no = r.getNo();
        // prepare intents
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        updateRow(i, Receiver.Status.Sent);
                        break;
                    default:
                        updateRow(i, Receiver.Status.Fail);
                        break;
                }
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        updateRow(i, Receiver.Status.Delivered);
                        break;
                    default:
                        updateRow(i, Receiver.Status.Fail);
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(no, null, message, sentPI, deliveredPI);
    }

    private void updateRow(int index, Receiver.Status status) {
        View rowView = listView.getChildAt(index - listView.getFirstVisiblePosition());
        if (rowView == null)
            return;
        ImageView statusImgView = (ImageView) rowView.findViewById(R.id.status);
        if (status == Receiver.Status.Fresh) {
            statusImgView.setImageResource(R.drawable.send);
        } else if (status == Receiver.Status.Sent) {
            statusImgView.setImageResource(R.drawable.checkmark);
        } else if (status == Receiver.Status.Delivered) {
            statusImgView.setImageResource(R.drawable.double_tick);
        } else {
            statusImgView.setImageResource(R.drawable.fail);
        }
    }
}
