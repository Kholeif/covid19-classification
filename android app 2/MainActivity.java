package com.example.april;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.april.ml.ModelCopy;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView imgView;
    private Button select, predict, clear, take;
    private TextView tv,tv2;
    private Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) findViewById(R.id.imageView);
        tv = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        select = (Button) findViewById(R.id.button);
        take = (Button) findViewById(R.id.buttonn);
        predict = (Button) findViewById(R.id.button2);
        clear = (Button) findViewById(R.id.button3);


        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.CAMERA
            },101);
        }

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);

            }
        });
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 101);

            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("");
                tv2.setText("");
            }
        });

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (img != null){
                        img = Bitmap.createScaledBitmap(img, 224, 224, true);
                        ModelCopy model = ModelCopy.newInstance(getApplicationContext());

                        TensorImage image = TensorImage.fromBitmap(img);
                        ModelCopy.Outputs outputs = model.process(image);
                        List<Category> probability = outputs.getProbabilityAsCategoryList();

                        // Releases model resources if no longer used.
                        model.close();

                        DecimalFormat df = new DecimalFormat("###.##");
//                        tv.setText(probability.get(1).toString());
//                        tv2.setText(probability.get(0).toString());
//                        tv.setText(probability.get(1).getLabel() +"   :   "+probability.get(1).getScore());
//                        tv2.setText(probability.get(0).getLabel() +"   :   "+probability.get(0).getScore());

                        if (probability.get(1).getScore() > probability.get(0).getScore()){
                            tv.setText(probability.get(1).getLabel() +"   :   "+probability.get(1).getScore()*100 +" %");
                            tv2.setText("");
                        }
                        else {
                            tv2.setText(probability.get(0).getLabel() +"   :   "+probability.get(0).getScore()*100 +" %");
                            tv.setText("");
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"no image selected", Toast.LENGTH_SHORT).show();
                    }


                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });

    }

//    predict.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            img = Bitmap.createScaledBitmap(img, 224, 224, true);
//            float[][][][] input = new float[1][224][224][3];
//            for (int x = 0; x < 224; x++) {
//                for (int y = 0; y < 224; y++) {
//                    int pixel = img.getPixel(x, y);
//                    input[0][x][y][0] = Color.red(pixel) / 255.0f;
//                    input[0][x][y][1] = Color.green(pixel) / 255.0f;
//                    input[0][x][y][2] = Color.blue(pixel) / 255.0f;
//                }
//            }
//            try {
//                Model model = Model.newInstance(getApplicationContext());
//
//                // Creates inputs for reference.
//                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
//
//                TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
//                tensorImage.load(img);
//                ByteBuffer byteBuffer = tensorImage.getBuffer();
//
//                inputFeature0.loadBuffer(byteBuffer);
//
//                // Runs model inference and gets result.
//                Model.Outputs outputs = model.process(inputFeature0);
//                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//                // Releases model resources if no longer used.
//                model.close();
//
//                DecimalFormat df = new DecimalFormat("###.##");
//                tv.setText("Negative = "+df.format(outputFeature0.getFloatArray()[0]*100) +"%");
//                tv2.setText("Positive = "+df.format(outputFeature0.getFloatArray()[1]*100) +"%");
//
////                    tv.setText("Negative = "+outputFeature0.getFloatArray()[0]*100 +"%");
////                    tv2.setText("Positive = "+outputFeature0.getFloatArray()[1]*100 +"%");
//
//
//            } catch (IOException e) {
//                // TODO Handle the exception
//            }
//
//        }
//    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100)
        {
            imgView.setImageURI(data.getData());

            Uri uri = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(requestCode == 101)
        {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            img = captureImage;
            imgView.setImageBitmap(captureImage);
        }
    }

}