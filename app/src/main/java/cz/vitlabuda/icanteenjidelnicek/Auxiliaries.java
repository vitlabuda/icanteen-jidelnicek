package cz.vitlabuda.icanteenjidelnicek;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.RawRes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

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
