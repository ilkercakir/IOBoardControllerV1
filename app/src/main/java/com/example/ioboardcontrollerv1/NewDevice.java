package com.example.ioboardcontrollerv1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewDevice implements Serializable
{
    private Integer modul, devid, chnnl, dtype, numstates, initval;
    private String level, dtext, dttext, categ, catxt, dicon, dticon;
    private String hostName, service;
    private Integer value;
    //transient RecyclerView.ViewHolder recyclerViewHolder;

    public NewDevice(String hostname, String service, Integer modul, Integer devid, Integer chnnl, Integer dtype, Integer numstates, Integer initval, String level, String dtext, String dttext, String categ, String catxt, String dicon, String dticon, Integer value)
    {
        this.hostName = hostname;
        this.service = service;
        this.modul = modul;
        this.devid = devid;
        this.chnnl = chnnl;
        this.dtype = dtype;
        this.numstates = numstates;
        this.initval = initval;
        this.level = level;
        this.dtext = dtext;
        this.dttext = dttext;
        this.categ = categ;
        this.catxt = catxt;
        this.dicon = dicon;
        this.dticon = dticon;
        this.value = value;
    }

    public String getHostname() { return (this.hostName); }
    public String getService() { return (this.service); }
    public Integer getModul()
    {
        return (this.modul);
    }
    public Integer getDevid()
    {
        return (this.devid);
    }
    public Integer getChnnl()
    {
        return (this.chnnl);
    }
    public Integer getDtype()
    {
        return (this.dtype);
    }
    public Integer getNumstates()
    {
        return (this.numstates);
    }
    public Integer getInitval()
    {
        return (this.initval);
    }
    public String getAuthorizationLevel()
    {
        return (this.level);
    }
    public String getDtext()
    {
        return (this.dtext);
    }
    public String getDttext()
    {
        return (this.dttext);
    }
    public String getCateg()
    {
        return (this.categ);
    }
    public String getCatxt()
    {
        return (this.catxt);
    }
    public String getDicon() { return (this.dicon); }
    public String getDticon()
    {
        return (this.dticon);
    }
    public Integer getValue()
    {
        return (this.value);
    }
    public void setValue(int value)
    {
        this.value = value;
    }

    void onDeviceValueReady(NewDevice dev, View view) {}

    private class RequestTask_getDeviceValue extends AsyncTasks<String, String>
    {
        String responseString = "", stacktraceString = "";
        boolean success = true;
        View view;
        NewDevice dev;

        public void setView(NewDevice dev, View view)
        {
            this.dev = dev;
            this.view = view;
        }

        private String getStackTrace(final Throwable throwable)
        {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            throwable.printStackTrace(pw);
            return sw.getBuffer().toString();
        }

        @Override
        protected String doInBackground(String... uri)
        {
            try {
                URL url = new URL(uri[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);

                // Response
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader inputStream = new BufferedReader(isr);

                StringBuffer sb = new StringBuffer();
                String str;
                while ((str = inputStream.readLine()) != null) {
                    sb.append(str);
                }
                inputStream.close();
                isr.close();
                is.close();

                responseString = sb.toString();

                urlConnection.disconnect();
            }
            catch (IOException e)
            {
                stacktraceString = getStackTrace(e);
                success = false;
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            //Do anything with response..

            value = 0;
            // Parse
            try
            {
                JSONObject json = new JSONObject(result);
                value = json.getInt("value");
            }
            catch (JSONException j)
            {
                stacktraceString = getStackTrace(j);
                success = false;
            }

            onDeviceValueReady(dev, view);
        }
    }

    private String makeURL(String service, String parameters)
    {
        String url = "http://" + hostName + ":8080/IOBoardV1/ControllerConsole?" + service + parameters;

        return(url);
    }

    private class DownloadImageTask extends AsyncTasks<String, String>
    {
        ImageView bmImage;
        Bitmap mIcon = null, bitmap = null;
        String stacktraceString = "";
        boolean success = true;

        public DownloadImageTask(ImageView bmImage)
        {
            this.bmImage = bmImage;
        }

        private String getStackTrace(final Throwable throwable)
        {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            throwable.printStackTrace(pw);
            return sw.getBuffer().toString();
        }

        protected String doInBackground(String... urls)
        {
            String url = "http://" + hostName + ":8080/IOBoardV1/images/" + urls[0];
            try
            {
                InputStream in = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                int width = bmImage.getWidth();
                int height = bmImage.getHeight();
                if ((width <= 0) || (height <= 0))
                {
                    Context c = MainActivity.getMainContext();
                    Bitmap ri = BitmapFactory.decodeResource(c.getResources(), R.drawable.controller);
                    width = ri.getWidth();
                    height = ri.getHeight();
                }
                mIcon = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }
            catch (Exception e)
            {
                stacktraceString = getStackTrace(e);
                success = false;
            }
            return "";
        }

        protected void onPostExecute(String result)
        {
            bmImage.setImageBitmap(mIcon);
        }
    }

    public void loadImage(ImageView imageView, String strIcon)
    {
        DownloadImageTask task = new DownloadImageTask(imageView);
        task.execute(strIcon);
    }

    public void getDeviceValue(View view)
    {
        String url = makeURL(service, "&id="+chnnl+"&modul="+modul+"&devid="+devid);

        //recyclerViewHolder = holder;
        RequestTask_getDeviceValue asyncTask = new RequestTask_getDeviceValue();
        asyncTask.setView(this, view);
        asyncTask.execute(url);
    }

    public void setDeviceValue(View view)
    {
        String url =  makeURL(service, "&id="+chnnl+"&modul="+modul+"&devid="+devid+"&value="+value);

        //recyclerViewHolder = holder;
        RequestTask_getDeviceValue asyncTask = new RequestTask_getDeviceValue();
        asyncTask.setView(this, view);
        asyncTask.execute(url);
    }

}
