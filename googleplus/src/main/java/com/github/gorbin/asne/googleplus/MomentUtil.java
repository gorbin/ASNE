/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.github.gorbin.asne.googleplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * For Google PLUS
 * Handles creation of moment JSON.
 */
public class MomentUtil {

    /**
     * A mapping of moment type to target URL.
     */
    public static final HashMap<String, String> MOMENT_TYPES;

    /**
     * A list of moment target types.
     */
    public static final ArrayList<String> MOMENT_LIST;
    public static final String[] ACTIONS;

    static {
        MOMENT_TYPES = new HashMap<String, String>(9);
        MOMENT_TYPES.put("AddActivity",
                "https://developers.google.com/+/plugins/snippet/examples/thing");
        MOMENT_TYPES.put("BuyActivity",
                "https://developers.google.com/+/plugins/snippet/examples/a-book");
        MOMENT_TYPES.put("CheckInActivity",
                "https://developers.google.com/+/plugins/snippet/examples/place");
        MOMENT_TYPES.put("CommentActivity",
                "https://developers.google.com/+/plugins/snippet/examples/blog-entry");
        MOMENT_TYPES.put("CreateActivity",
                "https://developers.google.com/+/plugins/snippet/examples/photo");
        MOMENT_TYPES.put("ListenActivity",
                "https://developers.google.com/+/plugins/snippet/examples/song");
        MOMENT_TYPES.put("ReserveActivity",
                "https://developers.google.com/+/plugins/snippet/examples/restaurant");
        MOMENT_TYPES.put("ReviewActivity",
                "https://developers.google.com/+/plugins/snippet/examples/widget");

        MOMENT_LIST = new ArrayList<String>(MomentUtil.MOMENT_TYPES.keySet());
        Collections.sort(MOMENT_LIST);

        ACTIONS = MOMENT_TYPES.keySet().toArray(new String[0]);
        int count = ACTIONS.length;
        for (int i = 0; i < count; i++) {
            ACTIONS[i] = "http://schemas.google.com/" + ACTIONS[i];
        }
    }
}
