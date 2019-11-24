package com.myapp.activity;



import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.Window;
import android.view.WindowManager;

import com.easypay.mobilehelper.auth.IdentityManager;
import com.easypay.mobile.AWSMobileClient;
import com.myapp.R;
import com.myapp.demo.bots.ConversationalBotVoiceFragment;
import com.myapp.model.HelpDeskModel;

import java.util.ArrayList;
import java.util.List;


public class BotActivity extends AppCompatActivity {

    private GridLayoutManager lLayout;
    private IdentityManager identityManager;
    private  String selected;

    List<HelpDeskModel> allItems = new ArrayList<HelpDeskModel>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_bot);
        Intent intent = getIntent();
        selected = intent.getStringExtra("selected");

        // Obtain a reference to the mobile client. It is created in the Application class,
        // but in case a custom Application class is not used, we initialize it here if necessary.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        // Obtain a reference to the mobile client. It is created in the Application class.
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();

        setContentView(R.layout.activity_home);

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        Bundle bundle = new Bundle();
        bundle.putString("selected", selected);

        Fragment fragment = new ConversationalBotVoiceFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment, ConversationalBotVoiceFragment.class.getSimpleName())
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }


    @Override
    protected void onStop() {
        super.onStop();
        com.easypay.mobile.bots.Conversation.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}