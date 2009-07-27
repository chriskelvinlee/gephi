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

package org.gephi.io.project;

import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class GephiFormatException extends RuntimeException {

	private Throwable cause;
    private String message;
    private boolean isImport=false;

	public GephiFormatException(Class source, Throwable cause)
	{
		super(cause);
		this.cause = cause;
        if(source.equals(GephiReader.class))
            isImport=true;
	}

    public GephiFormatException(String message)
    {
        super(message);
        this.message = message;
    }

	@Override
	public String getMessage() {
        if(this.cause==null)
            return message;
		return getLocalizedMessage();
	}

	@Override
	public String getLocalizedMessage()
	{
        if(this.cause==null)
            return message;

        Object[] params = new Object[3];
        params[0] = cause.getClass().getSimpleName();
        params[1] = cause.getLocalizedMessage();
        params[2] = cause.getStackTrace()[0].getLineNumber();

        if(isImport)
        {
            return String.format(NbBundle.getMessage(GephiFormatException.class, "gephiFormatException_import"), params);
        }
        else    //Export
        {
            return String.format(NbBundle.getMessage(GephiFormatException.class, "gephiFormatException_export"), params);
        }
        
	}

}
