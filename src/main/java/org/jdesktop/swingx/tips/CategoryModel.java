/*
 * $Id: CategoryModel.java 542 2005-10-10 18:03:15Z rbair $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.tips;

import org.jdesktop.swingx.JXCategory;

/**
 * A model for {@link org.jdesktop.swingx.JXCategory}.<br>
 * 
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public interface CategoryModel {

  /**
   * @return the number of categories in this model
   */
  int getCategoryCount();

  /**
   * @param index
   * @return the category at <code>index</code>
   * @throws IndexOutOfBoundsException
   *           if the index is out of range (index &lt; 0 || index &gt;=
   *           getCategoryCount()).
   */
  Category getCategoryAt(int index);

  /**
   * A category.<br>
   */
  interface Category {

    /**
     * @return very short (optional) text describing the category
     */
    String getCategoryName();

    /**
     * The category object to show. See {@link JXCategory} for supported object
     * types.
     * 
     * @return the category to display
     */
    Object getCategory();
  }

}
