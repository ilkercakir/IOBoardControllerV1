package com.example.ioboardcontrollerv1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RemoteControlActivity extends AppCompatActivity
{
    private NewController newController;
    private Intent myIntent;
    private ViewPager viewPager;
    List<ModuleTab> tablist;
    List<NewModule> RCAmodules;

    private class ExtendedModuleHelper extends ModuleHelper
    {
        List<NewDevice> devs;
        public ExtendedModuleHelper(String hostName, List<NewDevice> devices)
        {
            super(hostName);
            devs = devices;
        }

        @Override
        void onModulesReady(List<NewModule> modules)
        {
            RCAmodules = new ArrayList<>(modules);

            tablist = new ArrayList<ModuleTab>();
            TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
            for (int i = 0; i < modules.size(); i++)
            {
                tabLayout.addTab(tabLayout.newTab().setText(modules.get(i).getModuleText()));

                List<NewDevice> tabdevs = new ArrayList<NewDevice>();
                for (int j = 0; j < devs.size(); j++)
                {
                    if (devs.get(j).getModul()==modules.get(i).getModule())
                    {
                        tabdevs.add(devs.get(j));
                    }
                }

                tablist.add(new ModuleTab(modules.get(i), i, tabdevs));
            }

            //Initializing viewPager
            viewPager = (ViewPager) findViewById(R.id.pager);

            //Creating our pager adapter
            //ModulePager adapter = new ModulePager(getSupportFragmentManager(), tabLayout.getTabCount());
            ModulePager adapter = new ModulePager(getSupportFragmentManager(), tablist);

            //Adding adapter to pager
            viewPager.setAdapter(adapter);

            tabLayout.setupWithViewPager(viewPager);

            //Adding onTabSelectedListener to swipe views
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    private class ExtendedDeviceHelper extends DeviceHelper
    {
        private String hName;
        public ExtendedDeviceHelper(String hostName)
        {
            super(hostName);
            hName = hostName;
        }

        @Override
        void onDevicesReady(List<NewDevice> devices)
        {
            ExtendedModuleHelper emh = new ExtendedModuleHelper(hName, devices);
            emh.getModules();
        }
    }

    public class ImgButtonClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View arg0)
        {
            TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);

            int pos = tabLayout.getSelectedTabPosition();
            ModuleTab mt = tablist.get(pos);
            mt.refresh_devices();
        }
    }

    private class ExtendedNewController extends NewController
    {
        public ExtendedNewController()
        {
            super();
        }

        public ExtendedNewController(Integer id, String name, String hostname, String username, String password, boolean isrunning)
        {
            super(id, name, hostname, username, password, isrunning);
        }

        @Override
        public void onStatusReady()
        {
            if (isRunning())
            {
            }
            else
            {
            }
        }

        @Override
        public void onLoginReady()
        {
            if (isLoggedIn())
            {
                Toast.makeText(RemoteControlActivity.this, this.getName(), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(RemoteControlActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startChangePasswordActivity(NewController c)
    {
        Intent ncaIntent = new Intent(RemoteControlActivity.this, ChangePasswordActivity.class);
        ncaIntent.putExtra("newControllerDefinition", c); // Optional parameters
        //RemoteControlActivity.this.startActivity(myIntent);
        RemoteControlActivity.this.startActivityForResult(ncaIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Handle the logic for the requestCode, resultCode and data returned...
        try
        {
            super.onActivityResult(requestCode, resultCode, data);

            NewController newController = (NewController)data.getSerializableExtra("newControllerDefinition");
            switch (requestCode)
            {
                case 0: // Change Password
                    switch (resultCode)
                    {
                        case RESULT_OK:
                        case RESULT_CANCELED:
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(RemoteControlActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);

        // assigning ID of the toolbar to a variable
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        myIntent = getIntent();
        newController = (NewController)myIntent.getSerializableExtra("newControllerDefinition");

        ExtendedDeviceHelper edh = new ExtendedDeviceHelper(newController.getHostname());
        edh.getDevices(); // async
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_LONG).show();

        setResult(NewControllerActivity.RESULT_CANCELED, myIntent);
        RemoteControlActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change_password)
        {
            startChangePasswordActivity(newController);
            return true;
        }
        if (id == R.id.item_refresh)
        {
            TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
            int pos = tabLayout.getSelectedTabPosition();
            ModuleTab mt = tablist.get(pos);
            mt.refresh_devices();
            return true;
        }
        if (id == R.id.item_login)
        {
            ExtendedNewController enc = new ExtendedNewController(newController.getID(), newController.getName(), newController.getHostname(), newController.getUsername(), newController.getPassword(), newController.isRunning());
            enc.rLogin(); // async
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
