package ar.edu.utn.frba.myapplication;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    boolean tieneDosFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new MainFragment(), "Fragment")
                .commit();
        tieneDosFragments = findViewById(R.id.contentFrame) != null;
    }

    @Override
    public void share(String texto) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void navegar(String texto) {
        if (tieneDosFragments) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentFrame, OtroFragment.newInstance(texto), "Fragment")
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, OtroFragment.newInstance(texto), "Fragment")
                    .commit();
        }
    }
}
