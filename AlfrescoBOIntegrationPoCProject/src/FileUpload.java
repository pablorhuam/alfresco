
import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * @author aaa
 * 
 */
public class FileUpload {

	public static void uploadDocument(String authTicket, File fileobj, String filename, String filetype,
			String description, String destination) {
		try {

			String urlString = "http://172.18.23.64:8080/alfresco/service/api/upload?alf_ticket=" + authTicket;
			System.out.println("The upload url:::" + urlString);
			HttpClient client = new HttpClient();

			PostMethod mPost = new PostMethod(urlString);
			// File f1 =fileobj;
			Part[] parts = { new FilePart("filedata", filename, fileobj, filetype, null),
					new StringPart("filename", filename), 
					new StringPart("description", description),
					new StringPart("destination", destination),
					//new StringPart("description", description),
					// modify this according to where you wanna put your content
					new StringPart("siteid", "ironmountain"), 
					new StringPart("containerid", "documentLibrary"),
					new StringPart("uploaddirectory", "/files/")
			};
			mPost.setRequestEntity(new MultipartRequestEntity(parts, mPost.getParams()));
			int statusCode1 = client.executeMethod(mPost);
			System.out.println("statusLine>>>" + statusCode1 + "......" + "\n status line \n" + mPost.getStatusLine()
					+ "\nbody \n" + mPost.getResponseBodyAsString());
			mPost.releaseConnection();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String args[]) throws IOException {
		// SimpleUpload aw=new SimpleUpload();
		// String Ticket=aw.login();
		// String ticket="TICKET_3e61ccfa8a11690b10e1a2fb0eeee2c5583b0043";

		// aritz : not using predefined method to get credential
		String alfrescoTiccketURL = "http://172.18.23.64:8080/alfresco" + "/service/api/login?u=" + "admin" + "&pw="
				+ "admin";

		// InteractiveAuthentication ticket = new InteractiveAuthentication();
		// String ticketURLResponse = ticket.getTicket(alfrescoTiccketURL);
		String ticketURLResponse = "";
		ticketURLResponse = "TICKET_6b721f669405370643a8dd62c9a9917a4f0b8d60";
		File f = new File("C:/Users/AGILE-PC/Desktop/test.txt");

		// FileInputStream is=new FileInputStream(f);
		uploadDocument(ticketURLResponse, f, "test.txt", "application/text", "description", "workspace://SpacesStore/703c595e-240b-4a06-bf33-999160fc1446");
//																							   "workspace://SpacesStore/703c595e-240b-4a06-bf33-999160fc1446
		// uploadDocument("TICKET_3ef085c4e24f4e2c53a3fa72b3111e55ee6f0543",
		// f,"47.bmp","image
		// file","application/jpg","workspace://SpacesStore/65a06f8c-0b35-4dae-9835-e38414a99bc1");
	}

}