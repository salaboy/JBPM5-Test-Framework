package org.jbpm.test.matchers;

import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;



/**
 * Created by IntelliJ IDEA.
 * User: salaboy
 * Date: 2/14/11
 * Time: 11:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class IsInActivityMatcher extends TypeSafeMatcher<String> {
    private WorkflowProcessInstance process;
    public IsInActivityMatcher(WorkflowProcessInstance process){
       this.process = process;
    }

    @Override
    public boolean matchesSafely(String activityName) {
        for(NodeInstance nodeInstance : process.getNodeInstances()){
            if(activityName.equals(nodeInstance.getNodeName())){
                return true;
            }
        }
        return false;

    }



    @Factory
     public static <T> Matcher<String> isInActivity(WorkflowProcessInstance process) {
       return new IsInActivityMatcher(process);
     }


    public void describeTo(Description description) {
        description.appendText("the current process is executing the following list of activities: \n");
         for(NodeInstance nodeInstance : process.getNodeInstances()){
              description.appendText("  -> "+nodeInstance.getNodeName());
        }
    }
}
