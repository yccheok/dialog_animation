package org.yccheok.dialog_animation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.util.ArrayList;

/**
 * Created by yccheok on 7/11/2015.
 */
public class ShopDialogFragmentActivity extends SherlockFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            ShopDialogFragment shopDialogFragment = ShopDialogFragment.newInstance();
            shopDialogFragment.show(this.getSupportFragmentManager(), SHOP_DIALOG_FRAGMENT);
        }
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, ShopDialogFragmentActivity.class);
        context.startActivity(intent);
    }

    private static final String TAG = ShopDialogFragmentActivity.class.getSimpleName();

    public static final String SHOP_DIALOG_FRAGMENT = "SHOP_DIALOG_FRAGMENT";
}
