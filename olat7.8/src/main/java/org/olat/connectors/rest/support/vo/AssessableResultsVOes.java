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
package org.olat.connectors.rest.support.vo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Description:<br>
 * this is an helper class for the documentation. Don't use it in the REST API!!!
 * <P>
 * Initial Date: 20 apr. 2010 <br>
 * 
 * @author srosse, stephane.rosse@frentix.com
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "assessableResultsVOes")
public class AssessableResultsVOes {

    @XmlElement(name = "assessableResultsVO")
    private List<AssessableResultsVO> results = new ArrayList<AssessableResultsVO>();

    public AssessableResultsVOes() {
        // make jaxb happy
    }

    public List<AssessableResultsVO> getResults() {
        return results;
    }

    public void setResults(final List<AssessableResultsVO> results) {
        this.results = results;
    }
}
