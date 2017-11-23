package com.humanity.team.liveagentchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.salesforce.android.chat.core.ChatConfiguration;
import com.salesforce.android.chat.core.model.PreChatEntity;
import com.salesforce.android.chat.core.model.PreChatEntityField;
import com.salesforce.android.chat.core.model.PreChatField;
import com.salesforce.android.chat.ui.ChatUI;
import com.salesforce.android.chat.ui.ChatUIClient;
import com.salesforce.android.chat.ui.ChatUIConfiguration;
import com.salesforce.android.service.common.utilities.control.Async;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    public static final String ORG_ID = "00D3B000000DYiJ";
    public static final String DEPLOYMENT_ID = "5723B0000004Clb";
    public static final String BUTTON_ID = "5733B0000004D4T";
    public static final String LIVE_AGENT_POD = "d.la1-c1cs-phx.salesforceliveagent.com";
    // e.g. “d.la.salesforce.com”

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // List of pre-chat fields
        LinkedList<PreChatField> preChatFields = new LinkedList<>();

// Some required fields
        PreChatField firstName = new PreChatField.Builder().required(true)
                .build("FirstName", "Please enter your first name", PreChatField.STRING);
        PreChatField lastName = new PreChatField.Builder().required(true)
                .build("LastName", "Please enter your last name", PreChatField.STRING);
        PreChatField email = new PreChatField.Builder().required(true)
                .build("Email", "Please enter your email", PreChatField.EMAIL);

// Some optional fields
        PreChatField origin = new PreChatField.Builder()
                .hidden(true)
                .value("Live Agent Android")
                .build("Origin", "Origin", PreChatField.STRING);

// A required picklist field
        PreChatField status = new PreChatField.Builder()
                .hidden(true)
                .value("New")
                .build("Status", "Status", PreChatField.STRING);


// A subject field containing the subject
        PreChatField subject = new PreChatField.Builder()
                .required(true)
                .build("Subject", "Subject", PreChatField.STRING);

        // A subject field containing the subject
        PreChatField caseRecordType = new PreChatField.Builder()
                .hidden(true)
                .value("0123B0000008zEg")
                .build("RecordTypeId", "RecordTypeId", PreChatField.STRING);

// Add the fields to the list
        preChatFields.add(firstName);
        preChatFields.add(lastName);
        preChatFields.add(email);
        preChatFields.add(subject);
        preChatFields.add(status);
        preChatFields.add(origin);
        preChatFields.add(caseRecordType);


// List of pre-chat entity mappings
        LinkedList<PreChatEntity> preChatEntities = new LinkedList<>();

// Create an entity field builder for Contact fields
        PreChatEntityField.Builder contactEntityBuilder = new PreChatEntityField.Builder()
                .doFind(true)
                .isExactMatch(true)
                .doCreate(true);

// Create the contact entity
        PreChatEntity contactEntity = new PreChatEntity.Builder()
                .saveToTranscript("Contact")
                .linkToEntityName("Case")
                .linkToEntityField("ContactId")
                .addPreChatEntityField(contactEntityBuilder.build("FirstName", "FirstName"))
                .addPreChatEntityField(contactEntityBuilder.build("LastName", "LastName"))
                .addPreChatEntityField(contactEntityBuilder.build("Email", "Email"))
                .build("Contact");

// Create an entity field builder for Case fields
        PreChatEntityField.Builder caseEntityBuilder = new PreChatEntityField.Builder()
                .doCreate(true);

// Create the case entity
        PreChatEntity caseEntity = new PreChatEntity.Builder()
                .showOnCreate(true)
                .saveToTranscript("Case")
                .addPreChatEntityField(caseEntityBuilder.build("Subject", "Subject"))
                .addPreChatEntityField(caseEntityBuilder.build("Status", "Status"))
                .addPreChatEntityField(caseEntityBuilder.build("Origin", "Origin"))
                .addPreChatEntityField(caseEntityBuilder.build("RecordTypeId", "RecordTypeId"))
                .build("Case");

// Add the entities to the list
        preChatEntities.add(contactEntity);
        preChatEntities.add(caseEntity);


// Create a chat core configuration instance
// with pre-chat fields and entity mappings
        ChatConfiguration chatConfiguration =
                new ChatConfiguration.Builder(ORG_ID, BUTTON_ID,
                        DEPLOYMENT_ID, LIVE_AGENT_POD)
                        .preChatFields(preChatFields)
                        .preChatEntities(preChatEntities)
                        .build();

        /*// Create a core configuration instance
        ChatConfiguration chatConfiguration =
                new ChatConfiguration.Builder(ORG_ID, BUTTON_ID,
                        DEPLOYMENT_ID, LIVE_AGENT_POD)
                        .build();*/

        // Create a UI configuration instance from a core config object
// and start session!
        ChatUI.configure(ChatUIConfiguration.create(chatConfiguration))
                .createClient(getApplicationContext())
                .onResult(new Async.ResultHandler<ChatUIClient>() {
                    @Override public void handleResult (Async<?> operation,
                                                        @NonNull ChatUIClient chatUIClient) {
                        chatUIClient.startChatSession(MainActivity.this);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
