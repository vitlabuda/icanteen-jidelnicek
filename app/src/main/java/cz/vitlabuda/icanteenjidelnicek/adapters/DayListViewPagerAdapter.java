package cz.vitlabuda.icanteenjidelnicek.adapters;

/*
SPDX-License-Identifier: BSD-3-Clause

Copyright (c) 2021 Vít Labuda. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:
 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
    disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other materials provided with the distribution.
 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
    products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

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
