package xuanlinhha.smssender;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_NOS = "xuanlinhha.smssender.NOS";
    public static final String EXTRA_MESSAGE = "xuanlinhha.smssender.MESSAGE";
    private EditText phoneNumbersTxtEdit;
    private EditText messageTxtEdit;

    private Pattern chineseNo = Pattern.compile("(\\+86[0-9]{11})|(\\+65[0-9]{8})|([0-9]{11})");

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneNumbersTxtEdit = (EditText) findViewById(R.id.phoneNumbersTxtEdit);
        messageTxtEdit = (EditText) findViewById(R.id.messageTxtEdit);
        LinearLayout ll = (LinearLayout) findViewById(R.id.buttonView);
        SubscriptionManager subscriptionManager= (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        for (int i = 0; i < subscriptionInfoList.size(); i++) {
            final String simName = "SIM" + (i + 1);
            final int subId = subscriptionInfoList.get(i).getSubscriptionId();
            Button btn = new Button(this);
            btn.setText(simName);
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    prepareSending(view, simName, subId);
                }
            });
            ll.addView(btn);
        }
    }

    public void prepareSending(View view, String simName, int subId) {
        Set<String> nos = new HashSet<>();
        String tmp = phoneNumbersTxtEdit.getText().toString();
        Matcher m = chineseNo.matcher(tmp);
        while (m.find()) {
            String phone = m.group();
            if (!phone.startsWith("+") && !phone.startsWith("+86")) {
                phone = "+86" + phone;
            }
            nos.add(phone);
        }
        String message = messageTxtEdit.getText().toString();
        Intent intent = new Intent(this, SendActivity.class);
        intent.putExtra("SIMNAME", simName);
        intent.putExtra("SUBID", subId);
        intent.putStringArrayListExtra(EXTRA_NOS, new ArrayList<String>(nos));
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}
