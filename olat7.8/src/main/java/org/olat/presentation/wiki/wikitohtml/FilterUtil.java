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
package org.olat.presentation.wiki.wikitohtml;

/**
 * Description:<br>
 * Util method for setting the first letter of an wiki word to upperchase.
 * <P>
 * Initial Date: Jan 22, 2007 <br>
 * 
 * @author guido
 */
public class FilterUtil {

    public static String normalizeWikiLink(String wikiLink) {
        if (wikiLink.length() > 0) {
            final int index = wikiLink.lastIndexOf('/');
            if (index >= 0) {
                if (wikiLink.length() == index + 1) {
                    wikiLink = wikiLink.substring(0, index + 1) + wikiLink.substring(index + 1, 1).toUpperCase();
                } else if (wikiLink.length() > index + 1) {
                    wikiLink = wikiLink.substring(0, index + 1) + wikiLink.substring(index + 1, index + 2).toUpperCase() + wikiLink.substring(index + 2);
                }
            } else {
                if (wikiLink.length() > 1) {
                    wikiLink = wikiLink.substring(0, 1).toUpperCase() + wikiLink.substring(1);
                } else {
                    wikiLink = wikiLink.substring(0, 1).toUpperCase();
                }
            }
        }
        // wikiLink = wikiLink.replaceAll(":", "/"); //what was this used for: guido
        return wikiLink.replaceAll(" ", "_");
    }

}
