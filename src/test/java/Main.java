import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Main {

    public static void main(String... args){
        Main main = new Main();
        File folder = new File("/Users/domicianorincon/Downloads/PhotosCideim/Cloudinary_Archive_2020-06-10_19_32_30/");
        int counter = 0;
        for(String name : folder.list()){
            String fullpath = new File(folder.toString() + "/" + name).toString();
            if(name.contains("GUARAL")){
                main.uploadMinio(fullpath,name,"guaralrpc");
            }else{
                main.uploadMinio(fullpath,name,"guaralst");
            }
            counter++;
            System.out.println("Subidas a MinIO "+counter+" de "+ folder.list().length);
        }


        //

    }

    public void uploadMinio(String fullOriginPath, String filenameOnServer, String bucket) {
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
            File f = new File(fullOriginPath);
            FileInputStream fis = new FileInputStream(f);
            minioClient.putObject(bucket, filenameOnServer, fullOriginPath, null);
        } catch (MinioException e) {
            System.out.println("(Minio Exception): " + e);
        } catch (IOException e) {
            System.out.println("(IO Exception): " + e);
        } catch (InvalidKeyException e) {
            System.out.println("(Invalid key Exception): " + e);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("(No Such Algorithm): " + e);
        }
    }

}
