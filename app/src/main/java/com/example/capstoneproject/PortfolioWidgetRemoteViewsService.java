package com.example.capstoneproject;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class PortfolioWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PortfolioWidgetRemoteViewsFactory(this.getApplicationContext());
    }
}
