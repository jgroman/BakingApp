package cz.jtek.bakingapp.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe;

public class RecipeListAdapter
        extends RecyclerView.Adapter<RecipeListAdapter.RecipeGridAdapterViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeListAdapter.class.getSimpleName();

    public interface RecipeGridOnClickHandler {
        void onClick(int itemId);
    }

    private Context mContext;
    private ArrayList<Recipe> mRecipeList;

    private final RecipeGridOnClickHandler mClickHandler;

    public RecipeListAdapter(RecipeGridOnClickHandler clickHandler, Context context, ArrayList<Recipe> recipeList) {
        mClickHandler = clickHandler;
        mContext = context;
        mRecipeList = recipeList;
    }

    public class RecipeGridAdapterViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        final TextView mRecipeNameTextView;

        RecipeGridAdapterViewHolder(View view) {
            super(view);
            mRecipeNameTextView = view.findViewById(R.id.tv_recipe_name);
            // Attaching OnClick listener when creating view
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPos = getAdapterPosition();
            mClickHandler.onClick(itemPos);
        }
    }

    @NonNull
    @Override
    public RecipeGridAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_recipe_recycler, parent, false);
        return new RecipeGridAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecipeGridAdapterViewHolder holder, int position) {
        holder.mRecipeNameTextView.setText(mRecipeList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (mRecipeList == null) { return 0; }
        return mRecipeList.size();
    }


}
