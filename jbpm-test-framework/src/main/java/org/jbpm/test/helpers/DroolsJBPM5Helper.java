package org.jbpm.test.helpers;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.*;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItemHandler;

import java.util.Map;



import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.*;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItemHandler;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: salaboy
 * Date: 2/14/11
 * Time: 9:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DroolsJBPM5Helper {

    public static StatefulKnowledgeSession createKnowledgeSession(Map<String, ResourceType> kresources) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for(String key : kresources.keySet()){
            kbuilder.add(new ClassPathResource(key), kresources.get(key));
        }

        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.out.println(error.getMessage());

            }
            throw new RuntimeException("There are errors in the Knowledge Resources!");
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());


        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        return ksession;

    }

    public static void setWorkItemHandlers(StatefulKnowledgeSession ksession, Map<String, WorkItemHandler> handlers){
          for(String key : handlers.keySet()){
              ksession.getWorkItemManager().registerWorkItemHandler(key, handlers.get(key));
          }

    }


}
