import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main2 {

    public static void main(String... args) {
        Map config = new HashMap();
        config.put("cloud_name", "dodcgaskp");
        config.put("api_key", "451634862117428");
        config.put("api_secret", "Kmf6g1YhjqBts_a3_F-pjxp3PqE");
        Cloudinary cloudinary = new Cloudinary(config);



        try {
            String cedula = "1143983217";
            ArrayList<String> group = alfa(cloudinary, cedula);
            saveOnFolder(cedula, group);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void saveOnFolder(String cedula, ArrayList<String> alfa) {

        File folder = new File("/Users/domicianorincon/Downloads/cideim/" + cedula);
        if (!folder.exists()) folder.mkdirs();

        for (String url : alfa) {

            String[] parts = url.split("/");

            System.out.println("Descargando: "+parts[parts.length - 1]);

            saveURLImageOnFile(url, new File("/Users/domicianorincon/Downloads/cideim/" + cedula + "/" + parts[parts.length - 1]));
        }

    }


    public static ArrayList<String> alfa(Cloudinary cloudinary, String cedula) throws Exception {

        JSONObject outerObject = null;

        String jsonNext = null;

        boolean ifWeHaveMoreResources = true;

        ArrayList<String> listRes = new ArrayList<String>();

        while (ifWeHaveMoreResources) {

            outerObject = new JSONObject(cloudinary.api().resources(ObjectUtils.asMap("max_results", 2000, "next_cursor", jsonNext)));

            if (outerObject.has("next_cursor")) {

                jsonNext = outerObject.get("next_cursor").toString();

                ifWeHaveMoreResources = true;

            } else {

                ifWeHaveMoreResources = false;

            }

            JSONArray jsonArray = outerObject.getJSONArray("resources");

            for (int i = 0, size = jsonArray.length(); i < size; i++) {
                JSONObject objectInArray = jsonArray.getJSONObject(i);
                String public_id = objectInArray.get("public_id").toString();
                String url = objectInArray.get("secure_url").toString();
                listRes.add(url);
            }

        }

        ArrayList<String> out = new ArrayList<String>();
        for (String u : listRes) {
            if (u.contains(cedula)) {
                out.add(u);
            }
        }
        return out;
    }

    private static void saveURLImageOnFile(String url, File file) {
        try {
            URL page = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) page.openConnection();
            InputStream is = connection.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            is.close();
            fos.close();
            connection.disconnect();
            System.out.println(">>>HTTPS WebUtilDomi: Picture has been downloaded");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
