package com.khedmap.khedmap.Order.Adapters;

import com.khedmap.khedmap.R;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {

    private List<String> slidesUrl;

    @Override
    public int getItemCount() {
        return slidesUrl.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {

        if (slidesUrl.get(0).equals("default"))
            viewHolder.bindImageSlide(R.drawable.slide_default);
        else
            viewHolder.bindImageSlide(slidesUrl.get(position));

    }


    public void setSlides(List<String> slidesUrl) {

        this.slidesUrl = slidesUrl;
    }


    /*@Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        switch (position) {
            case 0:
                viewHolder.bindImageSlide(R.drawable.slide1);
                break;
            case 1:
                viewHolder.bindImageSlide(R.drawable.slide2);
                break;
            case 2:
                viewHolder.bindImageSlide(R.drawable.slide1);
                break;
            case 3:
                viewHolder.bindImageSlide(R.drawable.slide2);
                break;
        }
    }*/
}
