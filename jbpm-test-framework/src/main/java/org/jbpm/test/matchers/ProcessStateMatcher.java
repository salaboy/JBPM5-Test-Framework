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
 * Time: 8:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessStateMatcher extends TypeSafeMatcher<Integer> {
    private WorkflowProcessInstance processInstance;

    public ProcessStateMatcher(WorkflowProcessInstance processInstance){
        this.processInstance = processInstance;
    }

    @Override
    public boolean matchesSafely(Integer state) {
          return (this.processInstance.getState() == state);
    }

    public void describeTo(Description description) {
        description.appendText("is not in "+this.processInstance.getState()+" State");

    }

    @Factory
     public static <T> Matcher<Integer> isInState(WorkflowProcessInstance process) {
       return new ProcessStateMatcher(process);
     }

}
