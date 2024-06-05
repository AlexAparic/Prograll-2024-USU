package com.example.redsocial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redsocial.fragments.HomeFragment;
import com.example.redsocial.models.Post;
import com.example.redsocial.providers.AuthProvider;
import com.example.redsocial.providers.ImageProvider;
import com.example.redsocial.providers.PostProvider;
import com.example.redsocial.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class PostActivity extends AppCompatActivity {

    ImageView mImageViewPost1;
    ImageView mImageViewPost2;
    File mImageFile;
    File mImageFile2;
    FloatingActionButton btnRegresar;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;
    Button mButtonPost;
    ImageProvider mImageProvider;
    TextInputEditText mTextInputTitle;
    TextInputEditText mTextInputDescription;
    ImageView mImageViewDeportes;
    ImageView mImageViewCocina;
    ImageView mImageViewNoticias;
    ImageView mImageViewMemes;
    TextView mTextViewCategory;
    String mCategory = "";
    String mTitle = "";
    String mDescription = "";
    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];

    // FOTO 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int PHOTO_REQUEST_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        btnRegresar = findViewById(R.id.Regresarho);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Regresar();
            }
        });

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opcion");
        options = new CharSequence[]{"Imagen de galeria", "Tomar foto"};

        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();

        mTextInputTitle = findViewById(R.id.textInputUsername);
        mTextInputDescription = findViewById(R.id.textInputDescripcion);
        mImageViewDeportes = findViewById(R.id.ImageViewDeportes);
        mImageViewCocina = findViewById(R.id.ImageViewCocina);
        mImageViewNoticias = findViewById(R.id.ImageViewNoticias);
        mImageViewMemes = findViewById(R.id.ImageViewMemes);
        mTextViewCategory = findViewById(R.id.textViewCategory);
        mImageViewPost1 = findViewById(R.id.imageViewPost1);

        mButtonPost = findViewById(R.id.btnPost);

        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPost();
            }
        });



        mImageViewPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionimage( 1);


            }
        });
        mImageViewDeportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "Deportes";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewCocina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "Cocina";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewNoticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "Noticias";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewMemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "Memes";
                mTextViewCategory.setText(mCategory);
            }
        });
    }

    private void selectOptionimage(int numberImage) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == 0){
                    if(numberImage == 1){
                        openGallery(GALLERY_REQUEST_CODE);
                    }

                }
                else if(i == 1){
                    if(numberImage == 1){
                        takePhoto(PHOTO_REQUEST_CODE);
                    }

                }
            }
        });
        mBuilderSelector.show();

    }

    private void takePhoto(int resquestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createPhotoFile(resquestCode);

            }catch(Exception e){
                Toast.makeText(this, "Hubo un error con el archivo" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if(photoFile != null ){
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.example.redsocial", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE);
            }

        }
    }

    private File createPhotoFile(int resquestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );
        if(resquestCode == PHOTO_REQUEST_CODE){
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }

        return photoFile;
    }

    private void clickPost() {

        mTitle = mTextInputTitle.getText().toString();
        mDescription = mTextInputDescription.getText().toString();
        if (!mTitle.isEmpty() && !mDescription.isEmpty() && !mCategory.isEmpty()) {
            // SELECCIONO IMAGEN DE LA GALERIA
            if (mImageFile != null  ) {
                saveImage(mImageFile);

            } else if (mPhotoFile != null) {
                saveImage(mPhotoFile);

            } else {
                Toast.makeText(this, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Completa los campos para publicar", Toast.LENGTH_SHORT).show();
        }
    }

    private void  saveImage(File imageFile1){
        mImageProvider.save(PostActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Post post = new Post();
                            post.setImage1(url);
                            post.setTitle(mTitle.toLowerCase());
                            post.setDescription(mDescription);
                            post.setCategory(mCategory);
                            post.setIdUser(mAuthProvider.getUid());
                            post.setTimestamp(new Date().getTime());
                            mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> taskSave) {
                                    if(taskSave.isSuccessful()) {
                                        clearForm();
                                        Toast.makeText(PostActivity.this,"La informacion se almaceno correctamente", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(PostActivity.this,"No se pudo almacenar la informacion", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });

                }
                else {
                    Toast.makeText(PostActivity.this, "Hubo un error al almacenar la Imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void clearForm() {
        mTextInputTitle.setText("");
        mTextInputDescription.setText("");
        mTextViewCategory.setText("");
        mImageViewPost1.setImageResource(R.drawable.archivosubir);
        mTitle = "";
        mDescription = "";
        mCategory = "";
        mImageFile = null;
    }

    private void openGallery(int resquestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, resquestCode);
    }
    //seleccion de imagen galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                mPhotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch(Exception e) {
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        //seleccionde fotografia
        if(requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK){
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(PostActivity.this).load(mPhotoPath).into(mImageViewPost1);
        }
    }
    private void Regresar() {
        Intent abrirVentana = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(abrirVentana);
    }

}



