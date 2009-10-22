/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.graph.dhns.graph.iterators;

import java.util.concurrent.locks.Lock;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Predicate;
import org.gephi.graph.dhns.edge.iterators.AbstractEdgeIterator;

/**
 *
 * @author Mathieu Bastian
 */
public class FilteredEdgeIteratorImpl extends EdgeIteratorImpl {

    protected Predicate<Edge> predicate;
    protected Edge pointer;

    public FilteredEdgeIteratorImpl(AbstractEdgeIterator iterator, Lock lock, Predicate<Edge> predicate) {
        super(iterator, lock);
        this.predicate = predicate;
    }

    @Override
    public boolean hasNext() {
        while (iterator.hasNext()) {
            pointer = iterator.next();
            if (predicate.evaluate(pointer)) {
                return true;
            }
        }
        if (lock != null) {
            lock.unlock();
        }
        return false;
    }

    @Override
    public Edge next() {
        return pointer;
    }
}
