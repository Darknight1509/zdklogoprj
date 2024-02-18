package com.example.zdklogoprj;

import static android.os.Environment.DIRECTORY_DCIM;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.exifinterface.media.ExifInterface;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.zdklogoprj.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import kotlin.Metadata;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1; // 请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonSelectImage = findViewById(R.id.button_select_image);
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动图片选择器
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();


                // 获取到图片的InputStream，然后将其转换为Bitmap
            InputStream imageStream = null;
            try {

            imageStream = getContentResolver().openInputStream(selectedImageUri);

            ExifInterface exif = new ExifInterface(imageStream);

            imageStream = getContentResolver().openInputStream(selectedImageUri);

            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            // 找到布局中的ImageView并设置Bitmap
            ImageView imageView = findViewById(R.id.imageView);


            Bitmap src = selectedImage;

            Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

            InputStream is = null;
            try {
                is = getAssets().open("longlogo01-android.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);


            String model = exif.getAttribute(ExifInterface.TAG_MODEL);

            String lensModel = exif.getAttribute(ExifInterface.TAG_LENS_MODEL);

            String dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);

            String focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);

            String focalLength35mm = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM);

            // 将字符串分割为分子和分母
            String[] parts = focalLength.split("/");

            // 转换分子和分母为double类型进行计算
            double numerator = Double.parseDouble(parts[0]);
            double denominator = Double.parseDouble(parts[1]);
            double focalLengthResult = (numerator / denominator);

            String fNumber = exif.getAttribute(ExifInterface.TAG_F_NUMBER);

            String exposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);

            String isoSpeedRatings = exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);

            double time = 1 / (Double.parseDouble(exposureTime));

            String realExposure ="";

            if (time < 1 ) {
                realExposure = exposureTime;
            }else {
                realExposure = "1/" + String.valueOf(time);
            }

            realExposure = realExposure.split("\\.")[0];

            String sfocalLengthResult = String.valueOf(focalLengthResult).split("\\.")[0];

            if(!String.valueOf(focalLengthResult).split("\\.")[1].equals("0")){
                sfocalLengthResult = String.valueOf(focalLengthResult);
            }

            if(focalLength35mm!=null){
                sfocalLengthResult = focalLength35mm;
            }

            String exposure = sfocalLengthResult + "mm f/" + fNumber + " " + realExposure + "S ISO"+ isoSpeedRatings;

            Paint paint = new Paint();
            paint.setColor(Color.YELLOW); // 水印的颜色
            paint.setTextSize(62); // 水印的字体大小
            paint.setAntiAlias(true);
            paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置文字样式

            Canvas logoCanvas = new Canvas(mutableBitmap);
            if(model != null) {
                    logoCanvas.drawText(model, 200, 155, paint);
                }
            if(lensModel != null ){
                logoCanvas.drawText(lensModel, 200, 250, paint);
            }

            if(exposure != null) {
                logoCanvas.drawText(exposure, 3000, 155, paint);
            }
            if(dateTime != null) {
                logoCanvas.drawText(dateTime, 3000, 250, paint);
            }

            Bitmap topBitmap = result;
            Bitmap bottomBitmap = mutableBitmap;



                int topBitmapLongSide = topBitmap.getWidth();

            // 确定缩放比例
            float scale = topBitmapLongSide / (float) Math.max(bottomBitmap.getWidth(), bottomBitmap.getHeight());

            // 缩放下方Bitmap
            Bitmap scaledBottomBitmap = Bitmap.createScaledBitmap(bottomBitmap,
                    (int) (bottomBitmap.getWidth() * scale),
                    (int) (bottomBitmap.getHeight() * scale),
                    true);

            // 创建一个新的Bitmap，其宽度为两个Bitmap宽度的最大值，高度为两者之和
            Bitmap newresult = Bitmap.createBitmap(Math.max(topBitmap.getWidth(), scaledBottomBitmap.getWidth()),
                    topBitmap.getHeight() + scaledBottomBitmap.getHeight(),
                    topBitmap.getConfig());




            // 在新的Bitmap上绘制两个Bitmap
            Canvas canvas = new Canvas(newresult);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.drawBitmap(scaledBottomBitmap, 0, topBitmap.getHeight(), null);




            imageView.setImageBitmap(newresult);

            OutputStream os = null;
            try {
                // 保存图片到Pictures目录
                File directory = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);
                String filename = getFileName(selectedImageUri)+"_01.jpg";
                File file = new File(directory, filename);

                os = new FileOutputStream(file);
                newresult.compress(Bitmap.CompressFormat.JPEG, 100, os); // 以JPEG格式保存
                os.flush();
                os.close();
                MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, null);
                Toast.makeText(this, "图片已保存到" + directory, Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(columnIndex);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}