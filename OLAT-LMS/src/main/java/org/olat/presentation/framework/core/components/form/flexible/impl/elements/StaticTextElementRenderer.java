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
package org.olat.presentation.framework.core.components.form.flexible.impl.elements;

import org.olat.presentation.framework.core.components.Component;
import org.olat.presentation.framework.core.components.ComponentRenderer;
import org.olat.presentation.framework.core.components.form.flexible.impl.FormJSHelper;
import org.olat.presentation.framework.core.render.RenderResult;
import org.olat.presentation.framework.core.render.Renderer;
import org.olat.presentation.framework.core.render.RenderingState;
import org.olat.presentation.framework.core.render.StringOutput;
import org.olat.presentation.framework.core.render.URLBuilder;
import org.olat.presentation.framework.core.translator.Translator;

/**
 * Description:<br>
 * TODO: patrickb Class Description for StaticTextElementRenderer
 * <P>
 * Initial Date: 02.02.2007 <br>
 * 
 * @author patrickb
 */
class StaticTextElementRenderer implements ComponentRenderer {

    /**
     * org.olat.presentation.framework.components.Component, org.olat.presentation.framework.render.URLBuilder, org.olat.presentation.framework.translator.Translator,
     * org.olat.presentation.framework.render.RenderResult, java.lang.String[])
     */
    @Override
    @SuppressWarnings("unused")
    public void render(Renderer renderer, StringOutput sb, Component source, URLBuilder ubu, Translator translator, RenderResult renderResult, String[] args) {

        StaticTextElementComponent steC = (StaticTextElementComponent) source;

        String id = steC.getFormDispatchId();
        String value = steC.getValue();

        sb.append("<span id=\"");
        sb.append(id);
        sb.append("\" ");
        sb.append(FormJSHelper.getRawJSFor(steC.getRootForm(), id, steC.getAction()));
        // sb.append("title=\"");
        // sb.append(value);
        // sb.append("\" ");
        sb.append(" >");
        sb.append(value); //
        sb.append("</span>");

    }

    /**
     * org.olat.presentation.framework.components.Component, org.olat.presentation.framework.render.RenderingState)
     */
    @Override
    @SuppressWarnings("unused")
    public void renderBodyOnLoadJSFunctionCall(Renderer renderer, StringOutput sb, Component source, RenderingState rstate) {
        // TODO Auto-generated method stub

    }

    /**
     * org.olat.presentation.framework.components.Component, org.olat.presentation.framework.render.URLBuilder, org.olat.presentation.framework.translator.Translator,
     * org.olat.presentation.framework.render.RenderingState)
     */
    @Override
    @SuppressWarnings("unused")
    public void renderHeaderIncludes(Renderer renderer, StringOutput sb, Component source, URLBuilder ubu, Translator translator, RenderingState rstate) {
        // TODO Auto-generated method stub

    }

}
