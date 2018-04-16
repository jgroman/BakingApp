package cz.jtek.bakingapp.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe;

public class RecipeListAdapter
        extends RecyclerView.Adapter<RecipeListAdapter.RecipeListAdapterViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeListAdapter.class.getSimpleName();

    public interface RecipeListOnClickListener {
        void onClick(int itemId);
    }

    private Context mContext;
    private ArrayList<Recipe> mRecipeList;
    private final RecipeListOnClickListener mClickListener;

    RecipeListAdapter(Context context, ArrayList<Recipe> recipeList, RecipeListOnClickListener clickListener) {
        mContext = context;
        mRecipeList = recipeList;
        mClickListener = clickListener;
    }

    public class RecipeListAdapterViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        final TextView mRecipeNameTextView;
        final TextView mRecipeServingsTextView;

        /**
         * View holder constructor
         * Sets member variables and attaches local OnClick listener when creating views
         *
         * @param view View to be hold in holder
         */
        RecipeListAdapterViewHolder(View view) {
            super(view);
            mRecipeNameTextView = view.findViewById(R.id.tv_recipe_item_name);
            mRecipeServingsTextView = view.findViewById(R.id.tv_recipe_item_servings);
            view.setOnClickListener(this);
        }

        /**
         * Local OnClick listener
         * Sends OnClick event of clicked item via interface up to registered click listener
         * @param view View that was clicked on
         */
        @Override
        public void onClick(View view) {
            int itemPos = getAdapterPosition();
            mClickListener.onClick(itemPos);
        }
    }

    @NonNull
    @Override
    public RecipeListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_recipe_list, parent, false);
        return new RecipeListAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapterViewHolder holder, int position) {
        holder.mRecipeNameTextView.setText(mRecipeList.get(position).getName());

        int servings = mRecipeList.get(position).getServings();
        if (servings > 0) {
            holder.mRecipeServingsTextView.setText(String.format(Locale.getDefault(),mContext.getString(R.string.recipe_servings) + ": %d",servings));
        }

    }

    @Override
    public int getItemCount() {
        if (mRecipeList == null) { return 0; }
        return mRecipeList.size();
    }


}
