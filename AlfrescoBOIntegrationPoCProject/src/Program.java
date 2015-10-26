import java.io.IOException;

import javax.mail.MessagingException;
public class Program {

	public static void main(String[] args) throws IOException{
		FileUpload fileUpd = new FileUpload();
		String urlFile = fileUpd.uploadFileAndGetUrl();
		System.out.println(urlFile);
		Alfresco metadata = new Alfresco();
		metadata.storeAlfrescoMetadata();
//		BuildWebiDocument reportWebI = new BuildWebiDocument();
//		reportWebI.generateWebIntelligenceReportByUser();
		exportToPDF exportPdf = new exportToPDF();
		exportPdf.exportReportsAsPDF();
		fileUpd.uploadFileToAlfresco();
		
		StringBuilder message = new StringBuilder();
		message.append("This is a example message.");
		message.append("This is the alfresco weblink for the file you uploaded:" + urlFile);
		
		Alfresco.notifyAlfrescoUsers(Alfresco.getAlfrescoUsers(), message.toString());
	}
}