package cz.vitlabuda.icanteenjidelnicek.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import cz.vitlabuda.icanteenjidelnicek.CanteenList;
import cz.vitlabuda.icanteenjidelnicek.FavoriteCanteenManager;
import cz.vitlabuda.icanteenjidelnicek.MainActivity;
import cz.vitlabuda.icanteenjidelnicek.R;

public final class CanteenListRecyclerAdapter extends RecyclerView.Adapter<CanteenListRecyclerAdapter.CanteenListRecyclerViewHolder> {
    public interface CanteenRecyclerActionListener {
        void onClick(CanteenListRecyclerAdapter adapter, CanteenList.Canteen canteen, int position);
        void onSwipe(CanteenListRecyclerAdapter adapter, CanteenList.Canteen canteen, int position);
        void onSetAsFavoriteClick(CanteenListRecyclerAdapter adapter, CanteenList.Canteen canteen, int position);
    }

    public final class CanteenListRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView urlTextView;
        private final ImageView setAsFavoriteImageView;

        public CanteenListRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nameTextView = itemView.findViewById(R.id.recycleritem_canteen_list_name);
            this.urlTextView = itemView.findViewById(R.id.recycleritem_canteen_list_url);
            this.setAsFavoriteImageView = itemView.findViewById(R.id.recycleritem_canteen_list_set_as_favorite);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position < 0)
                    return;

                canteenRecyclerActionListener.onClick(CanteenListRecyclerAdapter.this, canteenList.getCanteens()[position], position);
            });

            setAsFavoriteImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position < 0)
                    return;

                canteenRecyclerActionListener.onSetAsFavoriteClick(CanteenListRecyclerAdapter.this, canteenList.getCanteens()[position], position);
            });
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getUrlTextView() {
            return urlTextView;
        }

        public ImageView getSetAsFavoriteImageView() {
            return setAsFavoriteImageView;
        }
    }

    private final Context context;
    private final RecyclerView canteenListRecyclerView;
    private final LinearLayout noCanteensAddedLinearLayout;
    private CanteenList canteenList;
    private final CanteenRecyclerActionListener canteenRecyclerActionListener;

    public CanteenListRecyclerAdapter(MainActivity activity, CanteenRecyclerActionListener canteenRecyclerActionListener) {
        this.context = activity;

        this.canteenListRecyclerView = activity.findViewById(R.id.canteen_list_recyclerview);
        this.noCanteensAddedLinearLayout = activity.findViewById(R.id.no_canteens_added_linear_layout);

        this.canteenList = new CanteenList(activity);
        this.canteenRecyclerActionListener = canteenRecyclerActionListener;

        attachItemTouchHelperToRecyclerView();
        hideRecyclerViewIfEmpty();
    }

    private void attachItemTouchHelperToRecyclerView() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if(position < 0)
                    return;

                canteenRecyclerActionListener.onSwipe(CanteenListRecyclerAdapter.this, canteenList.getCanteens()[position], position);
            }
        }).attachToRecyclerView(canteenListRecyclerView);
    }

    @NonNull
    @Override
    public CanteenListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleritem_canteen_list, parent, false);

        return new CanteenListRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CanteenListRecyclerAdapter.CanteenListRecyclerViewHolder holder, int position) {
        CanteenList.Canteen canteen = canteenList.getCanteens()[position];

        int setAsFavoriteDrawableResource = (new FavoriteCanteenManager(context).isCurrentFavoriteCanteenID(canteen.getId()) ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        Drawable setAsFavoriteDrawable = ContextCompat.getDrawable(context, setAsFavoriteDrawableResource);

        holder.getNameTextView().setText(canteen.getName());
        holder.getUrlTextView().setText(canteen.getUrl().toString());
        holder.getSetAsFavoriteImageView().setImageDrawable(setAsFavoriteDrawable);
    }

    @Override
    public int getItemCount() {
        return canteenList.getCanteens().length;
    }

    public void addCanteen(String name, String url, byte[] initialCache) {
        canteenList = canteenList.addCanteen(name, url, initialCache);

        canteenListMightHaveBeenChanged();
    }

    public void removeCanteen(int position) {
        int id = canteenList.getCanteens()[position].getId();

        FavoriteCanteenManager favoriteCanteenManager = new FavoriteCanteenManager(context);
        if(favoriteCanteenManager.isCurrentFavoriteCanteenID(id))
            favoriteCanteenManager.setFavoriteCanteenID(-1);

        canteenList = canteenList.removeCanteen(id);

        canteenListMightHaveBeenChanged();
    }

    public void canteenListMightHaveBeenChanged() {
        notifyDataSetChanged();
        hideRecyclerViewIfEmpty();
    }

    private void hideRecyclerViewIfEmpty() {
        if(canteenList.getCanteens().length == 0) {
            canteenListRecyclerView.setVisibility(View.GONE);
            noCanteensAddedLinearLayout.setVisibility(View.VISIBLE);
        } else {
            canteenListRecyclerView.setVisibility(View.VISIBLE);
            noCanteensAddedLinearLayout.setVisibility(View.GONE);
        }
    }
}
