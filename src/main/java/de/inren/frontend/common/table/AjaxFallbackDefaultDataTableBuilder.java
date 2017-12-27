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
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.StringResourceModel;

import com.google.common.base.Strings;

/**
 * 
 * This is an easy way to create a table, starting with a column of actions,
 * followed by columns representing the fields of the object.
 * 
 * This is a standard way to create tables for the admin interface. For special
 * tables in your gui, please use default wicket elements as a start.
 * 
 * 
 * @author Ingo Renner
 */
public final class AjaxFallbackDefaultDataTableBuilder<T extends Serializable> implements Serializable {

    /** List of the rows. */
    private List<IColumn<T, String>>         columns      = new ArrayList<IColumn<T, String>>();

    /** For a column with property name the description is found as name.label. */
    private static final String              LABEL        = ".label";

    /** The component holding the table. Used for property resolving. */
    private final Component                  component;

    /** Dataprovider for the table */
    private ISortableDataProvider<T, String> dataProvider;

    /** Default number of rows to display in the table. */
    private int                              numberOfRows = 10;

    /**
     * How many rows do should the table have? Default is 10.
     * 
     * @param numberOfRows
     *            must be greater 0, else use default.
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> setNumberOfRows(int numberOfRows) {
        if (numberOfRows > 0) {
            this.numberOfRows = numberOfRows;
        }
        return this;
    }

    /**
     * Constructor.
     * 
     * @param component
     *            used for property resolving.
     * 
     */
    public AjaxFallbackDefaultDataTableBuilder(Component component) {
        this.component = component;
        columns = new ArrayList<IColumn<T, String>>();
    }

    /**
     * Add an individual column
     * 
     * @param abstractColumn
     *            the column
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> add(IColumn<T, String> column) {
        columns.add(column);
        return this;
    }

    /**
     * 
     * @param id
     * @return the table
     */
    public Component build(String id) {
        if (Strings.isNullOrEmpty(id) || columns.isEmpty() || dataProvider == null) {
            throw new IllegalStateException("Can't create Table, not all elements are available. id=" + id + ", " + toString());
        }
        Component table = new AjaxFallbackDefaultDataTable<T, String>(id, columns, dataProvider, numberOfRows);
        table.setOutputMarkupId(true);
        table.add(AttributeModifier.append("class", "table-bordered"));
        table.add(AttributeModifier.append("class", "table-hover"));
        table.add(AttributeModifier.append("class", "table-condensed"));
        table.add(AttributeModifier.append("class", "table-striped"));

        return table;
    }

    /**
     * Fügt eine individuelle Spalte ein
     * 
     * @param abstractColumn
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> add(AbstractColumn<T, String> abstractColumn) {
        columns.add(abstractColumn);
        return this;
    }

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
    public AjaxFallbackDefaultDataTableBuilder<T> addPropertyColumn(String property) {
        return addPropertyColumn(property, false);
    }

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
    public AjaxFallbackDefaultDataTableBuilder<T> addPropertyColumn(String property, boolean sortable) {
        columns.add(createPropertyColumn(property, sortable));
        return this;
    }

    private PropertyColumn<T, String> createPropertyColumn(String property, boolean sortable) {
        if (sortable) {
            return new PropertyColumn<T, String>(new StringResourceModel(property + LABEL, component, null), property, property);
        } else {
            return new PropertyColumn<T, String>(new StringResourceModel(property + LABEL, component, null), property);
        }
    }

    /**
     * Datasource of the table.
     * 
     * @param repositoryDataProvider
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> addDataProvider(ISortableDataProvider<T, String> repositoryDataProvider) {
        this.dataProvider = repositoryDataProvider;
        return this;
    }

    /**
     * If the value of the property is a short list of Strings you can use this
     * method. If the list is more complex, you should consider using an
     * individual column.
     * 
     * @param listProperty
     * @param itemProperty
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> addListProperty(String listProperty, String itemProperty) {
        ListColumn<T> listColumn = new ListColumn<T>(new StringResourceModel(listProperty + LABEL, component, null), listProperty, itemProperty);
        columns.add(listColumn);
        return this;
    }

    /**
     * A default implementation to display boolean values.
     * 
     * @param property
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> addBooleanPropertyColumn(String property) {
        return addBooleanPropertyColumn(property, false);
    }

    /**
     * A default implementation to display boolean values. With the option of
     * sorting.
     * 
     * @param property
     * @param sortable
     * 
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> addBooleanPropertyColumn(String property, boolean sortable) {
        columns.add(createBooleanPropertyColumn(property, sortable));
        return this;
    }

    private PropertyColumn<T, String> createBooleanPropertyColumn(final String property, boolean sortable) {
        if (sortable) {
            return new BooleanPropertyColumn<T>(new StringResourceModel(property + LABEL, component, null), property, property, property);
        } else {
            return new BooleanPropertyColumn<T>(new StringResourceModel(property + LABEL, component, null), property, property);
        }
    }

    public AjaxFallbackDefaultDataTableBuilder<T> addMoneyPropertyColumn(String property) {
        return addMoneyPropertyColumn(property, false);
    }

    /**
     * A default implementation to display boolean values. With the option of
     * sorting.
     * 
     * @param property
     * @param sortable
     * 
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> addMoneyPropertyColumn(String property, boolean sortable) {
        columns.add(createMoneyPropertyColumn(property, sortable));
        return this;
    }

    private PropertyColumn<T, String> createMoneyPropertyColumn(final String property, boolean sortable) {
        if (sortable) {
            return new MoneyColumn(new StringResourceModel(property + LABEL, component, null), property, property, property);
        } else {
            return new MoneyColumn<T>(new StringResourceModel(property + LABEL, component, null), property, property);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AjaxFallbackDefaultDataTableBuilder [columns=");
        builder.append(columns);
        builder.append(", component=");
        builder.append(component);
        builder.append(", dataProvider=");
        builder.append(dataProvider);
        builder.append(", numberOfRows=");
        builder.append(numberOfRows);
        builder.append("]");
        return builder.toString();
    }

}
