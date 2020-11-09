import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class DriveAuthentication {
	private final static JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

	public static Drive createAuthenticatedDrive() {
		NetHttpTransport httpTransport = getHttpTransport();
		return new Drive.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
				.setApplicationName("Copywriting Portfolio")
				.build();
	}

	private static NetHttpTransport getHttpTransport() {
		try {
			return GoogleNetHttpTransport.newTrustedTransport();
		} catch(GeneralSecurityException | IOException e) {
			return null;
		}
	}

	private static Credential getCredentials(final NetHttpTransport httpTransport) {
		try {
			return getCredentialsForInstalledApp(httpTransport);
		} catch(IOException ex) {
			return null;
		}
	}

	private static Credential getCredentialsForInstalledApp(final NetHttpTransport httpTransport) throws IOException {
		InputStream in = DriveAuthentication.class.getResourceAsStream("credentials.json");
		if(in == null) {
			throw new FileNotFoundException("Resource not found: " + "credentials.json");
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, jsonFactory, clientSecrets, Collections.singletonList(DriveScopes.DRIVE))
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
				.build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}
}
