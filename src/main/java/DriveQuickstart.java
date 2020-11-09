import com.google.api.services.drive.model.File;

import java.util.List;

public class DriveQuickstart {
	public static void main(String[] args) {
		DriveService service = new DriveService();
		List<File> foldersInCopywriting = service.pagedSearch("mimeType = 'application/vnd.google-apps.folder' and '1ASsTLh9EDnGaSmF3ZZtmLLQbkOZBEu-N' in parents");
		for(File prospectFolder : foldersInCopywriting) {
			List<File> prospectItems = service.pagedSearch(String.format("'%s' in parents and (name contains 'FB' or name contains 'Facebook' or name contains 'Email')", prospectFolder.getId()));
			System.out.println(prospectFolder.getName() + "(" + prospectFolder.getId() + ")");
			for(File copywritingItem : prospectItems) {
				System.out.println(" - " + copywritingItem.getName() + "(" + copywritingItem.getId() + ")");
			}
			service.download(prospectItems);
		}
	}
}
