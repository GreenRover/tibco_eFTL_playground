package ch.sbb.ftl.demo.msgsize;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.tibco.ftl.FTL;
import com.tibco.ftl.FTLException;
import com.tibco.ftl.Realm;
import com.tibco.ftl.TibProperties;

public class FtlHelper {

	public static final String typeName = "msgsize_queue_1.0";
	
	// https://docs.tibco.com/pub/ftl/5.0.0/doc/html/GUID-560E459A-87E2-437A-AF6D-996013FE1896.html
	public static final String realmAppName = "default";
	public static final String ftlEndPoint = "lasttest";
	public static final String realmServer = "http://k54129:13131";
	public static final String realmUser = "admin";
	public static final String realmPassword = "admin-pw";

	public static void setupLogging(final Level level) {
		final LogManager manager = LogManager.getLogManager();
		final Logger rootLogger = manager.getLogger("");
		rootLogger.setLevel(level);
		for (final Handler h : rootLogger.getHandlers()) {
		    h.setLevel(level);
		}		
	}
	
	public static Realm getRealm() throws FTLException {
        final TibProperties props = FTL.createProperties();
        props.set(Realm.PROPERTY_STRING_USERNAME, realmUser);
        props.set(Realm.PROPERTY_STRING_USERPASSWORD, realmPassword);
        
		return FTL.connectToRealmServer(realmServer, realmAppName, props);
	}	
}
