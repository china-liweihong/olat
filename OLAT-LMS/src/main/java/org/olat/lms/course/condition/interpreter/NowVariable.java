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

import org.olat.lms.course.editor.CourseEditorEnv;
import org.olat.lms.course.run.environment.CourseEnvironment;
import org.olat.lms.course.run.userview.UserCourseEnvironment;

/**
 * @author gnaegi
 */
public class NowVariable extends AbstractVariable {

    public static final String name = "now";

    /**
     * Default constructor to use the current date
     * 
     * @param userCourseEnv
     */
    public NowVariable(final UserCourseEnvironment userCourseEnv) {
        super(userCourseEnv);
    }

    /**
	 */
    @Override
    public Object getValue() {
        final CourseEditorEnv cev = getUserCourseEnv().getCourseEditorEnv();
        if (cev != null) {
            return new Double(0);
        }
        final CourseEnvironment ce = getUserCourseEnv().getCourseEnvironment();
        final long time = ce.getCurrentTimeMillis();
        return new Double(time);
    }

}