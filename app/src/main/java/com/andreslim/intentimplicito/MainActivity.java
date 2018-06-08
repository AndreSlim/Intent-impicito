package com.andreslim.intentimplicito;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageButton telefono;
    EditText numero;

    // Codigo para saber que numero de permiso es (Mandado como parametro)
    private final int CODIGO_TELEFONO = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.telefono);


        telefono = (ImageButton) findViewById(R.id.boton_telefono);
        numero = (EditText) findViewById(R.id.numero_telefono);

        // - - - X X X X X X

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable =  (VectorDrawable) drawable;
            telefono.setBackground(vectorDrawable);
        } else {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            telefono.setBackground(bitmapDrawable);
        }
        // - - -X X X X X X

        telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numeroS = numero.getText().toString();   //lee el numero del EditText
                // Comprueba que no este vacio
                if (numeroS != null && !numeroS.isEmpty()) {
                    // Comprobar version de android SDK_INT significa la version del sistema
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        // Comprobar si ha aceptado, no ha aceptado, nunca se le ha preguntado
                        if(RevisarPermiso(Manifest.permission.CALL_PHONE)){ //El metodo devuelve boolean
                            // if para saber si ha aceptado
                            Intent i_xd = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numeroS));

                            // Permiso especial del sistema en el contexto del archivo Java
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) return;
                            startActivity(i_xd);

                        }else{  // no ha aceptado o es la primera vez

                            //  Metodo para saber si o se le ha preguntado aun
                            if (!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)){

                                //Se le pregunta por primera vez
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CODIGO_TELEFONO);
                            }else{
                                //lo negó
                                Toast.makeText(getApplicationContext(), "Habilita el permiso",
                                        Toast.LENGTH_LONG).show();

                                // Abriendo las configuraciones
                                Intent c_xd = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                                //agredando una categoria
                                c_xd.addCategory(Intent.CATEGORY_DEFAULT);

                                // Dando el nombre de la aplicacion
                                c_xd.setData(Uri.parse("package:" + getPackageName()));

                                //agregando banderas
                                c_xd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //Nueva tarea
                                c_xd.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); //Sin historia (del boton back)
                                c_xd.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);   //No mostrar en recientes
                                startActivity(c_xd);

                            }

                        }


                    } else {
                        VersionesViejas(numeroS);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No has escrito ningun número de telefono",
                            Toast.LENGTH_SHORT).show();
                }
            }

            private void VersionesViejas(String numeroS) {
                // Crea el Intent
                Intent intent_xd = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numeroS));

                // Atravez del metodo revisa el manifest para saber si existe el permiso
                if (RevisarPermiso(Manifest.permission.CALL_PHONE)) {
                    startActivity(intent_xd);

                } else {
                    Toast.makeText(getApplicationContext(), "No tienes permiso", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Metodo creado por el IDE solo escribiendo "onReques..." para administrar los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Llegando como parametros el codigo de permiso, array de permisos, array de resultados

        // Comprobando el numero de peticion, en este caso es el codigo para el telefono
        //sirve para varios codigo de peticion
        switch (requestCode) {

            case CODIGO_TELEFONO:

                // El permiso es cero porque aun no ha sido autorizado
                String permiso = permissions[0];
                int resultado = grantResults[0];

                // Comprobar si el permiso es igual al permiso del telefono (aceptado o denegado)
                if (permiso.equals(Manifest.permission.CALL_PHONE)) {

                    // Permiso aceptado
                    if (resultado == PackageManager.PERMISSION_GRANTED) {
                        String numeroTelefono = numero.getText().toString();
                        Intent intent_xdd = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numeroTelefono));

                        // Comprobacion agregada por el IDE
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(intent_xdd);
                        // El startActivity da error, solucionar con ayuda del IDE, agregando
                        //Una ultima comprobacion

                        // Permiso denegado
                    }else{
                        Toast.makeText(getApplicationContext(), "No se ha consedido el permiso",
                                Toast.LENGTH_SHORT).show();

                    }

                }

                break;

            default:
                // Por defecto llamando al contructor de su padre
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private boolean RevisarPermiso(String permiso){ // Metodo para comprobar el permiso (Cualquiera)
        // Agregado desde Manifest

        // Comprovando si se obtiene el permiso
        int resultado = this.checkCallingOrSelfPermission(permiso);

        // Regresa si se tiene acceso o no
        return resultado == PackageManager.PERMISSION_GRANTED;

    }

}
