package ar.edu.utn.frba.myapplication;

import android.content.Context;
import android.support.annotation.StringDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by emanuel on 27/3/17.
 */

public class OtroAdapter extends RecyclerView.Adapter<OtroAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private String texto;

    public OtroAdapter(Context context, String texto) {
        layoutInflater = LayoutInflater.from(context);
        this.texto = texto;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.otro_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(texto + " " + String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return 1000000;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
