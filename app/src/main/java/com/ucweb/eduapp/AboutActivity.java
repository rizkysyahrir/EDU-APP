package com.ucweb.eduapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Element adsElement = new Element();
        adsElement.setTitle("Febrian Syahrir Rizky");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.icon_app)
                .setDescription("This is About")
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(adsElement)
                .addGroup("Hubungi Saya")
                .addEmail("febriansyahrir43@gmail.com")
                .addWebsite("https://semangatpemuda-id.000webhostapp.com/")
                .addFacebook("https://web.facebook.com/febrian.s.rizky.7/")
                .addTwitter("https://twitter.com/febriansyahrir/")
                .addYoutube("UCmNV5hqueLvn6ON7h-a34xQ")
                .addPlayStore("My Playstore")
                .addInstagram("https://www.instagram.com/rizkysyahrir/")
                .addItem(createCopyright())
                .create();

        setContentView(aboutPage);
    }

    private Element createCopyright() {
        Element copyright = new Element();
        String copyrightString = String.format("Copyright %d by RayenDev", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setIcon(R.mipmap.ic_launcher_foreground);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AboutActivity.this, copyrightString, Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }
}