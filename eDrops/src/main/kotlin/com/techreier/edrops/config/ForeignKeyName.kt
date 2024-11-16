package com.techreier.edrops.config

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl

class ForeignKeyName : ImplicitNamingStrategyJpaCompliantImpl() {

    // Set foreign key name based on child parent relation
    // References column can default to the id of the parent, if that case there is no referencedColumns
    override fun determineForeignKeyName(association: ImplicitForeignKeyNameSource): Identifier {
        val referencedTable = association.referencedTableName
        val referencedColumns = association.referencedColumnNames?.joinToString("_")
        val refCol = if (!referencedColumns.isNullOrEmpty()) "_$referencedColumns" else ""
        val table = association.tableName
        val fkName = "fk_${table}_${referencedTable}$refCol"
        return Identifier.toIdentifier(fkName)
    }
}