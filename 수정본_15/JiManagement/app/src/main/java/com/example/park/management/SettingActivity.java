package com.example.park.management;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static android.app.PendingIntent.getActivity;

public class SettingActivity extends Activity implements View.OnClickListener {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private Uri mImageCaptureUri;
    private ImageView mPhotoImageView;
    private Button mButton;
    private Switch pButton;
    private Dialog dialog;
    private TextView userName;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        userName = (TextView) findViewById(R.id.userName);
        Intent nameintent = getIntent();
        String n = nameintent.getStringExtra("userName");
        userName.setText(n + "님 안녕하세요!");

        mButton = (Button) findViewById(R.id.selectbutton);
        mPhotoImageView = (ImageView) findViewById(R.id.user_image);
//        Bundle args = getIntent().getExtras();
        Intent imageintent = getIntent();
        String e = imageintent.getStringExtra("userImage");
        Log.d("bem",e + "zz");
        final Bitmap bitmap = decodeToBase64(e);
//        byte[] b = ((byte[]) e);
//        Log.d("image", e);
//        final Bitmap bitmap = getAppIcon(e);
            mPhotoImageView.setImageBitmap(bitmap);

            pButton = (Switch)findViewById(R.id.pushButton);
        pButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isServiceRunningCheck()) {
                    Log.d("ser", "running");
                    Intent intent = new Intent(SettingActivity.this, MyService.class);
                    stopService(intent);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    dialog = builder.setMessage("해제되었습니다")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                }
                else{
                    Intent intent = new Intent(SettingActivity.this, MyService.class);
                    startService(intent);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    dialog = builder.setMessage("설정되었습니다")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                }
            }
        });

        mButton.setOnClickListener(this);


        Button logoutButton = (Button)findViewById(R.id.logoutbutton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                SettingActivity.this.startActivity(intent);
                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(SettingActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Button saveButton = (Button) findViewById(R.id.savebutton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle args = getIntent().getExtras();
                String userID = args.getString("userID");
                String userPassword =
                        args.getString("userPassword");
                String userName = //"박범민";
                args.getString("userName");
                String userStudentNumber = //"13";
                args.getString("userStudentNumber");
                Log.d("idid",userID+"???" + userPassword + ";" + userName + ";" + userStudentNumber);
                Drawable drawable = mPhotoImageView.getDrawable();
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
//                byte[] b = getByteArrayFromDrawable(drawable);
                String userImage = //b.toString();//"beom";
                 encodeToBase64(bitmap);
                Log.d("bit",userImage +"????????");
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                SettingRequest settingRequest = new SettingRequest(userID, userPassword, userName, userStudentNumber, userImage, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SettingActivity.this);
                queue.add(settingRequest);
                Log.d("bit",userImage + "zzz");
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userStudentNumber",userStudentNumber);
                intent.putExtra("Frag","alpa");
                intent.putExtra("userImage",userImage);
                SettingActivity.this.startActivity(intent);

            }
        });
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.park.management.MyService".equals(service.service.getClassName())) {
                Log.d("ser","runnig");
                return true;
            }
        }
        Log.d("ser","stop");
        return false;
    }



    /**
     * 카메라에서 이미지 가져오기
     */
    /*
    private void doTakePhotoAction()
    {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
//        intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }
*/
    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction()
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case CROP_FROM_CAMERA:
            {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    mPhotoImageView.setImageBitmap(photo);
                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM:
            {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

                mImageCaptureUri = data.getData();
            }

            case PICK_FROM_CAMERA:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 90);
                intent.putExtra("outputY", 90);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }
        }
    }

    @Override
    public void onClick(View v)
    {
      /*  DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener()
        {
            @Override
           public void onClick(DialogInterface dialog, int which)
            {
                doTakePhotoAction();
            }
        };*/

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
               // .setPositiveButton("사진촬영", cameraListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();
    }

    public static String encodeToBase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeToBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
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
