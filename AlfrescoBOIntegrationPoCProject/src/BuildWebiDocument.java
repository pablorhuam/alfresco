import java.awt.Color;

import com.businessobjects.rebean.wi.Alignment;
import com.businessobjects.rebean.wi.Attributes;
import com.businessobjects.rebean.wi.BlockAxis;
import com.businessobjects.rebean.wi.CellMatrix;
import com.businessobjects.rebean.wi.ConditionContainer;
import com.businessobjects.rebean.wi.ConditionObject;
import com.businessobjects.rebean.wi.DataProvider;
import com.businessobjects.rebean.wi.DataProviders;
import com.businessobjects.rebean.wi.DataSource;
import com.businessobjects.rebean.wi.DataSourceObjects;
import com.businessobjects.rebean.wi.DocumentInstance;
import com.businessobjects.rebean.wi.FilterCondition;
import com.businessobjects.rebean.wi.Font;
import com.businessobjects.rebean.wi.HAlignmentType;
import com.businessobjects.rebean.wi.LogicalOperator;
import com.businessobjects.rebean.wi.Operator;
import com.businessobjects.rebean.wi.Query;
import com.businessobjects.rebean.wi.ReportBlock;
import com.businessobjects.rebean.wi.ReportBody;
import com.businessobjects.rebean.wi.ReportContainer;
import com.businessobjects.rebean.wi.ReportDictionary;
import com.businessobjects.rebean.wi.ReportEngine;
import com.businessobjects.rebean.wi.ReportEngines;
import com.businessobjects.rebean.wi.ReportEngines.ReportEngineType;
import com.businessobjects.rebean.wi.ReportStructure;
import com.businessobjects.rebean.wi.SimpleTable;
import com.businessobjects.rebean.wi.TableAxis;
import com.businessobjects.rebean.wi.TableCell;
import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.framework.CrystalEnterprise;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.framework.ISessionMgr;
import com.crystaldecisions.sdk.occa.infostore.IInfoObject;
import com.crystaldecisions.sdk.occa.infostore.IInfoObjects;
import com.crystaldecisions.sdk.occa.infostore.IInfoStore;

public class BuildWebiDocument {
	public static void main(String[] args) {
		// The exported documents will be saved to this folder
		System.out.println("Working Directory: " + System.getProperty("user.dir"));
	
		IEnterpriseSession enterpriseSession = null;
		ReportEngines reportEngines = null;
		try {
			
			String host = "AS1-300-89";
			String user = "Administrator";
			String pass = "Manager00";
			String auth = "secEnterprise";
			
			// Connect to CMS
			System.out.println("Connecting...");
			ISessionMgr sessionMgr = CrystalEnterprise.getSessionMgr();
			enterpriseSession = sessionMgr.logon(user, pass, host, auth);
			
			// Initialize Webi report engine
			reportEngines = (ReportEngines) enterpriseSession.getService("ReportEngines");
			ReportEngine reportEngine = (ReportEngine) reportEngines.getService(ReportEngines.ReportEngineType.WI_REPORT_ENGINE);
			
			// Find the universe
			IInfoStore infoStore = (IInfoStore) enterpriseSession.getService("InfoStore");
			//String unvQuery = "select * from CI_APPOBJECTS where SI_KIND = 'Universe' and SI_NAME='Island Resorts Marketing'";
			String unvQuery = "select * from CI_APPOBJECTS where SI_KIND = 'Universe' and SI_NAME='eFashon'";
			IInfoObjects infoObjects = (IInfoObjects) infoStore.query(unvQuery);
			IInfoObject infoObject = (IInfoObject)infoObjects.get(0); 
			
			// Create new document
			DocumentInstance documentInstance = reportEngine.newDocument("UnivCUID="+infoObject.getCUID());
			DataProviders dps = documentInstance.getDataProviders();
			DataProvider dataProvider = dps.getItem(0);
			Query query = dataProvider.getQuery();
			DataSource dataSource = dataProvider.getDataSource();
			
			// Add objects
			DataSourceObjects objects = dataSource.getClasses();
			query.addResultObject(objects.getChildByName("Service"));
			query.addResultObject(objects.getChildByName("Revenue"));
			
			// Add condition
			ConditionContainer container = query.createCondition(LogicalOperator.AND);
			ConditionObject conditionObject = container.createConditionObject(objects.getChildByName("Country"));
			FilterCondition filterCondition = conditionObject.createFilterCondition(Operator.EQUAL);
			filterCondition.createFilterConditionConstant("US");
			
			// 
			dataProvider.runQuery();
			
			// 
			ReportStructure reportStructure = documentInstance.getStructure();
			ReportDictionary reportDictionary = documentInstance.getDictionary();
			for (int i=0; i<reportDictionary.getChildCount(); ++i) {
				System.out.println(reportDictionary.getChildAt(i).getName());
			}
			
			ReportContainer reportContainer = reportStructure.createReport("My Report");
			ReportBody reportBody = reportContainer.createReportBody();
			ReportBlock reportBlock = reportBody.createBlock();
			BlockAxis hAxis = reportBlock.getAxis(TableAxis.HORIZONTAL);
			hAxis.addExpr(reportDictionary.getChildByName("Service"));
			hAxis.addExpr(reportDictionary.getChildByName("Revenue"));
			
			// Format table
			SimpleTable simpleTable = (SimpleTable)reportBlock.getRepresentation();
			CellMatrix bodyMatrix = simpleTable.getBody();
			CellMatrix headerMatrix = simpleTable.getHeader(null);
			CellMatrix[] matrices = new CellMatrix[]{bodyMatrix, headerMatrix}; 
			for (CellMatrix matrix : matrices) {
				for (int i=0; i<matrix.getColumnCount();++i)
				{	
					TableCell cell = matrix.getCell(0, i);
					Attributes attributes = cell.getAttributes();
					if (matrix == bodyMatrix) {
						attributes.setBackground(Color.white);
						attributes.setForeground(Color.black);
					} else {
						attributes.setBackground(new Color(81, 117,185));
						attributes.setForeground(Color.white);
					}
					cell.setAttributes(attributes);
					Font font = cell.getFont();
					font.setName("Arial");
					font.setSize(10);
					cell.setFont(font);
					Alignment alignment = cell.getAlignment();
					alignment.setHorizontal(HAlignmentType.LEFT);
					cell.setAlignment(alignment);
					cell.setWidth(50);
				}
			}
			documentInstance.applyFormat();
			
			// Find the universe
			String folderQuery = "select * from CI_INFOOBJECTS where SI_KIND = 'Folder' and SI_NAME='Report Samples'";
			infoObjects = (IInfoObjects) infoStore.query(folderQuery);
			infoObject = (IInfoObject)infoObjects.get(0); 
			
			documentInstance.saveAs("Test", infoObject.getID(), null, null, true);
			documentInstance.closeDocument();					

		} catch (SDKException ex) {
			ex.printStackTrace();
		} finally {
			if (reportEngines != null)
				reportEngines.close();
			if (enterpriseSession != null)
				enterpriseSession.logoff();
			System.out.println("Finished!");
		}		
	}
	public static void generateWebIntelligenceReportByUser(){
		// The exported documents will be saved to this folder
		System.out.println("Working Directory: " + System.getProperty("user.dir"));
	
		IEnterpriseSession enterpriseSession = null;
		ReportEngines reportEngines = null;
		try {
			
			String host = "AS1-300-89";
			String user = "Administrator";
			String pass = "Manager00";
			String auth = "secEnterprise";
			
			// Connect to CMS
			System.out.println("Connecting...");
			ISessionMgr sessionMgr = CrystalEnterprise.getSessionMgr();
			enterpriseSession = sessionMgr.logon(user, pass, host, auth);
			
			// Initialize Webi report engine
			reportEngines = (ReportEngines) enterpriseSession.getService("ReportEngines");
			ReportEngine reportEngine = (ReportEngine) reportEngines.getService(ReportEngines.ReportEngineType.WI_REPORT_ENGINE);
			
			// Find the universe
			IInfoStore infoStore = (IInfoStore) enterpriseSession.getService("InfoStore");
			//String unvQuery = "select * from CI_APPOBJECTS where SI_KIND = 'Universe' and SI_NAME='Island Resorts Marketing'";
			String unvQuery = "select * from CI_APPOBJECTS where SI_KIND = 'Universe' and SI_NAME='eFashon'";
			IInfoObjects infoObjects = (IInfoObjects) infoStore.query(unvQuery);
			IInfoObject infoObject = (IInfoObject)infoObjects.get(0); 
			
			// Create new document
			DocumentInstance documentInstance = reportEngine.newDocument("UnivCUID="+infoObject.getCUID());
			DataProviders dps = documentInstance.getDataProviders();
			DataProvider dataProvider = dps.getItem(0);
			Query query = dataProvider.getQuery();
			DataSource dataSource = dataProvider.getDataSource();
			
			// Add objects
			DataSourceObjects objects = dataSource.getClasses();
			query.addResultObject(objects.getChildByName("Service"));
			query.addResultObject(objects.getChildByName("Revenue"));
			
			// Add condition
			ConditionContainer container = query.createCondition(LogicalOperator.AND);
			ConditionObject conditionObject = container.createConditionObject(objects.getChildByName("Country"));
			FilterCondition filterCondition = conditionObject.createFilterCondition(Operator.EQUAL);
			filterCondition.createFilterConditionConstant("US");
			
			// 
			dataProvider.runQuery();
			
			// 
			ReportStructure reportStructure = documentInstance.getStructure();
			ReportDictionary reportDictionary = documentInstance.getDictionary();
			for (int i=0; i<reportDictionary.getChildCount(); ++i) {
				System.out.println(reportDictionary.getChildAt(i).getName());
			}
			
			ReportContainer reportContainer = reportStructure.createReport("My Report");
			ReportBody reportBody = reportContainer.createReportBody();
			ReportBlock reportBlock = reportBody.createBlock();
			BlockAxis hAxis = reportBlock.getAxis(TableAxis.HORIZONTAL);
			hAxis.addExpr(reportDictionary.getChildByName("Service"));
			hAxis.addExpr(reportDictionary.getChildByName("Revenue"));
			
			// Format table
			SimpleTable simpleTable = (SimpleTable)reportBlock.getRepresentation();
			CellMatrix bodyMatrix = simpleTable.getBody();
			CellMatrix headerMatrix = simpleTable.getHeader(null);
			CellMatrix[] matrices = new CellMatrix[]{bodyMatrix, headerMatrix}; 
			for (CellMatrix matrix : matrices) {
				for (int i=0; i<matrix.getColumnCount();++i)
				{	
					TableCell cell = matrix.getCell(0, i);
					Attributes attributes = cell.getAttributes();
					if (matrix == bodyMatrix) {
						attributes.setBackground(Color.white);
						attributes.setForeground(Color.black);
					} else {
						attributes.setBackground(new Color(81, 117,185));
						attributes.setForeground(Color.white);
					}
					cell.setAttributes(attributes);
					Font font = cell.getFont();
					font.setName("Arial");
					font.setSize(10);
					cell.setFont(font);
					Alignment alignment = cell.getAlignment();
					alignment.setHorizontal(HAlignmentType.LEFT);
					cell.setAlignment(alignment);
					cell.setWidth(50);
				}
			}
			documentInstance.applyFormat();
			
			// Find the universe
			String folderQuery = "select * from CI_INFOOBJECTS where SI_KIND = 'Folder' and SI_NAME='Report Samples'";
			infoObjects = (IInfoObjects) infoStore.query(folderQuery);
			infoObject = (IInfoObject)infoObjects.get(0); 
			
			documentInstance.saveAs("Test", infoObject.getID(), null, null, true);
			documentInstance.closeDocument();					

		} catch (SDKException ex) {
			ex.printStackTrace();
		} finally {
			if (reportEngines != null)
				reportEngines.close();
			if (enterpriseSession != null)
				enterpriseSession.logoff();
			System.out.println("Finished!");
		}	
	}
}