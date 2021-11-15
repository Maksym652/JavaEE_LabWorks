/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FirestoreDB;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

/**
 *
 * @author 1
 */
public class FirestoreProducer {
    
    private static Firestore db;
    
    @Produces @Default
    private Firestore getFirestore() throws FileNotFoundException, IOException{
        if(db==null){
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("D:\\Файли програм\\WebApplication1\\carviolations-60250-d2d7b0323a50.json"));
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .setProjectId("carviolations-60250")
                    .build();
            try{
                FirebaseApp.initializeApp(options);
                db = FirestoreClient.getFirestore();
            }catch(Exception ex){
                db = FirestoreClient.getFirestore(FirebaseApp.getInstance());
            }
        }
        return db;
    }
}
