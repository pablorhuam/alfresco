import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.json.Json;
import javax.json.JsonReader;

import java.io.FileOutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.auth.CredentialsProvider;

public class exportToPDFAPI {
	
	public static String exportReportAndUploadIntoAlfredo()
	{
		System.out.println("Connecting to SAP BO");
		String fileAccessLink = "";
		try{
			String urlString = "http://as1-300-89:6405/biprws/logon/long";
			HttpClient client = new HttpClient();
			PostMethod getToken = new PostMethod(urlString);

			getToken.addRequestHeader("Accept","application/json");
			getToken.addRequestHeader("Content-Type","application/json");

			StringRequestEntity requestEntity = new StringRequestEntity(
					"{\"userName\":\"Administrator\",\"password\":\"Manager00\",\"auth\":\"secEnterprise\"}",
				    "application/json",
				    "UTF-8");
			
			System.out.println("Requesting authentication token.");
			getToken.setRequestEntity(requestEntity);
			
			int statusCode1 = client.executeMethod(getToken);
			
			System.out.println("Authentication code complete. Authentication status: " + statusCode1);
			
			String sessionToken = getToken.getResponseBodyAsString();
			sessionToken = sessionToken.substring(sessionToken.indexOf("AS1")-1,sessionToken.length()-1);
			
			System.out.println("Refreshing document to get the latest information.");
			String refreshString = "http://as1-300-89:6405/biprws/raylight/v1/documents/7677/parameters";
			
			PutMethod refreshDocument = new PutMethod(refreshString);

			refreshDocument.addRequestHeader("Accept","application/json");
			refreshDocument.addRequestHeader("Content-Type","application/json");
			refreshDocument.addRequestHeader("X-SAP-LogonToken", sessionToken);

			int statusCodeRef = client.executeMethod(refreshDocument);
			
			System.out.println("Refresh successful. Status code: " + statusCodeRef);
			
			System.out.println("Requesting document to be exported");
			String exportString = "http://as1-300-89:6405/biprws/raylight/v1/documents/7677/";
			GetMethod getPDF = new GetMethod(exportString);
			
			getPDF.addRequestHeader("Accept", "application/pdf");
			getPDF.addRequestHeader("X-SAP-LogonToken", sessionToken);
			
			int statusCode2 = client.executeMethod(getPDF);
			
			System.out.println("Document exported successfully. Status Code: " + statusCode2);
			
			File pdf = writeBytes(getPDF.getResponseBody(), "AlfrescoWebIReportWithMetadata");
			
			System.out.println("Connecting to Alfresco.");
			
			String alfrescoTiccketURL = "http://172.18.23.64:8080/alfresco" + "/service/api/login?u=" + "admin" + "&pw="
					+ "admin";
			String ticketURLResponse = getTicket(alfrescoTiccketURL);
			
			fileAccessLink = uploadDocument(ticketURLResponse, pdf, "AlfrescoWebIReportWithMetadata.pdf", "application/pdf", "description",
					"workspace://SpacesStore/60273b3a-b9f2-42d9-bc2d-9697246bdacd");
			
			System.out.println("Uploading pdf successful. File Access link created");
			
			String logOffString = "http://as1-300-89:6405/biprws/logoff";
			
			PostMethod logOff = new PostMethod(logOffString);

			logOff.addRequestHeader("Accept","application/xml");
			logOff.addRequestHeader("X-SAP-LogonToken",sessionToken);
			
			int statusCode3 = client.executeMethod(logOff);
			
			System.out.println("Logging off SAP BO. Status code: " + statusCode3);			
			
			getToken.releaseConnection();
		} catch (Exception e){
			e.printStackTrace();
		}
		return fileAccessLink;
	}
	
	public static void main(String[] args) {
		exportReportAndUploadIntoAlfredo();
	}
	
	public static File writeBytes(byte[] data, String filename) throws IOException {
		File file = new File(filename);
		FileOutputStream fstream = new FileOutputStream(file);
		fstream.write(data);
		fstream.close();
		return file;
	}
	
	public static String uploadDocument(String authTicket, File fileobj, String filename, String filetype,
			String description, String destination) {
		String nodeRef="";
		try {

			String urlString = "http://172.18.23.64:8080/alfresco/service/api/upload?alf_ticket=" + authTicket;
			//System.out.println("The upload url:::" + urlString);
			HttpClient client = new HttpClient();
			PostMethod mPost = new PostMethod(urlString);

			Part[] parts = { new FilePart("filedata", filename, fileobj, filetype, null),
					new StringPart("filename", filename), new StringPart("description", description),
					new StringPart("destination", destination), new StringPart("description", description), };
			mPost.setRequestEntity(new MultipartRequestEntity(parts, mPost.getParams()));
			int statusCode1 = client.executeMethod(mPost);
			
			String responseBody = mPost.getResponseBodyAsString();
			System.out.println("statusLine>>>" + statusCode1 + "......" + "\n status line \n" + mPost.getStatusLine()
					+ "\nbody \n" + responseBody);
			
		

			mPost.releaseConnection();
			 nodeRef = responseBody.substring(responseBody.indexOf("\"nodeRef\":"), responseBody.indexOf(",", responseBody.indexOf("\"nodeRef\":"))).replaceAll("\"", "");
			
			return nodeRef.replaceAll("nodeRef: ", "");
		} catch (Exception e) {
			System.out.println(e);
		}
		return nodeRef;
	}

	public static String getTicket(String url) throws IOException {

		HttpClient client = new HttpClient();
		client.getParams().setParameter(CredentialsProvider.PROVIDER, new ConsoleAuthPrompter());
		GetMethod httpget = new GetMethod(url);
		httpget.setDoAuthentication(true);

		String response = null;
		String ticketURLResponse = null;

		try {
			System.out.println("Requesting authentication ticket.");
			int status = client.executeMethod(httpget);

			if (status != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + httpget.getStatusLine());
			}

			response = new String(httpget.getResponseBodyAsString());
			//System.out.println("response = " + response);

			int startindex = response.indexOf("_") - 6;
			int endindex = response.indexOf("/") - 1;
			ticketURLResponse = response.substring(startindex, endindex);
			System.out.println("Authentication ticket received successfully");

		} finally {
			httpget.releaseConnection();
		}
		return ticketURLResponse;
	}
}
