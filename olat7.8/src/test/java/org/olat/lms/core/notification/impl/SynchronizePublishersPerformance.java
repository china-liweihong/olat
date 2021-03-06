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
package org.olat.lms.core.notification.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.olat.data.basesecurity.Identity;
import org.olat.data.commons.database.DB;
import org.olat.data.commons.database.DBFactory;
import org.olat.data.notification.NotificationTestDataGenerator;
import org.olat.data.notification.Publisher;
import org.olat.data.repository.RepositoryEntry;
import org.olat.lms.commons.tree.INode;
import org.olat.lms.core.notification.service.NotificationService;
import org.olat.lms.core.notification.service.NotificationSubscriptionContext;
import org.olat.lms.core.notification.service.PublishEventTO;
import org.olat.lms.course.CourseFactory;
import org.olat.lms.course.ICourse;
import org.olat.lms.course.PersistingCourseImpl;
import org.olat.lms.course.access.notification.NotificationCourseAccessManager;
import org.olat.lms.course.condition.Condition;
import org.olat.lms.course.nodes.CourseNode;
import org.olat.lms.course.nodes.FOCourseNode;
import org.olat.lms.forum.ForumService;
import org.olat.lms.repository.RepositoryEBL;
import org.olat.lms.repository.RepositoryEntryInputData;
import org.olat.lms.repository.RepositoryService;
import org.olat.presentation.forum.ForumNotificationTypeHandler;
import org.olat.system.commons.resource.OLATResourceable;
import org.olat.system.logging.log4j.LoggerHelper;
import org.olat.test.JunitTestHelper;
import org.olat.test.OlatTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Runs the NotificationCourseAccessManager.execute() (a.k.a. synchronize task). <br/>
 * 
 * The test setup: <br/>
 * - generates courses (clones the demo course NUMBER_OF_COURSES times) <br/>
 * - generates identities (NUMBER_OF_SUBSCRIBERS) <br/>
 * - subscribes each identity to the Forum node of each course (there is only one Forum node in a course) <br/>
 * - publishes events for each publisher (each Forum node) <br/>
 * - changes course visibility: some are public, some private <br/>
 * - deletes some Forum nodes in some public courses <br/>
 * - Sets setPreConditionVisibility on some forum nodes (restricts visibility for course coach or administrators).
 * 
 * Initial Date: 11.04.2012 <br>
 * 
 * @author Branislav Balaz
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SynchronizePublishersPerformance extends OlatTestCase {

    private static final Logger log = LoggerHelper.getLogger();

    @Autowired
    private NotificationTestDataGenerator notificationTestDataGenerator;
    @Autowired
    private HibernateTransactionManager transactionManager;
    @Autowired
    private RepositoryEBL repositoryEBL;
    @Autowired
    private ForumService forumService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private NotificationCourseAccessManager notificationCourseAccessManager;
    DB dBImpl;

    // only one publisher (a Forum node), the number of subscribers and subscriptions is equals with the NUMBER_OF_COURSES
    private final int NUMBER_OF_SUBSCRIBERS = 300; // 50
    private final int NUMBER_OF_COURSES = 100; // 50
    private final long MAX_EXECUTION_TIME_IN_SECONDS = 2600;
    private final int NUMBER_OF_CREATOR_IDENTITIES = 1;
    private final int NUMBER_OF_EVENTS_FOR_PUBLISHER = 11;

    List<Identity> generatedIdentities;// size: NUMBER_OF_SUBSCRIBERS

    @Before
    public void setup() {
        log.info("setup started");

        final List<RepositoryEntry> courses = generateCourses(NUMBER_OF_COURSES);
        final List<NotificationSubscriptionContext> notificationSubscriptionContexts = new ArrayList<NotificationSubscriptionContext>();

        assertTrue(NUMBER_OF_SUBSCRIBERS >= NUMBER_OF_COURSES);

        new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                generatedIdentities = notificationTestDataGenerator.generateIdentities(NUMBER_OF_SUBSCRIBERS);
            }
        });

        for (RepositoryEntry repositoryEntry : courses) {
            ICourse course = loadCourseFromRepositoryEntry(repositoryEntry);
            CourseNode rootNode = course.getRunStructure().getRootNode();
            Long forumId = forumService.addAForum().getKey();
            for (Identity subscribeIdentity : generatedIdentities) {
                // each identity should subscribe in each course
                processPublisherNodes(rootNode, notificationSubscriptionContexts, repositoryEntry, subscribeIdentity, forumId);
                // setCourseAccess(repositoryEntry, RepositoryEntry.ACC_OWNERS); //not necessary, since the copied courses are already private
            }
            commitDBImplTransaction();
        }

        makeEverySecondCoursePublicAndRemoveForumNode(courses);

        subscribeAllInSeparateTransactions(notificationSubscriptionContexts);

        // publishEventsInSeparateTransactions(notificationSubscriptionContexts);

        log.info("setup finished");
    }

    /**
     * Restrict access for owners. <br/>
     * Not necessary since the copies are anyway visible only to owners.
     */
    private void setCourseAccess(RepositoryEntry repositoryEntry, int access) {
        repositoryEntry.setAccess(access);
        repositoryService.updateRepositoryEntry(repositoryEntry);
    }

    private void subscribeAllInSeparateTransactions(List<NotificationSubscriptionContext> notificationSubscriptionContexts) {
        for (final NotificationSubscriptionContext notificationSubscriptionContext : notificationSubscriptionContexts) {

            new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    notificationService.subscribe(notificationSubscriptionContext);
                }
            });
        }
        log.info("subscribeAll - subscriptions.size(): " + notificationSubscriptionContexts.size());
    }

    /**
     * Publishes only NUMBER_OF_EVENTS_FOR_PUBLISHER.
     */
    private void publishEventsInSeparateTransactions(List<NotificationSubscriptionContext> notificationSubscriptionContexts) {

        Map<Long, NotificationSubscriptionContext> contextsMap = getANotificationSubscriptionContextForEachContextId(notificationSubscriptionContexts);
        Iterator<Long> keysIterator = contextsMap.keySet().iterator();
        log.info("publishEvents contextsMap.size(): " + contextsMap.size());
        while (keysIterator.hasNext()) {
            final NotificationSubscriptionContext notificationSubscriptionContext = contextsMap.get(keysIterator.next());
            for (int i = 0; i < NUMBER_OF_EVENTS_FOR_PUBLISHER; i++) {

                new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        notificationService.publishEvent(getPublishEventTO(notificationSubscriptionContext));
                    }
                });
            }
        }
        log.info("publishEvents finished");
    }

    public Map<Long, NotificationSubscriptionContext> getANotificationSubscriptionContextForEachContextId(
            List<NotificationSubscriptionContext> notificationSubscriptionContexts) {
        Map<Long, NotificationSubscriptionContext> contextIdToNotificationSubscriptionContextMap = new HashMap<Long, NotificationSubscriptionContext>();
        for (NotificationSubscriptionContext notificationSubscriptionContext : notificationSubscriptionContexts) {
            if (!contextIdToNotificationSubscriptionContextMap.containsKey(notificationSubscriptionContext.getContextId())) {
                contextIdToNotificationSubscriptionContextMap.put(notificationSubscriptionContext.getContextId(), notificationSubscriptionContext);
            }
        }
        return contextIdToNotificationSubscriptionContextMap;
    }

    private void makeEverySecondCoursePublicAndRemoveForumNode(final List<RepositoryEntry> courses) {
        int i = 0;
        for (RepositoryEntry repositoryEntry : courses) {
            ICourse course = loadCourseFromRepositoryEntry(repositoryEntry);
            if (i % 3 == 0) { // every third course gets public and its forum removed
                setCourseAccess(repositoryEntry, RepositoryEntry.ACC_USERS);
                removeForum(course);
            } else if (i % 2 == 0) { // every third course gets public
                setCourseAccess(repositoryEntry, RepositoryEntry.ACC_USERS);
            }
            i++;
            commitDBImplTransaction();
        }
    }

    /**
     * assumption: there is only one forum node in this course.
     */
    private void removeForum(ICourse course) {
        CourseNode rootNode = course.getRunStructure().getRootNode();
        final PersistingCourseImpl editableCourse = CourseFactory.openCourseEditSession(course.getResourceableId());
        editableCourse.setReadAndWrite(true);
        removeForumNode(rootNode, editableCourse);
        CourseFactory.saveCourseEditorTreeModel(course.getResourceableId());
        CourseFactory.closeCourseEditSession(course.getResourceableId(), true);
    }

    /**
     * traverse recursive course tree and remove the forum node.
     */
    private void removeForumNode(INode publisherNode, PersistingCourseImpl editableCourse) {
        for (int i = 0; i < publisherNode.getChildCount(); i++) {
            final INode childCourseNode = publisherNode.getChildAt(i);
            if (childCourseNode instanceof FOCourseNode) {
                log.info("mark course node deleted: " + childCourseNode.getIdent());
                editableCourse.getEditorTreeModel().removeCourseNode((CourseNode) childCourseNode);
            }
            removeForumNode(childCourseNode, editableCourse);
        }
    }

    /**
     * Prepare the publisher node to be subscribed. Recursive traverse tree and find a forum node, create a NotificationSubscriptionContext (for a generated identity) for
     * this publisher and identity, and add to list. <br/>
     * Sets setPreConditionVisibility on the forum node (restricts visibility for course coach or administrators).
     */
    private void processPublisherNodes(INode publisherNode, List<NotificationSubscriptionContext> notificationSubscriptionContexts, RepositoryEntry repositoryEntry,
            Identity subscribeIdentity, Long forumId) {
        for (int i = 0; i < publisherNode.getChildCount(); i++) {
            final INode childCourseNode = publisherNode.getChildAt(i);
            if (childCourseNode instanceof FOCourseNode) {
                Long subcontextId = Long.valueOf(((FOCourseNode) childCourseNode).getIdent());
                Condition preConditionVisibility = new Condition();
                preConditionVisibility.setConditionId("visibility");
                preConditionVisibility.setConditionExpression("(  ( isCourseCoach(0) | isCourseAdministrator(0) ) )");
                ((FOCourseNode) childCourseNode).setPreConditionVisibility(preConditionVisibility);
                NotificationSubscriptionContext notificationSubscriptionContext = new NotificationSubscriptionContext(subscribeIdentity,
                        ForumNotificationTypeHandler.FORUM_SOURCE_TYPE, forumId, Publisher.ContextType.COURSE, repositoryEntry.getKey(), subcontextId);
                notificationSubscriptionContexts.add(notificationSubscriptionContext);
            }
            processPublisherNodes(childCourseNode, notificationSubscriptionContexts, repositoryEntry, subscribeIdentity, forumId);
        }
    }

    @Test
    public void synchronizePublishersManyCoursesManySubscriptionsForCourseElements() {
        long startTime = System.currentTimeMillis();
        notificationCourseAccessManager.execute();
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        log.info("DURATION: " + duration);
        assertTrue("maximal execution time for " + NUMBER_OF_COURSES + " courses exceeded: maximal time in seconds: " + MAX_EXECUTION_TIME_IN_SECONDS
                + ", actual duration in seconds: " + duration, duration < MAX_EXECUTION_TIME_IN_SECONDS);

    }

    @After
    public void cleanUp() {
        new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    notificationTestDataGenerator.cleanupNotificationTestData();
                } catch (Exception e) {
                    Log.error("cleanUp failed");
                }
            }
        });
    }

    private ICourse loadCourseFromRepositoryEntry(RepositoryEntry courseRepositoryEntry) {
        OLATResourceable course = courseRepositoryEntry.getOlatResource();
        return CourseFactory.loadCourse(course);
    }

    /**
     * Creates numberOfCourses copies of DemoCourse, which has one Forum (this is a publisher) node, with copyCourseIdentity (new generated identity) as owner. The copies
     * have private visibility, that is they are visible only to owners.
     */
    public List<RepositoryEntry> generateCourses(int numberOfCourses) {
        final List<RepositoryEntry> courses = new ArrayList<RepositoryEntry>();
        for (int i = 0; i < numberOfCourses; i++) {
            Identity copyCourseIdentity = new TransactionTemplate(transactionManager).execute(new TransactionCallback<Identity>() {
                public Identity doInTransaction(TransactionStatus status) {
                    return notificationTestDataGenerator.generateIdentities(1).get(0);
                }
            });
            RepositoryEntry demoCourse = JunitTestHelper.deployDemoCourse();
            OLATResourceable newCourse = CourseFactory.copyCourse(demoCourse.getOlatResource(), copyCourseIdentity);
            RepositoryEntry course = createNewRepositoryEntry(demoCourse, copyCourseIdentity, newCourse);
            courses.add(course);
            commitDBImplTransaction();
        }
        return courses;
    }

    private RepositoryEntry createNewRepositoryEntry(RepositoryEntry src, Identity identity, OLATResourceable newCourse) {
        RepositoryEntryInputData repositoryEntryInputData = new RepositoryEntryInputData(identity, notificationTestDataGenerator.getUniqueIdentifier(),
                notificationTestDataGenerator.getUniqueIdentifier(), newCourse);
        RepositoryEntry preparedEntry = repositoryEBL.copyRepositoryEntry(src, repositoryEntryInputData);
        return preparedEntry;
    }

    private PublishEventTO getPublishEventTO(NotificationSubscriptionContext notificationSubscriptionContext) {
        return PublishEventTO.getValidInstance(notificationSubscriptionContext.getContextType(), notificationSubscriptionContext.getContextId(), "",
                notificationSubscriptionContext.getSubcontextId(), notificationSubscriptionContext.getSourceType(), notificationSubscriptionContext.getSourceId(), "",
                "", notificationTestDataGenerator.generateIdentities(NUMBER_OF_CREATOR_IDENTITIES).get(0), PublishEventTO.EventType.NEW);
    }

    private void commitDBImplTransaction() {
        if (dBImpl == null) { // this is done to allow mocking dBImpl at testing
            dBImpl = DBFactory.getInstance(false);
        }
        dBImpl.intermediateCommit();
    }

}
