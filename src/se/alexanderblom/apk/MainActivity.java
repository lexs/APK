package se.alexanderblom.apk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class MainActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
    }
    
    public void sortByAPK(View v) {
    	Intent intent = new Intent(this, ArticlesActivity.class);
    	startActivity(intent);
    }
}