/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.AppElementFiles;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Auto updated cache, without shrink.
 *
 * @author mg
 * @param <E>
 */
public abstract class ActualCache<E> {

    protected Map<String, ActualCacheEntry<E>> entries = new ConcurrentHashMap<>();

    public E get(String aName, AppElementFiles aFiles) throws Exception {
        ActualCacheEntry<E> cached = entries.get(aName);
        Date cachedTime = null;
        if (cached != null) {
            cachedTime = cached.getTimeStamp();
        }
        Date filesModified = aFiles.getLastModified();
        if (filesModified != null && (cachedTime == null || filesModified.after(cachedTime))) {
            E parsed = parse(aName, aFiles);
            cached = new ActualCacheEntry<>(parsed, filesModified);
            entries.put(aName, cached);
        }
        return cached != null ? cached.getValue() : null;
    }

    protected abstract E parse(String aName, AppElementFiles aFiles) throws Exception;
}
