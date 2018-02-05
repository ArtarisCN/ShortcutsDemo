package cn.artaris.shortcutsdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * cn.artaris.shortcutsdemo
 * ShortcutsDemo
 * 2018.02.05.下午2:31
 *
 * @author : rick
 */
public class ShowShortcutsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_shortcuts);

        TextView textView = findViewById(R.id.text_view);

        String msg = getIntent().getStringExtra("msg");
        Log.i("ShowShortcutsActivity",msg);

        textView.setText(String.format(getString(R.string.msg_format),msg));
    }
}
