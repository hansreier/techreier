package com.techreier.edrops.config

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource
class NameForeignKey : ImplicitNamingStrategyJpaCompliantImpl() {

    override fun determineForeignKeyName(association: ImplicitForeignKeyNameSource): Identifier {
        val referencedTable = association.referencedTableName
        val table = association.tableName
        val fkName = "FK_${table}_to_${referencedTable}"
        return Identifier.toIdentifier(fkName)
    }
}