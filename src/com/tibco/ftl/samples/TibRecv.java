/*
 * Copyright (c) 2010-2017 TIBCO Software Inc.
 * All Rights Reserved. Confidential & Proprietary.
 * For more information, please contact:
 * TIBCO Software Inc., Palo Alto, California, USA
 */

/*
 * This is a sample of a basic java FTL subscriber program which receives a single message
 */

package com.tibco.ftl.samples;

import java.util.*;

import javax.swing.text.AbstractDocument.Content;

import com.tibco.ftl.*;
import com.tibco.ftl.exceptions.*;

public class TibRecv implements SubscriberListener
{
    boolean  done           = false;
    String   realmServer    = "http://10.144.208.69:13131";

    String   usageString    =
        "TibRecv [options] url\n" +
        " Default url is " + realmServer + "\n";

    public TibRecv(String[] args)
    {
        System.out.printf("#\n# %s\n#\n# %s\n#\n",
                          this.getClass().getName(),
                          FTL.getVersionInformation());

//        if (args.length > 0 && args[0] != null)
//            realmServer = args[0];
    }

    public void messagesReceived(List<Message> messages, EventQueue eventQueue)
    {
        int i;
        int msgNum = messages.size();
         
        for (i = 0;  i < msgNum;  i++) 
        {
            System.out.println(" " + messages.get(i));
            done = true;
        }
    }
    
    public int recv() throws FTLException
    {
        Realm           realm;
        Subscriber      sub;
        EventQueue      queue;
        ContentMatcher  cm;
	TibProperties   props;

        props = FTL.createProperties();
        props.set(Realm.PROPERTY_STRING_USERNAME, "admin");
        props.set(Realm.PROPERTY_STRING_USERPASSWORD, "admin-pw");

        // connect to the realmserver.
        realm = FTL.connectToRealmServer(realmServer, "EMS-SERVER", props);

        // The content matcher string is essentially a json string
        // with the following format {"type":"hello"}
        cm = realm.createContentMatcher("{\"str\":\"hello\"}");

        // Create a subscriber
        sub = realm.createSubscriber("alea_ems", cm, null);

        // dispose the content matcher
        cm.destroy();

        // Create a queue and add subscriber to it
        queue = realm.createEventQueue();
        queue.addSubscriber(sub, this);
        
        // Begin dispatching messages
        System.out.printf("waiting for message(s)\n");
        while (!done)
            queue.dispatch();

        // Clean up
        queue.removeSubscriber(sub);
        queue.destroy();
        sub.close();
        realm.close();

        return 0;
    }
    
    public static void main(String[] args)
    {
        TibRecv s  = new TibRecv(args);

        try {
            s.recv();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    } 
}
