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
package com.sea_monster.dao.query;

import java.util.Date;
import java.util.List;

import com.sea_monster.dao.DaoException;
import com.sea_monster.dao.Property;

/**
 * Internal interface to model WHERE conditions used in queries. Use the {@link com.sea_monster.dao.Property} objects in the DAO classes to
 * create new conditions.
 */
public interface WhereCondition {

    void appendTo(StringBuilder builder);

    void appendValuesTo(List<Object> values);

    public abstract static class AbstractCondition implements WhereCondition {

        protected final boolean hasSingleValue;
        protected final Object value;
        protected final Object[] values;

        public AbstractCondition() {
            hasSingleValue = false;
            value = null;
            values = null;
        }

        public AbstractCondition(Object value) {
            this.value = value;
            hasSingleValue = true;
            values = null;
        }

        public AbstractCondition(Object[] values) {
            this.value = null;
            hasSingleValue = false;
            this.values = values;
        }

        @Override
        public void appendValuesTo(List<Object> valuesTarget) {
            if (hasSingleValue) {
                valuesTarget.add(value);
            }
            if (values != null) {
                for (Object value : values) {
                    valuesTarget.add(value);
                }
            }
        }
    }

    public static class PropertyCondition extends AbstractCondition {

        private static Object checkValueForType(Property property, Object value) {
            if (value != null && value.getClass().isArray()) {
                throw new DaoException("Illegal value: found array, but simple object required");
            }
            Class<?> type = property.type;
            if (type == Date.class) {
                if (value instanceof Date) {
                    return ((Date) value).getTime();
                } else if (value instanceof Long) {
                    return value;
                } else {
                    throw new DaoException("Illegal date value: expected java.util.Date or Long for value " + value);
                }
            } else if (property.type == boolean.class || property.type == Boolean.class) {
                if (value instanceof Boolean) {
                    return ((Boolean) value) ? 1 : 0;
                } else if (value instanceof Number) {
                    int intValue = ((Number) value).intValue();
                    if (intValue != 0 && intValue != 1) {
                        throw new DaoException("Illegal boolean value: numbers must be 0 or 1, but was " + value);
                    }
                } else if (value instanceof String) {
                    String stringValue = ((String) value);
                    if ("TRUE".equalsIgnoreCase(stringValue)) {
                        return 1;
                    } else if ("FALSE".equalsIgnoreCase(stringValue)) {
                        return 0;
                    } else {
                        throw new DaoException(
                                "Illegal boolean value: Strings must be \"TRUE\" or \"FALSE\" (case insesnsitive), but was "
                                        + value);
                    }
                }
            }
            return value;
        }

        private static Object[] checkValuesForType(Property property, Object[] values) {
            for (int i = 0; i < values.length; i++) {
                values[i] = checkValueForType(property, values[i]);
            }
            return values;
        }

        public final Property property;
        public final String op;

        public PropertyCondition(Property property, String op) {
            this.property = property;
            this.op = op;
        }

        public PropertyCondition(Property property, String op, Object value) {
            super(checkValueForType(property, value));
            this.property = property;
            this.op = op;
        }

        public PropertyCondition(Property property, String op, Property value) {
            this.property = property;
            this.op = op + value.tableName+"."+"'"+value.columnName+"'";
        }

        public PropertyCondition(Property property, String op, Object[] values) {
            super(checkValuesForType(property, values));
            this.property = property;
            this.op = op;
        }

        @Override
        public void appendTo(StringBuilder builder) {
            builder.append(property.tableName).append(".'").append(property.columnName).append('\'').append(op);
        }
    }

    public static class PropertySelfCondition extends AbstractCondition {

        private static Object checkValueForType(Property property, Object value) {
            if (value != null && value.getClass().isArray()) {
                throw new DaoException("Illegal value: found array, but simple object required");
            }
            Class<?> type = property.type;
            if (type == Date.class) {
                if (value instanceof Date) {
                    return ((Date) value).getTime();
                } else if (value instanceof Long) {
                    return value;
                } else {
                    throw new DaoException("Illegal date value: expected java.util.Date or Long for value " + value);
                }
            } else if (property.type == boolean.class || property.type == Boolean.class) {
                if (value instanceof Boolean) {
                    return ((Boolean) value) ? 1 : 0;
                } else if (value instanceof Number) {
                    int intValue = ((Number) value).intValue();
                    if (intValue != 0 && intValue != 1) {
                        throw new DaoException("Illegal boolean value: numbers must be 0 or 1, but was " + value);
                    }
                } else if (value instanceof String) {
                    String stringValue = ((String) value);
                    if ("TRUE".equalsIgnoreCase(stringValue)) {
                        return 1;
                    } else if ("FALSE".equalsIgnoreCase(stringValue)) {
                        return 0;
                    } else {
                        throw new DaoException(
                                "Illegal boolean value: Strings must be \"TRUE\" or \"FALSE\" (case insesnsitive), but was "
                                        + value);
                    }
                }
            }
            return value;
        }

        private static Object[] checkValuesForType(Property property, Object[] values) {
            for (int i = 0; i < values.length; i++) {
                values[i] = checkValueForType(property, values[i]);
            }
            return values;
        }

        public final Property property;
        public final String op1;
        public final String op2;

        public PropertySelfCondition(Property property, String op1, String op2) {
            this.property = property;
            this.op1 = op1;
            this.op2 = op2;
        }

        public PropertySelfCondition(Property property, String op1, String op2, Object value) {
            super(checkValueForType(property, value));
            this.property = property;
            this.op1 = op1;
            this.op2 = op2;
        }

        public PropertySelfCondition(Property property, String op1, String op2, Object... values) {
            super(checkValuesForType(property, values));
            this.property = property;
            this.op1 = op1;
            this.op2 = op2;
        }

        @Override
        public void appendTo(StringBuilder builder) {
            builder.append(property.tableName).append(".'").append(property.columnName).append('\'').append(op1).append(op2);
        }
    }

    public static class StringCondition extends AbstractCondition {

        protected final String string;

        public StringCondition(String string) {
            this.string = string;
        }

        public StringCondition(String string, Object value) {
            super(value);
            this.string = string;
        }

        public StringCondition(String string, Object... values) {
            super(values);
            this.string = string;
        }

        @Override
        public void appendTo(StringBuilder builder) {
            builder.append(string);
        }

    }

    public static class UpdateCondition extends AbstractCondition {

        private static Object checkValueForType(Property property, Object value) {
            if (value != null && value.getClass().isArray()) {
                throw new DaoException("Illegal value: found array, but simple object required");
            }
            Class<?> type = property.type;
            if (type == Date.class) {
                if (value instanceof Date) {
                    return ((Date) value).getTime();
                } else if (value instanceof Long) {
                    return value;
                } else {
                    throw new DaoException("Illegal date value: expected java.util.Date or Long for value " + value);
                }
            } else if (property.type == boolean.class || property.type == Boolean.class) {
                if (value instanceof Boolean) {
                    return ((Boolean) value) ? 1 : 0;
                } else if (value instanceof Number) {
                    int intValue = ((Number) value).intValue();
                    if (intValue != 0 && intValue != 1) {
                        throw new DaoException("Illegal boolean value: numbers must be 0 or 1, but was " + value);
                    }
                } else if (value instanceof String) {
                    String stringValue = ((String) value);
                    if ("TRUE".equalsIgnoreCase(stringValue)) {
                        return 1;
                    } else if ("FALSE".equalsIgnoreCase(stringValue)) {
                        return 0;
                    } else {
                        throw new DaoException(
                                "Illegal boolean value: Strings must be \"TRUE\" or \"FALSE\" (case insesnsitive), but was "
                                        + value);
                    }
                }
            }
            return value;
        }

        private static Object[] checkValuesForType(Property property, Object[] values) {
            for (int i = 0; i < values.length; i++) {
                values[i] = checkValueForType(property, values[i]);
            }
            return values;
        }

        public final Property property;
        public final String op;

        public UpdateCondition(Property property, String op) {
            this.property = property;
            this.op = op;
        }

        public UpdateCondition(Property property, String op, Object value) {
            super(checkValueForType(property, value));
            this.property = property;
            this.op = op;
        }

        public UpdateCondition(Property property, String op, Object[] values) {
            super(checkValuesForType(property, values));
            this.property = property;
            this.op = op;
        }

        public UpdateCondition(Property property, String op, Property value) {
            this.property = property;
            this.op = op + value.tableName+"."+"'"+value.columnName+"'";
        }

        @Override
        public void appendTo(StringBuilder builder) {
            builder.append(property.columnName).append(op);
        }
    }

}
