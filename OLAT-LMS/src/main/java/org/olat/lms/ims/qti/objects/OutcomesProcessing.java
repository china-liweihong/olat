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

package org.olat.lms.ims.qti.objects;

import java.util.HashMap;

import org.dom4j.Element;

/**
 * Initial Date: Sep 23, 2003
 * 
 * @author gnaegi<br>
 *         Comment: Outcomes processing object (at level assessment). Values that can be changed: defaultval, minvalue, maxvalue, cutvalue. The scoremodel is set fix to
 *         'SumOfScores', the variables are of type 'Decimal' </pre>
 */
public class OutcomesProcessing implements QTIObject {

    private final HashMap outcomesProcessing = new HashMap();

    // Strings used in outcomes processing
    // public static final String DEFAULTVAL = "defaultval";
    // public static final String MINVALUE = "minvalue";
    // public static final String MAXVALUE = "maxvalue";
    public static final String CUTVALUE = "cutvalue";

    /*
     * (non-Javadoc)
     */
    @Override
    public void addToElement(final Element root) {
        if (outcomesProcessing.size() == 0) {
            return;
        }

        final Element outcomes_processing = root.addElement("outcomes_processing");
        outcomes_processing.addAttribute("scoremodel", "SumOfScores");
        final Element decvar = outcomes_processing.addElement("outcomes").addElement("decvar");
        // decvar.addAttribute(DEFAULTVAL, (String)outcomesProcessing.get(DEFAULTVAL));
        decvar.addAttribute("varname", "SCORE");
        decvar.addAttribute("vartype", "Decimal");
        // decvar.addAttribute(MINVALUE, (String)outcomesProcessing.get(MINVALUE));
        // decvar.addAttribute(MAXVALUE, (String)outcomesProcessing.get(MAXVALUE));
        decvar.addAttribute(CUTVALUE, (String) outcomesProcessing.get(CUTVALUE));
    }

    public String getField(final String key) {
        return (String) outcomesProcessing.get(key);
    }

    public void setField(final String key, final String value) {
        outcomesProcessing.put(key, value);
    }

}
