package com.cs160.joleary.catnip;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.List;

/**
 * Created by TonyDai on 3/3/2016.
 */
public class SampleGridPagerAdapter extends FragmentGridPagerAdapter{
    private final Context mContext;
    private List mRows;
    private Page[][] PAGES = new Page[1][4];

    public SampleGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;


        int name = R.string.name;
        int party = R.string.party;
        int icon = R.drawable.ic_abe;
        Page page1 = new Page();
        Page page2 = new Page();
        Page page3 = new Page();
        Page page4 = new Page();

        page1.titleRes = name;
        page1.textRes = party;
        page1.iconRes = icon;

        page2.titleRes = name;
        page2.textRes = party;
        page2.iconRes = icon;

        page3.titleRes = name;
        page3.textRes = party;
        page3.iconRes = icon;

        PAGES[0][0] = page1;
        PAGES[0][1] = page1;
        PAGES[0][2] = page1;
        PAGES[0][3] = page1;
    }

    static final int[] BG_IMAGES = new int[] {
            //R.drawable.debug_background_1
            //R.drawable.debug_background_5
    };

    // A simple container for static data in each page
    private class Page {
        // static resources
        int titleRes;
        int textRes;
        int iconRes;
    }




    @Override
    public Fragment getFragment(int row, int col) {
        Page page = PAGES[row][col];
        String title =
                page.titleRes != 0 ? mContext.getString(page.titleRes) : null;
        String text =
                page.textRes != 0 ? mContext.getString(page.textRes) : null;
        CardFragment fragment = CardFragment.create(title, text, page.iconRes);

        if (col != 3){
            //fragment.setArguments(Bundle);
            return new CustomFragment();
        } else {
            County c =new County();
            return c;
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
        return PAGES.length;
    }

    @Override
    public int getColumnCount(int rowNum) {
        return PAGES[rowNum].length;
    }
}
