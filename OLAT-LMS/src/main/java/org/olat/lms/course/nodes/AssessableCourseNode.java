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

package org.olat.lms.course.nodes;

import org.olat.data.basesecurity.Identity;
import org.olat.lms.course.run.scoring.ScoreEvaluation;
import org.olat.lms.course.run.userview.UserCourseEnvironment;
import org.olat.presentation.framework.core.UserRequest;
import org.olat.presentation.framework.core.control.Controller;
import org.olat.presentation.framework.core.control.WindowControl;

/**
 * Initial Date: Jun 18, 2004
 * 
 * @author gnaegi Comment: All course nodes that are of an assessement type must implement this interface so that the assessment results can be managed by the assessment
 *         tool.
 */
public interface AssessableCourseNode extends CourseNode {

    /**
     * @return Returns the maximal score that can be achieved on this node. Throws an OLATRuntimeException if hasScore set to false, maxScore is undefined in this case
     */
    public Float getMaxScoreConfiguration();

    /**
     * @return Returns the minimal score that can be achieved on this node. Throws an OLATRuntimeException if hasScore set to false, maxScore is undefined in this case
     */
    public Float getMinScoreConfiguration();

    /**
     * @return Returns the passed cut value or null if no such value is defined. A null value means that no cut value is definied and therefor the node can be passed
     *         having any score or no score at all. Throws an OLATRuntimeException if hasPassed is set to false, cutValue is undefined in this case
     */
    public Float getCutValueConfiguration();

    /**
     * @return True if this course node produces a score variable for the learner
     */
    public boolean hasScoreConfigured();

    /**
     * @return True if this course node produces a passed variable for the learner
     */
    public boolean hasPassedConfigured();

    /**
     * @return True if this course node produces a comment variable for the learner
     */
    public boolean hasCommentConfigured();

    /**
     * @return True if this course node produces an attempts variable for the learner
     */
    public boolean hasAttemptsConfigured();

    /**
     * @return True if this course node has additional details to be edited / viewed
     */
    public boolean hasDetails();

    /**
     * @return True if score, passed, attempts and comment are editable by the assessment tool
     */
    public boolean isEditableConfigured();

    /**
     * this method implementation must not cache any results! The user has no scoring results jet (e.g. made no test yet), then the ScoreEvaluation.NA has to be returned!
     * 
     * @param userCourseEnv
     * @return null, if this node cannot deliver any useful scoring info (this is not the case for a test never tried or manual scoring: those have default values 0.0f /
     *         false for score/passed; currently only the STNode returns null if there are no scoring rules defined.)
     */
    public ScoreEvaluation getUserScoreEvaluation(UserCourseEnvironment userCourseEnv);

    /**
     * @param userCourseEnvironment
     * @return the user comment for this user for this node, given by coach
     */
    public String getUserUserComment(UserCourseEnvironment userCourseEnvironment);

    /**
     * @param userCourseEnvironment
     * @return The coach comment for this user for this node (not visible to user)
     */
    public String getUserCoachComment(UserCourseEnvironment userCourseEnvironment);

    /**
     * @param userCourseEnvironment
     * @return the users log of this node
     */
    public String getUserLog(UserCourseEnvironment userCourseEnvironment);

    /**
     * @param userCourseEnvironment
     * @return the users attempts of this node
     */
    public Integer getUserAttempts(UserCourseEnvironment userCourseEnvironment);

    /**
     * @param userCourseEnvironment
     * @return the details view for this node and this user. will be displayed in the user list. if hasDetails= false this returns null
     */
    public String getDetailsListView(UserCourseEnvironment userCourseEnvironment);

    /**
     * @return the details list view header key that is used to label the table row
     */
    public String getDetailsListViewHeaderKey();

    /**
     * Returns a controller to edit the node specific details
     * 
     * @param ureq
     * @param wControl
     * @param userCourseEnvironment
     * @return a controller or null if hasDetails=false
     */
    public Controller getDetailsEditController(UserRequest ureq, WindowControl wControl, UserCourseEnvironment userCourseEnvironment);

    /**
     * @param scoreEvaluation
     *            if scoreEvaluation.getScore() != null, then the score will be updated, and/or if scoreEvaluation.getPassed() != null, then 'passed' will be updated
     * @param userCourseEnvironment
     * @param coachingIdentity
     */
    public void updateUserScoreEvaluation(ScoreEvaluation scoreEvaluation, UserCourseEnvironment userCourseEnvironment, Identity coachingIdentity,
            boolean incrementAttempts);

    /**
     * Updates the user comment for this node and this user. This comment is visible to the user.
     * 
     * @param userComment
     * @param userCourseEnvironment
     * @param coachingIdentity
     */
    public void updateUserUserComment(String userComment, UserCourseEnvironment userCourseEnvironment, Identity coachingIdentity);

    /**
     * Increments the users attempts for this node and this user + 1.
     * 
     * @param userCourseEnvironment
     */
    public void incrementUserAttempts(UserCourseEnvironment userCourseEnvironment);

    /**
     * Updates the users attempts for this node and this user.
     * 
     * @param userAttempts
     * @param userCourseEnvironment
     * @param coachingIdentity
     */
    public void updateUserAttempts(Integer userAttempts, UserCourseEnvironment userCourseEnvironment, Identity coachingIdentity);

    /**
     * Updates the coach comment for this node and this user. This comment is not visible to the user.
     * 
     * @param coachComment
     * @param userCourseEnvironment
     */
    public void updateUserCoachComment(String coachComment, UserCourseEnvironment userCourseEnvironment);

    /**
     * @return True if this course node produces an status variable for the learner
     */
    public boolean hasStatusConfigured();

}
