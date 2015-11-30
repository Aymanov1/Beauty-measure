package com.example.hamada.beauty_measure;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int RQS_LOADIMAGE = 1;
    private Button btnLoad, btnDetFace,showRst;

    final Context context = this;
    private ImageView imgView;
    private Bitmap myBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoad = (Button) findViewById(R.id.btnLoad);
        showRst = (Button) findViewById(R.id.showRst);
        btnDetFace = (Button) findViewById(R.id.btnDetectFace);
        imgView = (ImageView) findViewById(R.id.imgview);

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, RQS_LOADIMAGE);
            }
        });
        showRst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create a Paint object for drawing with
                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(5);
                myRectPaint.setColor(Color.GREEN);
                myRectPaint.setStyle(Paint.Style.STROKE);

                Paint landmarksPaint = new Paint();
                landmarksPaint.setStrokeWidth(10);
                landmarksPaint.setColor(Color.RED);
                landmarksPaint.setStyle(Paint.Style.STROKE);

                //Create a Canvas object for drawing on
                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                //Detect the Faces


                //!!!
                //Cannot resolve method setTrackingEnabled(boolean)
                //FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).build();
                //faceDetector.setTrackingEnabled(false);

                FaceDetector faceDetector =
                        new FaceDetector.Builder(getApplicationContext())
                                .setTrackingEnabled(false)
                                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                                .build();

                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Face> faces = faceDetector.detect(frame);

                //Draw Rectangles on the Faces
                for (int i = 0; i < faces.size(); i++) {
                    Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);

                    //get Landmarks for the first face
                    List<Landmark> landmarks = thisFace.getLandmarks();
                    float[] tabX=new float[landmarks.size()];
                    float[] tabY=new float[landmarks.size()];
                    for(int l=0; l<landmarks.size(); l++){
                        PointF pos = landmarks.get(l).getPosition();
                        tempCanvas.drawPoint(pos.x, pos.y, landmarksPaint);
                        //tempCanvas.drawText("" + l, pos.x, pos.y, landmarksPaint);
                        tabX[l]=pos.x;
                        tabY[l]=pos.y;


                    }
                    // distance between centres of interest of the face

                    //length mouth and corner mouth to other cheek
                    float distance1=tabX[5]-tabX[6];
                    float distance2=tabX[3]-tabX[6]+(tabX[2]-tabX[6]);
                    float rapport1=distance1/distance2;

                    //eye-nose and eye mouth level
                    float distance3=tabY[2]-tabY[0];
                    float distance4=tabY[5]-tabY[0];
                    float rapport2=distance3/distance4;

                    //cheek-nose and cheek-eye level
                    float distance5=tabX[0]-tabX[4]+(tabX[2]-tabX[6]);
                    float distance6=tabX[2]-tabX[4];
                    float rapport3=distance5/distance6;

                    // buttom  mouth and nose
                    float distance7=(3/2)*(tabY[7]-tabY[6]);
                    float distance8=tabY[7]-tabY[2];
                    float rapport4=distance7/distance8;

                    //between eyes and between cheeks
                    float distance9=tabX[1]-tabX[0];
                    float distance10=(tabX[3]-tabX[4])+2*(tabX[2]-tabX[6]);
                    float rapport5=distance9/distance10;

                    for (int j=0;j<landmarks.size();j++){
                        System.out.println("point "+j+"\n");
                        System.out.println("x="+tabX[j]+"\n");
                        System.out.println("y="+tabY[j]+"\n");
//show the report between distances
                        System.out.println("rapport 1  "+rapport1+"\n");
                        System.out.println("rapport 2  "+rapport2+"\n");
                        System.out.println("rapport 3  "+rapport3+"\n");
                        System.out.println("rapport 4  "+rapport4+"\n");
                        System.out.println("rapport 5  "+rapport5+"\n");

                    }
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom);
                dialog.setTitle("Your Beauty's degree");
                String mention=beautyClass(rapport1,rapport2,rapport3,rapport4,rapport5);

                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.text);
                text.setText("\nThe test of your beauty is done\n Your Beauty's degree is: "+mention);
                ImageView image = (ImageView) dialog.findViewById(R.id.image);
                //detectFace();

                String monthString,month;
                switch (mention) {
                    case "Incredible":
                        image.setImageResource(R.drawable.incredible);
                        break;
                    case "beautiful":
                        image.setImageResource(R.drawable.beautiful);
                        break;
                    case "ugly":
                        image.setImageResource(R.drawable.ugly);
                        break;
                    case "normal":
                        image.setImageResource(R.drawable.normal);
                        break;

                }

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }}

        });
        btnDetFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBitmap == null) {
                    Toast.makeText(MainActivity.this,
                            "myBitmap == null",
                            Toast.LENGTH_LONG).show();
                } else {
                    detectFace();
                    Toast.makeText(MainActivity.this,
                            "Done",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, RQS_LOADIMAGE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, RQS_LOADIMAGE);

        } else if (id == R.id.nav_gallery) {

            android.app.AlertDialog alert = new android.app.AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle("About us");
            alert.setMessage("Developers : AYMEN JALLABI\n MED AMINE MARNISSI\n EMNA KACHOUT\n  From GI3 TRANSMEDIA\n FOR Transmedia PROJECT");
            alert.setButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this,"Developed by AYMEN JALLABI\n" +
                            " MED AMINE MARNISSI\n" +
                            " EMNA KACHOUT", Toast.LENGTH_SHORT).show();

                }
            });


            //  alert.setCancelable(false);
            alert.show();

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } /*else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RQS_LOADIMAGE
                && resultCode == RESULT_OK) {

            if (myBitmap != null) {
                myBitmap.recycle();
            }

            try {
                InputStream inputStream =
                        getContentResolver().openInputStream(data.getData());
                myBitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                imgView.setImageBitmap(myBitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    reference:
    https://search-codelabs.appspot.com/codelabs/face-detection
     */
    private void detectFace() {

        //Create a Paint object for drawing with
        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.GREEN);
        myRectPaint.setStyle(Paint.Style.STROKE);

        Paint landmarksPaint = new Paint();
        landmarksPaint.setStrokeWidth(10);
        landmarksPaint.setColor(Color.RED);
        landmarksPaint.setStyle(Paint.Style.STROKE);

        //Create a Canvas object for drawing on
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        //Detect the Faces


        //!!!
        //Cannot resolve method setTrackingEnabled(boolean)
        //FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext()).build();
        //faceDetector.setTrackingEnabled(false);

        FaceDetector faceDetector =
                new FaceDetector.Builder(getApplicationContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .build();

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        //Draw Rectangles on the Faces
        for (int i = 0; i < faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);

            //get Landmarks for the first face
            List<Landmark> landmarks = thisFace.getLandmarks();
            float[] tabX=new float[landmarks.size()];
            float[] tabY=new float[landmarks.size()];
            for(int l=0; l<landmarks.size(); l++){
                PointF pos = landmarks.get(l).getPosition();
                tempCanvas.drawPoint(pos.x, pos.y, landmarksPaint);
                //tempCanvas.drawText("" + l, pos.x, pos.y, landmarksPaint);
                tabX[l]=pos.x;
                tabY[l]=pos.y;


            }
            // distance between centres of interest of the face

            //length mouth and corner mouth to other cheek
            float distance1=tabX[5]-tabX[6];
            float distance2=tabX[3]-tabX[6]+(tabX[2]-tabX[6]);
            float rapport1=distance1/distance2;

            //eye-nose and eye mouth level
            float distance3=tabY[2]-tabY[0];
            float distance4=tabY[5]-tabY[0];
            float rapport2=distance3/distance4;

            //cheek-nose and cheek-eye level
            float distance5=tabX[0]-tabX[4]+(tabX[2]-tabX[6]);
            float distance6=tabX[2]-tabX[4];
            float rapport3=distance5/distance6;

            // buttom  mouth and nose
            float distance7=(3/2)*(tabY[7]-tabY[6]);
            float distance8=tabY[7]-tabY[2];
            float rapport4=distance7/distance8;

            //between eyes and between cheeks
            float distance9=tabX[1]-tabX[0];
            float distance10=(tabX[3]-tabX[4])+2*(tabX[2]-tabX[6]);
            float rapport5=distance9/distance10;

            for (int j=0;j<landmarks.size();j++){
                System.out.println("point "+j+"\n");
                System.out.println("x="+tabX[j]+"\n");
                System.out.println("y="+tabY[j]+"\n");
//show the report between distances
                System.out.println("rapport 1  "+rapport1+"\n");
                System.out.println("rapport 2  "+rapport2+"\n");
                System.out.println("rapport 3  "+rapport3+"\n");
                System.out.println("rapport 4  "+rapport4+"\n");
                System.out.println("rapport 5  "+rapport5+"\n");

            }
            Toast.makeText(this,"rapport 1 "+rapport1,Toast.LENGTH_LONG).show();

            Toast.makeText(this,"rapport 2 "+rapport2,Toast.LENGTH_LONG).show();

            Toast.makeText(this,"rapport 3 "+rapport3,Toast.LENGTH_LONG).show();

            Toast.makeText(this,"rapport 4 "+rapport4,Toast.LENGTH_LONG).show();

            Toast.makeText(this,"rapport 5 "+rapport5,Toast.LENGTH_LONG).show();

            String mention=beautyClass(rapport1,rapport2,rapport3,rapport4,rapport5);


            Toast.makeText(this,"mention  "+mention,Toast.LENGTH_LONG).show();

        }

        imgView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

    }
    String beautyClass(float rapport1,float rapport2,float rapport3,float rapport4,float rapport5){
        int rating=0;
        if(rapport1>=0.58 && rapport1<=0.64) rating+=4;
        else if((rapport1>=0.54 && rapport1<0.58)||(rapport1>0.64 && rapport1<=0.68)) rating+=3;
        else if((rapport1>=0.50 && rapport1<0.54)||(rapport1>0.68 && rapport1<=0.7)) rating+=2;
        else rating+=1;

        if(rapport2>=0.58 && rapport2<=0.64) rating+=4;
        else if((rapport2>=0.54 && rapport2<0.58)||(rapport2>0.64 && rapport2<0.68)) rating+=3;
        else if((rapport2>=0.50 && rapport2<0.54)||(rapport2>0.68 && rapport2<0.7)) rating+=2;
        else rating+=1;

        if(rapport3>0.58 && rapport3<0.64) rating+=4;
        else if((rapport3>0.54 && rapport3<0.58)||(rapport3>0.64 && rapport3<0.67)) rating+=3;
        else if((rapport3>0.50 && rapport3<0.54)||(rapport3>0.67 && rapport3<0.7)) rating+=2;
        else rating+=1;


        if(rapport4>=0.26 && rapport4<=0.28) rating+=4;
        else if((rapport4>=0.265 && rapport4<0.26)||(rapport4>0.28 && rapport4<=0.29)) rating+=3;
        else if((rapport4>=0.25 && rapport4<0.265)||(rapport4>0.29 && rapport4<=0.30)) rating+=2;
        else rating+=1;

        if(rapport5>=0.43 && rapport5<=0.49) rating+=4;
        else if((rapport5>=0.40 && rapport5<0.43)||(rapport5>0.49 && rapport5<=0.53)) rating+=3;
        else if((rapport5>=0.37 && rapport5<0.40)||(rapport5>0.53 && rapport5<=0.57)) rating+=2;
        else rating+=1;

        if(rating>=15) return "Incredible";
        else if(rating>=12) return "beautiful";
        else if(rating>=8) return "normal";
        else  return "ugly";


    }

}
