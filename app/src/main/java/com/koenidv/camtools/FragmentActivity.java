package com.koenidv.camtools;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);


        AboutView view = AboutBuilder.with(this)
                .setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName("Florian König")
                .setSubTitle("koenidv")
                .setBrief("Photographer. Developer. Trying to create quality content.")
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                .addGooglePlayStoreLink("8958029813460515292")
                .addInstagramLink("koenidv")
                .addEmailLink("koenidv@gmail.com")
                .addWebsiteLink("http://dev.fkoe.org")
                .addTwitterLink("koeniDv")
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .addMoreFromMeAction("koenidv")
                .addChangeLogAction(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("dev.fkoe.org/camtools-changelog")))
                .addPrivacyPolicyAction("http://dev.fkoe.org/privacy")
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(false)
                .build();

        addContentView(view, findViewById(R.id.aboutView).getLayoutParams());

    }
}
