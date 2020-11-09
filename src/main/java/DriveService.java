import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DriveService {
	private final Drive drive;
	private final String portfolioDirectory = "C:\\Users\\andre\\Desktop\\portfolio\\";

	public DriveService() {
		this.drive = DriveAuthentication.createAuthenticatedDrive();
	}

	public void download(List<File> files) {
		createDirectoryIfNotExists();
		files.parallelStream().forEach(file -> {
			try {
				this.drive.files().export(file.getId(), "application/pdf")
						.executeMediaAndDownloadTo(new FileOutputStream(
								new java.io.File(portfolioDirectory + file.getName() + ".pdf")));
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void createDirectoryIfNotExists() {
		try {
			if(Files.notExists(Paths.get(portfolioDirectory)))
				Files.createDirectory(Paths.get(portfolioDirectory));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public List<File> pagedSearch(String query) {
		try {
			return pagedSearch2(query);
		} catch(IOException e) {
			return new ArrayList<>();
		}
	}

	private List<File> pagedSearch2(String query) throws IOException {
		String pageToken = null;
		List<File> files = new ArrayList<>();
		do {
			FileList result = drive.files().list()
					.setQ(query)
					.setSpaces("drive")
					.setFields("nextPageToken, files(id, name, parents)")
					.setPageToken(pageToken)
					.execute();
			files.addAll(result.getFiles());
			pageToken = result.getNextPageToken();
		} while(pageToken != null);
		return files;
	}
}
