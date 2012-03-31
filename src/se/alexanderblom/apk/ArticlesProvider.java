package se.alexanderblom.apk;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import se.alexanderblom.apk.ArticleContract.Columns;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.io.InputSupplier;
import com.google.common.io.LineReader;

public class ArticlesProvider extends ContentProvider {
	private static final String TAG = "ArticlesProvider";
	
	private static final String TABLE_NAME = "articles";
	
	private static final int ARTICLES = 1;
    private static final int ARTICLES_ID = 2;
    
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    private DatabaseHelper openHelper;
    
    static {
    	uriMatcher.addURI(ArticleContract.CONTENT_AUTHORITY, "article", ARTICLES);
    	uriMatcher.addURI(ArticleContract.CONTENT_AUTHORITY, "article/#", ARTICLES_ID);
    }
	
	@Override
	public boolean onCreate() {
		openHelper = new DatabaseHelper(getContext());
		
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

	private static class DatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "articles.db";
		private static final int DATABASE_VERSION = 1;

		private Context context;
		private String dbPath;
		
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			
			this.context = context;
			
			dbPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
			Log.d(TAG, "db path: " + dbPath);
			
			//SQLiteDatabase db = getWritableDatabase();
			//db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			//onCreate(db);
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//db.close();
			
			try {
				InputStream in = context.getAssets().open("database.txt");
				InputStreamReader reader = new InputStreamReader(in, "utf-8");
				LineReader lineReader = new LineReader(reader);
				
				String line;
				while ((line = lineReader.readLine()) != null) {
					try {
						if (!TextUtils.isEmpty(line)) {
							db.execSQL(line);
						}
					} catch (SQLException e) {
						Log.e(TAG, "Failed to execute sql: ", e);
					}
				}
				
				db.execSQL("CREATE INDEX apk_index ON " + TABLE_NAME + " ("+ Columns.APK +");");
			} catch (IOException e) {
				Log.e(TAG, "Failed to create database", e);
			}
			
			
			/*try {
				InputStream in = context.getAssets().open(DATABASE_NAME);
				File out = context.getDatabasePath(DATABASE_NAME);
				
				Files.copy(new Supplier(in), out);
				
				//Access the copied database so SQLiteHelper will cache it and mark it as created.
				//getWritableDatabase().close();
				//getWritableDatabase();
			} catch (IOException e) {
				Log.e(TAG, "Failed to copy database", e);
			}*/
			
			
			
			
			
			/*db.execSQL("CREATE TABLE " + TABLE_NAME + "("
					+ Columns._ID + " INTEGER PRIMARY KEY,"
					+ Columns.NAME + " TEXT,"
					+ Columns.NAME2 + " TEXT,"
					+ Columns.PRICE + " REAL,"
					+ Columns.VOLUME + " REAL,"
					+ Columns.PRICE_PER_LITER + " REAL,"
					+ Columns.PRODUCT_GROUP + " TEXT,"
					+ Columns.PACKAGING + " TEXT,"
					+ Columns.ORIGIN + " TEXT,"
					+ Columns.ORIGIN_COUNTRY + " TEXT,"
					+ Columns.PRODUCER + " TEXT,"
					+ Columns.DISTRIBUTOR + " TEXT,"
					+ Columns.ALCOHOL_PERCENTAGE + " REAL,"
					+ Columns.INGREDIENTS + " TEXT,"
					+ Columns.APK + " REAL"
					+ ")");*/
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "Upgrading database from version " + oldVersion + "to version " + newVersion
				+ ". Which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	        onCreate(db);
		}
		
		private static class Supplier implements InputSupplier<InputStream> {
			private InputStream is;
			
			public Supplier(InputStream is) {
				this.is = is;
			}

			@Override
			public InputStream getInput() throws IOException {
				return is;
			}
			
		}

	}
}
