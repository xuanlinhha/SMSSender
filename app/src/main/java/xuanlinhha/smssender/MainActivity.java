package xuanlinhha.smssender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_NOS = "xuanlinhha.smssender.NOS";
    public static final String EXTRA_MESSAGE = "xuanlinhha.smssender.MESSAGE";
    private EditText phoneNumbersTxtEdit;
    private EditText messageTxtEdit;

    private Pattern chineseNo = Pattern.compile("(\\+86[0-9]{11})|(\\+65[0-9]{8})|([0-9]{11})");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneNumbersTxtEdit = (EditText) findViewById(R.id.phoneNumbersTxtEdit);
        messageTxtEdit = (EditText) findViewById(R.id.messageTxtEdit);
    }

    public void prepareSending(View view) {
        List<String> nos = new ArrayList<>();
        String tmp = phoneNumbersTxtEdit.getText().toString();
        Matcher m = chineseNo.matcher(tmp);
        while (m.find()) {
            String phone = m.group();
            if (!phone.startsWith("+")) {
                phone = "+86" + phone;
            }

            if (!nos.contains(phone)) {
                nos.add(phone);
            }
        }
        String message = messageTxtEdit.getText().toString();
        Intent intent = new Intent(this, SendActivity.class);
        intent.putStringArrayListExtra(EXTRA_NOS, new ArrayList<String>(nos));
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}
