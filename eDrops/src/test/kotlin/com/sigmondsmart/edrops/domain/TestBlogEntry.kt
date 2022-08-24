package com.sigmondsmart.edrops.domain

//import org.hibernate.SessionFactory
//import org.hibernate.testing.orm.junit.SessionFactory
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.Test
import edrops.domain.BlogEntry
import org.hibernate.Session
import org.hibernate.testing.orm.junit.SessionFactory
import org.hibernate.testing.transaction.TransactionUtil.doInHibernate
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

// TODO Why not possible, pom problem
//https://www.baeldung.com/kotlin/jpa
//Problem: Uses JUNIT 4.
class TestBlogEntry {

   // val sessionFactory: SessionFactory = TODO()

    @Test
    fun givenBlogEntry_whenSaved_thenFound() {
        doInHibernate<Unit>({ this.sessionFactory() },
            { session ->
            val blogEntry = BlogEntry(
                LocalDateTime.now(), null,"Dette er Reiers første blog")
            session.persist(blogEntry)
            val blogEntryFound = session.find(BlogEntry::class.java, blogEntry.id)
            session.refresh(blogEntryFound)

            assertTrue(blogEntry.text == blogEntryFound.text)
        })

    }

    private fun sessionFactory() {
        TODO("Not yet implemented")
    }

    @Test
    fun createValidUser_saveUser_userSaved() {
        doInHibernate<SessionFactory>(({this.sessionFactory()) }), {session: Session ->
            val blogEntry = BlogEntry(
                LocalDateTime.now(), null,"Dette er Reiers første blog")
            session.persist(blogEntry)

            val blogEntryFound  = session.find(BlogEntry::class.java, blogEntry.id)

          //  assertThat(actualUser).isEqualTo(expectedUser)
        })
    }

}
