import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.auth.CredentialsProvider;

public class exportToPDFAPI {
	
	public static void main(String[] args) {
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
			
			getToken.setRequestEntity(requestEntity);
			
			int statusCode1 = client.executeMethod(getToken);
			System.out.println("statusLine>>>" + statusCode1 + "......" + "\n status line \n" + getToken.getStatusLine()
					+ "\nbody \n" + getToken.getResponseBodyAsString());
			
			String sessionToken = getToken.getResponseBodyAsString();
			sessionToken = sessionToken.substring(sessionToken.indexOf("AS1")-1,sessionToken.length()-1);
			
			String exportString = "http://as1-300-89:6405/biprws/raylight/v1/documents/6391/";
			GetMethod getPDF = new GetMethod(exportString);
			
			getPDF.addRequestHeader("Accept", "application/pdf");
			getPDF.addRequestHeader("X-SAP-LogonToken", sessionToken);
			
			int statusCode2 = client.executeMethod(getPDF);
			System.out.println("statusLine>>>" + statusCode2 + "......" + "\n status line \n" + getPDF.getStatusLine());
			
			File pdf = writeBytes(getPDF.getResponseBody(), "AlfrescoWebIReportWithMetadata");
			
			String alfrescoTiccketURL = "http://172.18.23.64:8080/alfresco" + "/service/api/login?u=" + "admin" + "&pw="
					+ "admin";
			String ticketURLResponse = getTicket(alfrescoTiccketURL);

			uploadDocument(ticketURLResponse, pdf, "AlfrescoWebIReportWithMetadata.pdf", "application/pdf", "description",
					"workspace://SpacesStore/60273b3a-b9f2-42d9-bc2d-9697246bdacd");
			
			String logOffString = "http://as1-300-89:6405/biprws/logoff";
			
			PostMethod logOff = new PostMethod(logOffString);

			logOff.addRequestHeader("Accept","application/xml");
			logOff.addRequestHeader("X-SAP-LogonToken",sessionToken);
			
			int statusCode3 = client.executeMethod(logOff);
			
			System.out.println("statusLine>>>" + statusCode3 + "......" + "\n status line \n" + logOff.getStatusLine()
					+ "\nbody \n" + logOff.getResponseHeaders().toString());			
			
			getToken.releaseConnection();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static File writeBytes(byte[] data, String filename) throws IOException {
		File file = new File(filename);
		FileOutputStream fstream = new FileOutputStream(file);
		fstream.write(data);
		fstream.close();
		return file;
	}
	
	public static void uploadDocument(String authTicket, File fileobj, String filename, String filetype,
			String description, String destination) {
		try {

			String urlString = "http://172.18.23.64:8080/alfresco/service/api/upload?alf_ticket=" + authTicket;
			System.out.println("The upload url:::" + urlString);
			HttpClient client = new HttpClient();
			PostMethod mPost = new PostMethod(urlString);

			Part[] parts = { new FilePart("filedata", filename, fileobj, filetype, null),
					new StringPart("filename", filename), new StringPart("description", description),
					new StringPart("destination", destination), new StringPart("description", description), };
			mPost.setRequestEntity(new MultipartRequestEntity(parts, mPost.getParams()));
			int statusCode1 = client.executeMethod(mPost);
			System.out.println("statusLine>>>" + statusCode1 + "......" + "\n status line \n" + mPost.getStatusLine()
					+ "\nbody \n" + mPost.getResponseBodyAsString());
			mPost.releaseConnection();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static String getTicket(String url) throws IOException {

		HttpClient client = new HttpClient();
		client.getParams().setParameter(CredentialsProvider.PROVIDER, new ConsoleAuthPrompter());
		GetMethod httpget = new GetMethod(url);
		httpget.setDoAuthentication(true);

		String response = null;
		String ticketURLResponse = null;

		try {
			int status = client.executeMethod(httpget);

			if (status != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + httpget.getStatusLine());
			}

			response = new String(httpget.getResponseBodyAsString());
			System.out.println("response = " + response);

			int startindex = response.indexOf("_") - 6;
			int endindex = response.indexOf("/") - 1;
			ticketURLResponse = response.substring(startindex, endindex);
			System.out.println("ticket = " + ticketURLResponse);

		} finally {
			httpget.releaseConnection();
		}
		return ticketURLResponse;
	}
}
