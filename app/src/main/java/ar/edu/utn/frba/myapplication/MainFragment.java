package ar.edu.utn.frba.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import ar.edu.utn.frba.myapplication.picture.SelectPictureActivity;
import ar.edu.utn.frba.myapplication.storage.Preferences;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainFragment extends Fragment {

    private static final int PICTURE_REQUEST_CODE = 35743;
    private static final String PICTURE = "picture";

    private OnFragmentInteractionListener mListener;

    private ImageView pictureView;
    private EditText editText;

    private Preferences preferences;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pictureView = (ImageView)view.findViewById(R.id.pictureView);
        view.findViewById(R.id.changePictureButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SelectPictureActivity.class);
                intent.putExtra(SelectPictureActivity.SELECTED_PICTURE, preferences.getMainPicture());
                startActivityForResult(intent, PICTURE_REQUEST_CODE);
            }
        });
        editText = (EditText)view.findViewById(R.id.editText);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.share(editText.getText().toString());
            }
        });
        view.findViewById(R.id.otroButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.navegar(editText.getText().toString());
            }
        });
        view.findViewById(R.id.termsAndConditions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.navegar(TermsAndConditionsFragment.newInstance());
            }
        });
        view.findViewById(R.id.helloButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.showToast(getString(R.string.hello_toast));
            }
        });
        view.findViewById(R.id.notiifcationsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.navegar(NotificationsFragment.newInstance());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadImage();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        preferences = Preferences.get(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void loadImage() {
        String path = preferences.getMainPicture();
        Bitmap bitmap = BitmapFactory.decodeFile(path, null);
        pictureView.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICTURE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String filePath = data.getStringExtra(SelectPictureActivity.SELECTED_PICTURE);
                    if (filePath != null) {
                        preferences.setMainPicture(filePath);
                        loadImage();
                    }
                }
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void share(String texto);
        void navegar(String texto);
        void navegar(Fragment fragment);
        void showToast(String value);
    }
}
