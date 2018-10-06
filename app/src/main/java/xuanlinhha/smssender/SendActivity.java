package xuanlinhha.smssender;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xuanlinhha.smssender.view.MyArrayAdapter;
import xuanlinhha.smssender.view.Receiver;
import xuanlinhha.smssender.view.ReceiverComparator;

public class SendActivity extends Activity {
    private static final String SENT = "SMS_SENT_ACTION";
    private static final String DELIVERED = "SMS_DELIVERED_ACTION";
    private TextView simLabel;
    private LinearLayout textContainer;
    private ListView listView;
    //
    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;
    // intent data
    private int subId;
    private String simName;
    private String message;
    private List<Receiver> receivers;
    // state
    private Button startBtn;
    private int nextReceiver;
    private int totalParts;
    private int partCounter;
    private ArrayList<String> parts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        simLabel = findViewById(R.id.msgLabel);
        textContainer = findViewById(R.id.container);
        listView = (ListView) findViewById(R.id.listView);
        startBtn = findViewById(R.id.startBtn);

        // get data from intent
        subId = getIntent().getIntExtra("SUBID", -1);
        simName = getIntent().getStringExtra("SIMNAME");
        message = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE).trim();
        receivers = new ArrayList<>();
        for (String no : getIntent().getStringArrayListExtra(MainActivity.EXTRA_NOS)) {
            Receiver receiver = new Receiver();
            receiver.setNo(no);
            receiver.setStatus(Receiver.Status.Fresh);
            receivers.add(receiver);
        }
        Collections.sort(receivers, new ReceiverComparator());

        // display sim & message
        simLabel.setText(simName);
        TextView tv = new TextView(this);
        tv.setText(message);
        textContainer.addView(tv);
        MyArrayAdapter myAdapter = new MyArrayAdapter(this,
                R.layout.row_layout, receivers);
        listView.setAdapter(myAdapter);

        // initial state
        startBtn.setText("Start");
        startBtn.setEnabled(true);
        nextReceiver = 0;
        totalParts = 0;
        partCounter = 0;

        // State 2
        sendBroadcastReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onReceive(Context arg0, Intent arg1) {
//                System.out.println("getResultCode()=" + getResultCode());
                switch (getResultCode()) {
                    case Activity.RESULT_OK: {
                        partCounter++;
                        if (partCounter == totalParts) {
                            updateRow(nextReceiver, Receiver.Status.Sent);
                            nextReceiver++;
                            // goto state 1
                            sendNextSMS();
                        }
                        break;
                    }
                    default: {
                        // update status of current receiver
                        updateRow(nextReceiver, Receiver.Status.Fail);
                        startBtn.setText("Start");
                        startBtn.setEnabled(true);
                        return;
                    }
                }
            }
        };
        deliveryBroadcastReceiver = new BroadcastReceiver() {
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
        };
        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
        registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sendBroadcastReceiver != null) {
            unregisterReceiver(sendBroadcastReceiver);
        }
        if (deliveryBroadcastReceiver != null) {
            unregisterReceiver(deliveryBroadcastReceiver);
        }
    }

    /**
     * State 0
     *
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void startSending(View view) {
        boolean send = false;
        for (int i = 0; i < receivers.size(); i++) {
            if (receivers.get(i).getStatus() != Receiver.Status.Sent) {
                nextReceiver = i;
                send = true;
                break;
            }
        }
        if (send) { // prepare & goto state 1
            startBtn.setText("Sending ...");
            startBtn.setEnabled(false);
            sendNextSMS();
        } else { // goto End
            return;
        }
    }


    /**
     * State 1
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void sendNextSMS() {
        // goto End
        if (nextReceiver >= receivers.size()) {
            startBtn.setText("Start");
            startBtn.setEnabled(true);
            return;
        }
        // Thread.sleep(1000);
        // prepare & goto state 2
        Receiver r = receivers.get(nextReceiver);
        SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subId);
        parts = smsManager.divideMessage(message);
        totalParts = parts.size();
        partCounter = 0;
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
        for (int i = 0; i < totalParts; i++) {
            sentIntents.add(sentPI);
            deliveryIntents.add(deliveredPI);
        }
        smsManager.sendMultipartTextMessage(r.getNo(), null, parts, sentIntents, deliveryIntents);
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
