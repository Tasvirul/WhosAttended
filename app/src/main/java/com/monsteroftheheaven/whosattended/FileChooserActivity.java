package com.monsteroftheheaven.whosattended;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;


public class FileChooserActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnFileChooser;
    private ViewFlipper myFlipper;
    private AppPreference _appPrefs;
    private Map<String,String> studentMap;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter = null;
    private MyAdapter mNewDevicesArrayAdapter;
    private ArrayList<StudentInfo> allStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allStudent = new ArrayList<StudentInfo>();
        setContentView(R.layout.activity_file_chooser);
        btnFileChooser = (Button) findViewById(R.id.btn_file_chooser);
        myFlipper = (ViewFlipper) findViewById(R.id.my_flipper);
        _appPrefs = new AppPreference(getApplicationContext());
        studentMap = _appPrefs.loadMap();
        if(studentMap.isEmpty()){
            myFlipper.setDisplayedChild(0);
        }else{
            for (Map.Entry<String, String> entry : studentMap.entrySet())
            {
                StudentInfo info = new StudentInfo(entry.getKey(),entry.getValue());
                allStudent.add(info);
            }
            myFlipper.setDisplayedChild(1);
        }

        btnFileChooser.setOnClickListener(this);
        mNewDevicesArrayAdapter=new MyAdapter(this,allStudent);
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        mNewDevicesArrayAdapter.notifyDataSetChanged();
        // newDevicesListView.setOnItemClickListener(mDeviceClickListener);


        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);


        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_file_chooser:
                showFileChooser();
                break;
        }
    }

    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("SOmETAG", "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    /*try {
                        path = this.getRealPathFromURI(this,uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    ContentResolver cR = this.getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String type = mime.getExtensionFromMimeType(cR.getType(uri));
                    Log.d("SOmETAG", "File Path: " + type);

                        //read(getPath(this,uri));
                    readCsvFile(getPath(this, uri));

                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void readCsvFile(String path){

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String [] next;
        try {

            /*br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] country = line.split(cvsSplitBy);

                Log.d("name= " + country[0]
                        , " , Id=" + country[1] );

            }*/
            CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(new File(path))));
            while(true) {
                next = reader.readNext();
                if(next != null) {
                    Log.d("name= " + next[0]
                            , " , Id=" + next[1]);
                    studentMap.put(next[0],next[1]);
                    StudentInfo info = new StudentInfo(next[0],next[1]);
                    allStudent.add(info);

                } else {
                    break;
                }

            }
            mNewDevicesArrayAdapter.notifyDataSetChanged();
            _appPrefs.saveMap(studentMap);
            FileChooserActivity.this.myFlipper.setDisplayedChild(1);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void onClickbtnActivateBluetooth(View view)
    {

        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, "Bluetooth is not available !", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        else if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        Toast.makeText(this, "Bluetooth has been activated!", Toast.LENGTH_LONG).show();
        findViewById(R.id.btnSearchDevices).setEnabled(true);


    }
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    //mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    for(int i=0;i<allStudent.size();i++){
                        if(mNewDevicesArrayAdapter.getItem(i).getStudentName().trim().equalsIgnoreCase(device.getName().trim())){
                            mNewDevicesArrayAdapter.getItem(i).setStatus(true);
                        }
                    }
                    mNewDevicesArrayAdapter.notifyDataSetChanged();
                }

            }
        }
    };

    public void onClickbtnSearchDevices(View view)
    {
        mBluetoothAdapter.startDiscovery();
        Toast.makeText(this, "Bluetooth devices are searched !", Toast.LENGTH_LONG).show();
        // Find and set up the ListView for newly discovered devices

    }
}
