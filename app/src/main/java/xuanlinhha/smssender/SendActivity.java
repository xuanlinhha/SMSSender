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
    private int currentReceiver;
    private int totalParts;
    private int currentPart;

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
        MyArrayAdapter myAdapter = new MyArrayAdapter(this,
                R.layout.row_layout, receivers);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(myAdapter);
    }

    public void startSending(View view) {

        if (!receivers.isEmpty()) {
            startBtn.setText("Sending ...");
            startBtn.setEnabled(false);
            currentReceiver = 0;
            registerBroadCastReceivers();
            sendSMS();
        }
    }

    private void sendSMS() {
        Receiver r = receivers.get(currentReceiver);
        if (r.getStatus() != Receiver.Status.Delivered) {
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
            currentPart = 0;

            smsManager.sendMultipartTextMessage(r.getNo(), null, parts, sentIntents, deliveryIntents);
        }
    }

    private void sendNextMessage() {
        if (currentReceiver < receivers.size()) {
            sendSMS();
        } else {
            startBtn.setText("Start");
            startBtn.setEnabled(true);
        }
    }

    private void registerBroadCastReceivers() {
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK: {
                        currentPart++;
                        if (currentPart == totalParts) {
                            // update status of current receiver
                            updateRow(currentReceiver, Receiver.Status.Sent);
                            // send to next receiver
                            currentReceiver++;
                            sendNextMessage();
                        }
                        break;
                    }

                    default: {
                        // update status of current receiver
                        updateRow(currentReceiver, Receiver.Status.Fail);
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
//                        currentPart++;
//                        System.out.println("received part" + currentPart);
//                        if (currentPart == totalParts) {
//                            System.out.println("received all parts");
//                            // update status of current receiver
//                            updateRow(currentReceiver, Receiver.Status.Delivered);
//                            // send to next receiver
//                            currentReceiver++;
//                            sendNextMessage();
//                        }
                        break;
                    }
                    default: {
                        // update status of current receiver
                        updateRow(currentReceiver, Receiver.Status.Fail);
                        startBtn.setText("Start");
                        startBtn.setEnabled(true);
                        break;
                    }
                }
            }
        }, new IntentFilter(DELIVERED));
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
