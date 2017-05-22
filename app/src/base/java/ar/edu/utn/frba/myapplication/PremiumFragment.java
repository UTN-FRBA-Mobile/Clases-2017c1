package ar.edu.utn.frba.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ar.edu.utn.frba.myapplication.R;
import ar.edu.utn.frba.myapplication.api.ImdbApi;
import ar.edu.utn.frba.myapplication.model.MovieListResponse;
import ar.edu.utn.frba.myapplication.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PremiumFragment extends Fragment {
    public static PremiumFragment newInstance() {
        return null;
    }
}
