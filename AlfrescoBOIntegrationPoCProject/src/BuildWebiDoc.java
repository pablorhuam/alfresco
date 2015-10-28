import com.businessobjects.rebean.wi.DocumentInstance;
import com.businessobjects.rebean.wi.ReportEngine;
import com.businessobjects.rebean.wi.ReportEngines;
import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.framework.CrystalEnterprise;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.framework.ISessionMgr;
import com.crystaldecisions.sdk.occa.infostore.IInfoObject;
import com.crystaldecisions.sdk.occa.infostore.IInfoObjects;
import com.crystaldecisions.sdk.occa.infostore.IInfoStore;
import com.crystaldecisions.sdk.occa.security.ILogonTokenMgr;
import com.businessobjects.rebean.wi.DataProvider;
import com.businessobjects.rebean.wi.DataProviders;
import com.businessobjects.rebean.wi.DataSource;
import com.businessobjects.rebean.wi.Query;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import com.businessobjects.rebean.wi.DataSourceObjects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildWebiDoc {
	
	public static void main(String[] args) {
		try {
			String user = "Administrator";
			String pwd = "Manager00";
			String CMS = "AS1-300-89:6400";
			String auth = "secEnterprise";
			HttpClient client = new HttpClient();

			String sessionToken = requestToken(client);
			int documentId = createDocument(sessionToken, client); 
			if(documentId != -1){
				/*ISessionMgr sessionManager = CrystalEnterprise.getSessionMgr();
				IEnterpriseSession enterpriseSession = sessionManager.logon(user, pwd, CMS, auth);

				ReportEngines reportEngines = (ReportEngines) enterpriseSession.getService("ReportEngines");
				ReportEngine reportEngine = (ReportEngine) reportEngines.getService(ReportEngines.ReportEngineType.WI_REPORT_ENGINE);
			
				DocumentInstance documentInstance = reportEngine.openDocument(documentId);
				createDataProvider(sessionToken, client, documentId);

				DataProviders dataProviders = documentInstance.getDataProviders();
				DataProvider dataProvider = dataProviders.getItem(0);
				System.out.println(dataProvider.getID());

				Query query = dataProvider.getQuery();
				DataSource dataSource = dataProvider.getDataSource();

				DataSourceObjects objects = dataSource.getClasses();
				System.out.println(objects.getChildCount());*/


			} else {
				System.out.println("Document failed to create");
			}

			logOff(sessionToken, client);

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static String requestToken(HttpClient client){
		String sessionToken = "";
		try {
			String urlString = "http://as1-300-89:6405/biprws/logon/long";		
			PostMethod getToken = new PostMethod(urlString);

			getToken.addRequestHeader("Accept","application/json");
			getToken.addRequestHeader("Content-Type","application/json");

			StringRequestEntity requestEntity = new StringRequestEntity(
					"{\"userName\":\"Administrator\",\"password\":\"Manager00\",\"auth\":\"secEnterprise\"}",
					"application/json",
					"UTF-8");
			
			getToken.setRequestEntity(requestEntity);
			int statusCode1 = client.executeMethod(getToken);

			System.out.println("StatusLine:" + statusCode1 + 
				"\nStatus line: \n" + getToken.getStatusLine() + 
				"\nBody: \n" + getToken.getResponseBodyAsString() + "\n");

			sessionToken = getToken.getResponseBodyAsString();
			sessionToken = sessionToken.substring(sessionToken.indexOf("AS1")-1,sessionToken.length()-1);
		} catch (Exception e){
			e.printStackTrace();
		} 
		return (sessionToken != "")?sessionToken:"No token retrieved";
	}

	public static int createDocument(String sessionToken, HttpClient client){
		int statusCode = 100;
		try{
			String createDocString = "http://as1-300-89:6405/biprws/raylight/v1/documents";
			PostMethod createDocument = new PostMethod(createDocString);

			createDocument.addRequestHeader("Accept", "application/json");
			createDocument.addRequestHeader("X-SAP-LogonToken", sessionToken);

			StringRequestEntity requestEntity = new StringRequestEntity(
					"<document><name>Test Webi Doc</name><folderId>5943</folderId></document>",
					"application/xml",
					"UTF-8");

			createDocument.setRequestEntity(requestEntity);

			statusCode = client.executeMethod(createDocument);
			System.out.println("StatusLine:" + statusCode + 
				"\nStatus line: \n" + createDocument.getStatusLine() + 
				"\nBody: \n" + createDocument.getResponseBodyAsString() + "\n");
			
			String pattern = "[0-9]+";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(createDocument.getResponseBodyAsString());
			if(m.find()){
				return Integer.parseInt(m.group(0));
			}

		} catch (Exception e){
			e.printStackTrace();
		}
		return -1;
	}

	public static void logOff(String sessionToken, HttpClient client){
		try{
			String createDocString = "http://as1-300-89:6405/biprws/raylight/v1/documents";
			PostMethod createDocument = new PostMethod(createDocString);

			createDocument.addRequestHeader("Accept", "application/json");
			createDocument.addRequestHeader("X-SAP-LogonToken", sessionToken);

			client.executeMethod(createDocument);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void createDataProvider(String sessionToken, HttpClient client, int documentId){
		int statusCode = 100;
		try{
			String createDocString = "http://as1-300-89:6405/biprws/raylight/v1/documents/"+ documentId +"/dataproviders";
			PostMethod createDataProvider = new PostMethod(createDocString);

			createDataProvider.addRequestHeader("Accept", "application/json");
			createDataProvider.addRequestHeader("X-SAP-LogonToken", sessionToken);

			StringRequestEntity requestEntity = new StringRequestEntity(
					"<dataprovider><name>Query 1</name><dataSourceId>5936</dataSourceId></dataprovider>",
					"application/xml",
					"UTF-8");

			createDataProvider.setRequestEntity(requestEntity);

			statusCode = client.executeMethod(createDataProvider);
			System.out.println("StatusLine:" + statusCode + 
				"\nStatus line: \n" + createDataProvider.getStatusLine() + 
				"\nBody: \n" + createDataProvider.getResponseBodyAsString() + "\n");

		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
