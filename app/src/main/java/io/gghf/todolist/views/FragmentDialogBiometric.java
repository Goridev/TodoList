package io.gghf.todolist.views;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import io.gghf.todolist.MainActivity;
import io.gghf.todolist.R;
import io.gghf.todolist.utils.Secret;

public class FragmentDialogBiometric extends DialogFragment implements View.OnClickListener {

    private View root;
    public static View parent;
    private ImageView fingerprint;

    private BiometricPrompt.PromptInfo promptInfo;
    private BiometricPrompt biometricPrompt;
    private Executor executor;

    private Secret secret = new Secret();

    public static FragmentDialogBiometric newInstance(View view) {
        Bundle args = new Bundle();
        FragmentDialogBiometric fragment = new FragmentDialogBiometric();
        fragment.setArguments(args);
        parent = view;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dialog_biometric,container,false);
        fingerprint = root.findViewById(R.id.fingerprint);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fingerprint.setOnClickListener(this);
        root.post( () -> {
            fingerprint.performClick();
        });
        try {
            secret.generateSecretKey(new KeyGenParameterSpec.Builder(
                    "mySecretKey",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    // Invalidate the keys if the user has registered a new biometric
                    // credential, such as a new fingerprint. Can call this method only
                    // on Android 7.0 (API level 24) or higher. The variable
                    // "invalidatedByBiometricEnrollment" is true by default.
                    .setInvalidatedByBiometricEnrollment(true)
                    .build());
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fingerprint:
                Log.d("FragmentDialogBiometric","Fingerprint ?");
                setAuthenticate();
                break;
            default:
                break;
        }
    }
    public void setAuthenticate(){
        executor = ContextCompat.getMainExecutor(getContext());
        biometricPrompt = new BiometricPrompt(getActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getContext().getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_LONG)
                        .show();
                Snackbar.make(parent,"Authentication error: " + errString,Snackbar.LENGTH_SHORT).show();
                fingerprint.setImageResource(R.drawable.fingerprint_error);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Snackbar.make(parent,"Authentication succeeded",Snackbar.LENGTH_SHORT).show();
                fingerprint.setImageResource(R.drawable.fingerprint_success);

                try {
                    byte[] encryptedInfo = result.getCryptoObject().getCipher().doFinal("mySecretKey".getBytes(Charset.defaultCharset()));
                    Log.d("FragmentDialogBiometric", "Encrypted information: " + Arrays.toString(encryptedInfo));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    }, 1000);
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Snackbar.make(parent,"Authentication failed",Snackbar.LENGTH_SHORT).show();
                fingerprint.setImageResource(R.drawable.fingerprint_add);
            }
        });
        try {
            Cipher cipher = secret.getCipher();
            SecretKey secretKey = secret.getSecretKey();
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Confirmez votre identité")
                    .setSubtitle("Utilisez votre empreinte pour confirmez votre identité")
                    .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                    .build();
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
            biometricPrompt.authenticate(promptInfo,
                    new BiometricPrompt.CryptoObject(cipher));

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }
}
