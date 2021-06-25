package cz.vitlabuda.icanteenjidelnicek;

/*
SPDX-License-Identifier: BSD-3-Clause

Copyright (c) 2021 Vít Labuda. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:
 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
    disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other materials provided with the distribution.
 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
    products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.RawRes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import cz.vitlabuda.icanteenextractor.FoodMenu;

public final class Auxiliaries {

    public static final String LINE_SEPARATOR = System.lineSeparator();

    public static ProgressDialog generateProgressDialog(Activity activity, int messageResource) {
        return generateProgressDialog(activity, activity.getString(messageResource));
    }

    public static ProgressDialog generateProgressDialog(Activity activity, String message) {
        ProgressDialog progressDialog = new ProgressDialog(activity, R.style.Theme_ICanteenJídelníček_ProgressDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);

        return progressDialog;
    }

    public static byte[] serializeFoodMenuObject(FoodMenu foodMenu) {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
        ) {
            objectOutputStream.writeObject(foodMenu);

            objectOutputStream.flush();
            byteArrayOutputStream.flush();

            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            throw new AppRuntimeException("Failed to serialize a food menu object to ByteArrayOutputStream!");
        }
    }

    public static String readRawResourceToString(Context context, @RawRes int rawResource) {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(rawResource)))) {
            String temp;
            while((temp = reader.readLine()) != null) {
                builder.append(temp);
                builder.append(LINE_SEPARATOR);
            }

        } catch (IOException e) {
            throw new AppRuntimeException("Failed to read an built-in raw resource!");
        }

        return builder.toString();
    }
}
