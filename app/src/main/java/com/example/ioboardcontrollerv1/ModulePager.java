package com.example.ioboardcontrollerv1;

import android.widget.TextView;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;

import java.util.List;

public class ModulePager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    List<ModuleTab> mt;

    //Constructor to the class
    public ModulePager(FragmentManager fm, List<ModuleTab> moduleTabs)
    {
        super(fm);
        mt = moduleTabs;
        this.tabCount = moduleTabs.size();
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position)
    {
        ModuleTab tab;

        return mt.get(position);
    }

    //Overridden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        super.getPageTitle(position);
        return mt.get(position).newmodule.getModuleText();
    }
}
