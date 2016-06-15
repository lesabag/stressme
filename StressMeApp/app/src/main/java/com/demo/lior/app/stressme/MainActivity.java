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

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ToggleButton toggleButton1;
    // Button stressBtBtn;
    String text;
    EditText number;
    TextView title, times, status;
    @SuppressWarnings("unused")
    private ProgressBar pb;
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
        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
        title = (TextView) findViewById(R.id.select);
        times = (TextView) findViewById(R.id.textView1);
        status = (TextView) findViewById(R.id.status);

        // pb = (ProgressBar) findViewById(R.id.progressBar);
        init();

        toggleButton1.setOnClickListener(new View.OnClickListener()
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

                        System.out.println("Bluetooth Adapter Before: " + mBluetoothAdapter.isEnabled());

                        if (isEnabled)
                        {
                            toggleButton1.setText("OFF");
                            res = toggleBluetooth(false);// disabling BT
                        }
                        else
                        {
                            toggleButton1.setText("ON");
                            res = toggleBluetooth(true);// enabling BT
                        }

                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    status.append(" Test Ended ");
                }
                else
                {
                    System.out.println("You must enter a number");
                    init();
                    return;
                }
                number.setText("");
            }
        });

    }

    private void init()
    {

        // stressBtBtn.setVisibility(Button.INVISIBLE);
        number.setVisibility(EditText.INVISIBLE);
        toggleButton1.setVisibility(Button.INVISIBLE);
        times.setVisibility(Button.INVISIBLE);
        status.setVisibility(TextView.INVISIBLE);
        number.setText("");

        if (mBluetoothAdapter.isEnabled())
            toggleButton1.setText("OFF");
        else
            toggleButton1.setText("ON");
    }

    public boolean toggleBluetooth(boolean enable)
    {

        if (enable)
        {
            toggleButton1.setText("OFF");
            mBluetoothAdapter.enable();
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println("Bluetooth Adapter After: " + mBluetoothAdapter.isEnabled());

            return true;
        }
        else
        {
            toggleButton1.setText("ON");
            mBluetoothAdapter.disable();
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println("Bluetooth Adapter After: " + mBluetoothAdapter.isEnabled());
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case BLUETOOTH_TEST:
                times.setText("How many times do you want to toggle?");
                times.setVisibility(Button.VISIBLE);
                // stressBtBtn.setVisibility(Button.VISIBLE);
                number.setVisibility(EditText.VISIBLE);
                toggleButton1.setVisibility(Button.VISIBLE);

                return true;

            case AIRPLANE_TEST:

                return true;

            case GEN_X_CONTACTS:

                times.setVisibility(EditText.VISIBLE);
                times.setText("How many contacts do you want to create?");
                number.setVisibility(EditText.VISIBLE);
                toggleButton1.setVisibility(Button.VISIBLE);
                status.setVisibility(TextView.VISIBLE);

                toggleButton1.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        pd = ProgressDialog.show(MainActivity.this, "Dialog", "Generating contacts");
                        setProgressBarIndeterminate(false);
                        setProgressBarVisibility(true);
                        // task.execute(number.getText().toString());
                        Thread th = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                for (int j = 1; j <= Integer.parseInt(number.getText().toString().trim()); j++)
                                {
                                    System.out.println("Thread #" + j + " started");
                                    try
                                    {
                                        Thread.sleep(1000);
                                    }
                                    catch (InterruptedException e1)
                                    {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                    }
                                    try
                                    {
                                        AddContact(getApplicationContext(), j);
                                    }
                                    catch (NumberFormatException e)
                                    {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    catch (RemoteException e)
                                    {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    catch (OperationApplicationException e)
                                    {
                                        // TODO Auto-generated catch block
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

            // for(int i = 1 ; i <= count ; i++)
            // {
            int random = (int) (Math.random() * count + count);

            System.out.println("Generating contact # : " + count);
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
            System.out.println("Contact number: " + count);
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
            System.out.println("Unable to create contact " + e.getStackTrace());
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
            System.out.println(" onPreExecute() ");
        }

        @Override
        protected String doInBackground(String... urls)
        {

            System.out.println(" doInBackground() + creating " + Integer.parseInt(urls[0]) + " contacts");

            try
            {

                for (int i = 1; i <= Integer.parseInt(urls[0]); i++)
                {
                    System.out.println("Contact number: " + i);

                    ContentValues values = new ContentValues();
                    Uri rawContactUri = getApplicationContext().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);

                    long rawContactId = ContentUris.parseId(rawContactUri);
                    values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "contact_" + i);
                    System.out.println("Contact number: " + i);
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

                    //status.setText(" Generated : " + i + "/" + urls[0] + " contacts");

                    counter++;

                    // publishProgress(urls[i].toString());
                }

            }
            catch (Exception e)
            {
                System.out.println("Unable to create contact " + e.getStackTrace());
            }

            return "true";

        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            for (int i = 0; i < values.length; i++)
            {
                System.out.println("values.length: " + values.length + " i: " + i);
                status.setText(values[i]);
                setProgress(5000);
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            System.out.println(" onPostExecute() ");
            status.setText("finished..");
            setProgressBarVisibility(false);

        }
    }

}

