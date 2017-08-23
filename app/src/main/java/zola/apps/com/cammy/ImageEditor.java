package zola.apps.com.cammy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * @author Zola
 */

public class ImageEditor extends AppCompatActivity implements View.OnClickListener {

    /* Views */
    ImageView imgView;
    ImageView frameImg;
    Bitmap originalBm;
    RelativeLayout cropView;
    ProgressDialog progDialog;
    TextView captionTxt;
    EditText captionEditText;
    Bitmap adjBm;


    /* Variables*/

    // Array of frame images
    int[] framesList = new int[]{
            R.drawable.frame0, R.drawable.frame1,
            R.drawable.frame2, R.drawable.frame3,
            R.drawable.frame4,R.drawable.frame5,
            R.drawable.frame6,

            // Add new frames here...
    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_editor);
        // Lock to Portrait orientation
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Set back Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set Title
        getSupportActionBar().setTitle("Cammy Editor");


        // Init AdMob banner
        AdView mAdView = (AdView) findViewById(R.id.adMobBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        // Init Views
        imgView = (ImageView)findViewById(R.id.finalImage);
        frameImg = (ImageView)findViewById(R.id.frameImg);
        cropView = (RelativeLayout)findViewById(R.id.cropView);
        captionTxt = (TextView) findViewById(R.id.captionTxt);
        captionEditText = (EditText)findViewById(R.id.captionEditText);

        final HorizontalScrollView filtersView = (HorizontalScrollView)findViewById(R.id.filtersView);
        final RelativeLayout adjustView = (RelativeLayout)findViewById(R.id.adjustView);
        final HorizontalScrollView framesView = (HorizontalScrollView)findViewById(R.id.framesView);

        final Button filtersButt = (Button)findViewById(R.id.filtersButt);
        final Button adjButt = (Button)findViewById(R.id.adjustButt);
        final Button framesButt = (Button)findViewById(R.id.framesButt);
        final Button captionButt = (Button)findViewById(R.id.captionButt);




        // Get image passed from Home.java
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(ImageEditor.this.openFileInput("imagePassed"));
            imgView.setImageBitmap(bitmap);

            // Set original bitmap
            originalBm = bitmap;
        } catch (FileNotFoundException e) { e.printStackTrace(); }






        // MARK: - TOOLBAR BUTTONS ------------------------------------------------

        // Filters Button
        filtersButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtersView.setVisibility(View.VISIBLE);
                filtersButt.setBackgroundResource(R.drawable.filters_on);

                adjustView.setVisibility(View.INVISIBLE);
                adjButt.setBackgroundResource(R.drawable.adjust_butt);
                framesView.setVisibility(View.INVISIBLE);
                framesButt.setBackgroundResource(R.drawable.frames_butt);
                captionButt.setBackgroundResource(R.drawable.caption_butt);
                captionEditText.setVisibility(View.INVISIBLE);
            }
        });


        // Adjustment Button
        adjButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjBm = ((BitmapDrawable)imgView.getDrawable()).getBitmap();

                adjustView.setVisibility(View.VISIBLE);
                adjButt.setBackgroundResource(R.drawable.adjust_on);

                filtersView.setVisibility(View.INVISIBLE);
                filtersButt.setBackgroundResource(R.drawable.filters_butt);
                framesView.setVisibility(View.INVISIBLE);
                framesButt.setBackgroundResource(R.drawable.frames_butt);
                captionButt.setBackgroundResource(R.drawable.caption_butt);
                captionEditText.setVisibility(View.INVISIBLE);
            }
        });

        // Frames Button
        framesButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                framesView.setVisibility(View.VISIBLE);
                framesButt.setBackgroundResource(R.drawable.frames_on);

                adjustView.setVisibility(View.INVISIBLE);
                adjButt.setBackgroundResource(R.drawable.adjust_butt);
                filtersView.setVisibility(View.INVISIBLE);
                filtersButt.setBackgroundResource(R.drawable.filters_butt);
                captionButt.setBackgroundResource(R.drawable.caption_butt);
                captionEditText.setVisibility(View.INVISIBLE);
            }
        });


        // Caption button
        captionButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captionButt.setBackgroundResource(R.drawable.caption_on);
                captionEditText.setVisibility(View.VISIBLE);
                captionEditText.setFocusable(true);

                // Tap Enter on keyboard
                captionEditText.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            /// Hide captionEditTxt
                            captionTxt.setText(captionEditText.getText().toString());
                            captionEditText.setVisibility(View.INVISIBLE);
                            // Dismiss keyboard
                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(captionEditText.getWindowToken(), 0);

                            return true;
                        }
                        return false;
                    }
                });

                framesView.setVisibility(View.INVISIBLE);
                framesButt.setBackgroundResource(R.drawable.frames_butt);
                adjustView.setVisibility(View.INVISIBLE);
                adjButt.setBackgroundResource(R.drawable.adjust_butt);
                filtersView.setVisibility(View.INVISIBLE);
                filtersButt.setBackgroundResource(R.drawable.filters_butt);
            }
        });









        // MARK: - BRIGHTNESS SLIDER ------------------------------------------------------------
        final SeekBar brightnessSeek = (SeekBar)findViewById(R.id.brightnessSeek);
        brightnessSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int intensity = seekBar.getProgress() + -50;
                Filters.brightnessEffect(imgView, adjBm, intensity);
            }
        });





        // MARK: - GENERATE FRAME BUTTONS INTO SCROLLVIEW --------------------------------
        for (int i = 0; i<framesList.length; i++) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.framesLayout);

            // Setup the Buttons
            Button btnTag = new Button(this);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            layoutParams.setMargins(5, 0, 0, 0);
            btnTag.setLayoutParams(layoutParams);
            btnTag.setId(i);
            btnTag.setBackgroundResource(framesList[i]);
            btnTag.setOnClickListener(this);

            //add button to the layout
            layout.addView(btnTag);
        }







        // MARK: - PHOTO FILTER BUTTONS --------------------------------------------

        // Original
        Button ob = (Button)findViewById(R.id.originalButt);
        ob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgView.setImageBitmap(originalBm);
            }});


        // Instant
        Button ib = (Button)findViewById(R.id.instantButt);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double red = 111;
                double green = 78;
                double blue = 55;
                int depth = 300;
                Filters.sepiaEffect(imgView, originalBm, depth, red/255.0, green/255.0, blue/255.0);

                Bitmap processedBm = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
                int intensity = 30;
                Filters.brightnessEffect(imgView, processedBm, intensity);
            }});



        // Invert
        Button invB = (Button)findViewById(R.id.invertButt);
        invB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filters.invertEffect(imgView, originalBm);
            }});

        // Tonal
        Button tb = (Button)findViewById(R.id.tonalButt);
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double red = 100;
                double green = 100;
                double blue = 100;
                Filters.grayscaleEffect(imgView, originalBm, red/255.0, green/255.0, blue/255.0);
            }});

        // Noir
        Button nb = (Button)findViewById(R.id.noirButt);
        nb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double intensity = 51;
                Filters.contrastEffect(imgView, originalBm, intensity);
            }});


        // Vintage
        Button vb = (Button)findViewById(R.id.vintageButt);
        vb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double red = 178;
                double green = 76;
                double blue = 2;
                int depth = 150;
                Filters.sepiaEffect(imgView, originalBm, depth, red/255.0, green/255.0, blue/255.0);
            }});

        // Vintage 2
        Button vb2 = (Button)findViewById(R.id.vintageButt2);
        vb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double red = 164;
                double green = 178;
                double blue = 2;
                int depth = 150;
                Filters.sepiaEffect(imgView, originalBm, depth, red/255.0, green/255.0, blue/255.0);
            }});


        // Light Blue
        Button fbB = (Button)findViewById(R.id.lightBlueButt);
        fbB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double red = 92;
                double green = 199;
                double blue = 255;
                int intensity = 180;
                Filters.sepiaEffect(imgView, originalBm, intensity, red/255.0, green/255.0, blue/255.0);
            }});


        // Light Green
        Button lgB = (Button)findViewById(R.id.lightGreenButt);
        lgB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double red = 92;
                double green = 255;
                double blue = 120;
                int intensity = 180;
                Filters.sepiaEffect(imgView, originalBm, intensity, red/255.0, green/255.0, blue/255.0);
            }});


        // Light Red
        Button lrB = (Button)findViewById(R.id.lightRedButt);
        lrB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double red = 255;
                double green = 108;
                double blue = 108;
                int intensity = 180;
                Filters.sepiaEffect(imgView, originalBm, intensity, red/255.0, green/255.0, blue/255.0);
            }});



        // END PHOTO FILTER BUTTONS ------------------------------------------------





    }// end onCreate()







    // TAKE SCREENSHOT OF THE cropView (RelativeLayout)
    public void takeScreenshotOfCropView() {
        View v = cropView;
        v.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(R.string.app_name + "", Context.MODE_PRIVATE);
        File filePath = new File(directory,"image.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // Call Load image
            loadImageFromStorage(filePath.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // LOAD IMAGE FOR SHARING
    private void loadImageFromStorage(String path) {
        try {
            File f = new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView shareImg = (ImageView)findViewById(R.id.shareImg);
            shareImg.setImageBitmap(b);

            // Call shareImage method
            shareImage();
        }
        catch (FileNotFoundException e)  {
            e.printStackTrace();
        }
    }



    // MARK: - SHARE THE EDITED IMAGE
    public void shareImage() {
        progDialog.dismiss();

        ImageView img = (ImageView) findViewById(R.id.shareImg);
        Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
        Uri uri = getImageUri(ImageEditor.this, bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share image on..."));
    }


    // Method to get URI of the eventImage
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "MyMeme", null);
        return Uri.parse(path);
    }












    // MENU ON ACTION BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_editor, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            // DEFAULT BACK BUTTON
            case android.R.id.home:
                this.finish();
                return true;


            // SHARE BUTTON
            case R.id.shareButt:
                // Init a ProgressDialog
                progDialog = new ProgressDialog(this);
                progDialog.setTitle(R.string.app_name);
                progDialog.setMessage("Preparing image for sharing...");
                progDialog.setIndeterminate(false);
                progDialog.setIcon(R.drawable.mini_logo);
                progDialog.show();


                cropView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        takeScreenshotOfCropView();
                    }
                }, 1000);


                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }



    // MARK: SET FRAME IMAGE ------------------------------------------------
    @Override
    public void onClick(View v) {
        frameImg.setImageResource(framesList[v.getId()]);
    }



}//@end
