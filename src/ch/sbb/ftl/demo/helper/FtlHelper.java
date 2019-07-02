package ch.sbb.ftl.demo.helper;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.tibco.ftl.FTL;
import com.tibco.ftl.FTLException;
import com.tibco.ftl.Realm;
import com.tibco.ftl.TibProperties;

public class FtlHelper {

	public static final String TYPE_NAME = "demo";

	// https://docs.tibco.com/pub/ftl/5.0.0/doc/html/GUID-560E459A-87E2-437A-AF6D-996013FE1896.html
	public static String realmAppName = "persisted";
	public static String ftlEndPoint = "default";
	public static String realmServer = "http://shared-rcsactivemq-node02.otc-test.sbb.ch:8585";
	public static String realmUser = "admin";
	public static String realmPassword = "admin-pw";

	public static void setupLogging(final Level level) {
		final LogManager manager = LogManager.getLogManager();
		final Logger rootLogger = manager.getLogger("");
		rootLogger.setLevel(level);
		for (final Handler h : rootLogger.getHandlers()) {
			h.setLevel(level);
		}
	}

	public static Realm getRealm() throws FTLException {
		if (System.getProperty("endpoint") != null) {
			FtlHelper.ftlEndPoint = System.getProperty("endpoint");
		}
		
		if (System.getProperty("realmUser") != null) {
			FtlHelper.realmUser = System.getProperty("realmUser");
		}
		
		if (System.getProperty("realmPassword") != null) {
			FtlHelper.realmPassword = System.getProperty("realmPassword");
		}
		
		if (System.getProperty("realmServer") != null) {
			FtlHelper.realmServer = System.getProperty("realmServer");
		}

		if (System.getProperty("realmAppName") != null) {
			FtlHelper.realmAppName = System.getProperty("realmAppName");
		}
		
		if (System.getProperty("count") != null) {
			MessageConstants.SENDING_COUNT = Integer.parseInt(System.getProperty("count"));
			if (MessageConstants.SENDING_COUNT < 1) {
				MessageConstants.SENDING_COUNT = Integer.MAX_VALUE;
			}
			
			System.out.println(MessageConstants.SENDING_COUNT);
		}
		
		if (System.getProperty("threads") != null) {
			MessageConstants.PARALLEL_THREADS = Integer.parseInt(System.getProperty("threads"));
		}


		final TibProperties props = FTL.createProperties();
		props.set(Realm.PROPERTY_STRING_USERNAME, realmUser);
		props.set(Realm.PROPERTY_STRING_USERPASSWORD, realmPassword);

		System.out.println(String.format("Try to connect to: %s User:%s, Endpoint:%s, AppName:%s", realmServer,
				realmUser, ftlEndPoint, realmAppName));

		return FTL.connectToRealmServer(realmServer, realmAppName, props);
	}
}
