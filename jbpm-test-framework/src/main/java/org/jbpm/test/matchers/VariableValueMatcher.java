package org.jbpm.test.matchers;

import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.mvel2.MVEL;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: salaboy
 * Date: 2/15/11
 * Time: 9:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class VariableValueMatcher extends TypeSafeMatcher<String> {

    private static final Pattern PARAMETER_MATCHER = Pattern.compile("#\\{(\\S+)\\}", Pattern.DOTALL);
    private WorkflowProcessInstance processInstance;
    private String expression;

    public VariableValueMatcher(WorkflowProcessInstance processInstance, String expression) {
        this.processInstance = processInstance;
        this.expression = expression;
    }

    @Override
    public boolean matchesSafely(String item) {


       for(NodeInstance currentNodeInstance : processInstance.getNodeInstances()){
            NodeInstanceImpl currentNode = (NodeInstanceImpl)  currentNodeInstance;
            java.util.regex.Matcher matcher = PARAMETER_MATCHER.matcher(this.expression);
            while (matcher.find()) {
                String paramName = matcher.group(1);


                try {
                    Object variableValue = MVEL.eval(paramName, new NodeInstanceResolverFactory(currentNode));
                    String variableValueString = variableValue == null ? "" : variableValue.toString();
                    return variableValueString.equals(item);
                } catch (Throwable t) {
                    System.err.println("Could not find variable scope for variable " + paramName);
                    System.err.println("when trying to replace variable in string for Node  " + currentNode.getNodeName());
                    System.err.println("Continuing without setting parameter.");
                }


            }
        }


        return false;
    }


    public void describeTo(Description description) {
        description.appendText("the expression "+this.expression +" cannot be resolved from the current Node Instances");
    }

    @Factory
    public static <T> Matcher<String> variableValue(WorkflowProcessInstance processInstance, String expression) {
        return new VariableValueMatcher(processInstance, expression);
    }


}

