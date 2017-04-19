package com.gagagugu.ggservice.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.interfaces.ServiceCommunicationCallback;
import com.gagagugu.ggservice.models.ConstantsGGS;
import com.gagagugu.ggservice.utils.PermissionHandler;

import java.util.ArrayList;


public class ContactChooseDailogFragmentGGS extends DialogFragment implements View.OnClickListener {
    private static final int REQUEST_CAMERA = 1000;
    private static final int REQUEST_MICROPHONE_FOR_CALL = 1111;
    private static final int REQUEST_CAMERA_AUDIO = 1100;
    private static ServiceCommunicationCallback contactCallback;
    private RelativeLayout chatLayout, audioCallLayout, videoCallLayout, cellularCallLayout, cellularMessageLayout;
    private TextView titleTv;
    private ArrayList<String> contactPref;
    private String title;
    private String contactProfileId;
    private String contactImage;
    private String contactName;
    private PermissionHandler permissionHandler;
    private boolean isAudioCall;
    private String phoneNumber;

    public ContactChooseDailogFragmentGGS() {
        // Required empty public constructor
    }

    public static com.gagagugu.ggservice.fragment.ContactChooseDailogFragmentGGS newInstance(String title, ArrayList<String> contactPref, String contactId, String contactImage, String contactName,
                                                                                             String phoneNumber) {
        com.gagagugu.ggservice.fragment.ContactChooseDailogFragmentGGS fragment = new com.gagagugu.ggservice.fragment.ContactChooseDailogFragmentGGS();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("contact_profile_id", contactId);
        args.putString("contact_name", contactName);
        args.putString("contact_image", contactImage);
        args.putStringArrayList("contact_pref", contactPref);
        args.putString("phone_number", phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_contact_choose_dailog_fragment_gg, null);
        dialog.setContentView(contentView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        initializeView(contentView);
        getBundles();
        initializeListner();
        permissionHandler = new PermissionHandler(getActivity());
    }

    private void getBundles() {
        if (getArguments() != null) {
            title = getArguments().getString("title");
            contactProfileId = getArguments().getString("contact_profile_id");
            contactImage = getArguments().getString("contact_image");
            contactName = getArguments().getString("contact_name");
            contactPref = getArguments().getStringArrayList("contact_pref");
            phoneNumber = getArguments().getString("phone_number");
            titleTv.setText(title);
            setContactPrefLayout();
        }
    }

    private void setContactPrefLayout() {
        if (contactPref.contains(ConstantsGGS.AUDIO_CALL)) {
            audioCallLayout.setVisibility(View.VISIBLE);
        }
        if (contactPref.contains(ConstantsGGS.VIDEO_CALL)) {
            videoCallLayout.setVisibility(View.VISIBLE);
        }
        if (contactPref.contains(ConstantsGGS.CELLULAR_CALL)) {
            cellularCallLayout.setVisibility(View.VISIBLE);
        }

        if (!phoneNumber.equals("")) {
            cellularCallLayout.setVisibility(View.VISIBLE);
            cellularMessageLayout.setVisibility(View.VISIBLE);
        }

    }


    private void initializeView(View contentView) {
        chatLayout = (RelativeLayout) contentView.findViewById(R.id.chat_layout);
        audioCallLayout = (RelativeLayout) contentView.findViewById(R.id.audio_call_layout);
        videoCallLayout = (RelativeLayout) contentView.findViewById(R.id.video_call_layout);
        cellularCallLayout = (RelativeLayout) contentView.findViewById(R.id.cellular_call_layout);
        titleTv = (TextView) contentView.findViewById(R.id.title_tv);
        cellularMessageLayout = (RelativeLayout) contentView.findViewById(R.id.cellular_message_layout);
    }

    private void initializeListner() {
        chatLayout.setOnClickListener(this);
        audioCallLayout.setOnClickListener(this);
        videoCallLayout.setOnClickListener(this);
        cellularCallLayout.setOnClickListener(this);
        cellularMessageLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Log.e("GGSERVICE", "Service contactProfileId: " + contactProfileId + " contactImage: " + contactImage + " contactName: " + contactName);
        int id = v.getId();
        if (id == R.id.chat_layout) {
            if (contactCallback != null) {
                contactCallback.onChat(contactProfileId, contactProfileId, contactName);
            }
            dismissAllowingStateLoss();
        } else if (id == R.id.audio_call_layout) {
            Log.e("GGSERVICE", "Audio call from service");
            isAudioCall = true;
            checkPermissions();
        } else if (id == R.id.video_call_layout) {
            Log.e("GGSERVICE", "Video call from service");
            isAudioCall = false;
            checkPermissions();
        } else if (id == R.id.cellular_call_layout) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            StringBuilder stringBuilder = new StringBuilder()
                    .append("+")
                    .append(phoneNumber.trim());
            callIntent.setData(Uri.parse("tel:" + Uri.encode(stringBuilder.toString())));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(callIntent);
            dismissAllowingStateLoss();
        } else if (id == R.id.cellular_message_layout) {
            StringBuilder stringBuilder = new StringBuilder()
                    .append("+")
                    .append(phoneNumber.trim());
            startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("sms", stringBuilder.toString(), null)));
            dismissAllowingStateLoss();
        }
    }

    private void checkPermissions() {
        if (isAudioCall) {
            if (!hasMicrophonePermission()) {
                requestMicroPhonePermission();
            } else {
                initiateCall();
            }
        } else {
            if (!hasCameraPermission() && !hasMicrophonePermission()) {
                requestCameraAndAudioPermission();
            } else if (!hasCameraPermission() && hasMicrophonePermission()) {
                requestCameraPermission();
            } else if (hasCameraPermission() && !hasMicrophonePermission()) {
                requestMicroPhonePermission();
            } else {
                initiateCall();
            }
        }
    }

    public boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }


    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            dismissAllowingStateLoss();
            permissionHandler.showSettingsSnackbar(getResources().getString(R.string.permission_msg_camera_ggs));
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    private void requestMicroPhonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.RECORD_AUDIO)) {
            dismissAllowingStateLoss();
            permissionHandler.showSettingsSnackbar(getResources().getString(R.string.permission_msg_microphone));
        } else {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE_FOR_CALL);
        }
    }

    public boolean hasMicrophonePermission() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraAndAudioPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.RECORD_AUDIO)) {
            dismissAllowingStateLoss();
            permissionHandler.showSettingsSnackbar(getResources().getString(R.string.permission_msg_camera_record));
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    REQUEST_CAMERA_AUDIO);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e("GGSERVICE", "onRequestPermissionsResult called");
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initiateCall();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_camera_permission_denied_ggs), Toast.LENGTH_LONG).show();
                }
            }
            break;
            case REQUEST_MICROPHONE_FOR_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initiateCall();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_microphone_permission_denied), Toast.LENGTH_LONG).show();
                }
            }
            break;
            case REQUEST_CAMERA_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initiateCall();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_camera_microphone_permission_denied), Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    private void initiateCall() {
        if (isAudioCall) {
            if (contactCallback != null) {
                contactCallback.onAudioCall(contactProfileId, contactImage, contactName);
            }
            dismissAllowingStateLoss();

        } else {
            if (contactCallback != null) {
                contactCallback.onVideoCall(contactProfileId, contactImage, contactName);
            }
            dismissAllowingStateLoss();
        }
    }


    /**
     * method to register contact (chat, audio/video call) callback from service
     *
     * @param callback
     */
    public static void registerContactCallbackFromService(ServiceCommunicationCallback callback) {
        contactCallback = callback;
    }

    /**
     * method to unregister contact (chat, audio/video) call from service
     */
    public static void unregisterContactCallbackFromService() {
        contactCallback = null;
    }
}
