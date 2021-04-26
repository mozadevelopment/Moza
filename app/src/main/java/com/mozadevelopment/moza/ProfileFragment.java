package com.mozadevelopment.moza;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    Button saveProfile;
    CircleImageView profilePhoto;
    TextInputEditText firstName, profilePhone;
    TextView fullName, profileEmail, changePhoto;
    String uid;
    CountryCodePicker ccpProfile;
    FirebaseUser user;
    DatabaseReference mDatabase;
    StorageReference storageReference;
    FirebaseFirestore fStore;


    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        firstName = rootView.findViewById(R.id.edit_text_first_name_profile);
        profileEmail = rootView.findViewById(R.id.edit_text_email_profile);
        profilePhone = rootView.findViewById(R.id.edit_text_phone_profile);
        fullName = rootView.findViewById(R.id.text_view_full_name_profile);
        profilePhoto = rootView.findViewById(R.id.image_view_profile_photo);
        saveProfile = rootView.findViewById(R.id.button_save_profile);
        changePhoto = rootView.findViewById(R.id.text_view_change_photo);
        ccpProfile = rootView.findViewById(R.id.ccp_phone_profile);

        ccpProfile.registerCarrierNumberEditText(profilePhone);

        fStore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        }

        storageReference = FirebaseStorage.getInstance().getReference("Uploads");

        changePhoto.setOnClickListener(v -> openImage());

        saveProfile.setOnClickListener(v -> {
            if(!validateName() | !validatePhone()){
                return;
            }else{
                    saveProfileSettings();
                }
        });




    showUserData();

        return rootView;
    }

    public static String PhoneNumberWithoutCountryCode(String phoneNumberWithCountryCode) {//+91 7698989898
        Pattern compile = Pattern.compile("\\+(?:998|996|995|994|993|992|977|976|975|974|973|972|971|970|968|967|966|965|964|963|962|961|960|886|880|856|855|853|852|850|692|691|690|689|688|687|686|685|683|682|681|680|679|678|677|676|675|674|673|672|670|599|598|597|595|593|592|591|590|509|508|507|506|505|504|503|502|501|500|423|421|420|389|387|386|385|383|382|381|380|379|378|377|376|375|374|373|372|371|370|359|358|357|356|355|354|353|352|351|350|299|298|297|291|290|269|268|267|266|265|264|263|262|261|260|258|257|256|255|254|253|252|251|250|249|248|246|245|244|243|242|241|240|239|238|237|236|235|234|233|232|231|230|229|228|227|226|225|224|223|222|221|220|218|216|213|212|211|98|95|94|93|92|91|90|86|84|82|81|66|65|64|63|62|61|60|58|57|56|55|54|53|52|51|49|48|47|46|45|44\\D?1624|44\\D?1534|44\\D?1481|44|43|41|40|39|36|34|33|32|31|30|27|20|7|1\\D?939|1\\D?876|1\\D?869|1\\D?868|1\\D?849|1\\D?829|1\\D?809|1\\D?787|1\\D?784|1\\D?767|1\\D?758|1\\D?721|1\\D?684|1\\D?671|1\\D?670|1\\D?664|1\\D?649|1\\D?473|1\\D?441|1\\D?345|1\\D?340|1\\D?284|1\\D?268|1\\D?264|1\\D?246|1\\D?242|1)\\D?");
        String number = phoneNumberWithCountryCode.replaceAll(compile.pattern(), "");
        //Log.e(tag, "number::_>" +  number);//OutPut::7698989898
        return number;
    }

    private void showUserData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String user_firstname = dataSnapshot.child("name").getValue().toString();
                String user_email = dataSnapshot.child("email").getValue().toString();
                String user_phone = dataSnapshot.child("phoneNumber").getValue().toString();
                String user_profilephoto = dataSnapshot.child("imageURL").getValue(String.class);

                firstName.setText(user_firstname);
                profileEmail.setText(user_email);
                profilePhone.setText(PhoneNumberWithoutCountryCode(user_phone));
                fullName.setText(user_firstname);

                if (user_profilephoto == null) {
                    profilePhoto.setImageResource(R.drawable.ic_profile);
                } else {
                    Picasso.get().load(user_profilephoto).into(profilePhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void openImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(uid + "/profile.jpg");
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageURL", mUri);
                    mDatabase.updateChildren(map);

                    pd.dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {

                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    private void saveProfileSettings() {
        final String first_name = firstName.getText().toString();
        final String profile_phone = profilePhone.getText().toString();
        final String full_phone_number = ccpProfile.getSelectedCountryCodeWithPlus() + profile_phone;
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", first_name);
        userMap.put("phoneNumber", full_phone_number);
        mDatabase.updateChildren(userMap);
    }
    private boolean validateName() {
        String val = firstName.getText().toString().trim();
        String nameNeededToast = getString(R.string.nameNeededToast);

        if (val.isEmpty()){
            firstName.setError(nameNeededToast);
            return false;
        } else {
            firstName.setError(null);
            return true;
        }
    }

    private boolean validatePhone(){
        String val = profilePhone.getText().toString().trim();
        String phoneNumber = ccpProfile.getSelectedCountryCodeWithPlus() + val;
        String phoneNeededToast = getString(R.string.phoneNeededToast);
        String phoneInvalidToast = getString(R.string.phoneInvalidToast);

        if (phoneNumber.isEmpty()){
            profilePhone.setError(phoneNeededToast);
            return false;
        } else if (!ccpProfile.isValidFullNumber()){
            profilePhone.setError(phoneInvalidToast);
            return false;
        } else {
            profilePhone.setError(null);
            return true;
        }
    }
}


