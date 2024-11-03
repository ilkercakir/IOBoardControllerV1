package com.example.ioboardcontrollerv1;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class ModuleTab extends Fragment
{
    public NewModule newmodule;
    private Integer index;
    private List<NewDevice> devices;
    public RecyclerView recyclerView;
    public DeviceRecyclerViewAdapter recyclerAdapter;

    public ModuleTab(NewModule nm, Integer i, List<NewDevice> tabDevices)
    {
        newmodule = nm;
        index = i;
        devices = tabDevices;
    }

    //Overridden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.module_tab, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDevices);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerAdapter = new DeviceRecyclerViewAdapter(getActivity(), devices);
        recyclerView.setAdapter(recyclerAdapter);
        return view;
    }

    public void refresh_devices()
    {
        for(int i=0;i<recyclerAdapter.getItemCount();i++)
        {
            NewDevice d = recyclerAdapter.mData.get(i);
            ExtendedNewDevice xnd = new ExtendedNewDevice(d);
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                View view = holder.itemView.findViewWithTag("device_" + d.getModul() + "_" + d.getDevid());
                xnd.getDeviceValue(view);
            }
        }
    }

    // stores and recycles views as they are scrolled off screen
    private class rViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewDtext, textViewCatxt;
        ImageView imageViewDevice;
        LinearLayout layoutControl;

        public rViewHolder(View itemView)
        {
            super(itemView);
            textViewDtext = itemView.findViewById(R.id.textViewDtext);
            textViewCatxt = itemView.findViewById(R.id.textViewCatxt);
            imageViewDevice = itemView.findViewById(R.id.imageViewDevice);
            layoutControl = itemView.findViewById(R.id.layoutControl);
        }
    }

    private class ExtendedNewDevice extends NewDevice
    {
        ExtendedNewDevice(NewDevice d)
        {
            super(d.getHostname(), d.getService(), d.getModul(), d.getDevid(), d.getChnnl(), d.getDtype(), d.getNumstates(), d.getInitval(), d.getAuthorizationLevel(), d.getDtext(), d.getDttext(), d.getCateg(), d.getCatxt(), d.getDicon(), d.getDticon(), d.getValue());
        }

        @Override
        void onDeviceValueReady(NewDevice dev, View view)
        {
            if (view instanceof Switch)
            {
                ((Switch)view).setChecked(dev.getValue()==1);
            }
            else if (view instanceof Spinner)
            {
                ((Spinner)view).setSelection(dev.getValue(), true);
            }
            else if (view instanceof Button)
            {
            }
        }
    }

    private class recyclerViewItemClickListener implements View.OnClickListener
    {
        NewDevice d;
        View view;

        recyclerViewItemClickListener(NewDevice d, View view)
        {
            this.d = d;
            this.view = view;
        }

        @Override
        public void onClick(View arg0)
        {
            ModuleTab.ExtendedNewDevice xnd = new ModuleTab.ExtendedNewDevice(d);
            xnd.getDeviceValue(view); // async
            //Toast.makeText(getApplicationContext(), d.getDtext(), Toast.LENGTH_SHORT).show();
        }
    }

    private class myCheckedChangedListener implements CompoundButton.OnCheckedChangeListener
    {
        NewDevice d;
        View view;

        public void setDevice(NewDevice d, View view)
        {
            this.d = d;
            this.view = view;
        }

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1)
        {
            int value = (arg1?1:0); // ischecked
            d.setValue(value);
            d.setDeviceValue(view);
            //Toast.makeText(getApplicationContext(), Integer.toString(d.getDevid()), Toast.LENGTH_LONG).show();
        }
    }

    private class myItemSelectedListener implements Spinner.OnItemSelectedListener
    {
        NewDevice d;
        View view;

        public void setDevice(NewDevice d, View view)
        {
            this.d = d;
            this.view = view;
        }

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id)
        {
            //String sel = arg0.getItemAtPosition(pos).toString();
            int value = pos;
            d.setValue(value);
            d.setDeviceValue(view);
            //Toast.makeText(getApplicationContext(), new Integer(value).toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
            // TODO Auto-generated method stub
        }
    }

    private class myClickListener implements View.OnClickListener
    {
        NewDevice d;
        View view;

        public void setDevice(NewDevice d, View view)
        {
            this.d = d;
            this.view = view;
        }

        @Override
        public void onClick(View arg0)
        {
            int value = d.getInitval();
            d.setValue(value);
            d.setDeviceValue(view);
            //Toast.makeText(getApplicationContext(), new Integer(value).toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<ModuleTab.rViewHolder>
    {
        private List<NewDevice> mData;
        private LayoutInflater mInflater;

        // data is passed into the constructor
        public DeviceRecyclerViewAdapter(Context context, List<NewDevice> data)
        {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public ModuleTab.rViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = mInflater.inflate(R.layout.device_row, parent, false);
            return(new ModuleTab.rViewHolder(view));
        }

        // binds the data to Views in each row
        @Override
        public void onBindViewHolder(ModuleTab.rViewHolder holder, int position)
        {
            ModuleTab.recyclerViewItemClickListener listener;
            Switch onOffSwitch;
            Spinner spinner;
            Button button;

            NewDevice d = this.mData.get(position);
            ExtendedNewDevice xnd = new ExtendedNewDevice(d);

            holder.textViewDtext.setText(d.getDtext());
            String catxt = d.getCatxt() + ", " + d.getDttext();
            holder.textViewCatxt.setText(catxt);
            d.loadImage(holder.imageViewDevice, d.getDicon());

            int childPos = holder.layoutControl.getChildCount();
            while(childPos>0)
            {
                childPos--;
                View view = holder.layoutControl.getChildAt(childPos);
                holder.layoutControl.removeView(view);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            switch (d.getDtype()) {
                case 1:
                case 5:
                case 7:
                    onOffSwitch = new Switch(getActivity());
                    //onOffSwitch.setId(); //onOffSwitch.generateViewId();
                    onOffSwitch.setTag("device_" + d.getModul() + "_" + d.getDevid());
                    onOffSwitch.setText("");
                    onOffSwitch.setTextOn("ON");
                    onOffSwitch.setTextOff("OFF");
                    onOffSwitch.setEnabled(d.getCateg().equals("A") && d.getAuthorizationLevel().equals("W"));
                    onOffSwitch.setLayoutParams(params);

                    myCheckedChangedListener ccl = new myCheckedChangedListener();
                    ccl.setDevice(d, onOffSwitch);
                    onOffSwitch.setOnCheckedChangeListener(ccl);

                    holder.layoutControl.addView(onOffSwitch);
                    xnd.getDeviceValue((View)onOffSwitch); // async

                    listener = new ModuleTab.recyclerViewItemClickListener(d, (View)onOffSwitch);
                    holder.itemView.setOnClickListener(listener);
                    break;
                case 2:
                case 6:
                    spinner = new Spinner(getActivity());
                    //spinner.setId(); //spinner.generateViewId();
                    spinner.setTag("device_" + d.getModul() + "_" + d.getDevid());
                    String[] items = new String[d.getNumstates()];
                    for(int j=0;j<d.getNumstates();j++)
                        items[j] = Integer.toString(j);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                    spinner.setAdapter(adapter);
                    spinner.setEnabled(d.getCateg().equals("A") && d.getAuthorizationLevel().equals("W"));
                    spinner.setLayoutParams(params);

                    myItemSelectedListener isl = new myItemSelectedListener();
                    isl.setDevice(d, (View)spinner);
                    spinner.setOnItemSelectedListener(isl);

                    holder.layoutControl.addView(spinner);
                    xnd.getDeviceValue((View)spinner); // async

                    listener = new ModuleTab.recyclerViewItemClickListener(d, (View)spinner);
                    holder.itemView.setOnClickListener(listener);
                    break;
                case 3:
                    button = new Button(getActivity());
                    //button.setId(); //button.generateViewId();
                    button.setTag("device_" + d.getModul() + "_" + d.getDevid());
                    button.setText("Press");
                    button.setEnabled(d.getAuthorizationLevel().equals("W"));
                    button.setLayoutParams(params);

                    myClickListener cl = new myClickListener();
                    cl.setDevice(d, (View)button);
                    button.setOnClickListener(cl);

                    holder.layoutControl.addView(button);
                    xnd.getDeviceValue((View)button); // async

                    listener = new recyclerViewItemClickListener(d, (View)button);
                    holder.itemView.setOnClickListener(listener);
                    break;
                case 4:
                    break;
            }
        }

        // total number of rows
        @Override
        public int getItemCount()
        {
            return mData.size();
        }
    }
}
