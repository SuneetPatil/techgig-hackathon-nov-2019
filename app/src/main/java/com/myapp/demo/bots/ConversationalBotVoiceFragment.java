package com.myapp.demo.bots;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.mobileconnectors.lex.interactionkit.InteractionClient;
import com.amazonaws.mobileconnectors.lex.interactionkit.Response;
import com.amazonaws.mobileconnectors.lex.interactionkit.continuations.LexServiceContinuation;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.InteractionListener;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexrts.model.DialogState;

import com.amazonaws.services.translate.AmazonTranslateAsyncClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import com.easypay.mobile.AWSMobileClient;
import com.easypay.mobile.bots.Conversation;
import com.easypay.mobilehelper.util.ViewHelper;
import com.google.gson.Gson;
import com.myapp.Helper.GifImageView;
import com.myapp.R;
import com.myapp.adapter.ChatAdapter;
import com.myapp.adapter.SuggestionAdapter;
import com.myapp.adapter.SwipeSuggestionAdapter;
import com.myapp.constants.Constants;
import com.myapp.model.Chat;
import com.myapp.model.Suggestions;
import com.myapp.model.SwipeSuggestion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.easypay.mobilehelper.util.ThreadUtils.runOnUiThread;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationalBotVoiceFragment extends Fragment implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    final private String TAG = "ConversationalBotVoice";
    private Context context;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 100;
    private Boolean mAudioPermissionGranted = false;
    private Boolean mShowRationale = true;
    private AWSCredentialsProvider credentialsProvider;
    private List<Chat> chatList = new ArrayList<>();
    private List<Suggestions> suggestionList;
    private RecyclerView recyclerView;
    private RecyclerView mSuggestionRecyclerView, swipe_suggestion_recycler_view;
    private ChatAdapter mAdapter;
    private SuggestionAdapter mSuggestionAdapter;
    private SwipeSuggestionAdapter swipeSuggestionAdapter;
    EditText msgEdtTxt;
    Button sndBtn, radioButtonSelectButton;
    boolean isVoiceText;
    boolean isVoice;
    boolean isSuggestionValueSelected;
    private SpeechRecognizer sr;
    private InteractionClient lexInteractionClient;
    private boolean inConversation = false;
    private LexServiceContinuation convContinuation;
    ImageView voiceImage;
    private TextToSpeech tts;
    Boolean voiceInit = false;
    Handler myHandler;
    Runnable updateRunnable;
    HashMap<String, String> myHash;
    static int micClickedTimes = 0;
    private static final String LOG_TAG = "VoiceTag";
    String result = "";
    AmazonTranslateAsyncClient translateAsyncClient;
    AWSCredentials awsCredentials;
    TranslateTextRequest translateTextRequest;
    private boolean askDate = false;
    private boolean askTime = false;
    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;
    private RadioGroup mainRadioGroup;
    private LinearLayout radioButtonLinearLayout;
    private View view;
    private String inputType = null;
    private  String AM_PM ;
    private ArrayList<SwipeSuggestion> list;
    private String selected;
    final int UPI_PAYMENT = 0;
    private GifImageView anim;
    private ArrayList<String> contactsName = new ArrayList<>();
    private ArrayList<String> contactsVPA = new ArrayList<>();
    private final int ANIM_DISPLAY_LENGTH = 5000;

    /*
    * For getting instance of ConversationalBotVoiceFragment.
    * */
    public static ConversationalBotVoiceFragment newInstance(Bot bot) {
        ConversationalBotVoiceFragment fragment = new ConversationalBotVoiceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_conversational_bot_voice, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mainRadioGroup = view.findViewById(R.id.mainRadioGroup);
        radioButtonLinearLayout = view.findViewById(R.id.radioButtonLinearLayout);
        radioButtonSelectButton = view.findViewById(R.id.radioButtonSelectButton);
        anim = view.findViewById(R.id.anim);
        selected = getArguments().getString("selected");
         ArrayList<String> Number = getContactNumber("Number",getContext());
        awsCredentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return Constants.AWSAccessKeyId;
            }

            @Override
            public String getAWSSecretKey() {
                return Constants.AWSSecretKey;
            }
        };

        translateAsyncClient = new AmazonTranslateAsyncClient(awsCredentials);
        translateTextRequest = new TranslateTextRequest();

        isSuggestionValueSelected = false; // To check if the suggestion value is selected or not.
        //This is for Suggestion Recycler View
        // Get the reference of RecyclerView
        mSuggestionRecyclerView = (RecyclerView) view.findViewById(R.id.suggestion_recycler_view);
        swipe_suggestion_recycler_view = view.findViewById(R.id.swipe_suggestion_recycler_view);

        tts = new TextToSpeech(getContext(), this);
        //create an handler for handling voice.
        myHandler = new Handler();
        // Start listening to voice from handler.
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                startListeningVoice();
            }
        };

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                myCalendar.set(Calendar.HOUR,i);
                myCalendar.set(Calendar.MINUTE, i1);
                if(i < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }
                updateTimeLabel();
            }
        };

        //For getting selected radio button
        radioButtonSelectButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = mainRadioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton) view.findViewById(selectedId);

                String radioButtonText = radioButton.getText().toString();

                isSuggestionValueSelected = true;

                if (isVoice) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            voiceImage.clearAnimation(); //For stopping animation(mic rotation).
                        }
                    });

                    Constants.muteAudio(getActivity().getApplicationContext()); //Mute the beep sound.
                    isVoice = false;
                    micClickedTimes = 0; //For activating mic again
                    if (sr != null) {
                        sr.stopListening();
                        sr.cancel();
                        sr.destroy();
                    }
                }

                if((inputType.contains("date")) && (radioButtonText.contains("Choose"))){
                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                } else if ((inputType.contains("time"))&& (radioButtonText.contains("Choose"))){
                    new TimePickerDialog(getActivity(),time, myCalendar
                            .get(Calendar.HOUR),myCalendar.get(Calendar.MINUTE),true).show();
                } else {
                    translateText(radioButtonText, false);
                }


            }
        });

        anim.setGifImageResource(R.drawable.spark);


        return view;
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        msgEdtTxt.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTimeLabel() {
        String myFormat = "hh:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        msgEdtTxt.setText(sdf.format(myCalendar.getTime()) + " " + AM_PM);
    }

    //This method is used for showing suggestion values in the suggestion recycler view and handle onclick
    // event of suggestion recycler view.

    private  void showSuggestionValues(Map<String, String> suggestionValues) {

        //hide keyboard
        hideKeyboardFrom(getActivity().getApplicationContext(),msgEdtTxt );
        radioButtonLinearLayout.setVisibility(View.GONE);
        swipe_suggestion_recycler_view.setVisibility(View.GONE);

        // make the suggestion recycler view visible.
        mSuggestionRecyclerView.setVisibility(View.VISIBLE);
        // set a LinearLayoutManager with horizontal orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);


        //LCM of 3 and 1
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // 4is the sum of items in one repeated section
                switch (position % 4) {
                    // first 3 items span 3 columns each
                    case 0:
                    case 1:
                        //return 1;
                    case 2:
                        return 1;
                    // next 1 items span 1 column each
                    case 3:
                        return 3;
                }
                throw new IllegalStateException("internal error");
            }
        });



        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        suggestionList = new ArrayList<>();
        //Converting Map value to array list
        ArrayList<String> list = new ArrayList<>(suggestionValues.values());

        //split the list into an String Array on the basis of ','.
        String[] valueList = list.get(0).split(","); //List will have only one item in it, so get(0) will get the first value only.

        if(valueList.length==2){
            mSuggestionRecyclerView.setLayoutManager(linearLayoutManager);
        } else if(valueList.length==3){
            mSuggestionRecyclerView.setLayoutManager(layoutManager);
        }

        //For setting the values in the suggestion list from the String Array.
        for (String value : valueList) {
            suggestionList.add(new Suggestions(value));
        }

        if(suggestionList.size()==1){
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return 3;
                }
            });
        }

        //Set the suggestion list values to Adapter
        mSuggestionAdapter = new SuggestionAdapter(suggestionList);

        mSuggestionRecyclerView.setAdapter(mSuggestionAdapter); // set the Adapter to RecyclerView

        //Handle onItemClick of Suggestion recycler view.
        mSuggestionAdapter.setOnItemClickListener(new SuggestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                isSuggestionValueSelected = true;
                String suggestionText = suggestionList.get(position).getSuggestion();
                if (isVoice) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            voiceImage.clearAnimation(); //For stopping animation(mic rotation).
                        }
                    });

                    Constants.muteAudio(getActivity().getApplicationContext()); //Mute the beep sound.
                    isVoice = false;
                    micClickedTimes = 0; //For activating mic again
                    if (sr != null) {
                        sr.stopListening();
                        sr.cancel();
                        sr.destroy();
                    }
                }


                    translateText(suggestionText, false);

            }
        });
    }


    /*
    * This method is for listening the voice.
    * */

    private void startListeningVoice() {
        isVoice = true;
        micClickedTimes = 1;//For deactivating mic
        final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                voiceImage.startAnimation(animation);
            }
        });

        regVoice();
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        voiceImage = (ImageView) view.findViewById(R.id.voice_img);
        voiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSuggestionValueSelected = false;
                msgEdtTxt.clearFocus();
                msgEdtTxt.setCursorVisible(false);
                Constants.unmuteAudio(getActivity().getApplicationContext()); //To unmute the beep sound of the device.
                hideKeyBoard(); //Hiding the keyboard of the device.
                if (micClickedTimes == 0) {
                    startListeningVoice();
                }
            }
        });
        context = getContext();

        //request microphone permissions*/
        requestPermission();
        msgEdtTxt = (EditText) view.findViewById(R.id.msg_edttxt);
        sndBtn = (Button) view.findViewById(R.id.send_btn);
        sndBtn.setVisibility(View.GONE);

        msgEdtTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !tts.isSpeaking()) {
                    msgEdtTxt.setCursorVisible(true);
                    isSuggestionValueSelected = false;
                    Constants.muteAudio(getActivity().getApplicationContext());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            voiceImage.clearAnimation(); //For stopping animation(mic rotation).
                            voiceImage.setVisibility(View.GONE);
                        }
                    });

                    if (sr != null) {
                        sr.stopListening();
                        sr.cancel();
                        sr.destroy();
                    }
                    sndBtn.setVisibility(View.VISIBLE);
                    micClickedTimes = 0;
                } else {
                    msgEdtTxt.setCursorVisible(false);
                    msgEdtTxt.clearFocus();

                }
            }
        });

        msgEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    if (sndBtn.getVisibility() == View.GONE) {
                        sndBtn.setVisibility(View.VISIBLE);
                        voiceImage.setVisibility(View.GONE);
                        msgEdtTxt.setCursorVisible(true);
                    }
                } else if (editable.length() == 0) {
                    if (sndBtn.getVisibility() == View.VISIBLE) {
                        sndBtn.setVisibility(View.GONE);
                        voiceImage.setVisibility(View.VISIBLE);

                    }
                }

            }
        });

        sndBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!msgEdtTxt.getText().toString().equals("")) {
                    isSuggestionValueSelected = false;
                    translateText(msgEdtTxt.getText().toString(), false);
                    micClickedTimes = 0;
                    isVoice = false;
                }
            }
        });

        mAdapter = new ChatAdapter(chatList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        if(selected.contains("pay")) {
            myHash = new HashMap<>();
            myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "finish");
            Constants.DIALOG_STATE = "";
            micClickedTimes = 0;
            uiChatData("Hello , How can I help you?", false);//Default text to display.
            uiChatData("What would you like to do?", false);
           // Constants.unmuteAudio(getActivity().getApplicationContext());
            // tts.speak(msg, TextToSpeech.QUEUE_FLUSH, myHash);
            Map<String,String> options  = new HashMap<>();
            options.put("options","Pay money to my contact,Purchase the product");
            showSuggestionValues(options);
        } else if(selected.contains("buy")) {

        }

        startNewConversation();
    }

    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.RECORD_AUDIO) && mShowRationale) {
                mShowRationale = false;
                ViewHelper.showDialog(getActivity(),
                        getString(R.string.feature_app_conversational_bots_permissions_header),
                        getString(R.string.feature_app_conversational_bots_permissions_string),
                        "Proceed", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermission();
                            }
                        },
                        "Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        } else {
            mAudioPermissionGranted = true;
            initializeLexSDK();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mAudioPermissionGranted = true;
                    initializeLexSDK();
                } else {
                    mAudioPermissionGranted = false;
                    mShowRationale = true;
                }
                return;
            }
        }
    }

    /*For hiding the device keyboard.*/
    private void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getActivity().getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        micClickedTimes = 0;
        credentialsProvider = AWSMobileClient.defaultMobileClient()
                .getIdentityManager().getUnderlyingProvider();
        //text bot
        lexInteractionClient = new InteractionClient(context,
                credentialsProvider,
                Regions.fromName(Constants.Region),
                Constants.BotName,
                Constants.BotAlias);
        lexInteractionClient.setInteractionListener(interactionListener);


    }

    /**
     * Initializes Lex client.
     */
    private void initializeLexSDK() {
        if (mAudioPermissionGranted) {

        }
    }

    //Supported Languages.
    public String getLanguage(String lang) {
        switch (lang) {
            case "ENGLISH":
                return "en";
            case "FRENCH":
                return "fr-FR";
            case "GERMANY":
                return "de-DE";
            case "NORWEGIAN":
                return "nb-NO";
            default:
                return "en";
        }
    }

    /*This method is to use AndroidSpeechRecognition.*/

    public void regVoice() {
        sr = SpeechRecognizer.createSpeechRecognizer(getContext());
        sr.setRecognitionListener(new Listener());

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //Specify language
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, getLanguage(Constants.SelectedLanguage));
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        sr.startListening(intent);
    }

    @Override
    public void onInit(int i) {

        if (i == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            // Set listener to know when a particular utterance is done playing
            int utterance_result = tts.setOnUtteranceCompletedListener(this);
            Log.d(TAG, "Utterance Result: " + utterance_result);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                voiceInit = true;
            }
            tts.speak("Hello , How can I help you? What would you like to do?", TextToSpeech.QUEUE_FLUSH, null);

        } else {
            Log.e("TTS", "Initialization Failed!");
        }

    }

    @Override
    public void onUtteranceCompleted(String s) {
        if (s.equals("done")) {
            myHandler.post(updateRunnable);
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    voiceImage.clearAnimation(); //For stopping animation(mic rotation).
                }
            });

            if (myHandler != null) {
                myHandler.removeCallbacks(updateRunnable); //For stopping the handler.
            }
            micClickedTimes = 0;
        }
    }

    /*For Speech Recognition Listener.*/

    class Listener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");

        }

        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
            //If no proper value is selected.
            if (!isSuggestionValueSelected) {
                uiChatData("****", true);
                isVoiceText = true;
                uiChatData("Please can you repeat that?", false);
                voiceImage.clearAnimation();
            }
        }

        public void onResults(Bundle results) {
            voiceImage.setAnimation(null);
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            translateText(data.get(0).toString(), true);
        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }


    /*
    * This method is for printing the response and request text on the screen.
    *
    * */
    public void uiChatData(String msg, Boolean isSent) {
        Log.d(TAG, " *** MESSAGE VALUE IS : " + msg + " AND DIALOG_STATE IS : " + Constants.DIALOG_STATE + " ***");
        Chat chat;

        // If block added for image support
        if(msg == "SHOW_IMAGE"){
            chat =   new Chat(msg.toString(), getTime(), isSent, true);
        }
        else {
            if (voiceInit && !isSent && isVoice) {
                myHash = new HashMap<>();
                if (Constants.DIALOG_STATE.equals("Fulfilled") || Constants.DIALOG_STATE.equals("Failed")) {
                    //This block is only for "Sorry, I could not understand. Will transfer Call to my superior." case.
                    if (Constants.DIALOG_STATE.equals("Failed")) {
                        mSuggestionRecyclerView.setVisibility(View.GONE);
                        radioButtonLinearLayout.setVisibility(View.GONE);
                        micClickedTimes = 0;
                    }
                    myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "finish");
                    Constants.DIALOG_STATE = "";
                    micClickedTimes = 0;

                } else {
                    myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "done");
                }
                Log.d(TAG, " *** MSG VALUE IS :" + msg + " ***");
                Constants.unmuteAudio(getActivity().getApplicationContext());
                tts.speak(msg, TextToSpeech.QUEUE_FLUSH, myHash);
                isVoiceText = false;
            }

            StringBuilder myName = new StringBuilder(msg);
            for (int i = 0; i < myName.length(); i++) {
                //For converting \n to line separator from response
                if (myName.charAt(i) == 92 && myName.charAt(i + 1) == 110) {
                    myName.replace(i, i + 2, System.getProperty("line.separator"));
                }
            }

             chat = new Chat(myName.toString(), getTime(), isSent, false);
        }
        chatList.add(chat);
        mAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(chatList.size() - 1);
    }

    /*
        To get the Current date and time.
     */
    public String getTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c.getTime());
        return String.valueOf(formattedDate);
    }

    /*
    *  To remove space from SR (Service Request) Number before sending to LEX for conversion.
    * */
    private String removeSpaceFromZero(String valueWithSpace) {
        char[] strArray;
        StringBuffer sb;

        if (valueWithSpace.matches(("\\d+[ ]?\\d+"))) {
            strArray = valueWithSpace.toCharArray();
            sb = new StringBuffer();
            for (int i = 0; i < strArray.length; i++) {

                if (!Character.isWhitespace(valueWithSpace.charAt(i))) {
                    sb.append(strArray[i]);
                }
            }
            Log.d(TAG, "IF Returning Value is : " + sb.toString());
            return sb.toString();
        } else {
            Log.d(TAG, "Returning Value is : " + valueWithSpace);
            return valueWithSpace;
        }
    }

    /**
     * Read user text input.
     */
    private void textEntered(String engText, Boolean isVoice, String selectedLangText) {
        Log.d(TAG,"inConversation: "+inConversation+", engText: "+engText+", isVoice: "+isVoice+", selectedLangText: "+selectedLangText);
        if (!inConversation) {
            Log.d(TAG, " -- New conversation started" + engText);
            startNewConversation(); //TODO: Uncomment this
            isVoiceText = isVoice;
            uiChatData(removeSpaceFromZero(selectedLangText), true);
            Log.d(TAG, "textEntered:: IF *** TEXT FOR CONVERSION : " + engText);
            engText = removeSpaceFromZero(engText);
            lexInteractionClient.textInForTextOut(removeSpaceFromZero(engText), null);
            inConversation = true;
        } else {
            Log.d(TAG, "textEntered:: ELSE *** TEXT FOR CONVERSION : " + engText);
            Log.d(TAG, " Responding with text: " + engText + " selected text : " + selectedLangText);
            isVoiceText = isVoice;
            uiChatData(removeSpaceFromZero(selectedLangText), true);
            convContinuation.continueWithTextInForTextOut(removeSpaceFromZero(engText));
        }
        clearTextInput();
    }


    /**
     * Pass user input to Lex client.
     *
     * @param continuation
     */
    private void readUserText(LexServiceContinuation continuation) {
        convContinuation = continuation;
        inConversation = true;
    }

    /**
     * Clears the current conversation history and closes the current request.
     */
    private void startNewConversation() {
        Log.d(TAG, "Starting new conversation");
        Conversation.clear();
        inConversation = false;
        clearTextInput();
    }

    /**
     * Clear text input field.
     */
    private void clearTextInput() {
        msgEdtTxt.setText("");
    }


    final InteractionListener interactionListener = new InteractionListener() {
        @Override
        public void onReadyForFulfillment(Response response) {
            Log.d("TAG", "Transaction completed successfully");
            inConversation = false;
            final Gson gson = new Gson();
            Log.d(TAG, " *** GSON VALUE IS : " + gson.toString() + "***");
            Log.d(TAG, " *** RESPONSE VALUE IS : " + response.toString() + "***");
            Log.d(TAG, " -- Responding with text: " + gson.toJson(response.getSlots()));
            isVoiceText = true;

            translateFromEngText(gson.toJson(response.getSlots()), false);

        }

        @Override
        public void promptUserToRespond(Response response, LexServiceContinuation continuation) {
            Constants.DIALOG_STATE = response.getDialogState();
            for (int i=0; i< mainRadioGroup.getChildCount(); i++){
                mainRadioGroup.removeViewAt(i);
            }
            Log.d("***Log", response.getDialogState());
                if (!DialogState.ReadyForFulfillment.toString().equals(response.getDialogState())
                    && !DialogState.Fulfilled.toString().equals(response.getDialogState())) {
                try {
                    int genericAttachmentsSize = 0;
                    ArrayList<String> buttonNames = new ArrayList<>();
                    ArrayList<String> swipeButtonNames = new ArrayList<>();
                    ArrayList<String> URLs = new ArrayList<String>();
                    Log.d("***Log-IF:", response.getTextResponse());
                    String commaSeperatedButtons = "";
                    Map<String, String> sessionValues = new HashMap<String, String>();
                    sessionValues.clear();
                    JSONObject appContext = new JSONObject(response.getSessionAttributes());
                    if (appContext.has("appContext")) {
                        String appContextin = appContext.getString("appContext");
                        JSONObject responseCardObject = new JSONObject(appContextin);
                        JSONObject responseCard = responseCardObject.getJSONObject("responseCard");
                        JSONArray genericAttachments = responseCard.getJSONArray("genericAttachments");
                        genericAttachmentsSize = genericAttachments.length();
                        if (genericAttachmentsSize > 1){
                            inputType = "swipeCard";

                            for(int i=0; i<genericAttachmentsSize; i++){
                                JSONObject genericAttachmentObject = genericAttachments.getJSONObject(i);
                                URLs.add(genericAttachmentObject.getString("imageUrl") + "," + genericAttachmentObject.getString("subTitle"));
                                swipeButtonNames.add(genericAttachmentObject.getString("title"));
                            }
                        }
                        JSONObject buttonObject = genericAttachments.getJSONObject(0);
                        JSONArray buttons = buttonObject.getJSONArray("buttons");

                        String validationin = appContext.getString("validation");
                        JSONObject validationinObject = new JSONObject(validationin);
                        String key = validationinObject.getString("key");
                        JSONArray Suggestion = validationinObject.getJSONArray("suggestion");



                        for (int i = 0; i < Suggestion.length(); i++) {
                            buttonNames.add(Suggestion.getString(i));
                        }

                        for (int i = 0; i < buttonNames.size(); i++) {
                            commaSeperatedButtons = commaSeperatedButtons + "," + buttonNames.get(i);
                            inputType = "button";
                            // inputType = "radio_button"; // TODO: Uncomment later for radio button
                        }
                        if(key.contains("date")){
                            commaSeperatedButtons = commaSeperatedButtons + "," + "Choose";
                            inputType = "date";
                        }
                        if(key.contains("time")){
                            commaSeperatedButtons = commaSeperatedButtons + "," + "Choose";
                            inputType = "time";
                        }
                        commaSeperatedButtons = commaSeperatedButtons.substring(1);//To remove first comma
                        sessionValues.put("button",commaSeperatedButtons);
                    }

                    System.out.print(" *** SESSION VALUE IS : " + sessionValues + " ***");

                    if("ElicitSlot".equals(response.getDialogState()) && "product".equals(response.getSlotToIllicit())){
                        Map<String,String> options  = new HashMap<>();
                        options.put("options","Cake,Lays,Chocolates");
                        inputType="button";
                        showSuggestionValues(options);
                    }else if (sessionValues.size() > 0) {
                        Log.d(TAG, " *** SESSION HAS VALUES. ***");
                        if(inputType=="button" || inputType=="date" || inputType=="time") {
                            if(genericAttachmentsSize==1) {
                                showSuggestionValues(sessionValues);
                            }
                            else {
                                inputType = "swipeCard";
                              Map<String,String> swipeMap = new HashMap<String,String>();
                              for(int i=0;i<genericAttachmentsSize;i++){
                                  swipeMap.put(swipeButtonNames.get(i),URLs.get(i));
                              }

                            }
                        }
                    } else {
                        radioButtonLinearLayout.setVisibility(View.GONE);
                        mSuggestionRecyclerView.setVisibility(View.GONE);
                        Log.d(TAG, " *** SESSION HAS NO VALUES. ***");
                    }

                    isVoiceText = true;



                        translateFromEngText(response.getTextResponse(), false);

                    readUserText(continuation);
                } catch (Exception ex){
                    Log.e("Exception",""+ex);
                }
            } else if (DialogState.Fulfilled.toString().equals(response.getDialogState())) {
                Log.d("***Log-ELSE:", response.getTextResponse());
                mSuggestionRecyclerView.setVisibility(View.GONE);
                radioButtonLinearLayout.setVisibility(View.GONE);
                isVoiceText = true;
                translateFromEngText( response.getTextResponse() +", Please choose payment apps from below options", false);//response.getTextResponse() +
                if(response.getIntentName().contains("npci_intent_buy")){
                    Map<String, String> slots = new HashMap<>();
                    slots = response.getSlots();
                    String product = slots.get("product");
                    if(product.contains("lays")){
                        payUsingUpi("Amount", "upiId", "Merchant name", "Purchasing lays");

                    } else  if(product.contains("chocolates")){
                        payUsingUpi("Amount", "upiId", "Merchant name", "Purchasing chocolates");

                    }else if(product.contains("cake")){
                        payUsingUpi("Amount", "upiId", "Merchant name", "Purchasing cake");
                    }
                } else {
                    try {
                        Map<String, String> slots = new HashMap<>();
                        slots = response.getSlots();
                        payUsingUpi(slots.get("amount"), slots.get("name"), "name", slots.get("purpose"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                inConversation = false;
            }

        }

        @Override
        public void onInteractionError(Response response, Exception e) {
            if (response != null) {
                if (DialogState.Failed.toString().equals(response.getDialogState())) {

                    Constants.DIALOG_STATE = response.getDialogState();
                    Log.d(TAG, "***Log @ NO CASE : " + response.getTextResponse());
                    isVoiceText = true;
                    translateFromEngText(response.getTextResponse(), false);
                    inConversation = false;
                } else {
                    isVoiceText = true;
                    uiChatData("Please retry", false);
                }
            } else {
                Log.e(TAG, "Interaction error", e);
                inConversation = false;
            }
        }
    };


    @Override
    public void onStop() {
        if (tts != null) {
            tts.stop();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Conversation.clear();
        inConversation = false;
        convContinuation = null;
        micClickedTimes = 0;
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.shutdown();
        }
        myHandler.removeCallbacks(updateRunnable);
        super.onDestroy();
    }

    /*
    * To translate text without voice
    * */

    public void translateText(String text, Boolean isVoice) {
        if (Constants.SelectedLanguage.equals("ENGLISH")) {
            Log.d(TAG, "translateText:: TEXT VALUE IS : " + text + " ***");
            textEntered(text, isVoice, text);
        } else {
            langTranslator(text, Constants.SelectedLanguage, "en", isVoice);
        }
    }

    //To translate text with voice
    public void translateFromEngText(String text, Boolean isVoice) {
        Log.d(TAG, " *** RESPONSE VALUE IS " + text);
        if (Constants.SelectedLanguage.equals("ENGLISH")) {
            uiChatData(text, isVoice);
        } else {
            langTranslatorFromEng(text, "en", Constants.SelectedLanguage, isVoice);
        }
    }


    public void langTranslator(final String text, String from, String to, final Boolean isVoice) {
        switch (from) {
            case "ENGLISH":
                from = "en";
                break;
            case "FRENCH":
                from = "fr";
                break;
            case "GERMANY":
                from = "de";
                break;
            case "NORWEGIAN":
                from = "no";
                break;
        }

        translateTextRequest.withText(text); // Eg: "Hello, world"
        translateTextRequest.withSourceLanguageCode(from); // Eg: "en"
        translateTextRequest.withTargetLanguageCode(to); // Eg: "es"
        translateAsyncClient.translateTextAsync(translateTextRequest, new AsyncHandler<TranslateTextRequest, TranslateTextResult>() {
            @Override
            public void onError(Exception e) {
                Log.e(LOG_TAG, "Error occurred in translating the text: " + e.getLocalizedMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uiChatData("Error while translating", true);
                    }
                });
            }

            @Override
            public void onSuccess(TranslateTextRequest request, TranslateTextResult translateTextResult) {
                Log.d(LOG_TAG, "Original Text: " + request.getText());
                Log.d(LOG_TAG, "Translated Text: " + translateTextResult.getTranslatedText());
                final String input = request.getText();
                final String output = translateTextResult.getTranslatedText();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textEntered(output, isVoice, ""+input);
                    }
                });
            }
        });
    }


    public void langTranslatorFromEng(final String text, String from, String to, final Boolean isVoice) {


        switch (to) {
            case "ENGLISH":
                to = "en";
                break;
            case "FRENCH":
                to = "fr";
                break;
            case "GERMANY":
                to = "de";
                break;
            case "NORWEGIAN":
                to = "no";
                break;
        }

        translateTextRequest.withText(text); // Eg: "Hello, world"
        translateTextRequest.withSourceLanguageCode(from); // Eg: "en"
        translateTextRequest.withTargetLanguageCode(to); // Eg: "es"
        translateAsyncClient.translateTextAsync(translateTextRequest, new AsyncHandler<TranslateTextRequest, TranslateTextResult>() {
            @Override
            public void onError(Exception e) {
                Log.e(LOG_TAG, "Error occurred in translating the text: " + e.getLocalizedMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uiChatData("Error while translating", false);
                    }
                });
            }

            @Override
            public void onSuccess(TranslateTextRequest request, TranslateTextResult translateTextResult) {
                Log.d(LOG_TAG, "Original Text: " + request.getText());
                Log.d(LOG_TAG, "Translated Text: " + translateTextResult.getTranslatedText());
                final  String output = translateTextResult.getTranslatedText();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uiChatData(output, isVoice);
                    }
                });
            }
        });

    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //region payments

    void payUsingUpi(String amount, String upiId, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getActivity().getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            // Toast.makeText(getActivity(),"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(getActivity())) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                // Toast.makeText(getActivity(), "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: "+approvalRefNo);
                //TODO: add gifImage here
                inConversation = false;
                Constants.DIALOG_STATE = "Fulfilled";
                translateFromEngText("Congratulations, Transaction successful", false);
                showAnimation(); // TODO: Uncomment this
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                // Toast.makeText(getActivity(), "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                inConversation = false;
                Constants.DIALOG_STATE = "Fulfilled";
                translateFromEngText("Sorry, Transaction failed", false);
                 // // showAnimation();// TODO: Comment this
            }
            else {
                // Toast.makeText(getActivity(), "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                inConversation = false;
                Constants.DIALOG_STATE = "Fulfilled";
                translateFromEngText("Sorry, Transaction failed", false);
                 // showAnimation();// TODO: Comment this
            }
        } else {
            // Toast.makeText(getActivity(), "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
            inConversation = false;
            Constants.DIALOG_STATE = "Fulfilled";
            translateFromEngText("Sorry, Transaction failed", false);
             // showAnimation();// TODO: Comment this
        }
    }

    private void showAnimation(){
        anim.setVisibility(View.VISIBLE);
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                anim.setVisibility(View.GONE);
            }
        }, ANIM_DISPLAY_LENGTH);
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    //endregion payments

    //region contacts
    public ArrayList<String> getContactNumber(final String Name, Context context)
    {
        String number="";
        contactsName.clear();
        contactsVPA.clear();
        ArrayList<String> numbers = new ArrayList<>();
        ContentResolver cr = getContext().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                "DISPLAY_NAME LIKE '" + Name + "%'", null, null);
        for(int i=0; i<cursor.getColumnCount();i++) {
        if (cursor.moveToPosition(i)) {
                String contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));//ContactsContract.Contacts._ID
                //
                //  Get all phone numbers.
                //
                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                while (phones.moveToNext()) {
                    number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String phoneName = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String VPA = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Note.DATA15));
                    int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    numbers.add(number);
                    contactsName.add(phoneName);
                    contactsVPA.add(VPA);
                }
                phones.close();
            }
        }
        cursor.close();
        return numbers;
    }
    //endregion contacts
}
