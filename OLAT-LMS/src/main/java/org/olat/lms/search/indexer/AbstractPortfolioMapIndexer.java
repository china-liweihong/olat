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

package org.olat.lms.search.indexer;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.olat.data.basesecurity.Identity;
import org.olat.data.basesecurity.Roles;
import org.olat.data.portfolio.structure.ElementType;
import org.olat.data.portfolio.structure.PortfolioStructure;
import org.olat.data.portfolio.structure.PortfolioStructureMap;
import org.olat.lms.commons.context.BusinessControl;
import org.olat.lms.commons.context.ContextEntry;
import org.olat.lms.portfolio.EPFrontendManager;
import org.olat.lms.portfolio.PortfolioModule;
import org.olat.lms.search.SearchResourceContext;
import org.olat.lms.search.document.PortfolioMapDocument;
import org.olat.system.commons.resource.OLATResourceable;
import org.olat.system.logging.log4j.LoggerHelper;

/**
 * Description:<br>
 * Index portoflio maps
 * <P>
 * Initial Date: 15 nov. 2010 <br>
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public abstract class AbstractPortfolioMapIndexer extends AbstractIndexer {

    private static final Logger log = LoggerHelper.getLogger();

    private PortfolioModule portfolioModule;
    private EPFrontendManager frontendManager;

    private static final int BATCH_SIZE = 500;

    /**
     * [used by Spring]
     * 
     * @param portfolioModule
     */
    public void setPortfolioModule(final PortfolioModule portfolioModule) {
        this.portfolioModule = portfolioModule;
    }

    /**
     * [used by Spring]
     * 
     * @param frontendManager
     */
    public void setFrontendManager(final EPFrontendManager frontendManager) {
        this.frontendManager = frontendManager;
    }

    protected abstract String getDocumentType();

    protected abstract ElementType getElementType();

    @Override
    public abstract String getSupportedTypeName();

    /**
     * Allow to accept or refuse some map for indexing
     * 
     * @param map
     * @return
     */
    protected boolean accept(final PortfolioStructureMap map) {
        return map != null;
    }

    @Override
    public void doIndex(final SearchResourceContext searchResourceContext, final Object object, final OlatFullIndexer indexerWriter) throws IOException,
            InterruptedException {
        if (!portfolioModule.isEnabled()) {
            return;
        }

        final SearchResourceContext resourceContext = new SearchResourceContext();

        int firstResult = 0;
        List<PortfolioStructure> structures = null;
        do {
            structures = frontendManager.getStructureElements(firstResult, 500, getElementType());
            for (final PortfolioStructure structure : structures) {
                if (structure instanceof PortfolioStructureMap) {
                    final PortfolioStructureMap map = (PortfolioStructureMap) structure;
                    if (accept(map)) {
                        resourceContext.setDocumentType(getDocumentType());
                        resourceContext.setBusinessControlFor(map.getOlatResource());
                        final Document document = PortfolioMapDocument.createDocument(resourceContext, map);
                        indexerWriter.addDocument(document);
                    }
                }
            }
            firstResult += structures.size();

        } while (structures != null && structures.size() == BATCH_SIZE);
    }

    @Override
    public boolean checkAccess(final ContextEntry contextEntry, final BusinessControl businessControl, final Identity identity, final Roles roles) {
        try {
            final OLATResourceable ores = contextEntry.getOLATResourceable();
            return frontendManager.isMapVisible(identity, ores);
        } catch (final Exception e) {
            log.warn("Couldn't ask if map is visible: " + contextEntry, e);
            return false;
        }
    }
}
