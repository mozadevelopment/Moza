package com.mozadevelopment.moza;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mozadevelopment.moza.Database.MenuHelperClass;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class AdminAddMenuFragment extends Fragment {

    Button saveItemButton, searchImageButton;
    TextInputEditText editTextItemName, editTextItemPrice, editTextItemDescription;
    String itemId, itemName, itemPrice, itemDescription;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_admin_add_menu, container, false);

        editTextItemName = rootView.findViewById(R.id.item_name);
        editTextItemPrice = rootView.findViewById(R.id.item_price);
        editTextItemDescription = rootView.findViewById(R.id.item_description);
        searchImageButton = rootView.findViewById(R.id.add_image_button);
        saveItemButton = rootView.findViewById(R.id.save_button);
        
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        
        searchImageButton.setOnClickListener(v -> openGallery());

        saveItemButton.setOnClickListener(v -> {

            if (!validateName() | !validateDescription() | !validatePrice()) {
                return;
            } else {
                itemName = editTextItemName.getText().toString();
                itemPrice = editTextItemPrice.getText().toString();
                itemDescription = editTextItemDescription.getText().toString();
                uploadImage();
            }
        });
        
        return rootView;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (imageUri != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Menu").child("Items");
            itemId = ref.push().getKey();
            final StorageReference fileReference = storageReference.child(itemId + "/item.jpg");
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
                    map.put("itemId", itemId);
                    map.put("imageURL", mUri);
                    map.put("description", itemDescription);
                    map.put("price", itemPrice);
                    map.put("name", itemName);
                    ref.child(itemId).setValue(map);

                    Toast.makeText(getContext(), "Item added", Toast.LENGTH_LONG).show();

                    progressDialog.dismiss();
                    clearEditText();


                } else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                progressDialog.dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });

        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress())
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                return;
            }
        }

    public void clearEditText(){
        editTextItemPrice.getText().clear();
        editTextItemName.getText().clear();
        editTextItemDescription.getText().clear();
        editTextItemName.requestFocus();
    }


    /* VALIDAR DATA DE ITEM */
    public boolean validateName(){
        String valName = editTextItemName.getText().toString().trim();
        String nameNeededToast = getString(R.string.itemNameNeededToast);

        if (valName.isEmpty()){
            editTextItemName.setError(nameNeededToast);
            return false;
        } else {
            editTextItemName.setError(null);
            return true;
        }
    }

    public boolean validateDescription(){
        String valDescription = editTextItemDescription.getText().toString().trim();
        String descriptionNeededToast = getString(R.string.itemDescriptionNeededToast);

        if (valDescription.isEmpty()){
            editTextItemDescription.setError(descriptionNeededToast);
            return false;
        } else {
            editTextItemDescription.setError(null);
            return true;
        }
    }

    public boolean validatePrice() {
        String valPrice = editTextItemPrice.getText().toString().trim();
        String priceNeededToast = getString(R.string.itemPriceNeededToast);
        String priceValidToast = getString(R.string.itemPriceValidToast);

        if (valPrice.isEmpty()) {
            editTextItemPrice.setError(priceNeededToast);
            return false;
        } else if (!valPrice.matches("[0-9]+")) {
            editTextItemPrice.setError(priceValidToast);
            return false;
        } else {
            editTextItemPrice.setError(null);
            return true;
        }
    }
}