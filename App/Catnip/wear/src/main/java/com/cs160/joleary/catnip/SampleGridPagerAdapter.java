package com.cs160.joleary.catnip;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.List;

/**
 * Created by TonyDai on 3/3/2016.
 */
public class SampleGridPagerAdapter extends FragmentGridPagerAdapter{
    private final Context mContext;
    private List mRows;

    public SampleGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;

        int name = R.string.name;
        int party = R.string.party;
        int icon = R.drawable.ic_abe;
    }


    @Override
    public Fragment getFragment(int row, int col) {

        if (col != 3){
            //fragment.setArguments(Bundle);
            CustomFragment cus = new CustomFragment();
            Bundle bun = new Bundle();
            bun.putInt("Who",col);

            cus.setArguments(bun);
            return cus;
        } else {
            County co =new County();
            return co;
        }

        // Advanced settings (card gravity, card expansion/scrolling)
        //fragment.setCardGravity(page.cardGravity);
        //fragment.setExpansionEnabled(page.expansionEnabled);
        //fragment.setExpansionDirection(page.expansionDirection);
        //fragment.setExpansionFactor(page.expansionFactor);
        //return fragment;
    }


    @Override
    public int getRowCount() {

        return 1;

    }

    @Override
    public int getColumnCount(int rowNum) {

        return 4;
    }
}
