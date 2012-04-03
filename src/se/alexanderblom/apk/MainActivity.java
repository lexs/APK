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
	
	public void niceBeer(View v) {
		Intent intent = new Intent(this, ArticlesActivity.class);
		
		intent.putExtra(ArticlesActivity.EXTRA_SELECTION, "product_group LIKE ?");
		intent.putExtra(ArticlesActivity.EXTRA_SELECTION_ARGS, new String[] { "…l%" });
		
		startActivity(intent);
	}
	
	public void niceWine(View v) {
		Intent intent = new Intent(this, ArticlesActivity.class);
		
		intent.putExtra(ArticlesActivity.EXTRA_SELECTION, "category IN (?,?)");
		intent.putExtra(ArticlesActivity.EXTRA_SELECTION_ARGS, new String[] { "Vitt vin", "Rštt vin" });
		
		startActivity(intent);
	}
}