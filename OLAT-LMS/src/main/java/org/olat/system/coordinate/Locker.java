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
 * Copyright (c) 1999-2007 at Multimedia- & E-Learning Services (MELS),<br>
 * University of Zurich, Switzerland.
 * <p>
 */
package org.olat.system.coordinate;

import java.util.List;

import org.olat.system.commons.resource.OLATResourceable;
import org.olat.system.security.OLATPrincipal;

/**
 * Description:<br>
 * Interface to acquire Locks (short time locks for gui locking (e.g. i am currently administrating this group) and long term locks for resource locking (e.g. i am
 * editing a qti test)
 * <P>
 * Initial Date: 19.09.2007 <br>
 * 
 * @author Felix Jost, http://www.goodsolutions.ch
 */
public interface Locker {

    /**
     * @param ores
     *            the OLATResourceable to lock upon, e.g a repositoryentry or such
     * @param identity
     *            the identity who tries to acquire the lock, not null
     * @param locksubkey
     *            null or any string to lock finer upon the resource (e.g. "authors", or "write", ...)
     * @return lock result
     */
    public LockResult acquireLock(OLATResourceable ores, OLATPrincipal principal, String locksubkey);

    /**
     * releases the lock. can also be called if the lock was not sucessfully acquired
     * 
     * @param le
     *            the LockResult received when locking
     */
    public void releaseLock(LockResult le);

    /**
     * @param ores
     * @param locksubkey
     * @return if the olatresourceable with the subkey is already locked by someone (returns true even if locked by "myself")
     */
    public boolean isLocked(OLATResourceable ores, String locksubkey);

    /**
     * acquires a persistent lock.
     * 
     * @param ores
     * @param ident
     * @param locksubkey
     *            may not be longer than 30 chars
     * @return the LockResult of this lock trial.
     */
    public LockResult aquirePersistentLock(OLATResourceable ores, OLATPrincipal principal, String locksubkey);

    /**
     * releases a persistent lock.
     * 
     * @param le
     *            the LockResult which stems from the lock acquired previously
     */
    public void releasePersistentLock(LockResult le);

    /**
     * for admin purposes only.
     * 
     * @return a list of lockentries.
     */
    public List<LockEntry> adminOnlyGetLockEntries();

    /**
     * [spring] object is not ready on startup due to circular reference so access is done via method lookup where spring is creating an instance and also an
     * implementation for this abstract method
     * 
     * @return
     */
    public PersistentLockManager getPersistentLockManager();

    /**
     * for admin purposes only. Release a lockentry.
     * 
     * @param lock
     *            release this lockentry
     */
    public void releaseLockEntry(LockEntry lock);

    /**
     * Delete any locks hold by this principal.
     * 
     * @param principal
     */
    public void releaseAllLocksForPrincipal(OLATPrincipal principal);

}