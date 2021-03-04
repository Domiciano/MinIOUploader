import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static final File folder = new File("/Users/domicianorincon/Downloads/cideim");
    private static int total = 0;

    //2000
    public static void main(String... args){
        if (!folder.exists()) folder.mkdirs();


        //DESCARGA
        Map config = new HashMap();
        config.put("cloud_name", "dodcgaskp");
        config.put("api_key", "451634862117428");
        config.put("api_secret", "Kmf6g1YhjqBts_a3_F-pjxp3PqE");
        Cloudinary cloudinary = new Cloudinary(config);
        ArrayList<String> group = getListOfPhotos(cloudinary);
        total = group.size();
        System.out.println("Hay un total de " + total+" muestras");
        saveOnFolderAndUpload(group);
    }

    private static void saveOnFolderAndUpload(ArrayList<String> group) {
        int counter = 0;
        for (String url : group) {
            String[] parts = url.split("/");
            //System.out.println("Descargando: "+parts[parts.length - 1]);
            File file = new File("/Users/domicianorincon/Downloads/cideim/" + parts[parts.length - 1]);
            saveURLImageOnFile(url, file);
            upload(file, parts[parts.length - 1]);
            counter++;
            System.out.println("Subidas a MinIO "+counter+" de "+ total);
        }

    }

    public static void upload(File file, String name){
            String fullpath = file.toString();
            if(name.contains("GUARAL")){
                uploadMinio(fullpath,name,"guaralrpc");
            }else{
                uploadMinio(fullpath,name,"guaralst");
            }
    }

    public static ArrayList<String> getListOfPhotos(Cloudinary cloudinary) {

        try{
            JSONObject outerObject = null;

            String jsonNext = null;

            boolean ifWeHaveMoreResources = true;

            ArrayList<String> listRes = new ArrayList<String>();

            while (ifWeHaveMoreResources) {
                outerObject = new JSONObject(cloudinary.api().resources(ObjectUtils.asMap("max_results", 3000, "next_cursor", jsonNext)));
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
                out.add(u);
            }
            return out;
        }catch(Exception ex){
            return null;
        }

    }

    public static void uploadMinio(String fullOriginPath, String filenameOnServer, String bucket) {
        try {
            // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient("i2thub.icesi.edu.co",9992, "guaralizador", "WLDuWy1USylYcv8wBNvxDA0VrDR18uOMfi0NdLibLe");
            // Check if the bucket already exists.
            boolean isExist = minioClient.bucketExists(bucket);
            if (isExist) {
                //System.out.println("Bucket already exists.");
            } else {
                // Make a new bucket called asiatrip to hold a zip file of photos.
                minioClient.makeBucket(bucket);
            }
            minioClient.putObject(bucket, filenameOnServer, fullOriginPath, null);
            System.out.println(">>>Subido: "+filenameOnServer);
        } catch (MinioException e) {
            System.out.println(">>>Error al subir: "+filenameOnServer);
            System.out.println("Minio Error: "+e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println(">>>Error al subir: "+filenameOnServer);
            System.out.println("IO Error: "+e.getLocalizedMessage());
        } catch (InvalidKeyException e) {
            System.out.println(">>>Error al subir: "+filenameOnServer);
            System.out.println("Invalid Key: "+e.getLocalizedMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println(">>>Error al subir: "+filenameOnServer);
            System.out.println("No Algoritm: "+e.getLocalizedMessage());
        }
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
            System.out.println("<<<Almacenado: "+file);
        } catch (IOException ex) {
            System.out.println("!!!!!!!!!!!La foto con URL: " + url + " no pudo ser descargada!");
        }
    }

}
