package com.mrbackend.url;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText editTextUrl;
    RecyclerView recyclerView;
    UrlAdapter adapter;
    ArrayList<UrlModel> urlList;
    SharedPreferences sharedPreferences;
    Gson gson = new Gson();

    private static final String PREF_NAME = "url_storage";
    private static final String KEY_URLS = "urls_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUrl = findViewById(R.id.editTextUrl);
        recyclerView = findViewById(R.id.recyclerViewUrls);
        findViewById(R.id.buttonAdd).setOnClickListener(v -> addUrl());

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loadUrls();

        adapter = new UrlAdapter(this, urlList, this::editUrl);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull android.view.View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = spacingInPixels;
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                urlList.remove(pos);
                adapter.notifyItemRemoved(pos);
                saveUrls();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void addUrl() {
        String url = editTextUrl.getText().toString().trim();
        if (!TextUtils.isEmpty(url)) {
            urlList.add(new UrlModel(url));
            adapter.notifyItemInserted(urlList.size() - 1);
            editTextUrl.setText("");
            saveUrls();
        } else {
            Toast.makeText(this, "لطفاً یک آدرس وارد کنید", Toast.LENGTH_SHORT).show();
        }
    }

    private void editUrl(int position) {
        UrlModel model = urlList.get(position);
        EditText input = new EditText(this);
        input.setText(model.getUrl());

        new AlertDialog.Builder(this)
                .setTitle("ویرایش لینک")
                .setView(input)
                .setPositiveButton("ذخیره", (dialog, which) -> {
                    String newUrl = input.getText().toString().trim();
                    if (!newUrl.isEmpty()) {
                        model.setUrl(newUrl);
                        adapter.notifyItemChanged(position);
                        saveUrls();
                    }
                })
                .setNegativeButton("لغو", null)
                .show();
    }

    private void saveUrls() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(urlList);
        editor.putString(KEY_URLS, json);
        editor.apply();
    }

    private void loadUrls() {
        String json = sharedPreferences.getString(KEY_URLS, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<UrlModel>>(){}.getType();
            urlList = gson.fromJson(json, type);
        } else {
            urlList = new ArrayList<>();
        }
    }
}
