package org.dgonzalo.m8_uf2_exam_dgonzalo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class IncidenciaActivity extends AppCompatActivity {

    ImageView add_image;
    EditText desc_input;
    Button add_incidencia;
    Uri uri;
    UUID uuid;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String fotomsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia);

        add_image = findViewById(R.id.add_image);
        desc_input = findViewById(R.id.descripcio_input);
        add_incidencia = findViewById(R.id.add_button);

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagenGaleria();
            }
        });

        add_incidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirImagen();
            }
        });


    }
    private void cargarImagenGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            add_image.setImageBitmap(bitmap);
        }
    }

    public void subirImagen() {
        uuid = UUID.randomUUID();
        fotomsg = desc_input.getText().toString();

        final StorageReference storageRef = storage.getReference()
                .child("fotos").child(uuid+".jpg");

        UploadTask uploadTask;
        uploadTask = storageRef.putFile(uri);

       Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Uri downloadUri = task.getResult();
                    Log.i("FIREBASE2", downloadUri.toString());

                    //Paso a la activity de seleccionar el usuario

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("downloadURL", downloadUri.toString());
                    intent.putExtra("imagen", uuid+".jpg");
                    intent.putExtra("mensaje", fotomsg);

                    startActivity(intent);



                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

}
