package me.littlecheesecake.cropimagelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.littlecheesecake.croplayout.EditPhotoView;
import me.littlecheesecake.croplayout.EditableImage;
import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;
import me.littlecheesecake.croplayout.model.ScalableBox;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditPhotoView imageView = (EditPhotoView) findViewById(R.id.editable_image);
        final TextView boxText = (TextView) findViewById(R.id.box_text);
        final EditableImage image = new EditableImage(this, R.drawable.photo2);
        ScalableBox box = new ScalableBox(25,180,640,880);
        image.setBox(box);
        imageView.initView(this, image);

        boxText.setText("box: [" + 25 + "," + 180 +"],[" + 640 + "," + 880 + "]");
        imageView.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                boxText.setText("box: [" + x1 + "," + y1 +"],[" + x2 + "," + y2 + "]");
            }
        });

        Button button = (Button)findViewById(R.id.rotate_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.rotateImageView();
            }
        });
    }

}
