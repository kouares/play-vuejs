package models

import scalikejdbc._
import org.joda.time.{DateTime}

case class Tag(
  id: Int,
  name: Option[String] = None,
  updatedAt: Option[DateTime] = None,
  createdAt: DateTime) {

  def save()(implicit session: DBSession = Tag.autoSession): Tag = Tag.save(this)(session)

  def destroy()(implicit session: DBSession = Tag.autoSession): Int = Tag.destroy(this)(session)

}


object Tag extends SQLSyntaxSupport[Tag] {

  override val tableName = "tag"

  override val columns = Seq("id", "name", "updated_at", "created_at")

  def apply(t: SyntaxProvider[Tag])(rs: WrappedResultSet): Tag = apply(t.resultName)(rs)
  def apply(t: ResultName[Tag])(rs: WrappedResultSet): Tag = new Tag(
    id = rs.get(t.id),
    name = rs.get(t.name),
    updatedAt = rs.get(t.updatedAt),
    createdAt = rs.get(t.createdAt)
  )

  val t = Tag.syntax("t")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Tag] = {
    withSQL {
      select.from(Tag as t).where.eq(t.id, id)
    }.map(Tag(t.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Tag] = {
    withSQL(select.from(Tag as t)).map(Tag(t.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Tag as t)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Tag] = {
    withSQL {
      select.from(Tag as t).where.append(where)
    }.map(Tag(t.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Tag] = {
    withSQL {
      select.from(Tag as t).where.append(where)
    }.map(Tag(t.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Tag as t).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: Option[String] = None,
    updatedAt: Option[DateTime] = None,
    createdAt: DateTime)(implicit session: DBSession = autoSession): Tag = {
    val generatedKey = withSQL {
      insert.into(Tag).namedValues(
        column.name -> name,
        column.updatedAt -> updatedAt,
        column.createdAt -> createdAt
      )
    }.updateAndReturnGeneratedKey.apply()

    Tag(
      id = generatedKey.toInt,
      name = name,
      updatedAt = updatedAt,
      createdAt = createdAt)
  }

  def batchInsert(entities: Seq[Tag])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'name -> entity.name,
        'updatedAt -> entity.updatedAt,
        'createdAt -> entity.createdAt))
    SQL("""insert into tag(
      name,
      updated_at,
      created_at
    ) values (
      {name},
      {updatedAt},
      {createdAt}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Tag)(implicit session: DBSession = autoSession): Tag = {
    withSQL {
      update(Tag).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.updatedAt -> entity.updatedAt,
        column.createdAt -> entity.createdAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Tag)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Tag).where.eq(column.id, entity.id) }.update.apply()
  }

}
