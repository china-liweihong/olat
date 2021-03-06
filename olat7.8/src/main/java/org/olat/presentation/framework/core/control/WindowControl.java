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
 * Copyright (c) 1999-2006 at Multimedia- & E-Learning Services (MELS),<br>
 * University of Zurich, Switzerland.
 * <p>
 */

package org.olat.presentation.framework.core.control;

import org.olat.lms.commons.context.BusinessControl;
import org.olat.presentation.framework.core.components.Component;
import org.olat.presentation.framework.core.control.info.WindowControlInfo;

/**
 * Description: <br>
 * an Interface to control some Gui things like messages, navigational issues, modal dialogs etc; It is passed from Controller to all subcontrollers to allow the setting
 * of info, warn, and error messages and to activate/deactivate modal dialogs and to replace the complete gui stack (e.g. when a header link is chosen)
 * 
 * @author Felix Jost
 */
public interface WindowControl {

    /**
     * put the component on the stack pane of the -current- guistack. useful for modal dialogs and such.
     * 
     * @deprecated todo merge with pushAsModalDialog (pb)
     * @param comp
     */
    @Deprecated
    public void pushToMainArea(Component comp);

    /**
     * @param comp
     */
    public void pushAsModalDialog(Component comp);

    /**
	 * 
	 */
    public void pop();

    /**
     * @param string
     */
    public void setInfo(String string);

    /**
     * @param string
     */
    public void setError(String string);

    /**
     * @param string
     */
    public void setWarning(String string);

    // brasato:: discuss if getDTabs should be via getService or as an attribute
    // on the windows.getWindows(ureq).getAttribute("DTabs")
    // public Object getService(Class interfaceClass);

    /**
     * @return the windowcontrolinfo
     */
    public WindowControlInfo getWindowControlInfo();

    /**
     * Use only when coding the Activatable interface. flatens (that is, does 0 to n pop() 's to the level where this windowcontrol is located. Useful only when
     * implementing the Activatable interface to dump all possible pushed components and return to "the base" which is the level at which the controller's
     * initialcomponent resides.
     */
    public void makeFlat();

    /**
     * @return the businesscontrol, never null
     */
    public BusinessControl getBusinessControl();

    public WindowBackOffice getWindowBackOffice();

}
