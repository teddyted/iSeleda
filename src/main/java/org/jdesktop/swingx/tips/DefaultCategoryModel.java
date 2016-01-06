/*
 * $Id: DefaultCategoryModel.java 542 2005-10-10 18:03:15Z rbair $
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Default {@link org.jdesktop.swingx.tipss.CategoryModel} implementation.<br>
 * 
 * @author <a href="mailto:teddyted@gmail.com.com">Teddy Desta</a>
 */
public class DefaultCategoryModel implements CategoryModel {

  private List<Category> categories = new ArrayList<Category>();

  public DefaultCategoryModel() {
  }
  
  public DefaultCategoryModel(Category[] categories) {
    this(Arrays.asList(categories));
  }

  public DefaultCategoryModel(Collection<Category> categories) {
    this.categories.addAll(categories);
  }

  public Category getCategoryAt(int index) {
    return categories.get(index);
  }

  public int getCategoryCount() {
    return categories.size();
  }

  public void add(Category tip) {
    categories.add(tip);
  }
  
  public void remove(Category tip) {
    categories.remove(tip);
  }
  
  public Category[] getCategories() {
    return categories.toArray(new Category[categories.size()]);
  }
  
  public void setCategories(Category[] categories) {
    this.categories.clear();
    this.categories.addAll(Arrays.asList(categories));
  }
  
}
