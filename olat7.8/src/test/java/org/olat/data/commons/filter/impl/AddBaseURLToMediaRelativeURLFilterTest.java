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
package org.olat.data.commons.filter.impl;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.olat.data.commons.filter.Filter;
import org.olat.data.commons.filter.FilterFactory;

/**
 * Description:<br>
 * This test case tests the add base url to html filter
 * <P>
 * Initial Date: 17.07.2009 <br>
 * 
 * @author gnaegi
 */
@RunWith(JUnit4.class)
public class AddBaseURLToMediaRelativeURLFilterTest {

    protected Filter filter;

    @Before
    public void setUp() {
        filter = FilterFactory.getBaseURLToMediaRelativeURLFilter("http://www.olat.org/my/testPath/");
    }

    @After
    public void tearDown() {
        filter = null;
    }

    private void t(String input, String result) {
        Assert.assertEquals(result, filter.filter(input));
    }

    @Test
    public void testPlainText() {
        t(null, null);
        t("", "");
        t("hello world", "hello world");
        t("hello \n \t \r world", "hello \n \t \r world");
        t("1+2=3", "1+2=3");
        t("media/hello.jpg", "media/hello.jpg");
        t("\"media\"/hello.jpg", "\"media\"/hello.jpg");
    }

    @Test
    public void testSimpleTags() {
        t("<b>hello</b> world", "<b>hello</b> world");
        t("<b>h<i>ell</i>o</b> world", "<b>h<i>ell</i>o</b> world");
        t("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        t("ABC <a href='#bla' \n title='gugus'>hello</b> world ", "ABC <a href='#bla' \n title='gugus'>hello</b> world ");
    }

    @Test
    public void testImages() {
        t("<img src=\"media/hello.jpg\">", "<img src=\"http://www.olat.org/my/testPath/media/hello.jpg\">");
        t("<img src=\"media/hello/world.jpg\" \\>", "<img src=\"http://www.olat.org/my/testPath/media/hello/world.jpg\" \\>");
        t("<img src=\"/hello.jpg\">", "<img src=\"/hello.jpg\">");
        t("<img src=\"/hello/world.jpg\">", "<img src=\"/hello/world.jpg\">");
        t("<img src=\"http://anotherserver.com/hello.jpg\">", "<img src=\"http://anotherserver.com/hello.jpg\">");
    }

    @Test
    public void testLinks() {
        t("<a href=\"media/hello.jpg\">media/hello.jpg</a>", "<a href=\"http://www.olat.org/my/testPath/media/hello.jpg\">media/hello.jpg</a>");
        t("<a href=\"media/hello/world.jpg\">media/hello.jpg</a>", "<a href=\"http://www.olat.org/my/testPath/media/hello/world.jpg\">media/hello.jpg</a>");
        t("<a href=\"/hello.jpg\">hello</a>", "<a href=\"/hello.jpg\">hello</a>");
        t("<a href=\"/hello/world.jpg\">hello</a>", "<a href=\"/hello/world.jpg\">hello</a>");
        t("<a href=\"http://anotherserver.com/hello.jpg\">hello</a>", "<a href=\"http://anotherserver.com/hello.jpg\">hello</a>");
    }

}
