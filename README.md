//TODO 

- Carrito -> Guardar todos los items pedidos en una misma orden.
          -> Precio total del pedido. 
          -> Guardar los datos del pedido en la base de datos en una vista de admin, con id de usuario, items pedidos, anotaciones, precio y status.

- MainAdminActivity -> Ver pedidos de los usuarios ordenados por status
                    -> Cambiar el status de los pedidos (Recibido, en preparacion, en camino, recibido)



                    ////////////////////////////////////////////////////



                    package com.mozadevelopment.moza;

                    import android.app.ProgressDialog;
                    import android.os.Bundle;
                    import android.view.LayoutInflater;
                    import android.view.View;
                    import android.view.ViewGroup;
                    import android.widget.Button;
                    import android.widget.Toast;

                    import androidx.annotation.NonNull;
                    import androidx.annotation.Nullable;
                    import androidx.fragment.app.Fragment;

                    import com.google.android.gms.tasks.OnFailureListener;
                    import com.google.android.gms.tasks.OnSuccessListener;
                    import com.google.android.material.textfield.TextInputEditText;
                    import com.google.firebase.auth.AuthCredential;
                    import com.google.firebase.auth.EmailAuthProvider;
                    import com.google.firebase.auth.FirebaseAuth;
                    import com.google.firebase.auth.FirebaseUser;
                    import com.google.firebase.database.DatabaseReference;
                    import com.google.firebase.database.FirebaseDatabase;


                    public class ChangePasswordFragment extends Fragment {

                        Button confirm;
                        TextInputEditText currentPassword, newPassword, confirmPassword;
                        String uid;
                        DatabaseReference mDatabase;
                        FirebaseAuth firebaseAuth;
                        ProgressDialog pd;


                        @Nullable
                        @Override
                        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                            View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);
                            currentPassword = rootView.findViewById(R.id.text_input_current_password);
                            newPassword = rootView.findViewById(R.id.text_input_new_password);
                            confirmPassword = rootView.findViewById(R.id.text_input_confirm_password);
                            confirm = rootView.findViewById(R.id.button_confirm);

                            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            confirm.setOnClickListener(v -> {
                                if (!validatePassword()) {
                                    return;
                                } else {
                                    String new_pass = newPassword.getText().toString();
                                    String current_pass = newPassword.getText().toString();
                                    //  HashMap<String, Object> userMap = new HashMap<>();
                                    //  userMap.put("password", new_pass);
                                    // mDatabase.updateChildren(userMap);
                                    updatePassword(current_pass, new_pass);
                                     }
                            });

                            return rootView;
                        }

                        private boolean validatePassword() {
                            String current_password = currentPassword.getText().toString().trim();
                            String confirm_password = confirmPassword.getText().toString().trim();
                            String new_password = newPassword.getText().toString().trim();
                            String checkValidPassword = "^" +
                                    "(?=.*[0-9])" + //por lo menos un caracter
                                    "(?=\\S+$)" + // sin espacios en blanco
                                    ".{6,}" + //por lo menos 6 caracteres
                                    "$";
                            String passwordValidToast = getString(R.string.passwordValidToast);
                            String passwordNeededToast = getString(R.string.passwordNeededToast);
                            String passwordMatchToast = getString(R.string.passwordMatchToast);

                            if (new_password.isEmpty()) {
                                newPassword.setError(passwordNeededToast);
                                return false;
                            } else if (confirm_password.isEmpty()) {
                                confirmPassword.setError(passwordNeededToast);
                                return false;
                            } else if (current_password.isEmpty()) {
                                currentPassword.setError(passwordNeededToast);
                                return false;
                            } else if (!new_password.matches(checkValidPassword)) {
                                newPassword.setError(passwordValidToast);
                                return false;
                            } else if (!new_password.equals(confirm_password)) {
                                newPassword.setError(passwordMatchToast);
                                confirmPassword.setError(passwordMatchToast);
                                return false;
                            } else {
                                newPassword.setError(null);
                                return true;
                            }
                        }

                        private void updatePassword(String current_pass, String new_pass) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //reauthenticate user
                            AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), current_pass);
                            user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //authenticate success
                                    user.updatePassword(new_pass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //password updated
                                            pd.dismiss();
                                            Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //update failed
                                            pd.dismiss();
                                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //authenticate failed
                                    pd.dismiss();
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
