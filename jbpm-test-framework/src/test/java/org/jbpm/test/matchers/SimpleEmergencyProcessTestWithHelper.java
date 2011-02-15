package org.jbpm.test.matchers;


import org.drools.builder.ResourceType;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.*;
import org.jbpm.test.helpers.DroolsJBPM5Helper;
import org.jbpm.test.model.Emergency;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.jbpm.test.matchers.CurrentActivitiesCountMatcher.currentActivitiesCount;
import static org.jbpm.test.matchers.IsInActivityMatcher.isInActivity;
import static org.jbpm.test.matchers.ProcessStateMatcher.isInState;
import static org.jbpm.test.matchers.VariableValueMatcher.variableValue;
import static org.junit.Assert.*;


/**
 * Created by IntelliJ IDEA.
 * User: salaboy
 * Date: 2/14/11
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleEmergencyProcessTestWithHelper {

    @Test
    public void emergencyServiceWithRulesNoReactiveTest() throws InterruptedException {
        Map<String, ResourceType> kresources = new HashMap<String, ResourceType>();
        kresources.put("EmergencyServiceSimple.bpmn", ResourceType.BPMN2);
        kresources.put("SelectEmergencyVehicleSimple.drl", ResourceType.DRL);

        StatefulKnowledgeSession ksession = DroolsJBPM5Helper.createKnowledgeSession(kresources);

        MyHumanActivitySimulatorChangeValuesWorkItemHandler humanActivitiesSimHandler = new MyHumanActivitySimulatorChangeValuesWorkItemHandler();

        Map<String, WorkItemHandler> handlers = new HashMap<String, WorkItemHandler>();
        handlers.put("Human Task", humanActivitiesSimHandler);
        DroolsJBPM5Helper.setWorkItemHandlers(ksession, handlers);


        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);


        Emergency emergency = new Emergency("555-1234");
        //Run with Heart Attack and check the output. An Ambulance must appear in the report
        //emergency.setType("Heart Attack");
        //Run with Fire and check the output. A FireTruck must appear in the report
        emergency.setType("Fire");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("emergency", emergency);


        WorkflowProcessInstance process = (WorkflowProcessInstance) ksession.startProcess("org.jbpm.test.bpmn2.SimpleEmergencyService", parameters);
        ksession.insert(emergency);
        ksession.insert(process);

        assertThat(ProcessInstance.STATE_ACTIVE, isInState(process));

        assertThat(1, currentActivitiesCount(process));
        assertThat("Ask for Emergency Information", isInActivity(process));
        assertThat("1", variableValue(process,"#{emergency.revision}"));

        //Complete the first human activity
        humanActivitiesSimHandler.completeWorkItem();
        assertThat(ProcessInstance.STATE_ACTIVE, isInState(process));


        // At this point we need to fireAllRules() that were activated
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);


        //Complete the second human activity
        assertThat(ProcessInstance.STATE_ACTIVE, isInState(process));

        assertThat(1, currentActivitiesCount(process));
        assertThat("Dispatch Vehicle", isInActivity(process));

        assertThat("2", variableValue(process, "#{emergency.revision}"));
        assertThat("555-1234", variableValue(process, "#{emergency.phoneCall}"));
        humanActivitiesSimHandler.completeWorkItem();


    }


}

class MyHumanActivitySimulatorChangeValuesWorkItemHandler implements WorkItemHandler {
    private static int counter = 1;
    private WorkItemManager workItemManager;
    private long workItemId;
    private Map<String, Object> results;
    private Emergency currentEmergency;

    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        this.workItemId = workItem.getId();
        this.workItemManager = workItemManager;
        currentEmergency = (Emergency) workItem.getParameter("emergency");
        currentEmergency.setRevision(currentEmergency.getRevision() + counter);


    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

    }

    public void completeWorkItem() {
        results = new HashMap<String, Object>();
        results.put("emergency", currentEmergency);
        workItemManager.completeWorkItem(workItemId, results);

    }


}