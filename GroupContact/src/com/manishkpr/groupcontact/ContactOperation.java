package com.manishkpr.groupcontact;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Data;

public class ContactOperation {
	 Context con;
	 private String GroupTitle = "YourGroupName";
	 ContactOperation(Context con){
		 this.con=con;
	 }
	//## Add Photo To Contact
	void addPhoto(ArrayList<ContentProviderOperation> ops){
		
		ContentValues cv = new ContentValues();
		cv.put(Photo.PHOTO,ImageContact(R.drawable.ic_launcher));
		cv.put(Photo.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
		Builder insertOp = createInsertForContact(-1, cv);
		ops.add(insertOp.build());
	}
	byte[] ImageContact(int img){
		Resources res = con.getResources();
		Drawable drawable = res.getDrawable(img);
		Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] bitMapData = stream.toByteArray();
		return bitMapData;
	}
	private Builder createInsertForContact(long rawContactId, ContentValues cv) {
		Builder insertOp = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValues(cv);
		if (rawContactId == -1) {
			insertOp.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
		} else {
			insertOp.withValue(Data.RAW_CONTACT_ID, rawContactId);
		}
		return insertOp;
	}
	//## Function to get Group Id 
	private String getGroupId()
	{
		String GroupId = ifGroup(GroupTitle);
		if (GroupId == null)
		{
			
			ArrayList<ContentProviderOperation> opsGroup = new ArrayList<ContentProviderOperation>();
			opsGroup.add(ContentProviderOperation.newInsert(ContactsContract.Groups.CONTENT_URI)
					.withValue(ContactsContract.Groups.TITLE, GroupTitle)
					.withValue(ContactsContract.Groups.GROUP_VISIBLE, true)
					.withValue(ContactsContract.Groups.ACCOUNT_NAME, GroupTitle)
					.withValue(ContactsContract.Groups.ACCOUNT_TYPE, GroupTitle)
					.build());
			try
			{
				
				con.getContentResolver().applyBatch(ContactsContract.AUTHORITY, opsGroup);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return ifGroup(GroupTitle);
	}
    //### Function return group id by Group Title
	private String ifGroup(String $name)
	{
		String selection = ContactsContract.Groups.DELETED + "=? and " + ContactsContract.Groups.GROUP_VISIBLE + "=?";
		String[] selectionArgs = { "0", "1" };
		Cursor cursor =con.getContentResolver().query(ContactsContract.Groups.CONTENT_URI, null, selection, selectionArgs, null);
		cursor.moveToFirst();
		int len = cursor.getCount();
		
		String GroupId = null;
		for (int i = 0; i < len; i++)
		{
			String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID));
			String title = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));
			
			
			if (title.equals(GroupTitle))
			{
				GroupId = id;
				break;
			}
			
			cursor.moveToNext();
		}
		cursor.close();
		
		return GroupId;
	}
	// Add Contact To Group
	 public void addContactToGroup(ArrayList<ContentProviderOperation> ops){
		  String GroupId = getGroupId();
		  ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
	  			.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	  			.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
	  			.withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, GroupId)
	  			.build());
	 }
	
}
