package com.example.nuevotrabajo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText etTelefono;
    private ImageButton btnLlamar,btnCamara;
    private String numeroDeTelefono;
    private ImageView ivImagen;
    private final int PHONE_CODE = 100;
    private final int CAMERA_CODE = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializarVistas();
        btnLlamar.setOnClickListener(view -> {
            obtenerInformacion();
            activarServicioLlamada();
        });
        btnCamara.setOnClickListener(view -> {
            activarServicioCamara();
        });
    }

    private void activarServicioCamara() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case CAMERA_CODE:
                if(resultCode == RESULT_OK) {
                    Bitmap foto  = (Bitmap) data.getExtras().get("data");
                    ivImagen.setImageBitmap(foto);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void activarServicioLlamada() {
        if (!numeroDeTelefono.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},PHONE_CODE);
            } else {
                configurarVersionAntigua();
            }
        }
    }

    private void configurarVersionAntigua() {
        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numeroDeTelefono));
        if(revisarPermisos(Manifest.permission.CALL_PHONE)) {
            startActivity(intentCall);
        }
    }

    private void obtenerInformacion() {
        numeroDeTelefono = etTelefono.getText().toString();
    }

    private void inicializarVistas() {
        etTelefono = findViewById(R.id.etTelefono);
        btnLlamar = findViewById(R.id.btnLlamar);
        btnCamara = findViewById(R.id.btnCamara);
        ivImagen = findViewById(R.id.IVImagen);
    }

    private boolean revisarPermisos(String permiso) {
        int valorPermiso = this.checkCallingOrSelfPermission(permiso);
        return valorPermiso == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Verificar el servicio a evaluar
        switch (requestCode) {
            case PHONE_CODE:
                String permiso = permissions[0];
                int permisoOtorgado = grantResults[0];
                if (permiso.equals(Manifest.permission.CALL_PHONE)) {
                    if(permisoOtorgado == PackageManager.PERMISSION_GRANTED) {
                        Intent intentLlamada = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+numeroDeTelefono));
                        startActivity(intentLlamada);
                    } else {
                        Toast.makeText(this,"El permiso esta denegado",Toast.LENGTH_LONG);
                    }
                }
                break;
            case CAMERA_CODE:
                int valor = grantResults[0];
                if(valor == PackageManager.PERMISSION_GRANTED) {
                    Intent intentCamara = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intentCamara, CAMERA_CODE);
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }
}

