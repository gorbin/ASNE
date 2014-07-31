/**
 * Copyright 2013 Joan Zapata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * It uses FontAwesome font, licensed under OFL 1.1, which is compatible
 * with this library's license.
 *
 *     http://scripts.sil.org/cms/scripts/render_download.php?format=file&media_id=OFL_plaintext&filename=OFL.txt
 */
package com.joanzapata.android.iconify;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spanned;
import android.widget.TextView;

import java.io.IOException;

import static android.text.Html.fromHtml;
import static android.text.Html.toHtml;
import static com.joanzapata.android.iconify.Utils.replaceIcons;
import static com.joanzapata.android.iconify.Utils.resourceToFile;
import static java.lang.String.valueOf;

public final class Iconify {

    private static final String TTF_FILE = "firsttry.ttf";

    public static final String TAG = Iconify.class.getSimpleName();

    private static Typeface typeface = null;

    private Iconify() {
        // Prevent instantiation
    }

    /** Transform the given TextViews replacing {icon_xxx} texts with icons. */
    public static final void addIcons(TextView... textViews) {
        for (TextView textView : textViews) {
            textView.setTypeface(getTypeface(textView.getContext()));
            textView.setText(compute(textView.getText()));
        }
    }

    public static CharSequence compute(CharSequence charSequence) {
        if (charSequence instanceof Spanned) {
            String text = toHtml((Spanned) charSequence);
            return fromHtml(replaceIcons(new StringBuilder((text))).toString());
        }
        String text = charSequence.toString();
        return replaceIcons(new StringBuilder(text));
    }

    public static final void setIcon(TextView textView, IconValue value) {
        textView.setTypeface(getTypeface(textView.getContext()));
        textView.setText(valueOf(value.character));
    }

    /**
     * The typeface that contains FontAwesome icons.
     * @return the typeface, or null if something goes wrong.
     */
    public static final Typeface getTypeface(Context context) {
        if (typeface == null) {
            try {
                typeface = Typeface.createFromFile(resourceToFile(context, TTF_FILE));
            } catch (IOException e) {
                return null;
            }
        }
        return typeface;
    }

    public static enum IconValue {

        icon_photo('\uf000'),
        icon_image('\uf001'),
		icon_th_grid('\uf002'),
		icon_pencil('\uf003'),
		icon_redo('\uf004'),
		icon_twitter_sq('\uf005'),
		icon_refresh('\uf006'),
		icon_undo('\uf007'),
		icon_linkedin('\uf008'),
		icon_linkedin_sq('\uf009'),
		icon_gplus('\uf00A'),
		icon_up_o('\uf00B'),
		icon_down_o('\uf00C'),
		icon_gplus_sq('\uf00D'),
		icon_facebook_sq('\uf00E'),
		icon_trash('\uf00F'),
		icon_facebook('\uf010'),
		icon_eraser('\uf011'),
		icon_tag('\uf012'),
		icon_done('\uf013'),
		icon_location('\uf014'),
		icon_save('\uf015'),
		icon_th_list('\uf016'),
		icon_contrast('\uf017'),
		icon_crop('\uf018'),
		icon_brightness('\uf019'),
		icon_twitter('\uf01A'),
		icon_vk('\uf01B'),
		icon_gallery('\uf01C'),
		icon_ya('\uf01D'),
		icon_ya_sq('\uf01E'),
		icon_ok('\uf01F'),
		icon_ok_sq('\uf020'),
		icon_share('\uf021'),
		icon_magic('\uf022'),
		icon_flip_vertical('\uf023'),
		icon_bitbucket('\uf024'),
		icon_bitbucket_sq('\uf025'),
		icon_instagram('\uf026'),
		icon_github_sq('\uf027'),
		icon_github('\uf028'),
		icon_bookmark('\uf029'),
		icon_flip_horizontal('\uf02A'),
        icon_info('\uf02B'),
        icon_friends('\uf02C');
        char character;

        IconValue(char character) {
            this.character = character;
        }

        public String formattedName() {
            return "{" + name() + "}";
        }

        public char character() {
            return character;
        }
    }
}
