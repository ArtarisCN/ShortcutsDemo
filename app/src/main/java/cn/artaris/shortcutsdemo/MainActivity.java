package cn.artaris.shortcutsdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * cn.artaris.shortcutsdemo
 * ShortcutsDemo
 * 2018.02.03.下午2:31
 *
 * @author : rick
 */

public class MainActivity extends AppCompatActivity {



    private Context mContext;


    private ShortcutManager mShortcutManager;

    private int mMaxShortcutCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_main);

        Button addButton = findViewById(R.id.btn_dialog_1);
        Button deleteButton = findViewById(R.id.btn_dialog_2);
        Button managerButton = findViewById(R.id.btn_dialog_3);
        Button updateButton = findViewById(R.id.btn_dialog_4);

        addButton.setOnClickListener(mButtonOnClickListener);
        deleteButton.setOnClickListener(mButtonOnClickListener);
        managerButton.setOnClickListener(mButtonOnClickListener);
        updateButton.setOnClickListener(mButtonOnClickListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            /*
            * 获取 ShortcutManager 管理 shortcuts
            */
            mShortcutManager = getSystemService(ShortcutManager.class);
            /*
            * 获取 ShortcutManager 支持的最多 shortcuts 个数
            */
            mMaxShortcutCount = mShortcutManager.getMaxShortcutCountPerActivity();
        } else {
            new AlertDialog.Builder(mContext)
                    .setTitle(getString(R.string.main_dialog_simple_title))
                    .setMessage(getString(R.string.main_dialog_simple_message))
                    .setNeutralButton(getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }


    int itemSelected = -1;
    private View.OnClickListener mButtonOnClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N_MR1)
        @Override
        public void onClick(View v) {
            final List<ShortcutInfo> shortcutInfos = mShortcutManager.getDynamicShortcuts();
            final String[] singleChoiceItems = getResources().getStringArray(R.array.dialog_choice_array);

            itemSelected = -1;
            switch (v.getId()){
                case R.id.btn_dialog_1:

                    if(shortcutInfos.size() > mMaxShortcutCount){
                        Toast.makeText(mContext, getString(R.string.shortcuts_size_is_max),Toast.LENGTH_LONG).show();
                        return;
                    }


                    new AlertDialog.Builder(mContext)
                            .setTitle(getString(R.string.main_dialog_single_choice))
                            .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    itemSelected = i;
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_cancel), null)
                            .setPositiveButton(getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Icon icon = Icon.createWithResource(mContext, R.mipmap.ic_favorite_black_24dp);

                                    Intent intent = new Intent(mContext, ShowShortcutsActivity.class);
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.putExtra("msg", singleChoiceItems[itemSelected]);

                                    ShortcutInfo shortcut = new ShortcutInfo.Builder(mContext, String.valueOf(itemSelected))
                                            .setShortLabel(singleChoiceItems[itemSelected])
                                            .setLongLabel(singleChoiceItems[itemSelected])
                                            .setIcon(icon)
                                            .setIntent(intent)
                                            .build();

                                    for (ShortcutInfo shortcutInfo : shortcutInfos) {

                                        if(shortcutInfo.getId().equals(String.valueOf(itemSelected))){
                                            Toast.makeText(mContext, getString(R.string.shortcuts_size_is_exist),Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }

                                    shortcutInfos.add(shortcut);

                                    mShortcutManager.addDynamicShortcuts(Arrays.asList(shortcut));
                                }
                            })
                            .show();
                    break;
                case R.id.btn_dialog_2: {
                    if (shortcutInfos.size() == 0) {
                        Toast.makeText(mContext, getString(R.string.shortcuts_size_is_empty), Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<String> shortName = new ArrayList<>();

                    for (ShortcutInfo shortcutInfo : shortcutInfos) {
                        if (!TextUtils.isEmpty(shortcutInfo.getShortLabel())) {
                            shortName.add(shortcutInfo.getShortLabel().toString());
                        }
                    }

                    String[] strArr = new String[shortName.size()];

                    new AlertDialog.Builder(mContext)
                            .setTitle(getString(R.string.main_dialog_single_choice))
                            .setSingleChoiceItems(shortName.toArray(strArr), itemSelected, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    itemSelected = i;
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_cancel), null)
                            .setPositiveButton(getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mShortcutManager.removeDynamicShortcuts(Collections.singletonList(shortcutInfos.get(itemSelected).getId()));
                                }
                            })
                            .show();

                    break;
                }
                case R.id.btn_dialog_3: {

                    final List<ShortcutInfo> pinnedShortcutInfos = mShortcutManager.getPinnedShortcuts();

                    if (pinnedShortcutInfos.size() == 0) {
                        Toast.makeText(mContext, getString(R.string.pinned_shortcuts_size_is_empty), Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<String> shortName = new ArrayList<>();

                    for (ShortcutInfo shortcutInfo : pinnedShortcutInfos) {
                        if (!TextUtils.isEmpty(shortcutInfo.getShortLabel())) {
                            shortName.add(shortcutInfo.getShortLabel().toString());
                        }
                    }

                    String[] strArr = new String[shortName.size()];

                    new AlertDialog.Builder(mContext)
                            .setTitle(getString(R.string.main_dialog_single_choice))
                            .setSingleChoiceItems(shortName.toArray(strArr), itemSelected, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    itemSelected = i;
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_cancel), null)
                            .setPositiveButton(getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mShortcutManager.disableShortcuts(Collections.singletonList(pinnedShortcutInfos.get(itemSelected).getId()), "这个泡泡睡着了");
                                }
                            })
                            .show();


                    break;
                }
                case R.id.btn_dialog_4: {
                    if (shortcutInfos.size() == 0) {
                        Toast.makeText(mContext, getString(R.string.shortcuts_size_is_empty), Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<String> shortName = new ArrayList<>();

                    for (ShortcutInfo shortcutInfo : shortcutInfos) {
                        if (!TextUtils.isEmpty(shortcutInfo.getShortLabel())) {
                            shortName.add(shortcutInfo.getShortLabel().toString());
                        }
                    }

                    String[] strArr = new String[shortName.size()];

                    new AlertDialog.Builder(mContext)
                            .setTitle(getString(R.string.main_dialog_single_choice))
                            .setSingleChoiceItems(shortName.toArray(strArr), itemSelected, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    itemSelected = i;
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_cancel), null)
                            .setPositiveButton(getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if(itemSelected < 0)
                                        return;

                                    ShortcutInfo info = shortcutInfos.get(itemSelected);
                                    boolean flag = info.getIntent().getBooleanExtra("flag",false);

                                    Icon icon;
                                    if(flag){
                                        icon = Icon.createWithResource(mContext, R.mipmap.ic_favorite_border_black_24dp);
                                    } else {
                                        icon = Icon.createWithResource(mContext, R.mipmap.ic_favorite_black_24dp);
                                    }

                                    Intent intent = new Intent(mContext, ShowShortcutsActivity.class);
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.putExtra("msg", shortcutInfos.get(itemSelected).getLongLabel());
                                    intent.putExtra("flag", !flag);

                                    String label = shortcutInfos.get(itemSelected).getLongLabel().toString();

                                    String id = "-1";

                                    for (int i = 0; i < singleChoiceItems.length; i++) {
                                        if(singleChoiceItems[i].equals(label)){
                                            id = String.valueOf(i);
                                        }
                                    }

                                    ShortcutInfo shortcut = new ShortcutInfo.Builder(mContext, id)
                                            .setShortLabel(label)
                                            .setLongLabel(label)
                                            .setIcon(icon)
                                            .setIntent(intent)
                                            .build();

                                    mShortcutManager.updateShortcuts(Collections.singletonList(shortcut));
                                }
                            })
                            .show();

                    break;
                }
                default:
                    break;
            }
        }
    };
}