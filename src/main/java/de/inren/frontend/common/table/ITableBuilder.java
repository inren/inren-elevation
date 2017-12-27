/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.inren.frontend.common.table;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;

import de.inren.data.domain.core.DomainObject;
import de.inren.frontend.common.dataprovider.RepositoryDataProvider;

public interface ITableBuilder<T extends DomainObject> {

    /**
     * How many rows do should the table have? Default is 10.
     * 
     * @param numberOfRows
     *            must be greater 0, else use default.
     * @return this for chaining
     */
    public abstract ITableBuilder<T> setNumberOfRows(int numberOfRows);

    /**
     * Add an individual column
     * 
     * @param abstractColumn
     *            the column
     * @return this for chaining
     */
    public abstract ITableBuilder<T> add(IColumn<T, String> column);

    /**
     * 
     * @param id
     * @return the table
     */
    public abstract Component build(String id);

    /**
     * Fügt eine individuelle Spalte ein
     * 
     * @param abstractColumn
     * @return this for chaining
     */
    public abstract ITableBuilder<T> add(AbstractColumn<T, String> abstractColumn);

    /**
     * 
     * Add a value to the row. This value will be displayed as is. The headline
     * for this row will be resolved as "property.label" from the
     * StringResources The value will be resolved with a PropertyModel of the
     * default model with name property. There is no sort option.
     * 
     * @param property
     * @return this for chaining
     */
    public abstract ITableBuilder<T> addPropertyColumn(String property);

    /**
     *
     * Add a value to the row. This value will be displayed as is. The headline
     * for this row will be resolved as "property.label" from the
     * StringResources The value will be resolved with a PropertyModel of the
     * default model with name property.
     *
     * If sortable is true, the resolfed field must be of a type that is
     * comparable.
     *
     * @param property
     * @param sortable
     * @return
     */
    public abstract ITableBuilder<T> addPropertyColumn(String property, boolean sortable);

    /**
     * Datasource of the table.
     * 
     * @param repositoryDataProvider
     * @return this for chaining
     */
    public abstract ITableBuilder<T> addDataProvider(RepositoryDataProvider<T> repositoryDataProvider);

    /**
     * If the value of the property is a short list of Strings you can use this
     * method. If the list is more complex, you should consider using an
     * individual column.
     * 
     * @param listProperty
     * @param itemProperty
     * @return this for chaining
     */
    public abstract ITableBuilder<T> addListProperty(String listProperty, String itemProperty);

    /**
     * A default implementation to display boolean values.
     * 
     * @param property
     * @return this for chaining
     */
    public abstract ITableBuilder<T> addBooleanPropertyColumn(String property);

    /**
     * A default implementation to display boolean values. With the option of
     * sorting.
     * 
     * @param property
     * @param sortable
     * 
     * @return this for chaining
     */
    public abstract ITableBuilder<T> addBooleanPropertyColumn(String property, boolean sortable);

    public abstract ITableBuilder<T> addMoneyPropertyColumn(String property);

    public abstract ITableBuilder<T> addMoneyPropertyColumn(String property, boolean sortable);

    public abstract String toString();

}