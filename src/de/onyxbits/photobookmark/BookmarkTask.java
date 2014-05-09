package de.onyxbits.photobookmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class BookmarkTask extends AsyncTask<Uri, Object, Object> {

	private Activity activity;
	private int size;

	public BookmarkTask(Activity activity) {
		this.activity = activity;
		size = activity.getPackageManager().getDefaultActivityIcon().getMinimumHeight();
		if (size == 0) {
			size = 48;
		}
	}

	@Override
	protected Object doInBackground(Uri... params) {
		for (Uri u : params) {
			bookmark(u);
		}
		return null;
	}

	protected void onPostExecute(Object result) {
		Toast.makeText(activity, R.string.msg_bookmarked, Toast.LENGTH_SHORT).show();
		activity.finish();
	}

	private void bookmark(Uri location) {
		Intent target = null;
		
		if ("file".equals(location.getScheme())) {
			target = new Intent(Intent.ACTION_VIEW, getImageContentUri(activity,new File(location.getPath())));
		}
		else {
			target = new Intent(Intent.ACTION_VIEW, location);
		}

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, location.getLastPathSegment());
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, loadBitmap(location));
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		activity.sendBroadcast(addIntent);
	}

	private Bitmap loadBitmap(Uri location) {
		Bitmap bitmap;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), location);
			return Bitmap.createScaledBitmap(bitmap, size, size, false);
		}
		catch (FileNotFoundException e) {
			Log.w(getClass().getName(), e);
		}
		catch (IOException e) {
			Log.w(getClass().getName(), e);
		}
		return null;
	}

	public static Uri getImageContentUri(Activity context, File imageFile) {
		String filePath = imageFile.getAbsolutePath();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.Media._ID },
				MediaStore.Images.Media.DATA + "=? ", new String[] { filePath }, null);
		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
			Uri baseUri = Uri.parse("content://media/external/images/media");
			return Uri.withAppendedPath(baseUri, "" + id);
		}
		else {
			if (imageFile.exists()) {
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.DATA, filePath);
				return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						values);
			}
			else {
				return null;
			}
		}
	}

}
