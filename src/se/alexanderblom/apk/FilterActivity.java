package se.alexanderblom.apk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class FilterActivity extends FragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_filter);
	}
	
	public void clearFilter(View v) {
		setFilter(null, null);
	}
	
	public void setCurrentFilter(View v) {
		setFilter("category=?", new String[] { "Vitt vin" });
	}
	
	private void setFilter(String selection, String[] selectionArgs) {
		Intent data = new Intent();
		data.putExtra(ArticlesActivity.EXTRA_SELECTION, selection);
		data.putExtra(ArticlesActivity.EXTRA_SELECTION_ARGS, selectionArgs);
		
		setResult(RESULT_OK, data);
		finish();
	}
}
