package de.onyxbits.photobookmark;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

public class BookmarkActivity extends Activity {

	private Intent intent;

	public static final int MAXIMAGES = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			handleSendImage(intent);
		}
		if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
			handleSendMultipleImages(intent);
		}

		finish();
	}

	void handleSendImage(Intent intent) {
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null) {
			new BookmarkTask(this).execute(imageUri);
		}
	}

	void handleSendMultipleImages(Intent intent) {
		ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		if (imageUris != null) {
			if (imageUris.size() <= MAXIMAGES) {
				new BookmarkTask(this).execute(imageUris.toArray(new Uri[0]));
			}
			else {
				String str = getString(R.string.msg_too_many, MAXIMAGES);
				Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
