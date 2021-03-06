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

package org.olat.presentation.framework.dispatcher.mapper;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.olat.lms.commons.mediaresource.MediaResource;
import org.olat.lms.commons.mediaresource.NotFoundMediaResource;
import org.olat.lms.commons.mediaresource.ServletUtil;
import org.olat.presentation.framework.dispatcher.Dispatcher;
import org.olat.presentation.framework.dispatcher.DispatcherAction;
import org.olat.system.commons.Settings;
import org.olat.system.commons.WebappHelper;
import org.olat.system.exception.AssertException;
import org.olat.system.exception.OLATSecurityException;

/**
 * Description:<br>
 * Allows to register so called mappers. Here you can create urls which are valid for all users and will be dispatched to the given mapper. If you need urls that are only
 * accessible for one user, use MapperRegistry.java
 * <P>
 * Initial Date: 10.06.2005 <br>
 * 
 * @author Felix Jost
 */
public class GlobalMapperRegistry implements Dispatcher {
    private static final MediaResource NOTFOUND = new NotFoundMediaResource("not found");
    private static final GlobalMapperRegistry INSTANCE = new GlobalMapperRegistry();

    // The global mapper hash map contains mappers:
    // keys: String objects representing the mapped path (derived from class name).
    // values: the mapper
    private Map<String, Mapper> pathToMapper = new HashMap<String, Mapper>();

    /**
	 * 
	 */
    private GlobalMapperRegistry() {
        // nothing to do
    }

    /**
     * @return MapperRegistry
     */
    public static GlobalMapperRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Register a named global mapper. This mapper is registered at /g/globalname/
     * 
     * @param globalNameClass
     *            class for the name of the mapper. the name of the mapper is the name of the class (including the package)
     * @param mapper
     * @return the path under which this mapper will be called, without / at the end, e.g. /olat/g/521org.olat.presentation.demo.tabledemo.MyController (the 521 here is
     *         the versionId to guarantee the uniqueness across releases to trick out buggy css browser caches)
     */
    public String register(Class globalNameClass, Mapper mapper) {
        String globalName = Settings.getBuildIdentifier() + globalNameClass.getName();
        if (pathToMapper.containsKey(globalName)) {
            throw new AssertException("Could not register global named mapper, name already used::" + globalName);
        }
        pathToMapper.put(globalName, mapper);
        return WebappHelper.getServletContextPath() + DispatcherAction.PATH_GLOBAL_MAPPED + globalName;
    }

    /**
     * @param hreq
     * @param hres
     */
    @Override
    public void execute(HttpServletRequest hreq, HttpServletResponse hres, String pathInfo) {
        // e.g. 23423/bla/blu.html
        String subInfo = pathInfo.substring(DispatcherAction.PATH_GLOBAL_MAPPED.length());
        int slashPos = subInfo.indexOf('/');
        if (slashPos == -1) {
            DispatcherAction.sendNotFound("not found", hres);
            return;
        }

        // smappath e.g. org.olat.presentation.demo.DemoController
        String smappath = subInfo.substring(0, slashPos);
        Mapper m = pathToMapper.get(smappath);

        MediaResource mr;
        if (m == null) { // not mapped
            mr = NOTFOUND;
        } else {
            String mod = subInfo.substring(slashPos);
            // brasato:: can this happen at all, or does tomcat filter out - till now never reached - needs some little cpu cycles
            if (mod.indexOf("..") != -1)
                throw new OLATSecurityException("mapper path contained '..' : " + mod);
            // /bla/blu.html
            mr = m.handle(mod, hreq);
        }
        ServletUtil.serveResource(hreq, hres, mr);
    }

    /**
     * remove a registered mapper if it is no longer needed.
     * 
     * @param mapper
     */
    public void deregister(Mapper mapper) {
        boolean removed = pathToMapper.values().remove(mapper);
        if (!removed)
            throw new AssertException("trying to remove a mapper that was never registered");
    }

}
