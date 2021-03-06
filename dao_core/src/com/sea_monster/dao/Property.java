/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sea_monster.dao;

import java.util.Collection;
import java.util.Objects;

import com.sea_monster.dao.internal.SqlUtils;
import com.sea_monster.dao.query.WhereCondition;
import com.sea_monster.dao.query.WhereCondition.PropertyCondition;

/**
 * Meta data describing a property mapped to a database column; used to create WhereCondition object used by the query builder.
 *
 * @author Markus
 */
public class Property {
    public final int ordinal;
    public final Class<?> type;
    public final String name;
    public final boolean primaryKey;
    public final String columnName;
    public final String tableName;
    public String asName;

    public Property(int ordinal, Class<?> type, String name, boolean primaryKey, String columnName, String tableName) {
        this.ordinal = ordinal;
        this.type = type;
        this.name = name;
        this.primaryKey = primaryKey;
        this.columnName = columnName;
        this.tableName = tableName;
    }

    public Property as(String asName) {
        this.asName = asName;
        return this;
    }

    @Override
    public String toString() {
        return tableName + "." + columnName;
    }

    /**
     * Creates an "equal ('=')" condition  for this property.
     */
    public WhereCondition.UpdateCondition obtainUpdateCondition(Object value) {
        return new WhereCondition.UpdateCondition(this, "=?", value);
    }

    public WhereCondition.UpdateCondition obtainUpdateCondition(Property value) {
        return new WhereCondition.UpdateCondition(this, "=", value);
    }

    /**
     * Creates an "equal ('=')" condition  for this property.
     */
    public WhereCondition eq(Object value) {
        return new PropertyCondition(this, "=?", value);
    }

    public WhereCondition eq(Property value) {
        return new PropertyCondition(this, "=", value);
    }

    /**
     * Creates an "not equal ('<>')" condition  for this property.
     */
    public WhereCondition notEq(Object value) {
        return new PropertyCondition(this, "<>?", value);
    }

    public WhereCondition notEq(Property value) {
        return new PropertyCondition(this, "<>", value);
    }


    /**
     * Creates an "LIKE" condition  for this property.
     */
    public WhereCondition like(String value) {
        return new PropertyCondition(this, " LIKE ?", value);
    }

    public WhereCondition like(Property value) {
        return new PropertyCondition(this, " LIKE ", value);
    }

    /**
     * Creates an "BETWEEN ... AND ..." condition  for this property.
     */
    public WhereCondition between(Object value1, Object value2) {
        Object[] values = {value1, value2};
        return new PropertyCondition(this, " BETWEEN ? AND ?", values);
    }

    /**
     * Creates an "IN (..., ..., ...)" condition  for this property.
     */
    public WhereCondition in(Object... inValues) {
        StringBuilder condition = new StringBuilder(" IN (");
        SqlUtils.appendPlaceholders(condition, inValues.length).append(')');
        return new PropertyCondition(this, condition.toString(), inValues);
    }

    /**
     * Creates an "IN (..., ..., ...)" condition  for this property.
     */
    public WhereCondition in(Collection<?> inValues) {
        return in(inValues.toArray());
    }

    /**
     * Creates an "NOT IN (..., ..., ...)" condition  for this property.
     */
    public WhereCondition notIn(Object... notInValues) {
        StringBuilder condition = new StringBuilder(" NOT IN (");
        SqlUtils.appendPlaceholders(condition, notInValues.length).append(')');
        return new PropertyCondition(this, condition.toString(), notInValues);
    }

    /**
     * Creates an "NOT IN (..., ..., ...)" condition  for this property.
     */
    public WhereCondition notIn(Collection<?> notInValues) {
        return notIn(notInValues.toArray());
    }

    /**
     * Creates an "greater than ('>')" condition  for this property.
     */
    public WhereCondition gt(Object value) {
        return new PropertyCondition(this, ">?", value);
    }

    public WhereCondition gt(Property value) {
        return new PropertyCondition(this, ">", value);
    }

    /**
     * Creates an "less than ('<')" condition  for this property.
     */
    public WhereCondition lt(Object value) {
        return new PropertyCondition(this, "<?", value);
    }

    public WhereCondition lt(Property value) {
        return new PropertyCondition(this, "<", value);
    }

    /**
     * Creates an "greater or equal ('>=')" condition  for this property.
     */
    public WhereCondition ge(Object value) {
        return new PropertyCondition(this, ">=?", value);
    }

    public WhereCondition ge(Property value) {
        return new PropertyCondition(this, ">=", value);
    }


    /**
     * Creates an "less or equal ('<=')" condition  for this property.
     */
    public WhereCondition le(Object value) {
        return new PropertyCondition(this, "<=?", value);
    }

    public WhereCondition le(Property value) {
        return new PropertyCondition(this, "<=", value);
    }

    /**
     * Creates an "IS NULL" condition  for this property.
     */
    public WhereCondition isNull() {
        return new PropertyCondition(this, " IS NULL");
    }

    /**
     * Creates an "IS NOT NULL" condition  for this property.
     */
    public WhereCondition isNotNull() {
        return new PropertyCondition(this, " IS NOT NULL");
    }

    /**
     * Creates an "greater or equal ('[*]&?=?')" condition  for this property.
     */
    public WhereCondition andEq(Object value) {
        return new WhereCondition.PropertySelfCondition(this, "&?", "=?&-1", value, value);
    }

    /**
     * Creates an "greater or equal ('[*]&?=?')" condition  for this property.
     */
    public WhereCondition andEq(Object value1, Objects value2) {
        return new WhereCondition.PropertySelfCondition(this, "&?", "=?&-1", value1, value2);
    }


    /**
     * Creates an "greater or equal ('[*]|?=?')" condition  for this property.
     */
    public WhereCondition orEq(Object value) {
        return new WhereCondition.PropertySelfCondition(this, "|?", "=?&-1", value, value);
    }

    /**
     * Creates an "greater or equal ('[*]|?=?')" condition  for this property.
     */
    public WhereCondition orEq(Object value1, Objects value2) {
        return new WhereCondition.PropertySelfCondition(this, "|?", "=?&-1", value1, value2);
    }


    /**
     * Creates an "greater or equal ('[*]&?<>?')" condition  for this property.
     */
    public WhereCondition andUneq(Object value) {
        return new WhereCondition.PropertySelfCondition(this, "&?", "<>?&-1", value, value);
    }

    /**
     * Creates an "greater or equal ('[*]&?<>?')" condition  for this property.
     */
    public WhereCondition andUneq(Object value1, Objects value2) {
        return new WhereCondition.PropertySelfCondition(this, "&?", "<>?&-1", value1, value2);
    }


    /**
     * Creates an "greater or equal ('[*]|?<>?')" condition  for this property.
     */
    public WhereCondition orUneq(Object value) {
        return new WhereCondition.PropertySelfCondition(this, "|?", "<>?&-1", value, value);
    }

    /**
     * Creates an "greater or equal ('[*]|?<>?')" condition  for this property.
     */
    public WhereCondition orUneq(Object value1, Objects value2) {
        return new WhereCondition.PropertySelfCondition(this, "|?", "<>?&-1", value1, value2);
    }

}
