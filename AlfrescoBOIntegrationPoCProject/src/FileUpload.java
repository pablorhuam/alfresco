
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

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
	public static String uploadFileAndGetUrl(){
		JsonObject jsob = Json.createReader(new StringReader(uploadFileToAlfresco()))
			.readObject();
//		uploadFileToAlfresco();
		System.out.println("1234");
		System.out.println(jsob);
		return "http://172.18.23.64:8080/share/page/site/ironmountain/document-details?nodeRef=" + jsob.getString("nodeRef");
	}
	public static String uploadDocument(String authTicket, File fileobj, String filename, String filetype,
			String description, String destination) {
		try {

			String urlString = "http://172.18.23.64:8080/alfresco/service/api/upload?alf_ticket=" + authTicket;
			System.out.println("The upload url:::" + urlString);
			HttpClient client = new HttpClient();

			PostMethod mPost = new PostMethod(urlString);
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
			return mPost.getResponseBodyAsString();
		} catch (Exception e) {
			System.out.println(e);
			return e.toString();
		}
	}
	public static String uploadFileToAlfresco(){
		String ticketURLResponse = "";
		ticketURLResponse = "TICKET_de6ca51d8dacedc8cf47333548cc8020c7d86bc4";
//		File f = new File("C:\\Users\\c5185151\\hdbstudio\\AlfrescoBOIntegrationPoCProject\\src\\fileToBeLoaded.xml");
		File f = new File("src/fileToBeLoaded.xml");

		 return uploadDocument(ticketURLResponse, f, "lala2.txt", "application/text", "description", "workspace://SpacesStore/703c595e-240b-4a06-bf33-999160fc1446");
	}
	public static void main(String args[]) throws IOException {
		String ticketURLResponse = "";
		ticketURLResponse = "TICKET_de6ca51d8dacedc8cf47333548cc8020c7d86bc4";
		File f = new File("src/fileToBeLoaded.xml");

//		uploadDocument(ticketURLResponse, f, "testeUpload.txt", "application/text", "description", "workspace://SpacesStore/703c595e-240b-4a06-bf33-999160fc1446");
		uploadFileAndGetUrl();
	}

}