/**
 * OLAT - Online Learning and Training<br>
 * http://www.olat.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
 * University of Zurich, Switzerland.
 * <p>
 */

package org.olat.lms.course.condition.interpreter;

import org.olat.data.basesecurity.Identity;
import org.olat.data.properties.PropertyImpl;
import org.olat.lms.course.editor.CourseEditorEnv;
import org.olat.lms.course.nodes.CourseNode;
import org.olat.lms.course.nodes.ENCourseNode;
import org.olat.lms.course.properties.CoursePropertyManager;
import org.olat.lms.course.run.userview.UserCourseEnvironment;

/**
 * Description:<BR>
 * Function to get the users inital enrollment date for this course node. If no enrollment has taken place so far, the date will have a future date value.
 * <P>
 * Initial Date: Oct 26, 2004
 * 
 * @author gnaegi
 */
public class GetInitialEnrollmentDateFunction extends AbstractFunction {

    public static final String name = "getInitialEnrollmentDate";

    /**
     * Default constructor to use the get initial enrollment date
     * 
     * @param userCourseEnv
     */
    public GetInitialEnrollmentDateFunction(final UserCourseEnvironment userCourseEnv) {
        super(userCourseEnv);
    }

    /**
	 */
    @Override
    public Object call(final Object[] inStack) {
        /*
         * argument check
         */
        if (inStack.length > 1) {
            return handleException(new ArgumentParseException(ArgumentParseException.NEEDS_FEWER_ARGUMENTS, name, "", "error.fewerargs",
                    "solution.provideone.nodereference"));
        } else if (inStack.length < 1) {
            return handleException(new ArgumentParseException(ArgumentParseException.NEEDS_MORE_ARGUMENTS, name, "", "error.moreargs",
                    "solution.provideone.nodereference"));
        }
        /*
         * argument type check
         */
        if (!(inStack[0] instanceof String)) {
            return handleException(new ArgumentParseException(ArgumentParseException.WRONG_ARGUMENT_FORMAT, name, "", "error.argtype.coursnodeidexpeted",
                    "solution.example.node.infunction"));
        }
        final String nodeId = (String) inStack[0];
        /*
         * check reference integrity
         */
        final CourseEditorEnv cev = getUserCourseEnv().getCourseEditorEnv();
        if (cev != null) {
            if (!cev.existsNode(nodeId)) {
                return handleException(new ArgumentParseException(ArgumentParseException.REFERENCE_NOT_FOUND, name, nodeId, "error.notfound.coursenodeid",
                        "solution.copypastenodeid"));
            }
            if (!cev.isEnrollmentNode(nodeId)) {
                return handleException(new ArgumentParseException(ArgumentParseException.REFERENCE_NOT_FOUND, name, nodeId, "error.notenrollment.coursenodeid",
                        "solution.chooseenrollment"));
            }
            // remember the reference to the node id for this condtion
            cev.addSoftReference("courseNodeId", nodeId);
            // return a valid value to continue with condition evaluation test
            return defaultValue();
        }

        /*
         * the real function evaluation which is used during run time
         */
        final CourseNode node = getUserCourseEnv().getCourseEnvironment().getRunStructure().getNode(nodeId);
        // invalid node id's return still a valid double
        // TODO fg: check with editor tree model DONE: the above checks ensure only
        // valid node references
        // if (node == null) return new Double(Double.NEGATIVE_INFINITY);

        final CoursePropertyManager pm = getUserCourseEnv().getCourseEnvironment().getCoursePropertyManager();
        final Identity identity = getUserCourseEnv().getIdentityEnvironment().getIdentity();

        final PropertyImpl firstTime = pm.findCourseNodeProperty(node, identity, null, ENCourseNode.PROPERTY_INITIAL_ENROLLMENT_DATE);

        if (firstTime != null) {
            final String firstTimeMillis = firstTime.getStringValue();
            return Double.valueOf(firstTimeMillis);
        } else {
            // what to do in case of no date available??? -> return date in the future
            return new Double(Double.POSITIVE_INFINITY);
        }
    }

    @Override
    protected Object defaultValue() {
        return new Double(Double.MIN_VALUE);
    }

}
