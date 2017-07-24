package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._


class TagMappingSpec extends Specification {

  "TagMapping" should {

    val tm = TagMapping.syntax("tm")

    "find by primary keys" in new AutoRollback {
      val maybeFound = TagMapping.find(123, 123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = TagMapping.findBy(sqls.eq(tm.notebookId, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = TagMapping.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = TagMapping.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = TagMapping.findAllBy(sqls.eq(tm.notebookId, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = TagMapping.countBy(sqls.eq(tm.notebookId, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = TagMapping.create(notebookId = 123, tagId = 123)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = TagMapping.findAll().head
      // TODO modify something
      val modified = entity
      val updated = TagMapping.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = TagMapping.findAll().head
      val deleted = TagMapping.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = TagMapping.find(123, 123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = TagMapping.findAll()
      entities.foreach(e => TagMapping.destroy(e))
      val batchInserted = TagMapping.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
