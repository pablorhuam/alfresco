import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;

import com.sun.jersey.core.util.Base64;

public class Alfresco {

	public static String getAlfrescoTicket(String URL, String username, String password)
	{
		String ticket = null;
		
		 try {
	
				URL restServiceURL = new URL(URL==null ? "http://172.18.23.64:8080/alfresco/service/api/login" : URL);
	
				HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
				httpConnection.setRequestMethod("POST");
				httpConnection.setRequestProperty("Accept", "application/json");
				httpConnection.setRequestProperty("Content-Type", "application/json");
				httpConnection.setDoInput(true);
				httpConnection.setDoOutput(true);
				
				JsonObject jsob = Json.createObjectBuilder()
						.add("username", username==null ? "admin" : username)
						.add("password", password==null ? "admin" : password)
						.build();
				
				//System.out.println(jsob);
				
				try (JsonWriter writer = Json.createWriter(httpConnection.getOutputStream())){
					writer.writeObject(jsob);
				}
				
				if (httpConnection.getResponseCode() != 200) {
					BufferedReader breader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
					while (true){
						String text = breader.readLine();
						if (text == null)
							break;
						System.out.println(text);
					}
				}
			
				JsonReader reader = Json.createReader(httpConnection.getInputStream());
					ticket = reader.readObject().getJsonObject("data").getString("ticket");
				
					
				httpConnection.disconnect();
	
		 } catch (MalformedURLException e) {
	
			e.printStackTrace();
	
		 } catch (IOException e) {
	
			e.printStackTrace();
		 }
		 
		 return ticket;
	}
	
	public static BufferedReader readPDFFile(String path)
	{
		 BufferedReader bufferedReader = null;
		 
        // The name of the file to open.
        String fileName = path;

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            bufferedReader = 
                new BufferedReader(fileReader);

          
            
            // Always close files.
            //bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        
        return bufferedReader;
	}
	
	public static void retrieveMetadataPOST()
	{
		try (Connection connection = DriverManager.getConnection("jdbc:mysql://172.18.23.64:3306/alfresco_metadata_db", "root", "abcd1234")) {
		    
		    System.out.println(connection);
		    try {
		    	
				String webPage = "http://172.18.23.64:8080/alfresco/service/api/version?nodeRef=workspace://SpacesStore/55364e31-8b24-44f8-ae00-14fae2e15349";
				String name = "admin";
				String password = "admin";
	
				String authString = name + ":" + password;

				byte[] authEncBytes = Base64.encode(authString.getBytes());
				String authStringEnc = new String(authEncBytes);
				
				URL url = new URL(webPage);
				URLConnection urlConnection = url.openConnection();
				urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
				InputStream is = urlConnection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
	
				int numCharsRead;
				char[] charArray = new char[1024];
				StringBuffer sb = new StringBuffer();
				while ((numCharsRead = isr.read(charArray)) > 0) {
					sb.append(charArray, 0, numCharsRead);
				}
				String result = sb.toString();
	
				JsonArray jarr = Json.createReader(new StringReader(result)).readArray();
				JsonObject jsob = jarr.getJsonObject(0);
				System.out.println(jsob);
				String firstName = jsob.getJsonObject("creator").getString("firstName");
				String createdDate  = jsob.getString("createdDate");
		        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
		        Timestamp ts = null;
		        try
		        { 
		        	Date date = simpleDateFormat.parse(createdDate);
		        	ts = new Timestamp(date.getTime());
		        	
		        	System.out.println("firstName : "+firstName);
		            System.out.println("date : "+ts);
		        }catch(Exception e){
		        	System.out.println(e);
		        	e.printStackTrace();
		        }
		        
				
				String sqlCadastrar = "INSERT INTO alfresco_metadata_db.alfresco_event (createdDate ,firstName) VALUES (?, ?)";
				PreparedStatement pst = null;
				try{
					pst = connection.prepareStatement(sqlCadastrar); 
					
					pst.setTimestamp(1, ts);
					pst.setString(2, firstName);
	//				rs = pst.executeQuery();
					pst.executeUpdate();  			
					
				} catch (SQLException sqe) {
					sqe.printStackTrace();
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		 
	}
	
	public static void retrieveMetadataGET()
	{
	}
		
	public static void uploadFileToAlfresco(){
	
		
		/**
		 * Local directory containing JPG files
		 */
		final String FILE_PATH = "/users/jpotts/Documents/sample/photos/Berlin";
		
		/**
		 * Code assumes that every file is of the type below
		 */
		final String FILE_TYPE = "image/jpeg";
		
		/**
		 * Files will be uploaded to this folder, which resides in the folder returned
		 * by super.getParentFolder()
		 */
		final String FOLDER_NAME = "Art";
//		try{
//		String URL = "http://172.18.23.64:8080/alfresco/service/api/upload/?alf_ticket = " + getAlfrescoTicket(null, null, null);
//	
////		PDFDocument pdfD = null;
////		try {
////			pdfD = new PDFDocument(new URL("C:/PDF_TEST.pdf"));
////		} catch (Exception e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
//		
//			File file = new File("C:/PDF_TEST.pdf");
//	
//			String filetype = "application/pdf";
//	
//			String filename="Test_Upload.pdf";
//	
//
//			URL restServiceURL = new URL(URL==null ? "http://172.18.23.64:8080/alfresco/service/api/login" : URL);
//
//			HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
//			httpConnection.setRequestMethod("POST");
//			httpConnection.setRequestProperty("Accept", "application/json");
//			httpConnection.setRequestProperty("Content-Type", "application/json");
//			httpConnection.setDoInput(true);
//			httpConnection.setDoOutput(true);
//			
//			
//			CloseableHttpClient client = HttpClientBuilder.create().build();
//	
//			PostMethod post = new PostMethod(URL);
//			
//			Part[] parts = null;
//	
//			parts = new Part[]{
//	
//				new FilePart("filedata", filename, file, filetype, null),
//	
//				new StringPart("filename", filename),
//	
//				new StringPart("description", "description"),
//	
//				new StringPart("siteid", "yoursite"),
//	
//				new StringPart("containerid", "documentLibrary")
//	
//				// your can add more paramter here
//	
//				//new StringPart("uploaddirectory", "documentLibrary"),
//	
//				//new StringPart("updatenoderef", "updatenoderef"),
//	
//				//new StringPart("contenttype", "contenttype"),
//	
//				//new StringPart("aspects", "aspects")
//	
//	
//	
//				};
//
//	
//			post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
//	
//			try {
//				int status = client.execute(post);
//			} catch (HttpException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	
//			try {
//				System.out.println(post.getResponseBodyAsString());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	
//			post.releaseConnection();
//	}
//	catch(Exception e){
//		e.printStackTrace();
//	}
	}
	
	public static void main(String[] args) {		
		String ticket = getAlfrescoTicket(null,null,null);
		System.out.println(ticket);
		//retrieveMetadataPOST();
		//uploadFileToAlfresco();
		
	}
}
