package com.material.components.fcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.material.components.R;
import com.material.components.activity.MainMenu;
import com.material.components.model.NotifType;
import com.material.components.room.AppDatabase;
import com.material.components.room.DAO;
import com.material.components.room.table.NotificationEntity;
import com.material.components.utils.Tools;

public class ActivityDialogNotification extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    private static final String EXTRA_FROM_NOTIF = "key.EXTRA_FROM_NOTIF";
    private static final String EXTRA_POSITION = "key.EXTRA_FROM_POSITION";

    // activity transition
    public static void navigate(Activity activity, NotificationEntity obj, Boolean from_notif, int position) {
        Intent i = navigateBase(activity, obj, from_notif);
        i.putExtra(EXTRA_POSITION, position);
        activity.startActivity(i);
    }

    public static Intent navigateBase(Context context, NotificationEntity obj, Boolean from_notif) {
        Intent i = new Intent(context, ActivityDialogNotification.class);
        i.putExtra(EXTRA_OBJECT, obj);
        i.putExtra(EXTRA_FROM_NOTIF, from_notif);
        return i;
    }

    private Boolean from_notif;
    private NotificationEntity notification;
    private Intent intent;
    private DAO dao;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_notification);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dao = AppDatabase.getDb(this).getDAO();

        notification = (NotificationEntity) getIntent().getSerializableExtra(EXTRA_OBJECT);
        from_notif = getIntent().getBooleanExtra(EXTRA_FROM_NOTIF, false);
        position = getIntent().getIntExtra(EXTRA_POSITION, -1);

        // set notification as read
        notification.read = true;
        dao.insertNotification(notification);

        initComponent();
    }

    private void initComponent() {
        ((TextView) findViewById(R.id.title)).setText(notification.title);
        ((TextView) findViewById(R.id.content)).setText(notification.content);
        ((TextView) findViewById(R.id.date)).setText(Tools.getFormattedDateSimple(notification.created_at));
        ((TextView) findViewById(R.id.type)).setText(notification.type);

        String image_url = null;
        final String type = notification.type;
        intent = new Intent(this, MainMenu.class);
        if (type.equalsIgnoreCase(NotifType.IMAGE.name())) {
            image_url = notification.image;

        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }

        if (from_notif) {
            (findViewById(R.id.bt_delete)).setVisibility(View.GONE);
            if (MainMenu.active && (type.equalsIgnoreCase(NotifType.NORMAL.name()) || type.equalsIgnoreCase(NotifType.IMAGE.name()))) {
                ((LinearLayout) findViewById(R.id.lyt_action)).setVisibility(View.GONE);
            }
            ((TextView) findViewById(R.id.dialog_title)).setText(R.string.app_name);
        } else {
            if (type.equalsIgnoreCase(NotifType.NORMAL.name()) || type.equalsIgnoreCase(NotifType.IMAGE.name())) {
                (findViewById(R.id.bt_open)).setVisibility(View.GONE);
            }
        }

        (findViewById(R.id.lyt_image)).setVisibility(View.GONE);
        if (image_url != null) {
            (findViewById(R.id.lyt_image)).setVisibility(View.VISIBLE);
            Tools.displayImageOriginal(this, ((ImageView) findViewById(R.id.image)), image_url);
        }

        ((ImageView) findViewById(R.id.img_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        (findViewById(R.id.bt_open)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionOpen(type);
            }
        });

        (findViewById(R.id.bt_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (!from_notif && position != -1) {
                    dao.deleteNotification(notification.id);
                    ActivityNotifications.getInstance().adapter.removeItem(position);
                    Snackbar.make(ActivityNotifications.getInstance().parent_view, "Delete successfully", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        if (from_notif && type.equalsIgnoreCase(NotifType.LINK.name())) {
            actionOpen(type);
        }
    }

    private void actionOpen(String type) {
        if (type.equalsIgnoreCase(NotifType.LINK.name())) {
            Tools.openInAppBrowser(ActivityDialogNotification.this, notification.link, from_notif);
        } else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 509) {
            if (from_notif) {
                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            finish();
        }
    }
}