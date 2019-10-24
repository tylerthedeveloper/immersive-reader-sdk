package com.example.java_based_client;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImmersiveReaderLauncher mImmersiveReaderLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ViewGroup webViewParent = findViewById(R.id.webViewParent);
        mImmersiveReaderLauncher = new ImmersiveReaderLauncher(MainActivity.this, webViewParent);
        Button goButton = findViewById(R.id.btnGo);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<ReadableTextChunk> readableTextChunks = new ArrayList<>();
                readableTextChunks.add(new ReadableTextChunk("New english content", "en"));
                readableTextChunks.add(new ReadableTextChunk("सारी दुनिया आपसे बस एक बटन की दुरी पर हैI जी हां, कंप्यूटर एक ऐसी मशीन है जिसने दुनिया को आपके पास ला दिया हैI यह विज्ञान का एक अद्भुत वरदान हैI", "hi"));
                readableTextChunks.add(new ReadableTextChunk("Some more random content", "en"));
                ReadableContent readableContent = new ReadableContent("This is title", readableTextChunks);

                mImmersiveReaderLauncher.launch(readableContent);

            }
        });
    }
}
