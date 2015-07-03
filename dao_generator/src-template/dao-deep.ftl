<#--

Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)     
                                                                           
This file is part of greenDAO Generator.                                   
                                                                           
greenDAO Generator is free software: you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by       
the Free Software Foundation, either version 3 of the License, or          
(at your option) any later version.                                        
greenDAO Generator is distributed in the hope that it will be useful,      
but WITHOUT ANY WARRANTY; without even the implied warranty of             
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              
GNU General Public License for more details.                               
                                                                           
You should have received a copy of the GNU General Public License          
along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.

-->
<#if entity.toOneRelations?has_content>
    private String selectDeep;
    private String joinDeep;
    public String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, TABLENAME, getAllColumns());
            builder.append(',');
<#list entity.toOneRelations as toOne>
            SqlUtils.appendColumns(builder, "${toOne.targetEntity.tableName}", daoSession.get${toOne.targetEntity.classNameDao}().getAllColumns());
<#if toOne_has_next>
            builder.append(',');
</#if>
</#list>
            builder.append(getDeepJoinString());
            selectDeep = builder.toString();
        }
        return selectDeep;
    }

    public String getDeepJoinString(){
        if (joinDeep == null) {
            StringBuilder builder = new StringBuilder(" FROM ${entity.tableName} ");
<#list entity.toOneRelations as toOne>
            builder.append(" <#if toOne.ignoreNull>INNER<#else>LEFT</#if> JOIN ${toOne.targetEntity.tableName}<#--
--> ON ${entity.tableName}.'${toOne.fkProperties[0].columnName}'=${toOne.targetEntity.tableName}.'${toOne.targetEntity.pkProperty.columnName}'");
</#list>
            builder.append(' ');
            joinDeep = builder.toString();
        }
        return joinDeep;
    }

    
    protected ${entity.className} loadCurrentDeep(Cursor cursor, boolean lock) {
        ${entity.className} entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

<#list entity.toOneRelations as toOne>
        ${toOne.targetEntity.className} ${toOne.name} = loadCurrentOther(daoSession.get${toOne.targetEntity.classNameDao}(), cursor, offset);
<#if toOne.fkProperties[0].notNull>         if(${toOne.name} != null) {
    </#if>        entity.set${toOne.name?cap_first}(${toOne.name});
<#if toOne.fkProperties[0].notNull>
        }
</#if>
<#if toOne_has_next>
        offset += daoSession.get${toOne.targetEntity.classNameDao}().getAllColumns().length;
</#if>

</#list>
        return entity;    
    }

    public ${entity.className} loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, TABLENAME, getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }

</#if>