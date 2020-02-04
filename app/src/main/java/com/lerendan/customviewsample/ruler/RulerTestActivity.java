package com.lerendan.customviewsample.ruler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.lerendan.customviewsample.R;
import yanzhikai.ruler.BooheeRuler;
import yanzhikai.ruler.KgNumberLayout;

public class RulerTestActivity extends AppCompatActivity {
    private BooheeRuler mBooheeRuler;
    private KgNumberLayout mKgNumberLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);
        mBooheeRuler =  findViewById(R.id.br);
        mKgNumberLayout =  findViewById(R.id.knl);
        mKgNumberLayout.bindRuler(mBooheeRuler);
    }


}
