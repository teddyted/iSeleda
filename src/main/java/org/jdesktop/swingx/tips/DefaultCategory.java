/*
 * $Id: DefaultCategory.java 542 2005-10-10 18:03:15Z rbair $
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

import org.jdesktop.swingx.tips.CategoryModel.Category;

/**
 * Default {@link org.jdesktop.swingx.tips.CategoryModel.Category} implementation.<br>
 * 
 * @author <a href="mailto:teddyted@gmail.com">Teddy Desta</a>
 */
public class DefaultCategory implements Category {

  private String name;
  private Object category;

  public DefaultCategory() {   
  }
  
  public DefaultCategory(String name, Object category) {
    this.name = name;
    this.category = category;
  }

  public Object getCategory() {
    return category;
  }

  public void setCategory(Object category) {
    this.category = category;
  }

  public String getCategoryName() {
    return name;
  }

  public void setCategoryName(String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return getCategoryName();
  }
  
}