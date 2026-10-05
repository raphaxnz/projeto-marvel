package com.aula.marvel.data.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/** Lê a api_key da Comic Vine do manifest (injetada pelo gradle via local.properties — nunca commitada). */
final class ApiKey {
    private ApiKey() {}

    static String read(Context context) {
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String key = info.metaData != null ? info.metaData.getString("com.aula.marvel.apikey") : null;
            return key == null ? "" : key;
        } catch (Exception e) {
            return "";
        }
    }
}
