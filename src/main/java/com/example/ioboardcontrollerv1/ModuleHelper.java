package com.example.ioboardcontrollerv1;

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

public class ModuleHelper {
    List<NewModule> modules;
    String hostName;
    String service = "";

    public ModuleHelper(String hostName)
    {
        this.modules = new ArrayList<NewModule>();
        this.hostName = hostName;
    }

    void onModulesReady(List<NewModule> modules) {}

    private class RequestTask_getModules extends AsyncTasks<String, String>
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
                url = new URL(makeURL("modules"));
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

            List<NewModule> moduleeList = new ArrayList<NewModule>();
            // Parse
            try {
                JSONObject json = new JSONObject(result);
                JSONArray mods = json.getJSONArray("modules");
                for (int i = 0; i < mods.length(); i++)
                {
                    JSONObject module = mods.getJSONObject(i);
                    NewModule mod = new NewModule(module.getInt("modul"), module.getString("modtx"), module.getInt("mtype"), module.getString("categ"), module.getString("catxt"));
                    modules.add(mod);
                }
            }
            catch (JSONException j)
            {
                stacktraceString = getStackTrace(j);
                success = false;
            }

            onModulesReady(modules);
        }
    }

    public void getModules()
    {
        ModuleHelper.RequestTask_getModules asyncTask = new ModuleHelper.RequestTask_getModules();
        asyncTask.execute();
    }
}
