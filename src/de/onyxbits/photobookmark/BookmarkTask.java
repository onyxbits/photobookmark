package de.onyxbits.photobookmark;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

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
		for (Uri u:params) {
			bookmark(u);
		}
		return null;
	}
	
	protected void onPostExecute(Object result) {
		activity.finish();
	}

	private void bookmark(Uri location) {
		Intent target = new Intent(Intent.ACTION_VIEW, location);

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

}
