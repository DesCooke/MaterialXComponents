package com.material.components.fcm;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.material.components.R;
import com.material.components.adapter.AdapterNotification;
import com.material.components.room.AppDatabase;
import com.material.components.room.DAO;
import com.material.components.room.table.NotificationEntity;
import com.material.components.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class ActivityNotifications extends AppCompatActivity {

    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, ActivityNotifications.class);
        activity.startActivity(i);
    }

    public View parent_view;
    private RecyclerView recyclerView;
    private DAO dao;
    public AdapterNotification adapter;
    static ActivityNotifications activityNotifications;

    public static ActivityNotifications getInstance() {
        return activityNotifications;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        activityNotifications = this;

        dao = AppDatabase.getDb(this).getDAO();

        initToolbar();
        iniComponent();
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Notifications");
        Tools.setSystemBarColor(this, android.R.color.black);
    }

    private void iniComponent() {
        parent_view = findViewById(android.R.id.content);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set data and list adapter
        adapter = new AdapterNotification(this, recyclerView, new ArrayList<NotificationEntity>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterNotification.OnItemClickListener() {
            @Override
            public void onItemClick(View view, NotificationEntity obj, int pos) {
                obj.read = true;
                ActivityDialogNotification.navigate(ActivityNotifications.this, obj, false, pos);
            }
        });

        startLoadMoreAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_notification, menu);
        Tools.changeMenuIconColor(menu, Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        } else if (item_id == R.id.action_delete) {
            if (adapter.getItemCount() == 0) {
                return true;
            }
            dialogDeleteConfirmation();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void dialogDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure want to delete all notifications ?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
                dao.deleteAllNotification();
                startLoadMoreAdapter();
                Snackbar.make(parent_view, "Delete successfully", Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, null);
        builder.show();
    }

    private void startLoadMoreAdapter() {
        adapter.resetListData();
        List<NotificationEntity> items = dao.getNotificationByPage(20, 0);
        adapter.insertData(items);
        showNoItemView();
        final int item_count = (int) dao.getNotificationCount();
        // detect when scroll reach bottom
        adapter.setOnLoadMoreListener(new AdapterNotification.OnLoadMoreListener() {
            @Override
            public void onLoadMore(final int current_page) {
                if (item_count > adapter.getItemCount() && current_page != 0) {
                    displayDataByPage(current_page);
                } else {
                    adapter.setLoaded();
                }
            }
        });
    }

    private void displayDataByPage(final int next_page) {
        adapter.setLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<NotificationEntity> items = dao.getNotificationByPage(20, (next_page * 20));
                adapter.insertData(items);
                showNoItemView();
            }
        }, 500);
    }

    private void showNoItemView() {
//        View lyt_no_item = findViewById(R.id.lyt_failed);
//        (findViewById(R.id.failed_retry)).setVisibility(View.GONE);
//        ((ImageView) findViewById(R.id.failed_icon)).setImageResource(R.drawable.img_no_item);
//        ((TextView) findViewById(R.id.failed_message)).setText(R.string.no_item);
//        if (adapter.getItemCount() == 0) {
//            lyt_no_item.setVisibility(View.VISIBLE);
//        } else {
//            lyt_no_item.setVisibility(View.GONE);
//        }
    }
}
