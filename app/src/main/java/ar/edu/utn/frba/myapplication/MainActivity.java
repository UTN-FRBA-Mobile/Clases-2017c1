package ar.edu.utn.frba.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainFragment.OnFragmentInteractionListener {

    boolean tieneDosFragments;
    private boolean userIsLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new MainFragment(), "Fragment")
                .commit();
        tieneDosFragments = findViewById(R.id.contentFrame) != null;
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
        getMenuInflater().inflate(R.menu.main, menu);
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
        } else if (id == R.id.action_login){
            onLogin();
            return true;
        } else if (id == R.id.action_logout){
            onLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            onLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    // Override this method to do what you want when the menu is recreated
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(userIsLoggedIn){
            menu.findItem(R.id.action_logout).setVisible(true);
            menu.findItem(R.id.action_login).setVisible(false);
        } else {
            menu.findItem(R.id.action_logout).setVisible(false);
            menu.findItem(R.id.action_login).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void onLogin() {
        userIsLoggedIn = true;
        updateDrawer();
    }

    private void onLogout() {
        userIsLoggedIn = false;
        updateDrawer();
    }

    private void updateDrawer() {
        NavigationView navView= (NavigationView) findViewById(R.id.nav_view);
        View header = navView.getHeaderView(0);
        Menu navMenu = navView.getMenu();
        LinearLayout sideNavLayout = (LinearLayout)header.findViewById(R.id.sideNavLayout);

        if(userIsLoggedIn){
            ((ImageView)findViewById(R.id.imageView)).setImageResource(R.drawable.ic_action_name);
            ((TextView)findViewById(R.id.nav_main_text)).setText(R.string.logged_in_drawer_message);
            ((TextView)findViewById(R.id.nav_sec_text)).setText(R.string.logged_in_drawer_mail);
            navMenu.findItem(R.id.nav_logout).setVisible(true);
            sideNavLayout.setBackgroundResource(R.drawable.side_nav_bar_loggedin);
        } else {
            ((ImageView)findViewById(R.id.imageView)).setImageResource(R.drawable.ic_profile);
            ((TextView)findViewById(R.id.nav_main_text)).setText(R.string.default_drawer_message);
            ((TextView)findViewById(R.id.nav_sec_text)).setText(R.string.default_drawer_mail);
            navMenu.findItem(R.id.nav_logout).setVisible(false);
            sideNavLayout.setBackgroundResource(R.drawable.side_nav_bar);
        }
    }
}
