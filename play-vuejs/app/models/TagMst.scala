package models

import scalikejdbc._

case class TagMst(
  id: Int,
  name: Option[String] = None) {

  def save()(implicit session: DBSession = TagMst.autoSession): TagMst = TagMst.save(this)(session)

  def destroy()(implicit session: DBSession = TagMst.autoSession): Int = TagMst.destroy(this)(session)

}


object TagMst extends SQLSyntaxSupport[TagMst] {

  override val tableName = "tag_mst"

  override val columns = Seq("id", "name")

  def apply(tm: SyntaxProvider[TagMst])(rs: WrappedResultSet): TagMst = apply(tm.resultName)(rs)
  def apply(tm: ResultName[TagMst])(rs: WrappedResultSet): TagMst = new TagMst(
    id = rs.get(tm.id),
    name = rs.get(tm.name)
  )

  val tm = TagMst.syntax("tm")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[TagMst] = {
    withSQL {
      select.from(TagMst as tm).where.eq(tm.id, id)
    }.map(TagMst(tm.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[TagMst] = {
    withSQL(select.from(TagMst as tm)).map(TagMst(tm.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(TagMst as tm)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[TagMst] = {
    withSQL {
      select.from(TagMst as tm).where.append(where)
    }.map(TagMst(tm.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[TagMst] = {
    withSQL {
      select.from(TagMst as tm).where.append(where)
    }.map(TagMst(tm.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(TagMst as tm).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: Option[String] = None)(implicit session: DBSession = autoSession): TagMst = {
    val generatedKey = withSQL {
      insert.into(TagMst).namedValues(
        column.name -> name
      )
    }.updateAndReturnGeneratedKey.apply()

    TagMst(
      id = generatedKey.toInt,
      name = name)
  }

  def batchInsert(entities: Seq[TagMst])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'name -> entity.name))
    SQL("""insert into tag_mst(
      name
    ) values (
      {name}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: TagMst)(implicit session: DBSession = autoSession): TagMst = {
    withSQL {
      update(TagMst).set(
        column.id -> entity.id,
        column.name -> entity.name
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: TagMst)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(TagMst).where.eq(column.id, entity.id) }.update.apply()
  }

}
