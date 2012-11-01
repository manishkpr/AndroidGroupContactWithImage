package com.manishkpr.groupcontact;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionButton();
        
    }
    void actionButton(){
    	Button btn=(Button)findViewById(R.id.button1);
    	btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), createContact(), Toast.LENGTH_SHORT).show();
				
			}
		});
    }
    String createContact(){
    	ArrayList<ContentProviderOperation> ops =  new ArrayList<ContentProviderOperation>(); 
    	ContactOperation co=new ContactOperation(this);    
	        ops.add(ContentProviderOperation.newInsert( 
	            ContactsContract.RawContacts.CONTENT_URI) 
	            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null) 
	            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null) 
	            .build() 
	        ); 
	        co.addPhoto(ops);
	        co.addContactToGroup(ops);
	        contactName("Demo Contact",ops);
	        contactNumber("1234567890",ops);
	        return contactProvider(ops);
    }
    //### Contact provider to create a new contact   
    String contactProvider(ArrayList<ContentProviderOperation> ops){      
    	String what;
        try{ 
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops); 
            what="Contact Created ";
        }  
        catch (Exception e){                
            e.printStackTrace(); 
            what="Unable to Create Contact ";
        } 
        return what;
    }
    //### Contact Name
    void contactName(String name,ArrayList<ContentProviderOperation> ops){
    	 ops.add(ContentProviderOperation.newInsert( 
	                ContactsContract.Data.CONTENT_URI)               
	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
	                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,name).build() 
	            ); 
    }
    //### Contact Number
    void contactNumber(String no,ArrayList<ContentProviderOperation> ops){
    	 ops.add(ContentProviderOperation. 
	                newInsert(ContactsContract.Data.CONTENT_URI) 
	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
	                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
	                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, no) 
	                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) .build() 
	            ); 
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
