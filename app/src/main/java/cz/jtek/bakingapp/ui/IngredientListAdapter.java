package cz.jtek.bakingapp.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe.Ingredient;

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = IngredientListAdapter.class.getSimpleName();

    private ArrayList<Ingredient> mIngredientList;

    IngredientListAdapter(ArrayList<Ingredient> recipeList) {
        mIngredientList = recipeList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView mQuantityTextView;
        final TextView mMeasureTextView;
        final TextView mIngredientTextView;

        /**
         * View holder constructor
         * Sets member variables and attaches local OnClick listener when creating views
         *
         * @param view View to be held in holder
         */
        ViewHolder(View view) {
            super(view);
            mQuantityTextView = view.findViewById(R.id.tv_ingredient_item_quantity);
            mMeasureTextView = view.findViewById(R.id.tv_ingredient_item_measure);
            mIngredientTextView = view.findViewById(R.id.tv_ingredient_item_name);
        }
    }

    @NonNull
    @Override
    public IngredientListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_ingredient_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int quantity = mIngredientList.get(position).getQuantity();
        holder.mQuantityTextView.setText(String.format(Locale.getDefault(),"%d", quantity));

        holder.mMeasureTextView.setText(mIngredientList.get(position).getMeasure());
        holder.mIngredientTextView.setText(mIngredientList.get(position).getIngredient());
    }

    @Override
    public int getItemCount() {
        if (mIngredientList == null) { return 0; }
        return mIngredientList.size();
    }
}
