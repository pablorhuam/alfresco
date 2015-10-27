FILE_TYPE:DAAA96DE-B0FB-4c6e-AF7B-A445F5BF9BE2
ENCODING:UTF-8
RECORD_SEPARATOR:30
COLUMN_SEPARATOR:124
ESC_CHARACTER:27
COLUMNS:Location|Guid|Time|Tzone|Trace|Log|Importance|Severity|Exception|DeviceName|ProcessID|ThreadID|ThreadName|ScopeTag|MajorTick|MinorTick|MajorDepth|MinorDepth|RootName|RootID|CallerName|CallerID|CalleeName|CalleeID|ActionID|DSRRootContextID|DSRTransaction|DSRConnection|DSRCounter|User|ArchitectComponent|DeveloperComponent|Administrator|Unit|CSNComponent|Text
SEVERITY_MAP: |None| |Success|W|Warning|E|Error|A|Assertion
HEADER_END
|2229549D8DE34734B6E171F9E52E6B520|2015 10 26 16:32:17.345|-0200| |Fatal|  | | |TraceLog|11052|   1|main            | |40|0|0|0|Webi SDK.CorbaServerImpl.doProcess()|AGILE:11052:1.14:1|-|-|Webi SDK.CorbaServerImpl.doProcess()|AGILE:11052:1.14:1|ChhBB9JMbEFZoVWf8qmNi8Ac|||||||||com.crystaldecisions.enterprise.ocaframework.ManagedService||invoke(): the service is dead 
com.crystaldecisions.enterprise.ocaframework.OCAFrameworkException$AllServicesDown: Unable to find servers in CMS AS1-300-89.agilesolutions.com:6400 and cluster @AS1-300-89.agilesolutions.com:6400 with kind webiserver and service null. All such servers could be down or disabled by the administrator. (FWM 01014)
	at com.crystaldecisions.enterprise.ocaframework.ServerController.validateServer(ServerController.java:417)
	at com.crystaldecisions.enterprise.ocaframework.ServiceMgr.validateServer(ServiceMgr.java:961)
	at com.crystaldecisions.enterprise.ocaframework.ManagedSession.validateServer(ManagedSession.java:698)
	at com.crystaldecisions.enterprise.ocaframework.ManagedSession.validateStatelessService(ManagedSession.java:528)
	at com.crystaldecisions.enterprise.ocaframework.ManagedSession.validate(ManagedSession.java:502)
	at com.crystaldecisions.enterprise.ocaframework.ManagedService.invoke(ManagedService.java:491)
	at com.crystaldecisions.enterprise.ocaframework.idl.OCA.OCAcdz.WICDZServer._WICDZUserSessionProxy.openSession(_WICDZUserSessionProxy.java:722)
	at com.businessobjects.rebean.wi.newserver.exec.WebiUserSessionExecutor.openSession(WebiUserSessionExecutor.java:23)
	at com.businessobjects.sdk.core.server.internal.session.ConnectSessionCommand.execute(ConnectSessionCommand.java:93)
	at com.businessobjects.sdk.core.server.internal.corba.CorbaServerImpl.doProcess(CorbaServerImpl.java:79)
	at com.businessobjects.sdk.core.server.internal.AbstractServer.processIt(AbstractServer.java:171)
	at com.businessobjects.sdk.core.server.internal.AbstractServer.process(AbstractServer.java:133)
	at com.businessobjects.rebean.wi.impl.engine.ReportEngineContext.initUserServer(ReportEngineContext.java:405)
	at com.businessobjects.rebean.wi.impl.services.ConfigurationServiceImpl.getAppSettings(ConfigurationServiceImpl.java:159)
	at com.businessobjects.rebean.wi.impl.engine.DocumentPropertiesHelper.updateComputedProperties(DocumentPropertiesHelper.java:78)
	at com.businessobjects.rebean.wi.impl.services.DocumentLoadingServiceImpl.loadOutputResponse(DocumentLoadingServiceImpl.java:253)
	at com.businessobjects.rebean.wi.impl.services.DocumentLoadingServiceImpl.updateWithOutputResponses(DocumentLoadingServiceImpl.java:309)
	at com.businessobjects.rebean.wi.impl.services.DocumentInstanceManagementServiceImpl.loadResponses(DocumentInstanceManagementServiceImpl.java:946)
	at com.businessobjects.rebean.wi.impl.services.DocumentInstanceManagementServiceImpl.openDocument(DocumentInstanceManagementServiceImpl.java:175)
	at com.businessobjects.rebean.wi.impl.services.DocumentInstanceManagementServiceImpl.openDocument(DocumentInstanceManagementServiceImpl.java:78)
	at com.businessobjects.rebean.wi.internal.WIReportEngine.openDocument(WIReportEngine.java:430)
	at com.businessobjects.rebean.wi.internal.WIReportEngine.openDocument(WIReportEngine.java:439)
	at exportToPDF.main(exportToPDF.java:57)

|2229549D8DE34734B6E171F9E52E6B521|2015 10 26 16:32:17.366|-0200| |Fatal|  | | |TraceLog|11052|   1|main            | |40|0|0|0|Webi SDK.CorbaServerImpl.doProcess()|AGILE:11052:1.14:1|-|-|Webi SDK.CorbaServerImpl.doProcess()|AGILE:11052:1.14:1|ChhBB9JMbEFZoVWf8qmNi8Ac|||||||||com.crystaldecisions.enterprise.ocaframework.ManagedService||[2015-10-26 16:32:17,316] [FATAL] [com.crystaldecisions.enterprise.ocaframework.ManagedService] invoke(): the service is dead  
com.crystaldecisions.enterprise.ocaframework.OCAFrameworkException$AllServicesDown: Unable to find servers in CMS AS1-300-89.agilesolutions.com:6400 and cluster @AS1-300-89.agilesolutions.com:6400 with kind webiserver and service null. All such servers could be down or disabled by the administrator. (FWM 01014)
	at com.crystaldecisions.enterprise.ocaframework.ServerController.validateServer(ServerController.java:417)
	at com.crystaldecisions.enterprise.ocaframework.ServiceMgr.validateServer(ServiceMgr.java:961)
	at com.crystaldecisions.enterprise.ocaframework.ManagedSession.validateServer(ManagedSession.java:698)
	at com.crystaldecisions.enterprise.ocaframework.ManagedSession.validateStatelessService(ManagedSession.java:528)
	at com.crystaldecisions.enterprise.ocaframework.ManagedSession.validate(ManagedSession.java:502)
	at com.crystaldecisions.enterprise.ocaframework.ManagedService.invoke(ManagedService.java:491)
	at com.crystaldecisions.enterprise.ocaframework.idl.OCA.OCAcdz.WICDZServer._WICDZUserSessionProxy.openSession(_WICDZUserSessionProxy.java:722)
	at com.businessobjects.rebean.wi.newserver.exec.WebiUserSessionExecutor.openSession(WebiUserSessionExecutor.java:23)
	at com.businessobjects.sdk.core.server.internal.session.ConnectSessionCommand.execute(ConnectSessionCommand.java:93)
	at com.businessobjects.sdk.core.server.internal.corba.CorbaServerImpl.doProcess(CorbaServerImpl.java:79)
	at com.businessobjects.sdk.core.server.internal.AbstractServer.processIt(AbstractServer.java:171)
	at com.businessobjects.sdk.core.server.internal.AbstractServer.process(AbstractServer.java:133)
	at com.businessobjects.rebean.wi.impl.engine.ReportEngineContext.initUserServer(ReportEngineContext.java:405)
	at com.businessobjects.rebean.wi.impl.services.ConfigurationServiceImpl.getAppSettings(ConfigurationServiceImpl.java:159)
	at com.businessobjects.rebean.wi.impl.engine.DocumentPropertiesHelper.updateComputedProperties(DocumentPropertiesHelper.java:78)
	at com.businessobjects.rebean.wi.impl.services.DocumentLoadingServiceImpl.loadOutputResponse(DocumentLoadingServiceImpl.java:253)
	at com.businessobjects.rebean.wi.impl.services.DocumentLoadingServiceImpl.updateWithOutputResponses(DocumentLoadingServiceImpl.java:309)
	at com.businessobjects.rebean.wi.impl.services.DocumentInstanceManagementServiceImpl.loadResponses(DocumentInstanceManagementServiceImpl.java:946)
	at com.businessobjects.rebean.wi.impl.services.DocumentInstanceManagementServiceImpl.openDocument(DocumentInstanceManagementServiceImpl.java:175)
	at com.businessobjects.rebean.wi.impl.services.DocumentInstanceManagementServiceImpl.openDocument(DocumentInstanceManagementServiceImpl.java:78)
	at com.businessobjects.rebean.wi.internal.WIReportEngine.openDocument(WIReportEngine.java:430)
	at com.businessobjects.rebean.wi.internal.WIReportEngine.openDocument(WIReportEngine.java:439)
	at exportToPDF.main(exportToPDF.java:57)

