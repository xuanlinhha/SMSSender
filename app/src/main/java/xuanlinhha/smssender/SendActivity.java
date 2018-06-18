package xuanlinhha.smssender;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xuanlinhha.smssender.view.MyArrayAdapter;
import xuanlinhha.smssender.view.Receiver;

public class SendActivity extends Activity {
    private TextView msgTxtView;
    private ListView listView;
    private List<Receiver> receivers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        String message = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);
        msgTxtView = (TextView) findViewById(R.id.msgTxtView);
        msgTxtView.setText(message);

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
}
