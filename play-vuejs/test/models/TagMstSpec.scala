package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._


class TagMstSpec extends Specification {

  "TagMst" should {

    val tm = TagMst.syntax("tm")

    "find by primary keys" in new AutoRollback {
      val maybeFound = TagMst.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = TagMst.findBy(sqls.eq(tm.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = TagMst.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = TagMst.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = TagMst.findAllBy(sqls.eq(tm.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = TagMst.countBy(sqls.eq(tm.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = TagMst.create()
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = TagMst.findAll().head
      // TODO modify something
      val modified = entity
      val updated = TagMst.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = TagMst.findAll().head
      val deleted = TagMst.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = TagMst.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = TagMst.findAll()
      entities.foreach(e => TagMst.destroy(e))
      val batchInserted = TagMst.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
