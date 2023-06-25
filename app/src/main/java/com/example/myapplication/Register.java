package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ImageView;

import com.example.myapplication.api.API;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private API api;
    Button BSelectImage;
    ImageView IVPreviewImage;

    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = API.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        BSelectImage = findViewById(R.id.register_select_image);
        IVPreviewImage = findViewById(R.id.register_iv_preview_image);

        AppCompatButton linkToLogin = findViewById(R.id.register_link_to_login);
        linkToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        });

        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        AppCompatButton registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do something when the Cancel button is clicked
                }
            });
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            if (!checkUsername()) {
                builder.setTitle("Username Error");
                builder.setMessage("username must have at least one english letter");
                builder.show();
                return;
            }

            if (!checkPassword()) {
                builder.setTitle("Password Error");
                builder.setMessage("password must contain at least 8 characters, combination of english letters and digits");
                builder.show();
                return;
            }

            if (!checkConfirmPassword()) {
                builder.setTitle("Confirm Password Error");
                builder.setMessage("Passwords don't match");
                builder.show();
                return;
            }

            if (!checkDisplayName()) {
                builder.setTitle("Display Name Error");
                builder.setMessage("display name must have at least one english letter and contain a maximum of 15 characters");
                builder.show();
                return;
            }

            //check profile pic
            String profilePicBase64 = "";
            Bitmap resizedBitmap = null;
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Bitmap bm = ((BitmapDrawable) IVPreviewImage.getDrawable()).getBitmap();
                resizedBitmap = Bitmap.createScaledBitmap(bm, 30, 25, false);
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] profilePicBitmap = byteArrayOutputStream.toByteArray();
                profilePicBase64 = Base64.encodeToString(profilePicBitmap, Base64.DEFAULT);
            } catch (Exception e) {
                builder.setTitle("Profile Pic Error");
                builder.setMessage("User must have a profile pic");
                builder.show();
                return;
            }

            EditText usernameEt = findViewById(R.id.register_username_edittext);
            String username = usernameEt.getText().toString();
            EditText passwordEt = findViewById(R.id.register_password_edittext);
            String password = passwordEt.getText().toString();
            EditText displayNameEt = findViewById(R.id.register_display_name_edittext);
            String displayName = displayNameEt.getText().toString();
            String profilePicIntent = profilePicBase64;
            Intent intent = new Intent(this, ContactList.class);
            Callback<Void> callback = new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 409) {
                        builder.setTitle("Register Error");
                        builder.setMessage("User already registered");
                        builder.show();
                    } else {
                        Callback<ResponseBody> stringCallback = new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String token = response.body().string();
                                    intent.putExtra("username", username);
                                    intent.putExtra("token", token);
                                    intent.putExtra("displayName", displayName);
                                    intent.putExtra("profilePic", profilePicIntent);
                                    startActivity(intent);
                                }
                                catch (Exception e) {

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                int a = 1;
                            }
                        };
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Register.this, instanceIdResult -> {
                            final String newToken = instanceIdResult.getToken();
                            api.getToken(username, password, newToken, stringCallback);
                        });
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    int a = 1;
                }
            };
            api.register(username, password, displayName, profilePicBase64, callback);
        });
    }

    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                            IVPreviewImage.setImageBitmap(
                                    selectedImageBitmap);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    IVPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

    private boolean checkUsername() {
        EditText usernameEt = findViewById(R.id.register_username_edittext);
        String username = usernameEt.getText().toString();
        String regex = ".*[a-zA-Z].*";
        boolean hasEnglishChar = username.matches(regex);
        return hasEnglishChar;
    }

    private boolean checkPassword() {
        EditText passwordEt = findViewById(R.id.register_password_edittext);
        String password = passwordEt.getText().toString();
        String regexLetter = ".*[a-zA-Z].*";
        String regexNumber = ".*\\d.*";
        boolean hasMinimumLength = password.length() > 7;
        boolean hasEnglishLetter = password.matches(regexLetter);
        boolean hasNumber = password.matches(regexNumber);
        return hasMinimumLength && hasEnglishLetter && hasNumber;
    }

    private boolean checkConfirmPassword() {
        EditText passwordEt = findViewById(R.id.register_password_edittext);
        String password = passwordEt.getText().toString();
        EditText confirmPasswordEt = findViewById(R.id.register_confirm_password_edittext);
        String confirmPassword = confirmPasswordEt.getText().toString();
        return password.equals(confirmPassword);
    }

    private boolean checkDisplayName() {
        EditText displayNameEt = findViewById(R.id.register_display_name_edittext);
        String displayName = displayNameEt.getText().toString();
        String regexLetter = ".*[a-zA-Z].*";
        boolean hasEnglishLetter = displayName.matches(regexLetter);
        return displayName.length() < 16 && hasEnglishLetter;
    }
}
