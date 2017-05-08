package ar.edu.utn.frba.myapplication;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ar.edu.utn.frba.myapplication.api.responses.Chat;
import ar.edu.utn.frba.myapplication.api.responses.Identifiable;
import ar.edu.utn.frba.myapplication.api.responses.event.Event;
import ar.edu.utn.frba.myapplication.oauth.OAuthActivity;
import ar.edu.utn.frba.myapplication.service.RTMService;
import ar.edu.utn.frba.myapplication.session.Session;

public class MainActivity extends AppCompatActivity
        implements MainFragment.OnFragmentInteractionListener, DrawerAdapter.Listener, TermsAndConditionsFragment.OnFragmentInteractionListener, ChatFragment.OnFragmentInteractionListener {

    boolean tieneDosFragments;
    private RTMService service;
    private DrawerLayout drawer;
    private DrawerAdapter drawerAdapter;
    private ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        drawerAdapter = new DrawerAdapter(this, this);
        RecyclerView drawerRecycler = (RecyclerView) findViewById(R.id.nav_view);
        drawerRecycler.setAdapter(drawerAdapter);
        drawerRecycler.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new MainFragment(), "Fragment")
                    .commit();
        }
        tieneDosFragments = findViewById(R.id.contentFrame) != null;
        updateDrawer();
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

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, RTMService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        registerReceiver(sessionChangedReceiver, new IntentFilter(RTMService.SessionChangedIntentAction));
        registerReceiver(eventReceiver, new IntentFilter(RTMService.NewEventIntentAction));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (service != null) {
            service = null;
            unbindService(serviceConnection);
        }
        unregisterReceiver(sessionChangedReceiver);
        unregisterReceiver(eventReceiver);
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
        navegar(OtroFragment.newInstance(texto));
    }

    @Override
    public void navegar(Fragment fragment) {
        if (tieneDosFragments) {
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.contentFrame, fragment, "Fragment")
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainer, fragment, "Fragment")
                    .commit();
        }
    }

    @Override
    public void hello() {
        Toast.makeText(this, getString(R.string.hello_toast), Toast.LENGTH_LONG).show();
    }

    @Override
    public RTMService getService() {
        return service;
    }

    @Override
    public void sendMessage(Chat chat, String message) {
        service.sendMessage(chat.getId(), message);
    }

    // Override this method to do what you want when the menu is recreated
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isUserLoggedIn()){
            menu.findItem(R.id.action_logout).setVisible(true);
            menu.findItem(R.id.action_login).setVisible(false);
        } else {
            menu.findItem(R.id.action_logout).setVisible(false);
            menu.findItem(R.id.action_login).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void onLogin() {
        Intent intent = new Intent(this, OAuthActivity.class);
        startActivity(intent);
    }

    private void onLogout() {
        service.logout();
        updateDrawer();
    }

    private void updateDrawer() {
        boolean connecting = service != null && service.isConnecting();
        Session session = service != null ? service.getSession() : null;
        drawerAdapter.update(session, connecting);
    }

    boolean isUserLoggedIn() {
        return service != null && service.getSession() != null;
    }

    @Override
    public void selectChat(Identifiable chat) {
        gotoChat(chat);
        drawer.closeDrawer(GravityCompat.START);
    }

    void gotoChat(Identifiable destination) {
        Session session = service != null ? service.getSession() : null;
        Chat chat = session != null ? session.findChat(destination) : null;
        if (chat == null) {
            return;
        }
        if (chatFragment == null) {
            chatFragment = ChatFragment.newInstance(chat);
        }
        else {
            chatFragment.changeChat(chat);
        }
        if (!chatFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .setCustomAnimations(R.anim.push_show, 0, 0, R.anim.pop_hide)
                    .replace(R.id.fragmentContainer, chatFragment, "ChatFragment")
                    .commit();
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
            RTMService.Binder binder = (RTMService.Binder) serviceBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };

    private BroadcastReceiver sessionChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateDrawer();
        }
    };

    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Event event = (Event) intent.getSerializableExtra(RTMService.EventExtraKey);
            Toast.makeText(MainActivity.this, event.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void goBack() {
        getSupportFragmentManager().popBackStack();
    }
}
