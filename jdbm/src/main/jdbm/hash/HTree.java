/**
 * JDBM LICENSE v1.00
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "JDBM" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Cees de Groot.  For written permission,
 *    please contact cg@cdegroot.com.
 *
 * 4. Products derived from this Software may not be called "JDBM"
 *    nor may "JDBM" appear in their names without prior written
 *    permission of Cees de Groot. 
 *
 * 5. Due credit should be given to the JDBM Project
 *    (http://jdbm.sourceforge.net/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE JDBM PROJECT AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * CEES DE GROOT OR ANY CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2000 (C) Cees de Groot. All Rights Reserved.
 * Contributions are (C) Copyright 2000 by their associated contributors.
 *
 */

package jdbm.hash;

import jdbm.JDBMEnumeration;
import jdbm.JDBMHashtable;
import jdbm.recman.RecordManager;
import java.io.IOException;

/**
 *  Persistent hashtable implementation for PageManager.
 *  Implemented as an H*Tree structure.
 *
 *  WARNING!  If this instance is used in a transactional context, it
 *            *must* be discarded after a rollback.
 *
 *  @author <a href="mailto:boisvert@exoffice.com">Alex Boisvert</a>
 *  @version $Id: HTree.java,v 1.1 2000/05/06 00:00:31 boisvert Exp $
 */
public class HTree implements JDBMHashtable {

    RecordManager _recman;
    HashDirectory _root;

    /**
     * Create a persistent hashtable
     */
    public static long createRootDirectory(RecordManager recman)
    throws IOException {
        HashDirectory root = new HashDirectory((byte)0);
        return recman.insert(root);
    }

    /**
     * Load a persistent hashtable
     *
     * @arg recman RecordManager used to store the persistent hashtable
     * @arg root_recid Record id of the root directory of the HTree
     */
    public HTree(RecordManager recman, long root_recid)
    throws IOException {
        _recman = recman;
        try {
            Object obj = _recman.fetchObject(root_recid);
            _root = (HashDirectory)obj;
            _root.setPersistenceContext(recman, root_recid);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            throw new Error("Hashtable root object must be HashDirectory");
        }
    }

    /**
     * Associates the specified value with the specified key.
     *
     * @arg key key with which the specified value is to be assocated.
     * @arg value value to be associated with the specified key.
     */
    public synchronized void put(Object key, Object value)
    throws IOException {
        _root.put(key, value);
    }

    /**
     * Returns the value which is associated with the given key. Returns
     * <code>null</code> if there is not association for this key.
     *
     * @arg key key whose associated value is to be returned
     */
    public synchronized Object get(Object key) 
    throws IOException {
        return _root.get(key);
    }

    /**
     * Remove the value which is associated with the given key.  If the
     * key does not exist, this method simply ignores the operation.
     *
     * @arg key key whose associated value is to be removed
     */
    public synchronized void remove(Object key) throws IOException {
        _root.remove(key);
    }

    /**
     * Returns an enumeration of the keys contained in this 
     */
    public synchronized JDBMEnumeration keys()
    throws IOException {
        return _root.keys();
    }

    /**
     * Returns an enumeration of the values contained in this 
     */
    public synchronized JDBMEnumeration values()
    throws IOException {
        return _root.values();
    }

    /**
     * Disposes of any resources used by this class
     */
    public synchronized void dispose()
    throws IOException {
        _root = null;
        _recman = null;
    }
}
