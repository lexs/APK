package se.alexanderblom.apk;

import se.alexanderblom.apk.ArticleContract.Columns;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ArticlesProvider extends ContentProvider {
	private static final String DATABASE_NAME = "articles.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_NAME = "articles";
	
	private static final int ARTICLES = 1;
    private static final int ARTICLES_ID = 2;
    
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    private AssetDBOpenHelper openHelper;

    static {
    	uriMatcher.addURI(ArticleContract.CONTENT_AUTHORITY, "article", ARTICLES);
    	uriMatcher.addURI(ArticleContract.CONTENT_AUTHORITY, "article/#", ARTICLES_ID);
    }
	
	@Override
	public boolean onCreate() {
		openHelper = new AssetDBOpenHelper(getContext(), DATABASE_NAME, DATABASE_NAME, null, DATABASE_VERSION);
		
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		int match = uriMatcher.match(uri);
		switch (match) {
			case ARTICLES:
				return "vnd.android.cursor.dir/articles";
			case ARTICLES_ID:
				return "vnd.android.cursor.item/articles";
			default:
				throw new IllegalArgumentException("Unknown uri");
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		int match = uriMatcher.match(uri);
		switch (match) {
			case ARTICLES_ID:
				long id = ContentUris.parseId(uri);
				qb.appendWhere(Columns._ID + "=");
				qb.appendWhere(String.valueOf(id));
			case ARTICLES:
				qb.setTables(TABLE_NAME);
				break;
			default:
				throw new IllegalArgumentException("Unknown uri");
		}
		
		if (sortOrder == null) {
			sortOrder = ArticleContract.DEFAULT_SORT_ORDER;
		}
		
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (uriMatcher.match(uri) != ARTICLES) {
			throw new IllegalArgumentException("Can't insert to: " + uri);
		}
		
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long id = db.insert(TABLE_NAME, null, values);
		
		if (id < 0) {
			throw new SQLException("Failed to insert row");
		}
		
		notifyChange(ArticleContract.CONTENT_URI);
		
		return ContentUris.withAppendedId(ArticleContract.CONTENT_URI, id);
	}
	

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		if (uriMatcher.match(uri) != ARTICLES_ID) {
			throw new UnsupportedOperationException("Can not update uri: " + uri);
		}
		
		long id = ContentUris.parseId(uri);
		
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count = db.update(TABLE_NAME, values, Columns._ID + "=?", new String[] { String.valueOf(id) });
		
		notifyChange(uri);
		
		return count;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = uriMatcher.match(uri);
		switch (match) {
			case ARTICLES:
				break;
			case ARTICLES_ID:
				long id = ContentUris.parseId(uri);
				selection = Columns._ID + "=?";
				selectionArgs = new String[] { String.valueOf(id) };
				break;
			default:
				throw new IllegalArgumentException("Unknown uri: " + uri);
		}
		
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count = db.delete(TABLE_NAME, selection, selectionArgs);
		
		notifyChange(uri);
		
		return count;
	}

	private void notifyChange(Uri uri) {
		getContext().getContentResolver().notifyChange(uri, null);
	}
}
