package xuanlinhha.smssender;

import android.app.Activity;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import xuanlinhha.smssender.view.Receiver;

public class SendActivity extends Activity {
    private TextView msgTxtView;

    private List<String> success;
    private List<String> fail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        String message = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);
        msgTxtView = (TextView) findViewById(R.id.msgTxtView);
        msgTxtView.setText(message);

        List<Receiver> receivers = new ArrayList<>();
        for (String no:getIntent().getStringArrayListExtra(MainActivity.EXTRA_NOS)) {
            Receiver receiver = new Receiver();
            receiver.setNo(no);
            receiver.setStatus(Receiver.Status.Fresh);
        }

        ArrayAdapter<Receiver> adapter = new ArrayAdapter<Receiver>(this,
                android.R.layout.activity_list_item, receivers);
        final ListView listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(adapter);

        success = new ArrayList<>();
        fail = new ArrayList<>();
    }

//    private void smsSendMessage(View view, String no, String message) {
//        // prepare intents
//        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
//                new Intent(SENT), 0);
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
//                new Intent(DELIVERED), 0);
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS sent",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
////                        fail.add();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(getBaseContext(), "No service",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(getBaseContext(), "Null PDU",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(getBaseContext(), "Radio off",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));
//
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(getBaseContext(), "SMS not delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(DELIVERED));
//        SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage(no, null, message, sentPI, deliveredPI);
//    }
}
