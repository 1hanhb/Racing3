package com.example.munak.comptest;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Munak on 2017. 4. 5..
 */

public class EditActivity extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_IMAGE = 2;

    private Uri mImageCaptureUri;
    private ImageView iv_UserPhoto;
    private String absoultePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        iv_UserPhoto = (ImageView) findViewById(R.id.editProfile);
        iv_UserPhoto.setBackground(new ShapeDrawable(new OvalShape()));
        iv_UserPhoto.setClipToOutline(true);

        Button editPicture = (Button) findViewById(R.id.editPicture);
        editPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTakePicture();
            }
        });

        Button editUpload = (Button) findViewById(R.id.editUpload);
        editUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTakeAlbum();
            }
        });

        Button editRotate = (Button) findViewById(R.id.editRotate);
        editRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable tempD = iv_UserPhoto.getDrawable();
                Bitmap bitmap = ((BitmapDrawable)tempD).getBitmap();
                bitmap = rotate(bitmap, 90);
                iv_UserPhoto.setImageBitmap(bitmap);
            }
        });
    }

    public void doTakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + "jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void doTakeAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode)
        {
            case PICK_FROM_ALBUM:
            {
                mImageCaptureUri = data.getData();
                Log.d("SmartWheel", mImageCaptureUri.getPath().toString());
            }

            case PICK_FROM_CAMERA:
            {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX",200);
                intent.putExtra("outputY",200);
                intent.putExtra("aspectX",1);
                intent.putExtra("aspectY",1);
                intent.putExtra("scale",true);
                intent.putExtra("return-data",true);
                startActivityForResult(intent, PICK_FROM_IMAGE);
                break;
            }

            case PICK_FROM_IMAGE:
            {
                if(resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel/" + System.currentTimeMillis() + "jpg";

                if(extras != null) {
                    Bitmap photo = extras.getParcelable("data");

                    iv_UserPhoto.setImageBitmap(photo);

                    storeCropImage(photo, filePath);
                    absoultePath = filePath;
                    break;
                }

                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
            }
        }
    }

    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    private void storeCropImage(Bitmap bitmap, String filePath) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel";
        File directory_SmartWheel = new File(dirPath);
        if (!directory_SmartWheel.exists()) {
            directory_SmartWheel.mkdir();
        }

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
