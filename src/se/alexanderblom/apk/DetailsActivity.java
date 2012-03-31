package se.alexanderblom.apk;

import se.alexanderblom.apk.ArticleContract.Columns;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

public class DetailsActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {
	private Uri articleUri;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_details);
		
		Intent intent = getIntent();
		articleUri = intent.getData();
		
		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, articleUri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		c.moveToFirst();
		
		String name = c.getString(c.getColumnIndexOrThrow(Columns.NAME));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
}
