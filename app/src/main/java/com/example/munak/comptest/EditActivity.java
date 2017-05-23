package com.example.munak.comptest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Munak on 2017. 4. 5..
 */

public class EditActivity extends AppCompatActivity {

    final private String DBNAME = "playerinfo.db";
    final private String PLAYERTABLE = "player";
    SQLiteDatabase db;
    boolean createdDB = false;

    String email;
    String path;
    String path2;

    String mmr;

    ImageView editProfileEdge;

    Bitmap mSaveBm;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    //Uri photoUri;

    //private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}; //권한 설정 변수

    //private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

    private Uri mImageCaptureUri;
    private ImageView iv_UserPhoto;
    private ImageView iv_UserPhoto2;
    private String absoultePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //keyE-Mail
        Intent LoginToMainIntent = getIntent();
        email = LoginToMainIntent.getStringExtra("keyEmail");

        createDatabase(DBNAME);
        String sql = "select * from " + PLAYERTABLE;
        Cursor cursor = db.rawQuery(sql, null);

        int count = cursor.getCount();
        String emailFromDB="";
        for (int i = 0; i < count; i++) {
            cursor.moveToNext();

            emailFromDB = cursor.getString(0);
            if(emailFromDB.equals(email)) {
                mmr = cursor.getString(8);
                break;
            }
        }

        editProfileEdge = (ImageView) findViewById(R.id.profileEdge);

        int integerMmr = Integer.valueOf(mmr);
        if(integerMmr<100){
            editProfileEdge.setImageResource(R.drawable.rank_img_bronze);
        }
        else if(integerMmr<200){
            editProfileEdge.setImageResource(R.drawable.rank_img_silver);
        }
        else if(integerMmr<300) {
            editProfileEdge.setImageResource(R.drawable.rank_img_gold);
        }
        else if(integerMmr<400) {
            editProfileEdge.setImageResource(R.drawable.ranl_img_platinum);
        }
        else if(integerMmr<500) {
            editProfileEdge.setImageResource(R.drawable.rank_img_master);
        }
        else {

        }


        //Title Bar Back Button Visible
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        iv_UserPhoto = (ImageView) findViewById(R.id.editProfile);
        iv_UserPhoto.setBackground(new ShapeDrawable(new OvalShape()));
        iv_UserPhoto.setClipToOutline(true);

        Button editPicture = (Button) findViewById(R.id.editPicture);
        editPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //doTakePicture();
                doTakePicture();
            }
        });

        Button editUpload = (Button) findViewById(R.id.editUpload);
        editUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //doTakeAlbum();
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

        Button editChange = (Button) findViewById(R.id.editChange);
        editChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                iv_UserPhoto.buildDrawingCache();
                Bitmap captureView = iv_UserPhoto.getDrawingCache();
                FileOutputStream fos;
                try {
                    path2 = Environment.getExternalStorageDirectory().toString()+"/capture.jpeg";
                    fos = new FileOutputStream(path2);
                    captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Captured!", Toast.LENGTH_LONG).show();
                Toast.makeText(EditActivity.this, path2, Toast.LENGTH_SHORT).show();
                */

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bitmap sendBitmap = ((BitmapDrawable)iv_UserPhoto.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("image",byteArray);
                setResult(RESULT_OK, intent);

                /*
                String sql = "update player set photo = '" + byteArray + "';";
                try{
                    db.execSQL(sql);
                }catch(Exception e){
                    Toast.makeText(EditActivity.this, "photo update fail", Toast.LENGTH_SHORT).show();
                }
                */

                finish();
                //startActivity(intent);


                /*
                String sql = "update player set path = '" + absoultePath + "' where email = '" + email + "';";
                Toast.makeText(EditActivity.this, absoultePath, Toast.LENGTH_SHORT).show();
                try{
                    db.execSQL(sql);
                    Toast.makeText(EditActivity.this, absoultePath, Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    Toast.makeText(EditActivity.this, "path update fail", Toast.LENGTH_SHORT).show();
                }

                iv_UserPhoto2 = (ImageView) findViewById(R.id.editProfile2);

                //mSaveBm = BitmapFactory.decodeFile(absoultePath);
                iv_UserPhoto2.setImageURI(Uri.parse("/sdcard/emulator/0/smartwheel/2132141.jpg"));

                */

                /*
                appIcon = getByteArrayFromDrawable(iv_UserPhoto.getDrawable());

                Toast.makeText(EditActivity.this, "1 : " + appIcon.toString(), Toast.LENGTH_SHORT).show();
                try{
                    String sql = "insert into photo values(?);";
                    SQLiteStatement pp = db.compileStatement(sql);
                    pp.clearBindings();
                    pp.bindBlob(1, appIcon);
                    pp.execute();
                }catch(Exception e){
                    Toast.makeText(EditActivity.this, "insert fail", Toast.LENGTH_SHORT).show();
                }

                */
/*
                try {
                    String sql2 = "update photo set image = ? where email = '" + email + "'";
                    SQLiteStatement p = db.compileStatement(sql2);
                    p.clearBindings();
                    p.bindBlob(1, appIcon);
                    p.execute();
                }catch(Exception e){
                    Toast.makeText(EditActivity.this, "update fail", Toast.LENGTH_SHORT).show();
                }
*/
                //queryData();
            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
/*
    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();

        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);

            if (result != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }

        if (!permissionList.isEmpty()) { //권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    //아래는 권한 요청 Callback 함수입니다. PERMISSION_GRANTED로 권한을 획득했는지 확인할 수 있습니다. 아래에서는 !=를 사용했기에
    //권한 사용에 동의를 안했을 경우를 if문으로 코딩되었습니다.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }

                return;
            }
        }
    }

    //권한 획득에 동의를 하지 않았을 경우 아래 Toast 메세지를 띄우며 해당 Activity를 종료시킵니다.
    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //사진을 찍기 위하여 설정합니다.
        File photoFile = null;

        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(EditActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (photoFile != null) {

            //photoUri = FileProvider.getUriForFile(EditActivity.this, "com.example.munak.comptest.provider", photoFile); //FileProvider의 경우 이전 포스트를 참고하세요.
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //사진을 찍어 해당 Content uri를 photoUri에 적용시키기 위함
            startActivityForResult(intent, PICK_FROM_CAMERA);

        }

    }


    // Android M에서는 Uri.fromFile 함수를 사용하였으나 7.0부터는 이 함수를 사용할 시 FileUriExposedException이
    // 발생하므로 아래와 같이 함수를 작성합니다. 이전 포스트에 참고한 영문 사이트를 들어가시면 자세한 설명을 볼 수 있습니다.
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/test/"); //test라는 경로에 이미지를 저장하기 위함

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        return image;
    }


    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK); //ACTION_PICK 즉 사진을 고르겠다!
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(EditActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == PICK_FROM_ALBUM) {
            if(data==null){
                return;
            }
            photoUri = data.getData();
            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            cropImage();
            MediaScannerConnection.scanFile(EditActivity.this, //앨범에 사진을 보여주기 위해 Scan을 합니다.
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            try { //저는 bitmap 형태의 이미지로 가져오기 위해 아래와 같이 작업하였으며 Thumbnail을 추출하였습니다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축

                //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.
                iv_UserPhoto.setImageBitmap(thumbImage);
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }
        }
    }

    //Android N crop image (이 부분에서 몇일동안 정신 못차렸습니다 ㅜ)
//모든 작업에 있어 사전에 FALG_GRANT_WRITE_URI_PERMISSION과 READ 퍼미션을 줘야 uri를 활용한 작업에 지장을 받지 않는다는 것이 핵심입니다.
    public void cropImage() {
        this.grantUriPermission("com.android.camera", photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 4);
            intent.putExtra("aspectY", 3);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(EditActivity.this, "com.example.munak.comptest.provider", tempFile);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            grantUriPermission(res.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);


        }

    }

*/


/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivityResult", "CALL");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                isAlbum = false;
                galleryAddPic();
                cropImage();
                break;

            case REQUEST_TAKE_ALBUM:
                isAlbum = true;
                File albumFile = null;
                try {
                    albumFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(albumFile != null){
                    albumURI = Uri.fromFile(albumFile);
                }
                photoURI = data.getData();
                cropImage();
                break;

            case REQUEST_IMAGE_CROP:
                galleryAddPic();
                // 업로드
                //uploadFile(mCurrentPhotoPath);
                break;
        }
    }

*/

    public void doTakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".JPG";
        path = url;
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
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            }

            case CROP_FROM_IMAGE:
            {
                if(resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel/" + System.currentTimeMillis() + ".JPG";

                if(extras != null) {
                    Bitmap photo = extras.getParcelable("data");

                    iv_UserPhoto.setImageBitmap(photo);
                    storeCropImage(photo, filePath);
                    Toast.makeText(this, "abc", Toast.LENGTH_SHORT).show();
                    absoultePath = filePath;
/*
                    MediaStore.Images.Media.insertImage(getContentResolver(), photo, "title", "descripton");

                    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
                    intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
                    intentFilter.addDataScheme("file");
                    registerReceiver(mReceiver, intentFilter);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
*/
                    break;
                }

                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(Intent.ACTION_MEDIA_SCANNER_STARTED))
                Toast.makeText(EditActivity.this, "사진을 업데이트하고있습니다" ,
                        Toast.LENGTH_SHORT).show();
            else if (intent.getAction().equals(Intent.ACTION_MEDIA_SCANNER_FINISHED))
                Toast.makeText(EditActivity.this, "사진이 업데이트 되었습니다" ,
                        Toast.LENGTH_SHORT).show();
        }
    };



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
            Toast.makeText(this, "okok", Toast.LENGTH_SHORT).show();
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
/*
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
            out.flush();
            out.close();
*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //db 생성
    private void createDatabase(String name){
        try {
            db = openOrCreateDatabase(name, MODE_ENABLE_WRITE_AHEAD_LOGGING,null);
            Toast.makeText(this, "db생성 성공", Toast.LENGTH_SHORT).show();
            createdDB = true;
            try {
                if(createdDB) {
                    createTable();
                }
            } catch(Exception e){}
        } catch(Exception ex) {
            Toast.makeText(this, "db 생성 안됨", Toast.LENGTH_SHORT).show();
        }
    }

    //table 생성
    private void createTable() {
        if(createdDB) {
            try {
                db.execSQL("create table player ("
                        + "email text primary key,"
                        + "name text,"
                        + "password text,"
                        + "totalScore integer,"
                        + "violationAccel integer,"
                        + "violationVelocity integer,"
                        + "violationKal integer,"
                        + "useSleepinessCenter integer,"
                        + "mmr integer,"
                        + "conpetitionCount integer,"
                        + "winCount integer,"
                        + "photo blob,"
                        + "mission integer)"
                );
            }catch(Exception e){}
        }
    }

    //table에 data 넣기
    private boolean insertData(String name,Player p){
        if(createdDB) {
            try {
                String sql ="insert into " + name
                        + "(email, name, password, totalScore, violationAccel, " +
                        "violationVelocity, violationKal, useSleepinessCenter, mmr, " +
                        "conpetitionCount, winCount, mission ) values("
                        + "'" + p.getEmail() + "',"
                        + "'" + p.getName() + "',"
                        + "'" + p.getPassword() + "',"
                        + "'" + p.getTotalScore() + "',"
                        + "'" + p.getViolationAccel() + "',"
                        + "'" + p.getViolationVelocity() + "',"
                        + "'" + p.getViolationKal() + "',"
                        + "'" + p.getUseSleepinessCenter() + "',"
                        + "'" + 0 + "',"
                        + "'" + 0 + "',"
                        + "'" + 0 + "',"
                        + "'" + 0 +"')";
                db.execSQL(sql);
                return true;
            } catch(Exception e) {
                Toast.makeText(this, "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return false;
        }
    }



    //table 제거
    private void removeTable(String tableName){
        if(createdDB){
            String sql = "drop table " + tableName;
            try {
                db.execSQL(sql);
                Toast.makeText(this, "테이블 제거", Toast.LENGTH_SHORT).show();
            }catch(Exception e){Toast.makeText(this, "테이블 제거 실패", Toast.LENGTH_SHORT).show();}
        }
    }

    //data 조회하기

    private void queryData(){
        if(createdDB) {
            String sql = "select * from " + PLAYERTABLE;
            try {
                Cursor cursor = db.rawQuery(sql, null);

                if (cursor != null) {
                    int count = cursor.getCount();

                    for (int i = 0; i < count; i++) {
                        cursor.moveToNext();

                        if(email.equals(cursor.getString(0))){
                            path = cursor.getString(11);
                            break;
                        }
                    }
                }
            } catch(Exception e){
                Toast.makeText(this, "query 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //table에서 data제거
    private void removeData(String email){
        if(createdDB) {
            String sql = "delete from " + PLAYERTABLE + " where email = " + email + ";";
            try {
                db.execSQL(sql);
            }catch(Exception e){}

        }
    }

    //data 변경하기
    private void updateData(Player p){
        if(createdDB){
            String sql = "UPDATE " + PLAYERTABLE
                    + " SET totalScore = totalScore + '" + 100
                    + "', violationAccel = violationAccel + '" + p.getViolationAccel()
                    + "', violationVelocity = violationVelocity + '" + p.getViolationVelocity()
                    + "', violationKal = violationKal +'" + p.getViolationKal()
                    + "', useSleepinessCenter = useSleepinessCenter +'" + p.getUseSleepinessCenter()
                    + "', mmr = mmr +'" + 10
                    + "', conpetitionCount = conpetitionCount + '" + 10
                    + "', winCount = winCount + '" + 10 + "'"
                    + " WHERE email = '"+p.getEmail() +"';";
            try {
                db.execSQL(sql);
            }catch(Exception e){
                Toast.makeText(this, "update 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public byte[] getByteArrayFromDrawable(Drawable d) {
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        return data;
    }

    public Bitmap getAppIcon(byte[] b) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bitmap;
    }


}
