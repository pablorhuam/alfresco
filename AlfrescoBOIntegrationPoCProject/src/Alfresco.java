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
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import com.sun.jersey.core.util.Base64;

public class Alfresco {

	public static String getAlfrescoTicket(String URL, String username,
			String password) {
		String ticket = null;

		try {

			URL restServiceURL = new URL(
					URL == null ? "http://172.18.23.64:8080/alfresco/service/api/login"
							: URL);

			HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL
					.openConnection();
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Accept", "application/json");
			httpConnection.setRequestProperty("Content-Type",
					"application/json; charser=utf-8");
			httpConnection.setRequestProperty("accept-charset",
					"UTF-8");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);

			JsonObject jsob = Json.createObjectBuilder()
					.add("username", username == null ? "admin" : username)
					.add("password", password == null ? "admin" : password)
					.build();

			// System.out.println(jsob);

			try (JsonWriter writer = Json.createWriter(httpConnection
					.getOutputStream())) {
				writer.writeObject(jsob);
			}

			if (httpConnection.getResponseCode() != 200) {
				BufferedReader breader = new BufferedReader(
						new InputStreamReader(httpConnection.getErrorStream()));
				while (true) {
					String text = breader.readLine();
					if (text == null)
						break;
					System.out.println(text);
				}
			}

			JsonReader reader = Json.createReader(httpConnection
					.getInputStream());
			ticket = reader.readObject().getJsonObject("data")
					.getString("ticket");

			httpConnection.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}

		return ticket;
	}

	public static BufferedReader readPDFFile(String path) {
		BufferedReader bufferedReader = null;

		// The name of the file to open.
		String fileName = path;

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(fileName);

			// Always wrap FileReader in BufferedReader.
			bufferedReader = new BufferedReader(fileReader);

			// Always close files.
			// bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}

		return bufferedReader;
	}

	public static void retrieveMetadataPOST() {
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://172.18.23.64:3306/alfresco_metadata_db", "root",
				"abcd1234")) {

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
				urlConnection.setRequestProperty("Authorization", "Basic "
						+ authStringEnc);
				InputStream is = urlConnection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);

				int numCharsRead;
				char[] charArray = new char[1024];
				StringBuffer sb = new StringBuffer();
				while ((numCharsRead = isr.read(charArray)) > 0) {
					sb.append(charArray, 0, numCharsRead);
				}
				String result = sb.toString();

				JsonArray jarr = Json.createReader(new StringReader(result))
						.readArray();
				JsonObject jsob = jarr.getJsonObject(0);
				System.out.println(jsob);
				String firstName = jsob.getJsonObject("creator").getString(
						"firstName");
				String createdDate = jsob.getString("createdDate");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
				Timestamp ts = null;
				try {
					Date date = simpleDateFormat.parse(createdDate);
					ts = new Timestamp(date.getTime());

					System.out.println("firstName : " + firstName);
					System.out.println("date : " + ts);
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}

				String sqlCadastrar = "INSERT INTO alfresco_metadata_db.alfresco_event (createdDate ,firstName) VALUES (?, ?)";
				PreparedStatement pst = null;
				try {
					pst = connection.prepareStatement(sqlCadastrar);

					pst.setTimestamp(1, ts);
					pst.setString(2, firstName);
					// rs = pst.executeQuery();
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

	public static String getFiles(String folder) {
		String files = null;
		String ticket = getAlfrescoTicket(null, null, null);
		try {

			String webPage = "http://172.18.23.64:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser/root/sites/ironmountain/documentlibrary/" + folder;
			String name = "admin";
			String password = "admin";

			String authString = name + ":" + password;

			byte[] authEncBytes = Base64.encode(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			URL url = new URL(webPage);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic "
					+ authStringEnc);
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			String result = sb.toString();

			JsonObject jsob = Json.createReader(new StringReader(result))
					.readObject();
			JsonArray jarr = jsob.getJsonArray("objects");
			ArrayList<JsonObject> arrayObj = new ArrayList<JsonObject>();
			for (JsonValue value : jarr){
				JsonObject jsobj = (JsonObject) value;
				JsonObject jprop = jsobj.getJsonObject("object").getJsonObject("properties").getJsonObject("cmis:name");
				arrayObj.add(jprop);
			}
			int idx ;
			ArrayList<JsonObject> arrayMetaData = new ArrayList<JsonObject>();
			for (int i = 0; i < arrayObj.size(); i++) {
				JsonObject jobj = (JsonObject) arrayObj.get(i);
				String value = jobj.getJsonString("value").toString().replace("\"","").replace(" ", "%20");
				String res = getResConection("http://172.18.23.64:8080/alfresco/service/slingshot/doclib/treenode/site/ironmountain/" + value);
				JsonObject jres = Json.createReader(new StringReader(res))
						.readObject();
				String idAux = jres.getJsonObject("parent").getJsonString("nodeRef").toString();
				idx = idAux.lastIndexOf("/");
				idAux = idAux.substring(idx+1);
				String[] arrAux = idAux.split("\"");
				String conMetadata = "http://172.18.23.64:8080/alfresco/service/api/version?nodeRef=workspace://SpacesStore/" + arrAux[0];
				res = getResConection(conMetadata);
				JsonArray jarrMetaD = Json.createReader(new StringReader(res))
						.readArray();
				arrayMetaData.add(jarrMetaD.getJsonObject(0));
				if(!jarrMetaD.getJsonObject(0).getJsonString("name").toString().contains(".")){
					getFiles(jarrMetaD.getJsonObject(0).getJsonString("name").toString());
				}else{
					String createdData = jarrMetaD.getJsonObject(0).getJsonString("createdDate").getString();
					String nameFile = jarrMetaD.getJsonObject(0).getJsonString("name").getString();
					String firstName = jarrMetaD.getJsonObject(0).getJsonObject("creator").getJsonString("firstName").getString();
					System.out.println(nameFile);
					insertMetadata(createdData,firstName,nameFile);						
				}
			}
			for (int z = 0; z < arrayMetaData.size(); z++){
//				System.out.println(arrayMetaData.get(z));
//				----------------------------
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}
	public static void insertMetadata(String createdDate, String firstName, String name){
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://172.18.23.64:3306/alfresco_metadata_db", "root",
				"abcd1234")) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
				Timestamp ts = null;
				try {
					Date date = simpleDateFormat.parse(createdDate);
					ts = new Timestamp(date.getTime());

					System.out.println("firstName : " + firstName);
					System.out.println("date : " + ts);
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
				PreparedStatement pst = null;
				String sqlCadastrar = "INSERT INTO alfresco_metadata_db.alfresco_event (createdDate ,firstName, name) VALUES (?, ?, ?)";
				try {
					pst = connection.prepareStatement(sqlCadastrar);

					pst.setTimestamp(1, ts);
					pst.setString(2, firstName);
					pst.setString(3, name);
					// rs = pst.executeQuery();
					pst.executeUpdate();

				} catch (SQLException sqe) {
					sqe.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} 
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}
	public static void deleteMetadata()throws IOException{
		try (Connection connection = DriverManager.getConnection(
				"jdbc:mysql://172.18.23.64:3306/alfresco_metadata_db", "root",
				"abcd1234")) {
			String sqlDelete = "Delete from alfresco_metadata_db.alfresco_event";
			PreparedStatement pst = null;
			try {
				pst = connection.prepareStatement(sqlDelete);
				pst.executeUpdate();
	
			} catch (SQLException sqe) {
				sqe.printStackTrace();
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public static String getResConection(String webUrl) throws IOException {
		String webPage = null;
		webPage = webUrl;
		//String ticket = getAlfrescoTicket(null, null, null);
		String name = "admin";
		String password = "admin";

		String authString = name + ":" + password;

		byte[] authEncBytes = Base64.encode(authString.getBytes());
		String authStringEnc = new String(authEncBytes);

		URL url = new URL(webPage);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic "
				+ authStringEnc);
		JsonStructure jsst;
		try (JsonReader reader = Json.createReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))){
			jsst = reader.read();
		}

		System.out.println(jsst.toString());
		return jsst.toString();
	}

	public static void main(String[] args) throws IOException {
		// String ticket = getAlfrescoTicket(null, null, null);
		// System.out.println(ticket);
		deleteMetadata();
		getFiles("files");
		// retrieveMetadataPOST();
		// uploadFileToAlfresco();

	}
}
