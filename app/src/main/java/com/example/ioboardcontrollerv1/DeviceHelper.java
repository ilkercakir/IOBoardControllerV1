package com.example.ioboardcontrollerv1;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DeviceHelper
{
    List<NewDevice> devices;
    String hostName;
    String services[] = {"void", "channel", "channel", "pulse", "void", "channel", "channel", "bit"};

    public DeviceHelper(String hostName)
    {
        this.devices = new ArrayList<NewDevice>();
        this.hostName = hostName;
    }

    void addToList(List<NewDevice> deviceList) { this.devices.addAll(deviceList); }

    void onDevicesReady(List<NewDevice> devices) {}

    private class RequestTask_getDevices extends AsyncTasks<String, String>
    {
        String responseString = "", stacktraceString = "";
        boolean success = true;

        private String getStackTrace(final Throwable throwable)
        {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            throwable.printStackTrace(pw);
            return sw.getBuffer().toString();
        }

        private String makeURL(String service)
        {
            String url = "http://" + hostName + ":8080/IOBoardV1/ControllerConsole?" + service;

            return(url);
        }

        @Override
        protected String doInBackground(String... uri)
        {
            URL url = null;
            try {
                url = new URL(makeURL("devices"));
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
                //e.printStackTrace();
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

            List<NewDevice> deviceList = new ArrayList<NewDevice>();
            // Parse
            try {
                JSONObject json = new JSONObject(result);
                JSONArray devs = json.getJSONArray("devices");
                for (int i = 0; i < devs.length(); i++)
                {
                    JSONObject device = devs.getJSONObject(i);
                    String service = services[device.getInt("dtype")];
                    NewDevice dev = new NewDevice(hostName, service, device.getInt("modul"), device.getInt("devid"), device.getInt("chnnl"), device.getInt("dtype"), device.getInt("numstates"), device.getInt("initval"), device.getString("level"), device.getString("dtext"), device.getString("dttext"), device.getString("categ"), device.getString("catxt"), device.getString("dicon"), device.getString("dticon"), 0);
                    deviceList.add(dev);
                }
                addToList(deviceList);
            }
            catch (JSONException j)
            {
                stacktraceString = getStackTrace(j);
                success = false;
            }

            onDevicesReady(devices);
        }
    }

    public void getDevices()
    {
        RequestTask_getDevices asyncTask = new RequestTask_getDevices();
        asyncTask.execute();
    }
}
