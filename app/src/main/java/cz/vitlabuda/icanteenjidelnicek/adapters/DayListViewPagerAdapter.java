package cz.vitlabuda.icanteenjidelnicek.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import cz.vitlabuda.icanteenextractor.FoodMenu;
import cz.vitlabuda.icanteenjidelnicek.R;

public final class DayListViewPagerAdapter extends RecyclerView.Adapter<DayListViewPagerAdapter.DayListViewPagerViewHolder> {
    public static final class DayListViewPagerViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final RecyclerView inDayListRecyclerView;

        public DayListViewPagerViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            this.inDayListRecyclerView = itemView.findViewById(R.id.in_day_list_recyclerview);
        }

        public Context getContext() {
            return context;
        }

        public RecyclerView getInDayListRecyclerView() {
            return inDayListRecyclerView;
        }
    }

    private final ArrayList<FoodMenu.Day> days;

    public DayListViewPagerAdapter(FoodMenu foodMenu) {
        this.days = foodMenu.getDays();
    }

    @NonNull
    @Override
    public DayListViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewpageritem_day_list, parent, false);

        return new DayListViewPagerViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull DayListViewPagerAdapter.DayListViewPagerViewHolder holder, int position) {
        FoodMenu.Day day = days.get(position);
        RecyclerView inDayListRecyclerView = holder.getInDayListRecyclerView();

        inDayListRecyclerView.setAdapter(new DishListRecyclerAdapter(day));
        inDayListRecyclerView.setLayoutManager(new LinearLayoutManager(holder.getContext()));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public Date getDateOfDay(int position) {
        return days.get(position).getDate();
    }
}
