package batalhaagil.ufrpe.iversonluis.batalhaagil.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Iverson Luís on 12/02/2017.
 */

public class JsonRead {

    public static String loadJSONFromAsset(Context context, String file) {
        String json = null;
        try {

            InputStream is = context.getAssets().open(file);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
