package cz.jtek.bakingapp.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe;
import cz.jtek.bakingapp.model.Recipe.Step;

public class StepListAdapter extends RecyclerView.Adapter<StepListAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeListAdapter.class.getSimpleName();

    public interface StepListOnClickListener {
        void onClick(int position);
    }

    private final StepListOnClickListener mClickListener;

    private ArrayList<Step> mStepList;

    StepListAdapter(ArrayList<Step> stepList, StepListOnClickListener clickListener) {
        mStepList = stepList;
        mClickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        final TextView mShortDescriptionTextView;

        ViewHolder(View view) {
            super(view);
            mShortDescriptionTextView = view.findViewById(R.id.tv_step_item_short_description);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickListener.onClick(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_step_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mShortDescriptionTextView.setText(mStepList.get(position).getShortDescription());

    }

    @Override
    public int getItemCount() {
        if (mStepList == null) { return 0; }
        return mStepList.size();
    }



}
