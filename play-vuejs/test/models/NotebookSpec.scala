package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import org.joda.time.{DateTime}


class NotebookSpec extends Specification {

  "Notebook" should {

    val n = Notebook.syntax("n")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Notebook.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Notebook.findBy(sqls.eq(n.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Notebook.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Notebook.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Notebook.findAllBy(sqls.eq(n.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Notebook.countBy(sqls.eq(n.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Notebook.create(title = "MyString", createdAt = DateTime.now)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Notebook.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Notebook.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Notebook.findAll().head
      val deleted = Notebook.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Notebook.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Notebook.findAll()
      entities.foreach(e => Notebook.destroy(e))
      val batchInserted = Notebook.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
