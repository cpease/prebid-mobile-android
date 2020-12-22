/*
 *    Copyright 2020-2021 Prebid.org, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.prebid.mobile;

import android.os.Handler;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.UUID;

public class CacheManager {
    private static final int NATIVE_AD_EXPIRY_TIMEOUT = 300000;
    private static HashMap<String, String> savedValues = new HashMap<>();
    private static HashMap<String, CacheExpiryListener> cacheExpiryListenerMap = new HashMap<>();
    private static Handler handler = new Handler();

    public static String save(String content) {
        if (!TextUtils.isEmpty(content)) {
            final String cacheId = "Prebid_" + UUID.randomUUID().toString();
            savedValues.put(cacheId, content);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (cacheExpiryListenerMap.containsKey(cacheId)) {
                        cacheExpiryListenerMap.remove(cacheId).onCacheExpired();
                    }
                    savedValues.remove(cacheId);
                }
            }, NATIVE_AD_EXPIRY_TIMEOUT);
            return cacheId;
        } else {
            return null;
        }
    }

    public static boolean isValid(String cacheId) {
        return savedValues.keySet().contains(cacheId);
    }

    protected static String get(String cacheId) {
        return savedValues.remove(cacheId);
    }

    protected static void registerCacheExpiryListener(String cacheId, CacheExpiryListener expiryListener) {
        cacheExpiryListenerMap.put(cacheId, expiryListener);
    }

    interface CacheExpiryListener {
        void onCacheExpired();
    }
}