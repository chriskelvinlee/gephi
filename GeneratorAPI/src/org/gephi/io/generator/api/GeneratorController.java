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
package org.gephi.io.generator.api;

import org.gephi.io.generator.spi.Generator;

/**
 * Generator tasks controller, maintains generators list and the
 * execution flow.
 * 
 * @author Mathieu Bastian
 * @see Generator
 */
public interface GeneratorController {

    /**
     * Returns generators currently loaded in the system.
     * @return          generators array that are available
     */
    public Generator[] getGenerators();

    /**
     * Execute a generator task in a background thread.
     * <p>
     * The created elements are appened in the current workspace, or in a new
     * workspace if the project is empty.
     * @param generator the generator that is to be executed
     */
    public void generate(Generator generator);
}
