package com.ucweb.eduapp.Permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.ucweb.eduapp.Constant.AllConstant;

public class AppPermissions {

    public boolean isStorageOk(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, AllConstant.STORAGE_REQUEST_CODE);
    }


    public void startpermissionrequest(FragmentActivity fr, AllConstant ac, String manifest){
        ActivityResultLauncher<String> requestPermissionLauncher = fr.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted ->{

                });
        requestPermissionLauncher.launch(manifest);
    }
}
