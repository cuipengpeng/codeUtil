package safekeyboard.ly.com.safekeyboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.kh.keyboard.KeyBoardDialogUtils;

import static android.R.id.edit;

public class MainActivity extends AppCompatActivity {

    private KeyBoardDialogUtils keyBoardDialogUtils;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.et);
        keyBoardDialogUtils = new KeyBoardDialogUtils(this);
        et.setInputType(InputType.TYPE_NULL);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyBoardDialogUtils.show(et);
            }
        });
    }
}
