package models

import scalikejdbc._

case class TagMapping(
  title: String,
  tagId: Option[Int] = None) {

  def save()(implicit session: DBSession = TagMapping.autoSession): TagMapping = TagMapping.save(this)(session)

  def destroy()(implicit session: DBSession = TagMapping.autoSession): Int = TagMapping.destroy(this)(session)

}


object TagMapping extends SQLSyntaxSupport[TagMapping] {

  override val tableName = "tag_mapping"

  override val columns = Seq("title", "tag_id")

  def apply(tm: SyntaxProvider[TagMapping])(rs: WrappedResultSet): TagMapping = apply(tm.resultName)(rs)
  def apply(tm: ResultName[TagMapping])(rs: WrappedResultSet): TagMapping = new TagMapping(
    title = rs.get(tm.title),
    tagId = rs.get(tm.tagId)
  )

  val tm = TagMapping.syntax("tm")

  override val autoSession = AutoSession

  def find(title: String, tagId: Option[Int])(implicit session: DBSession = autoSession): Option[TagMapping] = {
    withSQL {
      select.from(TagMapping as tm).where.eq(tm.title, title).and.eq(tm.tagId, tagId)
    }.map(TagMapping(tm.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[TagMapping] = {
    withSQL(select.from(TagMapping as tm)).map(TagMapping(tm.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(TagMapping as tm)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[TagMapping] = {
    withSQL {
      select.from(TagMapping as tm).where.append(where)
    }.map(TagMapping(tm.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[TagMapping] = {
    withSQL {
      select.from(TagMapping as tm).where.append(where)
    }.map(TagMapping(tm.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(TagMapping as tm).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    title: String,
    tagId: Option[Int] = None)(implicit session: DBSession = autoSession): TagMapping = {
    withSQL {
      insert.into(TagMapping).namedValues(
        column.title -> title,
        column.tagId -> tagId
      )
    }.update.apply()

    TagMapping(
      title = title,
      tagId = tagId)
  }

  def batchInsert(entities: Seq[TagMapping])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'title -> entity.title,
        'tagId -> entity.tagId))
    SQL("""insert into tag_mapping(
      title,
      tag_id
    ) values (
      {title},
      {tagId}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: TagMapping)(implicit session: DBSession = autoSession): TagMapping = {
    withSQL {
      update(TagMapping).set(
        column.title -> entity.title,
        column.tagId -> entity.tagId
      ).where.eq(column.title, entity.title).and.eq(column.tagId, entity.tagId)
    }.update.apply()
    entity
  }

  def destroy(entity: TagMapping)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(TagMapping).where.eq(column.title, entity.title).and.eq(column.tagId, entity.tagId) }.update.apply()
  }

}
