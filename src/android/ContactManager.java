/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package org.apache.cordova.contacts;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Organization;

import android.util.Log;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.Intents;

import java.lang.reflect.Method;

public class ContactManager extends CordovaPlugin {

    private ContactAccessor contactAccessor;
    private CallbackContext callbackContext;        // The callback context from which we were invoked.
    private JSONArray executeArgs;

    private static final String LOG_TAG = "Contact Query";

    public static final int UNKNOWN_ERROR = 0;
    public static final int INVALID_ARGUMENT_ERROR = 1;
    public static final int TIMEOUT_ERROR = 2;
    public static final int PENDING_OPERATION_ERROR = 3;
    public static final int IO_ERROR = 4;
    public static final int NOT_SUPPORTED_ERROR = 5;
    public static final int OPERATION_CANCELLED_ERROR = 6;
    public static final int PERMISSION_DENIED_ERROR = 20;
    private static final int CONTACT_PICKER_RESULT = 1000;
    private static final int CONTACT_SAVE_RESULT = 2000;
    public static String [] permissions;


    //Request code for the permissions picker (Pick is async and uses intents)
    public static final int SEARCH_REQ_CODE = 0;
    public static final int SAVE_REQ_CODE = 1;
    public static final int REMOVE_REQ_CODE = 2;
    public static final int PICK_REQ_CODE = 3;

    public static final String READ = Manifest.permission.READ_CONTACTS;
    public static final String WRITE = Manifest.permission.WRITE_CONTACTS;


    /**
     * Constructor.
     */
    public ContactManager() {

    }


    protected void getReadPermission(int requestCode)
    {
        PermissionHelper.requestPermission(this, requestCode, READ);
    }


    protected void getWritePermission(int requestCode)
    {
        PermissionHelper.requestPermission(this, requestCode, WRITE);
    }


    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArray of arguments for the plugin.
     * @param callbackContext   The callback context used when calling back into JavaScript.
     * @return                  True if the action was valid, false otherwise.
     */
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        this.callbackContext = callbackContext;
        this.executeArgs = args;

        /**
         * Check to see if we are on an Android 1.X device.  If we are return an error as we
         * do not support this as of Cordova 1.0.
         */
        if (android.os.Build.VERSION.RELEASE.startsWith("1.")) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, ContactManager.NOT_SUPPORTED_ERROR));
            return true;
        }

        /**
         * Only create the contactAccessor after we check the Android version or the program will crash
         * older phones.
         */
        if (this.contactAccessor == null) {
            this.contactAccessor = new ContactAccessorSdk5(this.cordova);
        }

        if (action.equals("search")) {
            if(PermissionHelper.hasPermission(this, READ)) {
                search(executeArgs);
            }
            else
            {
                getReadPermission(SEARCH_REQ_CODE);
            }
        }
        else if (action.equals("save")) {
            if(PermissionHelper.hasPermission(this, WRITE))
            {
                save(executeArgs);
            }
            else
            {
                getWritePermission(SAVE_REQ_CODE);
            }
        }
        else if (action.equals("saveAndEdit")) {
            if(PermissionHelper.hasPermission(this, WRITE))
            {
                saveAndEdit(executeArgs);
            }
            else
            {
                getWritePermission(SAVE_REQ_CODE);
            }
        }
        else if (action.equals("remove")) {
            if(PermissionHelper.hasPermission(this, WRITE))
            {
                remove(executeArgs);
            }
            else
            {
                getWritePermission(REMOVE_REQ_CODE);
            }
        }
        else if (action.equals("pickContact")) {
            if(PermissionHelper.hasPermission(this, READ))
            {
                pickContactAsync();
            }
            else
            {
                getReadPermission(PICK_REQ_CODE);
            }
        }
        else {
            return false;
        }
        return true;
    }

    private void remove(JSONArray args) throws JSONException {
        final String contactId = args.getString(0);
        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                if (contactAccessor.remove(contactId)) {
                    callbackContext.success();
                } else {
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, UNKNOWN_ERROR));
                }
            }
        });
    }

    private void save(JSONArray args) throws JSONException {
        final JSONObject contact = args.getJSONObject(0);
        this.cordova.getThreadPool().execute(new Runnable(){
            public void run() {
                JSONObject res = null;
                String id = contactAccessor.save(contact);
                if (id != null) {
                    try {
                        res = contactAccessor.getContactById(id);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSON fail.", e);
                    }
                }
                if (res != null) {
                    callbackContext.success(res);
                } else {
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, UNKNOWN_ERROR));
                }
            }
        });
    }

    private void saveAndEdit(JSONArray args) throws JSONException {
        final JSONObject contact = args.getJSONObject(0);
        final CordovaPlugin plugin = (CordovaPlugin) this;
        Runnable worker = new Runnable() {
            public void run() {
                String contactId = "";
                JSONObject editContact = null;
                JSONObject name = null;
                JSONArray addresses = null;
                JSONArray emails = null;
                JSONArray phoneNumbers = null;

                ArrayList<ContentValues> data = new ArrayList<ContentValues>();

                try {
                    if (!contact.isNull("id")) {
                        contactId = contact.getString("id");
                    }
                    if (!contact.isNull("name")) {
                        name = contact.getJSONObject("name");
                    }
                    if (!contact.isNull("addresses")) {
                        addresses = contact.getJSONArray("addresses");
                    }
                    if (!contact.isNull("emails")) {
                        emails = contact.getJSONArray("emails");
                    }
                    if (!contact.isNull("phoneNumbers")) {
                        phoneNumbers = contact.getJSONArray("phoneNumbers");
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSON fail.", e);
                }

                if (addresses != null) {
                    for (int i = 0; i < addresses.length(); i++) {
                        JSONObject currentAddress = null;
                        try {
                            currentAddress = addresses.getJSONObject(i);
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "JSON fail.", e);
                        }

                        ContentValues addressValues = new ContentValues();
                        addressValues.put(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
                        if ("home".equals(getJsonString(currentAddress, "type"))) {
                            addressValues.put(StructuredPostal.TYPE, StructuredPostal.TYPE_HOME);
                        } else if ("work".equals(getJsonString(currentAddress, "type"))) {
                            addressValues.put(StructuredPostal.TYPE, StructuredPostal.TYPE_WORK);
                        } else {
                            addressValues.put(StructuredPostal.TYPE, StructuredPostal.TYPE_OTHER);
                        }
                        
                        String formattedAddress = "";
                        if (!currentAddress.isNull("streetAddress")) {
                            formattedAddress = formattedAddress + getJsonString(currentAddress, "streetAddress");
                            if (!currentAddress.isNull("locality") || !currentAddress.isNull("postalCode") || !currentAddress.isNull("country")) {
                                formattedAddress = formattedAddress + ", ";
                            }
                        }
                        if (!currentAddress.isNull("locality")) {
                            formattedAddress = formattedAddress + getJsonString(currentAddress, "locality");
                            if (!currentAddress.isNull("postalCode") || !currentAddress.isNull("country")) {
                                formattedAddress = formattedAddress + ", ";
                            }
                        }
                        if (!currentAddress.isNull("postalCode")) {
                            formattedAddress = formattedAddress + getJsonString(currentAddress, "postalCode");
                            if (!currentAddress.isNull("country")) {
                                formattedAddress = formattedAddress + ", ";
                            }
                        }
                        if (!currentAddress.isNull("country")) {
                            formattedAddress = formattedAddress + getJsonString(currentAddress, "country");
                        }

                        addressValues.put(StructuredPostal.FORMATTED_ADDRESS, formattedAddress);
                        addressValues.put(StructuredPostal.STREET, getJsonString(currentAddress, "streetAddress"));
                        addressValues.put(StructuredPostal.CITY, getJsonString(currentAddress, "locality"));
                        addressValues.put(StructuredPostal.REGION, getJsonString(currentAddress, "region"));
                        addressValues.put(StructuredPostal.POSTCODE, getJsonString(currentAddress, "postalCode"));
                        addressValues.put(StructuredPostal.COUNTRY, getJsonString(currentAddress, "country"));
                        data.add(addressValues);
                    }
                }

                if (phoneNumbers != null) {
                    for (int i = 0; i < phoneNumbers.length(); i++) {
                        JSONObject currentPhone = null;
                        try {
                            currentPhone = phoneNumbers.getJSONObject(i);
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "JSON fail.", e);
                        }

                        ContentValues phoneValues = new ContentValues();
                        phoneValues.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                        if ("home".equals(getJsonString(currentPhone, "type"))) {
                            phoneValues.put(Phone.TYPE, Phone.TYPE_HOME);
                        } else if ("work".equals(getJsonString(currentPhone, "type"))) {
                            phoneValues.put(Phone.TYPE, Phone.TYPE_WORK);
                        } else if ("mobile".equals(getJsonString(currentPhone, "type"))) {
                            phoneValues.put(Phone.TYPE, Phone.TYPE_MOBILE);
                        } else if ("fax".equals(getJsonString(currentPhone, "type"))) {
                            phoneValues.put(Phone.TYPE, Phone.TYPE_FAX_WORK);
                        } else if ("main".equals(getJsonString(currentPhone, "type"))) {
                            phoneValues.put(Phone.TYPE, Phone.TYPE_MAIN);
                        } else {
                            phoneValues.put(Phone.TYPE, Phone.TYPE_OTHER);
                        }
                        phoneValues.put(Phone.NUMBER, getJsonString(currentPhone, "value"));
                        data.add(phoneValues);
                    }
                }

                if (emails != null) {
                    for (int i = 0; i < emails.length(); i++) {
                        JSONObject currentEmail = null;
                        try {
                            currentEmail = emails.getJSONObject(i);
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "JSON fail.", e);
                        }

                        ContentValues emailValues = new ContentValues();
                        emailValues.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
                        if ("home".equals(getJsonString(currentEmail, "type"))) {
                            emailValues.put(Email.TYPE, Email.TYPE_HOME);
                        } else if ("work".equals(getJsonString(currentEmail, "type"))) {
                            emailValues.put(Email.TYPE, Email.TYPE_WORK);
                        } else {
                            emailValues.put(Email.TYPE, Email.TYPE_OTHER);
                        }
                        emailValues.put(Email.ADDRESS, getJsonString(currentEmail, "value"));
                        data.add(emailValues);
                    }
                }

                Intent intent = null;

                if (!contactId.equals("")) {
                    ContentResolver cr = plugin.cordova.getActivity().getContentResolver();
                    Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, "_ID = '" + contactId + "'", null, null);
                    if (cursor.getCount() > 0) {
                        Long contactIdLong = Long.parseLong(contactId);
                        intent = new Intent(Intent.ACTION_EDIT);
                        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactIdLong);
                        intent.setData(contactUri);
                        intent.putExtra("finishActivityOnSaveCompleted", true);
                        if (name != null) {
                            String displayedName = "";
                            displayedName = getFormattedName(name);
                            if (!"".equals(displayedName)) {
                                intent.putExtra(Intents.Insert.NAME, displayedName);
                            }
                        }
                        if (!data.isEmpty()) {
                            intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
                        }
                        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
                    }
                }
                if (intent == null) {
                    intent = new Intent(ContactsContract.Intents.Insert.ACTION, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    intent.putExtra("finishActivityOnSaveCompleted", true);
                    if (name != null) {
                        String displayedName = "";
                        displayedName = getFormattedName(name);
                        if (!"".equals(displayedName)) {
                            intent.putExtra(Intents.Insert.NAME, displayedName);
                        }
                    }
                    if (!data.isEmpty()) {
                        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
                    }
                }
                plugin.cordova.startActivityForResult(plugin, intent, CONTACT_SAVE_RESULT);
            }
        };
        this.cordova.getThreadPool().execute(worker);
    }

    /**
     * Convenience method to get a string from a JSON object. Saves a lot of try/catch writing. If the property is not found in the
     * object null will be returned.
     *
     * @param obj
     *            contact object to search
     * @param property
     *            to be looked up
     * @return The value of the property
     */
    private static String getJsonString(JSONObject obj, String property) {
        String value = null;
        try {
            if (obj != null) {
                value = obj.getString(property);
                if (value.equals("null")) {
                    Log.d(LOG_TAG, property + " is string called 'null'");
                    value = null;
                }
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "Could not get = " + e.getMessage());
        }
        return value;
    }

    /**
     * @param name
     * @return The formatted name
     */
    private static String getFormattedName(JSONObject name) {
        if (!name.isNull("givenName")) {
            if (!name.isNull("familyName")) {
                return getJsonString(name, "givenName") + " " + getJsonString(name, "familyName");
            }
        }
        if (!name.isNull("familyName")) {
            return getJsonString(name, "familyName") + " " + getJsonString(name, "familyName");
        }
        return "";
    }
    
    private void search(JSONArray args) throws JSONException
    {
        final JSONArray filter = args.getJSONArray(0);
        final JSONObject options = args.get(1) == null ? null : args.getJSONObject(1);
        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                JSONArray res = contactAccessor.search(filter, options);
                callbackContext.success(res);
            }
        });
    }


    /**
     * Launches the Contact Picker to select a single contact.
     */
    private void pickContactAsync() {
        final CordovaPlugin plugin = (CordovaPlugin) this;
        Runnable worker = new Runnable() {
            public void run() {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
                plugin.cordova.startActivityForResult(plugin, contactPickerIntent, CONTACT_PICKER_RESULT);
            }
        };
        this.cordova.getThreadPool().execute(worker);
    }

    /**
     * Called when user picks contact.
     * @param requestCode       The request code originally supplied to startActivityForResult(),
     *                          allowing you to identify who this result came from.
     * @param resultCode        The integer result code returned by the child activity through its setResult().
     * @param intent            An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     * @throws JSONException
     */
    public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        if (requestCode == CONTACT_PICKER_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                String contactId = intent.getData().getLastPathSegment();
                // to populate contact data we require  Raw Contact ID
                // so we do look up for contact raw id first
                Cursor c =  this.cordova.getActivity().getContentResolver().query(RawContacts.CONTENT_URI,
                            new String[] {RawContacts._ID}, RawContacts.CONTACT_ID + " = " + contactId, null, null);
                if (!c.moveToFirst()) {
                    this.callbackContext.error("Error occured while retrieving contact raw id");
                    return;
                }
                String id = c.getString(c.getColumnIndex(RawContacts._ID));
                c.close();

                try {
                    JSONObject contact = contactAccessor.getContactById(id);
                    this.callbackContext.success(contact);
                    return;
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSON fail.", e);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                callbackContext.error(OPERATION_CANCELLED_ERROR);
                return;
            }

            this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, UNKNOWN_ERROR));
        } else if (requestCode == CONTACT_SAVE_RESULT) {

            JSONObject saveResult = new JSONObject();

            if (resultCode == Activity.RESULT_OK) {

                Uri contactData = intent.getData();
                Cursor c = this.cordova.getActivity().getContentResolver().query(contactData, null, null, null, null);
                String id = "";
                c.moveToLast();
                id = c.getString(c.getColumnIndex(RawContacts._ID));
                c.close();
                try {
                    saveResult.put("id", id);
                    this.callbackContext.success(saveResult);
                    return;
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSON fail.", e);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                callbackContext.error(OPERATION_CANCELLED_ERROR);
                return;
            }
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                             int[] grantResults) throws JSONException
    {
        for(int r:grantResults)
        {
            if(r == PackageManager.PERMISSION_DENIED)
            {
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PERMISSION_DENIED_ERROR));
                return;
            }
        }
        switch(requestCode)
        {
            case SEARCH_REQ_CODE:
                search(executeArgs);
                break;
            case SAVE_REQ_CODE:
                save(executeArgs);
                break;
            case REMOVE_REQ_CODE:
                remove(executeArgs);
                break;
            case PICK_REQ_CODE:
                pickContactAsync();
                break;
        }
    }

    /**
     * This plugin launches an external Activity when a contact is picked, so we
     * need to implement the save/restore API in case the Activity gets killed
     * by the OS while it's in the background. We don't actually save anything
     * because picking a contact doesn't take in any arguments.
     */
    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        this.contactAccessor = new ContactAccessorSdk5(this.cordova);
    }
}
