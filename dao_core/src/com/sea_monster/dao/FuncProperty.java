package com.sea_monster.dao;

import android.text.TextUtils;

/**
 * Created by dragonj on 2/19/14.
 */
public abstract class FuncProperty {

    Property property;

    String asName;

    public FuncProperty as(String asName) {
        this.asName = asName;
        return this;
    }

    public Property getProperty() {
        return property;
    }

    public abstract String getColumn();

    public static class CountProperty extends FuncProperty {


        public CountProperty(Property property) {
            this.property = property;
        }

        @Override
        public String getColumn() {
            if (property == null) {
                return TextUtils.isEmpty(asName) ? "COUNT(*)" : "COUNT(*) AS " + asName;
            }
            return TextUtils.isEmpty(asName) ? "COUNT(" + property.tableName + ".'" + property.columnName + "')" : "COUNT(" + property.tableName + ".'" + property.columnName + "') AS " + asName;
        }

    }

    public static class MinProperty extends FuncProperty {

        public MinProperty(Property property) {
            this.property = property;
        }

        @Override
        public String getColumn() {
            return TextUtils.isEmpty(asName) ? "MIN(" + property.tableName + ".'" + property.columnName + "')" : "MIN(" + property.tableName + ".'" + property.columnName + "') AS " + asName;
        }
    }

    public static class MaxProperty extends FuncProperty {

        public MaxProperty(Property property) {
            this.property = property;
        }

        @Override
        public String getColumn() {
            return TextUtils.isEmpty(asName) ? "MAX(" + property.tableName + ".'" + property.columnName + "')" : "MAX(" + property.tableName + ".'" + property.columnName + "') AS " + asName;
        }
    }
}

