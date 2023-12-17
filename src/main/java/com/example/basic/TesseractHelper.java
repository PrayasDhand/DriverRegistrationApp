package com.example.basic;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TesseractHelper {
    private TessBaseAPI tessBaseAPI;
    private String ocrResult;

    public TesseractHelper(Context context, String language){
        tessBaseAPI = new TessBaseAPI();
        try {
            // Copy the tessdata folder from assets to app's internal storage
            String datapath = context.getFilesDir().getPath() + "/tessdata/";
            AssetManager assetManager = context.getAssets();
            String[] files = assetManager.list("tessdata");
            for (String file : files) {
                InputStream in = assetManager.open("tessdata/" + file); 
                OutputStream out = new FileOutputStream(datapath + file);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                out.close();
                in.close();
            }
            tessBaseAPI.init(datapath, language);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String performOCR(Bitmap bitmap){
        tessBaseAPI.setImage(bitmap);
        ocrResult = tessBaseAPI.getUTF8Text();
        return ocrResult;
    }

    public String getOCRResult() {
        return ocrResult;
    }

    public  void onDestroy(){
        if (tessBaseAPI != null){
            tessBaseAPI.end();
        }
    }
}
