package com.example.justsavings.TABS;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.example.justsavings.MAIN_ACTIVITY.Goal;

public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private String name;
    private int url;
    private  String required_money;
    private int id;
    private String saved_money;

    PageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;

    }

    public void setGoal(Goal goal) {
        this.name = goal.name;
        this.url = goal.url;
        this.required_money = goal.required_money;
        this.id= goal.id;
        this.saved_money=goal.amount_collected;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                GoalsFragment fragmentB = new GoalsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Name", name);
                bundle.putString("Money", required_money);
                bundle.putInt("Image", url);
                bundle.putInt("ID",id);
                bundle.putString("Saved",saved_money);
                fragmentB.setArguments(bundle);

                return fragmentB;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    public void setName(String name) {
        this.name = name;
    }
}