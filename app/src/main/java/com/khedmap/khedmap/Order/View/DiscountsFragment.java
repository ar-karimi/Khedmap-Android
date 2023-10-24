package com.khedmap.khedmap.Order.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.viewanimator.ViewAnimator;
import com.khedmap.khedmap.R;


public class DiscountsFragment extends Fragment {

    protected View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discounts, container, false);

/*
        view.findViewById(R.id.sub_m_daneshjooii1_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.qut.ac.ir/fa/studentsdepartment/disciplinary_committee"));
                startActivity(browserIntent);
            }
        });
*/


        //to set animation to titleDiscounts
        ViewAnimator
                .animate(view.findViewById(R.id.text_discounts))
                .fadeIn()
                .duration(1000)
                .repeatMode(ViewAnimator.RESTART)
                .repeatCount(ViewAnimator.INFINITE)
                .start();


        mView = view;     //set this like suggestionsFragment////////////////////////////////////// !!!!!!!!!!!!!!!!!!!!!!!!!

        return view;
    }

    public static DiscountsFragment newInstance() {

        Bundle args = new Bundle();

        DiscountsFragment fragment = new DiscountsFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
