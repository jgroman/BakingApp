package cz.jtek.bakingapp.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe;

public class RecipeOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeOverviewAdapter.class.getSimpleName();

    public interface RecipeOverviewOnClickListener {
        void onStepClick(int position);
    }

    // View types
    private static final int VIEW_TYPE_INGREDIENT_HEADER = 1;
    private static final int VIEW_TYPE_INGREDIENT_ITEM = 2;
    private static final int VIEW_TYPE_STEP_HEADER = 3;
    private static final int VIEW_TYPE_STEP_ITEM = 4;


    private Recipe mRecipe;
    private int mIngredientListSize;

    private final RecipeOverviewOnClickListener mClickListener;

    RecipeOverviewAdapter(Recipe recipe, RecipeOverviewOnClickListener clickListener) {
        mRecipe = recipe;
        mIngredientListSize = recipe.getIngredients().size();
        mClickListener = clickListener;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View view) {
            super(view);
        }
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {

        final TextView mIngredientQuantity;
        final TextView mIngredientMeasure;
        final TextView mIngredientName;

        IngredientViewHolder(View view) {
            super(view);
            mIngredientQuantity = view.findViewById(R.id.tv_overview_ingredient_quantity);
            mIngredientMeasure = view.findViewById(R.id.tv_overview_ingredient_measure);
            mIngredientName = view.findViewById(R.id.tv_overview_ingredient_name);
        }
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mStepShortDescription;

        StepViewHolder(View view) {
            super(view);
            mStepShortDescription = view.findViewById(R.id.tv_overview_step_short_description);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPos = getAdapterPosition() - mIngredientListSize - 2;
            mClickListener.onStepClick(itemPos);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_INGREDIENT_HEADER;

        else if (position > 0 && position < mIngredientListSize + 1)
            return VIEW_TYPE_INGREDIENT_ITEM;

        else if (position == mIngredientListSize + 1)
            return VIEW_TYPE_STEP_HEADER;

        else
            return VIEW_TYPE_STEP_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch(viewType) {
            case VIEW_TYPE_INGREDIENT_HEADER:
                view = inflater.inflate(R.layout.item_overview_ingredient_header, parent, false);
                return new HeaderViewHolder(view);

            case VIEW_TYPE_INGREDIENT_ITEM:
                view = inflater.inflate(R.layout.item_overview_ingredient, parent, false);
                return new IngredientViewHolder(view);

            case VIEW_TYPE_STEP_HEADER:
                view = inflater.inflate(R.layout.item_overview_step_header, parent, false);
                return new HeaderViewHolder(view);

            case VIEW_TYPE_STEP_ITEM:
            default:
                view = inflater.inflate(R.layout.item_overview_step, parent, false);
                return new StepViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_INGREDIENT_ITEM:
                IngredientViewHolder ingredientViewHolder = (IngredientViewHolder) holder;
                ingredientViewHolder.mIngredientQuantity.setText(Integer.toString(mRecipe.getIngredients().get(position - 1).getQuantity()));
                ingredientViewHolder.mIngredientMeasure.setText(mRecipe.getIngredients().get(position - 1).getMeasure());
                ingredientViewHolder.mIngredientName.setText(mRecipe.getIngredients().get(position - 1).getIngredient());
                break;

            case VIEW_TYPE_STEP_ITEM:
                StepViewHolder stepViewHolder = (StepViewHolder) holder;
                stepViewHolder.mStepShortDescription.setText(mRecipe.getSteps().get(position - mIngredientListSize - 2).getShortDescription());
                break;

            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipe == null) { return 0; }
        return mIngredientListSize + mRecipe.getSteps().size() + 2;
    }
}
