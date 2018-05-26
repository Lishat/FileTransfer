package com.example.subhadeep.filetransfer;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String path;
    TextView editText, Received;
    EditText Message;
    private static final String[] INITIAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int INITIAL_REQUEST = 1337;

    private static final int REQUEST_WRITE_STORAGE = INITIAL_REQUEST + 4;
    WifiManager wifiManager;
    WifiP2pManager myManager;
    WifiP2pManager.Channel myChannel;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_WRITE_STORAGE:
                if (canAccessWriteStorage()) {

                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=(TextView) findViewById(R.id.textView);
        Message = (EditText) findViewById(R.id.editText);
        Received = (TextView) findViewById(R.id.received);
        Received.setMovementMethod(new ScrollingMovementMethod());
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        myManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        myChannel = myManager.initialize(this,getMainLooper(),null);
        if (!canAccessLocation() || !canAccessCamera() || !canAccessWriteStorage() || !canAccessReadStorage() || !canAccessReadContacts() || !canAccessWriteContacts()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }
        }
    }
    private boolean canAccessWriteStorage() {
        return (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean canAccessReadStorage() {
        return (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    private boolean canAccessReadContacts() {
        return (hasPermission(Manifest.permission.READ_CONTACTS));
    }

    private boolean canAccessWriteContacts() {
        return (hasPermission(Manifest.permission.WRITE_CONTACTS));
    }

    private boolean canAccessCamera() {
        return (hasPermission(Manifest.permission.CAMERA));
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001
                && resultCode == Activity.RESULT_OK) {
            Uri uriPath = data.getData();
            path = getPath(this, uriPath);
            editText.setText(path);

        }
    }

    void getFile(View view)
    {
        Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mediaIntent.setType("*/*");
        startActivityForResult(mediaIntent, 1001);
    }
    void sendMsgBluetooth(View view)
    {
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "BTSending");
            if(!root.exists())
            {
                root.mkdir();
            }
            File filepath = new File(root,"MyFile.txt");
            FileWriter writer = new FileWriter(filepath, true);
            String temp = Message.getText().toString();
            temp += "\n";
            writer.append(temp);
            writer.flush();
            writer.close();
            Message.setText("");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("*/*");

            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filepath));

            PackageManager pm = getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);

            if (appsList.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info : appsList) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(this, "Bluetooth isn't found",
                            Toast.LENGTH_LONG).show();
                } else {
                    intent.setClassName(packageName, className);
                    startActivity(intent);
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    void sendMsgWifiDirect(View view)
    {
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "BTSending");
            if(!root.exists())
            {
                root.mkdir();
            }
            File filepath = new File(root,"MyFile.txt");
            FileWriter writer = new FileWriter(filepath, true);
            String temp = Message.getText().toString();
            temp += "\n";
            writer.append(temp);
            writer.flush();
            writer.close();
            Message.setText("");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("*/*");

            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filepath));

            PackageManager pm = getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);

            if (appsList.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info : appsList) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.samsung.android.app.FileShareClient")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(this, "Wifi-Direct isn't found",
                            Toast.LENGTH_LONG).show();
                } else {
                    intent.setClassName(packageName, className);
                    startActivity(intent);
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    void shareViaBluetooth(View view)
    {
        if(path == null)
        {
            Toast.makeText(this, "Please Select The File First", Toast.LENGTH_SHORT);
        }
        else
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("*/*");

            File f = new File(path);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

            PackageManager pm = getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);

            if (appsList.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info : appsList) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(this, "Bluetooth isn't found",
                            Toast.LENGTH_LONG).show();
                } else {
                    intent.setClassName(packageName, className);
                    startActivity(intent);
                }
            }
        }
    }
    void shareViaWifiDirect(View view)
    {
        if(path == null)
        {
            Toast.makeText(this, "Please Select The File First", Toast.LENGTH_SHORT);
        }
        else
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("*/*");

            File f = new File(path);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

            PackageManager pm = getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);

            if (appsList.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info : appsList) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.samsung.android.app.FileShareClient")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(this, "WifiDirect isn't found",
                            Toast.LENGTH_LONG).show();
                } else {
                    intent.setClassName(packageName, className);
                    startActivity(intent);
                }
            }
        }
    }
    void updateMsgs(View view)
    {
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "Download");
            if(!root.exists())
            {
                root.mkdir();
            }
            File filepath = new File(root,"MyFile.txt");
            File prevFilePath = new File(root, "PreviousMsgs.txt");
            if(filepath.exists())
            {
                int length = (int) filepath.length();
                byte[] bytes = new byte[length];
                FileInputStream in = new FileInputStream(filepath);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }
                String contents = new String(bytes);
                FileWriter writer = new FileWriter(prevFilePath);
                writer.append(contents+"\n");
                writer.flush();
                writer.close();
                filepath.delete();
            }
            if(prevFilePath.exists())
            {
                int length = (int) prevFilePath.length();
                byte[] bytes = new byte[length];
                FileInputStream in = new FileInputStream(prevFilePath);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }
                String contents = new String(bytes);
                Received.setText(contents);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    void BTDiscover(View view)
    {
        if(BluetoothAdapter.getDefaultAdapter().getScanMode()!= BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
            startActivity(discoverableIntent);
            Toast.makeText(MainActivity.this, "Turning on Discoverability for 30 seconds", Toast.LENGTH_SHORT).show();
        }
    }
    void WifiDirectDiscover(View view)
    {
        wifiManager.setWifiEnabled(true);
        myManager.discoverPeers(myChannel,new WifiP2pManager.ActionListener(){
            @Override
            public  void onSuccess() {
                Toast.makeText(MainActivity.this, "Discovery Started", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(int i)  {
                Toast.makeText(MainActivity.this, "Discovery Failed", Toast.LENGTH_SHORT);
            }
        });
    }

    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKatOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKatOrAbove && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
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
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
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

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
