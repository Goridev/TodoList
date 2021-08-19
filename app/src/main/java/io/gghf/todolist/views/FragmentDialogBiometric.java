package io.gghf.todolist.views;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.os.Bundle;
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

import java.util.concurrent.Executor;

import io.gghf.todolist.MainActivity;
import io.gghf.todolist.R;

public class FragmentDialogBiometric extends DialogFragment implements View.OnClickListener {

    private View root;
    private ImageView fingerprint;

    private BiometricPrompt.PromptInfo promptInfo;
    private BiometricPrompt biometricPrompt;
    private Executor executor;

    public static FragmentDialogBiometric newInstance() {
        Bundle args = new Bundle();
        FragmentDialogBiometric fragment = new FragmentDialogBiometric();
        fragment.setArguments(args);
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
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getContext().getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_LONG).show();
                dismiss();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getContext().getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirmez votre identité")
                .setSubtitle("Utilisez votre empreinte pour confirmez votre identité")
                .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                .build();
        biometricPrompt.authenticate(promptInfo);
    }
}
