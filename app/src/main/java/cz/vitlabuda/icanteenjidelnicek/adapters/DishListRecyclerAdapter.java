package cz.vitlabuda.icanteenjidelnicek.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cz.vitlabuda.icanteenextractor.FoodMenu;
import cz.vitlabuda.icanteenjidelnicek.R;

public final class DishListRecyclerAdapter extends RecyclerView.Adapter<DishListRecyclerAdapter.DishListRecyclerViewHolder> {
    public static final class DishListRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView dishNameTextView;
        private final TextView dishPlaceTextView;
        private final TextView dishDescriptionTextView;

        public DishListRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            dishNameTextView = itemView.findViewById(R.id.dish_name_textview);
            dishPlaceTextView = itemView.findViewById(R.id.dish_place_textview);
            dishDescriptionTextView = itemView.findViewById(R.id.dish_description_textview);
        }

        public TextView getDishNameTextView() {
            return dishNameTextView;
        }

        public TextView getDishPlaceTextView() {
            return dishPlaceTextView;
        }

        public TextView getDishDescriptionTextView() {
            return dishDescriptionTextView;
        }
    }

    private final ArrayList<FoodMenu.Dish> dishes;

    public DishListRecyclerAdapter(FoodMenu.Day day) {
        this.dishes = day.getDishes();
    }

    @NonNull
    @Override
    public DishListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleritem_dish_list, parent, false);

        return new DishListRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishListRecyclerAdapter.DishListRecyclerViewHolder holder, int position) {
        FoodMenu.Dish dish = dishes.get(position);


        holder.getDishNameTextView().setText(dish.getDishName());

        holder.getDishPlaceTextView().setVisibility(dish.getDishPlace().isEmpty() ? View.GONE : View.VISIBLE);
        holder.getDishPlaceTextView().setText(dish.getDishPlace());

        holder.getDishDescriptionTextView().setText(dish.getDishDescription());
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }
}
