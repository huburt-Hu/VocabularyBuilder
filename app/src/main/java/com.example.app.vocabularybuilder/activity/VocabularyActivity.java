package com.example.app.vocabularybuilder.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.app.vocabularybuilder.R;

/**
 * Created by hxb on 2016/5/7.
 */
public class VocabularyActivity extends Activity {
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        listView = (ListView)findViewById(R.id.vocabulary_listView);
        
    }
}
