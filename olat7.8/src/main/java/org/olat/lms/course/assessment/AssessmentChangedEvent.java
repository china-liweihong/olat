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

package org.olat.lms.course.assessment;

import java.util.ArrayList;
import java.util.List;

import org.olat.data.basesecurity.Identity;
import org.olat.system.event.MultiUserEvent;
import org.olat.system.exception.AssertException;

/**
 * Description:<BR>
 * TODO: Class Description for AssessmentChangedEvent Initial Date: Nov 30, 2004
 * 
 * @author gnaegi
 */
public class AssessmentChangedEvent extends MultiUserEvent {

    /** Changed attempts value **/
    public static final String TYPE_ATTEMPTS_CHANGED = "attempts";
    /** Changed score, passed, and attempts value **/
    public static final String TYPE_SCORE_EVAL_CHANGED = "score.eval";
    /** Changed efficiency statement **/
    public static final String TYPE_EFFICIENCY_STATEMENT_CHANGED = "efficiency.statement";
    /** Changed user comment value **/
    public static final String TYPE_USER_COMMENT_CHANGED = "user.comment";
    /** Changed coach comment value **/
    public static final String TYPE_COACH_COMMENT_CHANGED = "coach.comment";

    private static final List<String> assessmentTypes = new ArrayList<String>();

    static {
        assessmentTypes.add(TYPE_ATTEMPTS_CHANGED);
        assessmentTypes.add(TYPE_SCORE_EVAL_CHANGED);
        assessmentTypes.add(TYPE_EFFICIENCY_STATEMENT_CHANGED);
        assessmentTypes.add(TYPE_USER_COMMENT_CHANGED);
        assessmentTypes.add(TYPE_COACH_COMMENT_CHANGED);
    }

    private final Long identityKey;

    /**
     * @param changedAssessmentType
     *            On of the static types from this class
     * @param identity
     *            The identity that is target of the change
     */
    public AssessmentChangedEvent(final String changedAssessmentType, final Identity identity) {
        super(changedAssessmentType);
        if (!assessmentTypes.contains(changedAssessmentType)) {
            throw new AssertException("Wrong changed assessment type::" + changedAssessmentType + " not supported");
        }
        identityKey = identity.getKey();
    }

    /**
     * @return The key of the identity that is target of the change. the identity itself is not available and must be refetched if needed since it is not serialized.
     *         (performance and possibly unserializable implementations of Identity)
     */
    public Long getIdentityKey() {
        return identityKey;
    }

    @Override
    public String toString() {
        return "assesstype:" + getCommand() + ", for identity with key:" + identityKey;
    }
}
