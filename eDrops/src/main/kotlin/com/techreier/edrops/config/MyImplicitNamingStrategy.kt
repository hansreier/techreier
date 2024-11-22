package com.techreier.edrops.config

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource
import org.hibernate.boot.model.naming.ImplicitUniqueKeyNameSource
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy

class MyImplicitNamingStrategy : SpringImplicitNamingStrategy() {

    // Set foreign key name based on child parent relation
    // References column can default to the id of the parent, if that case there is no referencedColumns
    override fun determineForeignKeyName(association: ImplicitForeignKeyNameSource): Identifier {
        val referencedTable = association.referencedTableName
        val referencedColumns = association.referencedColumnNames?.joinToString("_")
        val refCol = if (!referencedColumns.isNullOrEmpty()) "_$referencedColumns" else ""
        val table = association.tableName
        val foreignKeyName = "fk_${table}_${referencedTable}$refCol"
        logger.debug("Foreign key name: ${foreignKeyName}")
        return Identifier.toIdentifier(foreignKeyName)
    }

    //Set unique key name based on table name and columns names
    override fun determineUniqueKeyName(source: ImplicitUniqueKeyNameSource): Identifier {
        val table = source.tableName
        val columns = source.columnNames?.joinToString("_")
        val col = if (!columns.isNullOrEmpty()) "_$columns" else ""
        val uniqueKeyName = "uk_${table}$col"
        logger.debug("Unique key name: ${uniqueKeyName}")
        return Identifier.toIdentifier(uniqueKeyName)
    }

}