package com.excellentsrc.tools.contacts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ImportContactsActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button importBtn = (Button) findViewById(R.id.import_button);
		importBtn.setOnClickListener(this);
	}

	Handler importHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			
			default:
				
			
			}
			
			
		}
	};
	
	private void importContacts(){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getAssets().open("backup_phb.txt"),"gbk"));
			String mLine = reader.readLine();
			System.out.println(mLine);
			int count = 0;
			while(mLine!=null){
				mLine =  reader.readLine();
				System.out.println(mLine);
				if(addLine(mLine))
					count++;
			}
			System.out.println("add "+ count+ "contracts");
			
			Toast.makeText(this, "add "+ count+ "contracts", Toast.LENGTH_SHORT).show();

		} catch (Exception ex) {
			ex.printStackTrace();
		
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				importContacts();
			}
		}).start();
	}

	private boolean addLine(String line) {
		String DisplayName = null;
		String MobileNumber = null;
		int dpIndex = line.indexOf("|");
		if (dpIndex != -1) {
			DisplayName = line.substring(1, dpIndex);
		}
		int mNuberIndex = line.indexOf('|', dpIndex + 1);
		if (mNuberIndex != -1)
			MobileNumber = line.substring(dpIndex + 1, mNuberIndex);
		// if(strs!=null && strs.length>=3){
		// DisplayName = strs[1];
		// MobileNumber = strs[2];
		// }
		if (DisplayName == null && MobileNumber == null)
			return false;
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
				.build());

		// ------------------------------------------------------ Names
		if (DisplayName != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
							DisplayName).build());
		}

		// ------------------------------------------------------ Mobile Number
		if (MobileNumber != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
							MobileNumber)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
							ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
					.build());
		}

		// Asking the Contact provider to create a new contact
		try {
			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Exception: " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		return false;

	}
}