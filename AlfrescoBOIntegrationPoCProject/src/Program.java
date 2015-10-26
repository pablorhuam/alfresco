import java.io.IOException;

import javax.mail.MessagingException;
public class Program {

	public static void main(String[] args) throws IOException{
		FileUpload fileUpd = new FileUpload();
		String urlFile = fileUpd.uploadFileAndGetUrl();
		System.out.println(urlFile);
		Alfresco metadata = new Alfresco();
		metadata.storeAlfrescoMetadata();
		BuildWebiDocument reportWebI = new BuildWebiDocument();
		reportWebI.generateWebIntelligenceReportByUser();
		exportToPDF exportPdf = new exportToPDF();
		exportPdf.exportReportsAsPDF();
		fileUpd.uploadFileToAlfresco();
		//getUserEmailAddresses();
		EmailHandler email = new EmailHandler("smtp.office365.com", "587", "prhua@agilesolutions.com");
		try {
			email.sendEmail("prhua@agilesolutions.com", "Joya0804", "Email teste");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
