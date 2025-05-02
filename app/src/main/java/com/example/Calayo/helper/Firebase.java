package com.example.Calayo.helper;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.database.Cursor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.*;

/**
 * Firebase helper class for uploading images to ImageKit.
 * This class handles image uploading to ImageKit, file conversion, and asynchronous callback handling.
 */
public class Firebase {

    // The endpoint for uploading images to ImageKit.
    private static final String IMAGEKIT_UPLOAD_URL = "https://upload.imagekit.io/api/v1/files/upload";

    // The private key for ImageKit authentication. 🔐 This should be moved to environment variables in production.
    private static final String IMAGEKIT_PRIVATE_KEY = "private_pvso1tTvm9olX/1ID3vHXOxbb60=";

    /**
     * Interface for upload callbacks.
     * Used to notify the calling activity/fragment about the result of the image upload.
     */
    public interface UploadCallback {
        /**
         * Callback method when the upload is successful.
         *
         * @param imageUrl The URL of the uploaded image.
         */
        void onSuccess(String imageUrl);

        /**
         * Callback method when the upload fails.
         *
         * @param errorMessage The error message indicating why the upload failed.
         */
        void onFailure(String errorMessage);
    }

    /**
     * Uploads an image to ImageKit.
     *
     * @param context   The application context.
     * @param uri       The URI of the image to be uploaded.
     * @param fileName  The file name to be used for the uploaded image.
     * @param callback  The callback to be triggered when the upload is complete.
     */
    public static void upload(Context context, Uri uri, String fileName, UploadCallback callback) {
        try {
            // Open input stream from the URI.
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            // Convert the input stream to a byte array.
            byte[] imageBytes = getBytes(inputStream);

            // Dynamically determine the content type based on the URI (default to image/jpeg if not found).
            String contentType = context.getContentResolver().getType(uri);
            if (contentType == null) contentType = "image/jpeg"; // Default to JPEG if content type is not found

            // Create the request body for the file upload.
            RequestBody fileBody = RequestBody.create(imageBytes, MediaType.parse(contentType));

            // Build the multipart request body with the file and file name.
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, fileBody)
                    .addFormDataPart("fileName", fileName)
                    .build();

            // Build the upload request.
            Request request = new Request.Builder()
                    .url(IMAGEKIT_UPLOAD_URL)
                    .addHeader("Authorization", Credentials.basic(IMAGEKIT_PRIVATE_KEY, ""))
                    .post(requestBody)
                    .build();

            // Create an OkHttpClient to send the request.
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // On failure, run the callback on the main thread.
                    runOnMainThread(context, () -> callback.onFailure("Upload failed: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Parse the response and extract the image URL.
                        String jsonResponse = response.body().string();
                        JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
                        String imageUrl = json.get("url").getAsString();

                        // Run the callback on the main thread with the URL of the uploaded image.
                        runOnMainThread(context, () -> callback.onSuccess(imageUrl));
                    } else {
                        // If the response is not successful, notify failure.
                        runOnMainThread(context, () -> callback.onFailure("Upload failed: " + response.message()));
                    }
                }
            });

        } catch (Exception e) {
            // Catch any exception and notify failure.
            callback.onFailure("Error: " + e.getMessage());
        }
    }

    /**
     * Utility method to convert an InputStream to a byte array.
     *
     * @param inputStream The input stream to convert.
     * @return The byte array containing the data from the input stream.
     * @throws IOException If an I/O error occurs during conversion.
     */
    private static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /**
     * Utility method to run a task on the main UI thread.
     *
     * @param context The context (to get the main thread's looper).
     * @param runnable The task to run on the main thread.
     */
    private static void runOnMainThread(Context context, Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    /**
     * Utility method to resolve the real file path from a URI.
     * This is useful for getting the actual file path of a content URI.
     *
     * @param context The context to access content resolver.
     * @param uri The URI of the image.
     * @return The file path corresponding to the URI, or null if not found.
     */
    private static String getPathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(colIndex);
            }
        }
        return null;
    }
}
