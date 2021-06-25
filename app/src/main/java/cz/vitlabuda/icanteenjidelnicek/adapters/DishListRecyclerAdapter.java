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
