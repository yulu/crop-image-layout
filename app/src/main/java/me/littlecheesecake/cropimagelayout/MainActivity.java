package me.littlecheesecake.cropimagelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.littlecheesecake.croplayout.EditPhotoView;
import me.littlecheesecake.croplayout.EditableImage;
import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditPhotoView imageView = (EditPhotoView) findViewById(R.id.editable_image);
        imageView.initView(this, new EditableImage(this, R.drawable.photo));
        imageView.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                System.out.println("box: [" + x1 + "," + y1 +"],[" + x2 + "," + y2 + "]");
            }
        });
    }

}
