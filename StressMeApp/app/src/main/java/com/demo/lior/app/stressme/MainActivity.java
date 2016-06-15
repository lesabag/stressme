package com.demo.lior.app.stressme;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
/*
    Lior esabag
 */
public class MainActivity extends AppCompatActivity {

    private String TAG = getPackageName() + "_" + getLocalClassName();
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ToggleButton toggleButton;
    String text;
    EditText number;
    TextView title, times, status;
    @SuppressWarnings("unused")
    private ProgressDialog pd;
    ContactCreationTask task = new ContactCreationTask();

    public static final int BLUETOOTH_TEST = Menu.FIRST;
    public static final int AIRPLANE_TEST = Menu.FIRST + 1;
    public static final int GEN_X_CONTACTS = Menu.FIRST + 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number = (EditText) findViewById(R.id.numberEt);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        title = (TextView) findViewById(R.id.select);
        times = (TextView) findViewById(R.id.textView1);
        status = (TextView) findViewById(R.id.status);

        init();

        toggleButton.setOnClickListener(new View.OnClickListener()
        {
            @SuppressWarnings("unused")
            @Override
            public void onClick(View v)
            {
                if (number.getText().length() > 0)
                {
                    for (int i = 0; i < Integer.parseInt(number.getText().toString()); i++)
                    {
                        boolean res, isEnabled = mBluetoothAdapter.isEnabled();

                        Log.i(TAG, "Bluetooth Adapter Before: " + mBluetoothAdapter.isEnabled());

                        if (isEnabled)
                        {
                            toggleButton.setText("OFF");
                            res = toggleBluetooth(false);// disabling BT
                        }
                        else
                        {
                            toggleButton.setText("ON");
                            res = toggleBluetooth(true);// enabling BT
                        }

                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    status.append(" Test Ended ");
                }
                else
                {
                    Log.i(TAG, "You must enter a number");
                    init();
                    return;
                }
                number.setText("");
            }
        });

    }

    private void init()
    {
        number.setVisibility(EditText.INVISIBLE);
        toggleButton.setVisibility(Button.INVISIBLE);
        times.setVisibility(Button.INVISIBLE);
        status.setVisibility(TextView.INVISIBLE);
        number.setText("");

        if (mBluetoothAdapter.isEnabled())
            toggleButton.setText("OFF");
        else
            toggleButton.setText("ON");
    }

    public boolean toggleBluetooth(boolean enable)
    {
        if (enable)
        {
            toggleButton.setText("OFF");
            mBluetoothAdapter.enable();
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            Log.i(TAG, "Bluetooth Adapter After: " + mBluetoothAdapter.isEnabled());

            return true;
        }
        else
        {
            toggleButton.setText("ON");
            mBluetoothAdapter.disable();
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            Log.i(TAG, "Bluetooth Adapter After: " + mBluetoothAdapter.isEnabled());
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stress_main, menu);
        menu.add(Menu.NONE, BLUETOOTH_TEST, Menu.NONE, "Bluetooth test");
        menu.add(Menu.NONE, AIRPLANE_TEST, Menu.NONE, "Airplane test");
        menu.add(Menu.NONE, GEN_X_CONTACTS, Menu.NONE, "Generate X contacts test");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks.
        switch (item.getItemId())
        {
            case BLUETOOTH_TEST:
                times.setText("How many times do you want to toggle?");
                times.setVisibility(Button.VISIBLE);
                number.setVisibility(EditText.VISIBLE);
                toggleButton.setVisibility(Button.VISIBLE);

                return true;

            case AIRPLANE_TEST:

                return true;

            case GEN_X_CONTACTS:

                times.setVisibility(EditText.VISIBLE);
                times.setText("How many contacts do you want to create?");
                number.setVisibility(EditText.VISIBLE);
                toggleButton.setVisibility(Button.VISIBLE);
                status.setVisibility(TextView.VISIBLE);

                toggleButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        pd = ProgressDialog.show(MainActivity.this, "Dialog", "Generating contacts");
                        setProgressBarIndeterminate(false);
                        setProgressBarVisibility(true);
                        Thread th = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (int j = 1; j <= Integer.parseInt(number.getText().toString().trim()); j++)
                                {
                                    Log.i(TAG, "Thread #" + j + " started");
                                    try
                                    {
                                        Thread.sleep(1000);
                                    }
                                    catch (InterruptedException e1)
                                    {
                                        e1.printStackTrace();
                                    }
                                    try
                                    {
                                        AddContact(getApplicationContext(), j);
                                    }
                                    catch (NumberFormatException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    catch (RemoteException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    catch (OperationApplicationException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                status.setText(" Contacts created");
                                pd.dismiss();
                            }
                        });
                        th.start();
                    }
                });
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        public PlaceholderFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_stress_main, container, false);
            return rootView;
        }
    }

    public void AddContact(Context env, int count) throws RemoteException, OperationApplicationException
    {
        try
        {
            env = getApplicationContext();

            int random = (int) (Math.random() * count + count);

            Log.i(TAG, "Generating contact # : " + count);
            ContentValues values = new ContentValues();
            Uri rawContactUri = env.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);

            long rawContactId = ContentUris.parseId(rawContactUri);

            values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "contact_" + count); // the
            // contact
            // name
            // +
            // random
            // number
            // between
            // 1->count
            Log.i(TAG, "Contact number: " + count);
            env.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

            // Enter phone number
            values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, count + random); // the contact phone
            // number
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

            env.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

            values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

            env.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

            status.setText(" Generated : " + count + "/" + count + " contacts");
            Toast.makeText(getApplicationContext(), " Generated : " + count + "/" + count + " contacts", Toast.LENGTH_SHORT).show();
            Thread.sleep(1000);
            setProgress(count / 10);
            // }
            setProgressBarVisibility(false);
        }
        catch (Exception e)
        {
            Log.i(TAG, "Unable to create contact " + e.getStackTrace());
        }
    }

    public class ContactCreationTask extends AsyncTask<String, String, String>
    {

        @SuppressWarnings("unused")
        private int counter = 0;

        @Override
        protected void onPreExecute()
        {
            status.setText("PRE for");
            setProgressBarIndeterminate(false);
            setProgressBarVisibility(true);
            Log.i(TAG, " onPreExecute() ");
        }

        @Override
        protected String doInBackground(String... urls)
        {
            Log.i(TAG, " doInBackground() + creating " + Integer.parseInt(urls[0]) + " contacts");
            try
            {
                for (int i = 1; i <= Integer.parseInt(urls[0]); i++)
                {
                    Log.i(TAG, "Contact number: " + i);

                    ContentValues values = new ContentValues();
                    Uri rawContactUri = getApplicationContext().getContentResolver()
                            .insert(ContactsContract.RawContacts.CONTENT_URI, values);

                    long rawContactId = ContentUris.parseId(rawContactUri);
                    values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "contact_" + i);
                    Log.i(TAG, "Contact number: " + i);
                    getApplicationContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

                    // Enter phone number
                    values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "contact_" + urls[0]);
                    values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

                    getApplicationContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

                    values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

                    getApplicationContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

                    Thread.sleep(1000);
                    counter++;
                }
            }
            catch (Exception e)
            {
                Log.i(TAG, "Unable to create contact " + e.getStackTrace());
            }

            return "true";

        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            for (int i = 0; i < values.length; i++)
            {
                Log.i(TAG, "values.length: " + values.length + " i: " + i);
                status.setText(values[i]);
                setProgress(5000);
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            Log.i(TAG, " onPostExecute() ");
            status.setText("finished..");
            setProgressBarVisibility(false);

        }
    }

}

