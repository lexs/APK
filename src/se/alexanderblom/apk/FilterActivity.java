package se.alexanderblom.apk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class FilterActivity extends FragmentActivity {
	public static final String EXTRA_SELECTION = "selection";
	public static final String EXTRA_SELECTION_ARGS = "selection_args";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_filter);
	}
	
	public void setFilter(View v) {
		Intent data = new Intent();
		data.putExtra(EXTRA_SELECTION, "product_group=?");
		data.putExtra(EXTRA_SELECTION_ARGS, new String[] { "Vitt vin" });
		
		setResult(RESULT_OK, data);
		finish();
	}
}
