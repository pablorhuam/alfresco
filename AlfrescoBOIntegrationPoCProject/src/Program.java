import java.io.IOException;

import javax.mail.MessagingException;
public class Program {

	public static void main(String[] args) throws IOException{
//		FileUpload fileUpd = new FileUpload();
//		String urlFile = fileUpd.uploadFileAndGetUrl();
//		System.out.println(urlFile);
		Alfresco metadata = new Alfresco();
		metadata.storeAlfrescoMetadata();
//		BuildWebiDocument reportWebI = new BuildWebiDocument();
//		reportWebI.generateWebIntelligenceReportByUser();
//	
		String fileAccessLink = exportToPDFAPI.exportReportAndUploadIntoAlfredo();
		
		//String fileAccessLink = "workspace://SpacesStore/7cd81dbd-2366-4431-8674-a7474ace94ca";
		
//		exportPdf.exportReportsAsPDF();
//		fileUpd.uploadFileToAlfresco();		
		
		Alfresco.notifyAlfrescoUsers(Alfresco.getAlfrescoUsers(), fileAccessLink);
	}
}