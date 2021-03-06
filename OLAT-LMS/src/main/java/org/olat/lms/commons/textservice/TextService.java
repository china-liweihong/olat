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
 * frentix GmbH, Switzerland, http://www.frentix.com
 * <p>
 */
package org.olat.lms.commons.textservice;

import java.util.Locale;

public interface TextService {

    /**
     * Return a possible locale for the text. See details of the implementation to know the limitation of the algorithm used to find it.
     * 
     * @param text
     * @return
     */
    public Locale detectLocale(String text);

    /**
     * Return the number of words in the text. See details of the implementation to know the limitation of the algorithm used to find it.
     * 
     * @param text
     * @return
     */
    public int wordCount(String text, Locale locale);

    /**
     * Return the number of characters in the text. See details of the implementation to know the limitation of the algorithm used to find it.
     * 
     * @param text
     * @return
     */
    public int characterCount(String text, Locale locale);
}
