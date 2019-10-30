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
import android.widget.Button;
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
    private Button startBtn;
    private List<Receiver> receivers;
    //
    private int nextReceiver;
    private int totalParts;
    private int partCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        startBtn = findViewById(R.id.startBtn);
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
//        Collections.sort(receivers, new ReceiverComparator());
        MyArrayAdapter myAdapter = new MyArrayAdapter(this,
                R.layout.row_layout, receivers);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(myAdapter);
        nextReceiver = 0;
    }

    public void startSending(View view) {
        if (!receivers.isEmpty()) {
            startBtn.setText("Sending ...");
            startBtn.setEnabled(false);
            registerBroadCastReceivers();
            sendNextSMS();
        }
    }

    private void sendNextSMS() {
        if (nextReceiver >= receivers.size()) {
            startBtn.setText("Start");
            startBtn.setEnabled(true);
            return;
        }
        while (nextReceiver < receivers.size()) {
            Receiver r = receivers.get(nextReceiver);
            if (r.getStatus() != Receiver.Status.Sent) {
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> parts = smsManager.divideMessage(message);
                totalParts = parts.size();
                ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
                ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
                PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
                for (int i = 0; i < this.totalParts; i++) {
                    sentIntents.add(sentPI);
                    deliveryIntents.add(deliveredPI);
                }
                partCounter = 0;
                smsManager.sendMultipartTextMessage(r.getNo(), null, parts, sentIntents, deliveryIntents);
                break;
            } else {
                nextReceiver++;
            }
        }

    }

    private void registerBroadCastReceivers() {
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK: {
                        partCounter++;
                        if (partCounter == totalParts) {
                            // update status of current receiver
                            updateRow(nextReceiver, Receiver.Status.Sent);
                            // send to next receiver
                            nextReceiver++;
                            sendNextSMS();
                        }
                        break;
                    }
                    default: {
                        // update status of current receiver
                        updateRow(nextReceiver, Receiver.Status.Fail);
                        startBtn.setText("Start");
                        startBtn.setEnabled(true);
                        break;
                    }
                }
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK: {
                        // TODO
                        break;
                    }
                    default: {
                        // TODO
                        break;
                    }
                }
            }
        }, new IntentFilter(DELIVERED));
    }

    private void updateRow(int index, Receiver.Status status) {
        receivers.get(index).setStatus(status);
        View rowView = listView.getChildAt(index - listView.getFirstVisiblePosition());
        if (rowView == null)
            return;
        ImageView statusImgView = (ImageView) rowView.findViewById(R.id.status);
        if (status == Receiver.Status.Fresh) {
            statusImgView.setImageResource(R.drawable.send);
        } else if (status == Receiver.Status.Sent) {
            statusImgView.setImageResource(R.drawable.checkmark);
        } else {
            statusImgView.setImageResource(R.drawable.fail);
        }
    }
}
