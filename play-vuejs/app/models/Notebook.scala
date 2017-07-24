package models

import scalikejdbc._
import org.joda.time.{DateTime}

case class Notebook(
  id: Int,
  title: String,
  mainText: Option[String] = None,
  updatedAt: Option[DateTime] = None,
  createdAt: DateTime) {

  def save()(implicit session: DBSession = Notebook.autoSession): Notebook = Notebook.save(this)(session)

  def destroy()(implicit session: DBSession = Notebook.autoSession): Int = Notebook.destroy(this)(session)

}


object Notebook extends SQLSyntaxSupport[Notebook] {

  override val tableName = "notebook"

  override val columns = Seq("id", "title", "main_text", "updated_at", "created_at")

  def apply(n: SyntaxProvider[Notebook])(rs: WrappedResultSet): Notebook = apply(n.resultName)(rs)
  def apply(n: ResultName[Notebook])(rs: WrappedResultSet): Notebook = new Notebook(
    id = rs.get(n.id),
    title = rs.get(n.title),
    mainText = rs.get(n.mainText),
    updatedAt = rs.get(n.updatedAt),
    createdAt = rs.get(n.createdAt)
  )

  val n = Notebook.syntax("n")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Notebook] = {
    withSQL {
      select.from(Notebook as n).where.eq(n.id, id)
    }.map(Notebook(n.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Notebook] = {
    withSQL(select.from(Notebook as n)).map(Notebook(n.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Notebook as n)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Notebook] = {
    withSQL {
      select.from(Notebook as n).where.append(where)
    }.map(Notebook(n.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Notebook] = {
    withSQL {
      select.from(Notebook as n).where.append(where)
    }.map(Notebook(n.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Notebook as n).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    title: String,
    mainText: Option[String] = None,
    updatedAt: Option[DateTime] = None,
    createdAt: DateTime)(implicit session: DBSession = autoSession): Notebook = {
    val generatedKey = withSQL {
      insert.into(Notebook).namedValues(
        column.title -> title,
        column.mainText -> mainText,
        column.updatedAt -> updatedAt,
        column.createdAt -> createdAt
      )
    }.updateAndReturnGeneratedKey.apply()

    Notebook(
      id = generatedKey.toInt,
      title = title,
      mainText = mainText,
      updatedAt = updatedAt,
      createdAt = createdAt)
  }

  def batchInsert(entities: Seq[Notebook])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'title -> entity.title,
        'mainText -> entity.mainText,
        'updatedAt -> entity.updatedAt,
        'createdAt -> entity.createdAt))
    SQL("""insert into notebook(
      title,
      main_text,
      updated_at,
      created_at
    ) values (
      {title},
      {mainText},
      {updatedAt},
      {createdAt}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Notebook)(implicit session: DBSession = autoSession): Notebook = {
    withSQL {
      update(Notebook).set(
        column.id -> entity.id,
        column.title -> entity.title,
        column.mainText -> entity.mainText,
        column.updatedAt -> entity.updatedAt,
        column.createdAt -> entity.createdAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Notebook)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Notebook).where.eq(column.id, entity.id) }.update.apply()
  }

}
