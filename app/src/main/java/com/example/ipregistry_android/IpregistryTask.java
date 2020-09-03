package com.example.ipregistry_android;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import co.ipregistry.api.client.IpregistryClient;
import co.ipregistry.api.client.exceptions.ApiException;
import co.ipregistry.api.client.exceptions.ClientException;
import co.ipregistry.api.client.model.RequesterIpInfo;

public class IpregistryTask extends AsyncTask<Void, Void, RequesterIpInfo> {

    private final WeakReference<Activity> activity;

    private final IpregistryClient ipregistry;

    private Throwable throwable;


    public IpregistryTask(Activity activity) {
        super();
        this.activity = new WeakReference<>(activity);
        this.ipregistry = new IpregistryClient("tryout");
    }

    protected RequesterIpInfo doInBackground(Void... empty) {
        try {
            return ipregistry.lookup();
        } catch (ApiException | ClientException e) {
            throwable = e;
            return null;
        }
    }

    protected void onProgressUpdate(Void... progress) {
        // do nothing
    }

    protected void onPostExecute(RequesterIpInfo ipInfo) {
        final Activity activity = this.activity.get();

        if (activity == null || activity.isFinishing()) {
            return;
        }

        TextView ipinfoTextView = (TextView) activity.findViewById(R.id.ipinfo);

        if (throwable == null) {
            StringBuilder message = new StringBuilder(ipInfo.getIp());
            message.append('\n');
            message.append(ipInfo.getLocation().getCountry().getName());
            message.append('\n');

            if (ipInfo.getLocation().getRegion().getName() != null) {
                message.append(ipInfo.getLocation().getRegion().getName());
                message.append('\n');
            }

            if (ipInfo.getLocation().getCity() != null) {
                message.append(ipInfo.getLocation().getCity());
                message.append('\n');
            }

            ipinfoTextView.setText(message.toString());
        } else {
            ipinfoTextView.setText("Error while getting IP info: " + throwable.getMessage());
        }
    }

}