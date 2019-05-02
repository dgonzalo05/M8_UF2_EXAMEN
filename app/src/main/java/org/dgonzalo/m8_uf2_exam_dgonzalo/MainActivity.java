package org.dgonzalo.m8_uf2_exam_dgonzalo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private miAdapter recyclerAdapter;
    private ArrayList<Incidencia> incidencias;
    private DatabaseReference mDatabaseRef;
    private MediaPlayer mediaPlayer;
    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mediaPlayer = MediaPlayer.create(this,R.raw.cancion);
        mediaPlayer.start();
        Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
            }
        });

        recyclerView = findViewById(R.id.recyclerID);
        incidencias = new ArrayList<Incidencia>();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter = new miAdapter(incidencias);

        mDatabaseRef.child("incidencias").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Incidencia incidencia = dataSnapshot.getValue(Incidencia.class);
                addIncidencia(incidencia);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void addIncidencia(Incidencia incidencia){
        incidencias.add(incidencia);
        recyclerAdapter.notifyDataSetChanged();
    }

    public class miAdapter extends RecyclerView.Adapter<miAdapter.miViewHolder> {
        ArrayList<Incidencia> incidencias;

        public miAdapter(ArrayList<Incidencia> incidencias){
            this.incidencias = incidencias;
        }

        @NonNull
        @Override
        public miViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.incidencia_layout,viewGroup,false);
            miViewHolder holder = new miViewHolder(v);
            v.setOnClickListener(holder);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull miViewHolder miViewHolder, int i) {
            try {
                getImageUrl(incidencias.get(i).getImagen(),miViewHolder.imagen);
            } catch (IOException e) {
                e.printStackTrace();
            }
            miViewHolder.descripcion.setText(incidencias.get(i).getDescripcion());
            miViewHolder.aula.setText(incidencias.get(i).getAula());
            miViewHolder.setIncidencia(incidencias.get(i));
        }

        @Override
        public int getItemCount() {
            return incidencias.size();
        }
        public class miViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView imagen;
            TextView descripcion, aula;
            CheckBox realizado;
            Incidencia incidencia;

            public void setIncidencia(Incidencia incidencia) {
                this.incidencia = incidencia;
            }

            public miViewHolder(@NonNull View itemView) {
                super(itemView);

                imagen = itemView.findViewById(R.id.incidencia_image);
                descripcion = itemView.findViewById(R.id.incidencia_descripcion);
                aula = itemView.findViewById(R.id.incidencia_aula);
                realizado = itemView.findViewById(R.id.incidencia_realizada);
                realizado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        realizado.animate().alpha(1).setDuration(2000);
                    }
                });

            }
            @Override
            public void onClick(View v) {
                // cambiar de activity
            }
        }

    }

    public void getImageUrl(String url, ImageView imagen) throws IOException {

        final ImageThread thread = new ImageThread(imagen);
        thread.execute(url);
    }
    public class ImageThread extends AsyncTask<String, Void, Bitmap> {

        ImageView imagen;
        public ImageThread(ImageView imagen){
            this.imagen = imagen;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpURLConnection connection = null;
            URL url;
            Bitmap result = null;
            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                result = BitmapFactory.decodeStream(inputStream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(Bitmap data){
            super.onPostExecute(data);
            imagen.setImageBitmap(data);
        }
    }


}
