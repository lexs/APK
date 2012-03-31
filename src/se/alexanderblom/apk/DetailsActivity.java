package se.alexanderblom.apk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class DetailsActivity extends FragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_details);
		
		Intent intent = getIntent();
		Uri uri = intent.getData();
	}
}
