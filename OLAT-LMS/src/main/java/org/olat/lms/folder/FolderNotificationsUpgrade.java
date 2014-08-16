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
 * Copyright (c) frentix GmbH<br>
 * http://www.frentix.com<br>
 * <p>
 */

package org.olat.lms.folder;

import org.olat.data.notifications.Publisher;
import org.olat.lms.notifications.NotificationsUpgrade;
import org.olat.lms.notifications.NotificationsUpgradeHelper;

/**
 * Description:<br>
 * Upgrade publisher of folders
 * <P>
 * Initial Date: 7 jan. 2010 <br>
 * 
 * @author srosse, stephane.rosse@frentix.com
 */
public class FolderNotificationsUpgrade implements NotificationsUpgrade {

    protected FolderNotificationsUpgrade() {
    }

    @Override
    public Publisher ugrade(final Publisher publisher) {
        String businessPath = publisher.getBusinessPath();
        if (businessPath != null && businessPath.startsWith("[")) {
            return null;
        }

        final String resName = publisher.getResName();
        if ("BusinessGroup".equals(resName)) {
            businessPath = "[BusinessGroup:" + publisher.getResId() + "][toolfolder:0]";
        } else if ("CourseModule".equals(resName)) {
            businessPath = NotificationsUpgradeHelper.getCourseNodePath(publisher);
        }

        if (businessPath != null) {
            publisher.setBusinessPath(businessPath);
            return publisher;
        }
        return null;
    }

    @Override
    public String getType() {
        return "FolderModule";
    }
}