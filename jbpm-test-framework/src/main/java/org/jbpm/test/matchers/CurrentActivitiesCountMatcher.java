package org.jbpm.test.matchers;

import org.drools.runtime.process.WorkflowProcessInstance;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;


/**
 * Created by IntelliJ IDEA.
 * User: salaboy
 * Date: 2/15/11
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class CurrentActivitiesCountMatcher extends TypeSafeMatcher<Integer> {

    private WorkflowProcessInstance processInstance;

    public CurrentActivitiesCountMatcher(WorkflowProcessInstance processInstance){
        this.processInstance = processInstance;
    }
    @Override
    public boolean matchesSafely(Integer numberOfActivities) {
         return (numberOfActivities == processInstance.getNodeInstances().size());
    }

    public void describeTo(Description description) {
        description.appendText("the current process have "+processInstance.getNodeInstances().size() +" running activities");
    }
    @Factory
     public static <T> Matcher<Integer> currentActivitiesCount(WorkflowProcessInstance process) {
       return new CurrentActivitiesCountMatcher(process);
     }
}
