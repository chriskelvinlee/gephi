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
package org.gephi.io.database.standard;

import java.sql.Connection;
import org.gephi.io.container.ContainerLoader;
import org.gephi.io.database.Database;
import org.gephi.io.database.DatabaseType;
import org.gephi.io.database.drivers.SQLUtils;
import org.gephi.io.importer.DatabaseImporter;

/**
 *
 * @author Mathieu Bastian
 */
public class EdgeListImporter implements DatabaseImporter {

    public void importData(Database database, ContainerLoader container) throws Exception {

        System.out.println("Try to connect at " + SQLUtils.getUrl(database.getSQLDriver(), database.getHost(), database.getPort(), database.getDBName()));
        Connection connection = database.getSQLDriver().getConnection(SQLUtils.getUrl(database.getSQLDriver(), database.getHost(), database.getPort(), database.getDBName()), database.getUsername(), database.getPasswd());
        
    }

    public boolean isMatchingImporter(DatabaseType databaseType) {
        if (databaseType instanceof EdgeList) {
            return true;
        }
        return false;
    }
}
