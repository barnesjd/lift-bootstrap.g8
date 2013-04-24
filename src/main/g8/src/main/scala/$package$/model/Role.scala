package $package$.model

import net.liftweb._
import net.liftweb.mapper.KeyedMetaMapper
import net.liftweb.mapper.KeyedMapper
import net.liftweb.mapper.MappedStringIndex
import net.liftweb.mapper.OneToMany
import $package$.lib.APermission
import net.liftweb.common.Full
import net.liftweb.mapper.MappedString
import net.liftweb.common.Loggable
import net.liftweb.mapper.By
import net.liftweb.http.S


/*
 * Simple record for storing roles. Role name is the PK.
 */
object Role extends Role with KeyedMetaMapper[String, Role] with Loggable {

  val R_SUPERUSER    = "superuser"
  val R_USER         = "user"
  val R_TEAM_OWNER   = "owner"
  val R_TEAM_MEMBER  = "member"
  val R_TEAM_WATCHER = "watcher"

  val CAT_SYSTEM     = "system"
  val CAT_TEAM       = "team"

  override def dbTableName = "roles"

  def findOrCreate(roleId: String): Role = find(roleId).openOr(create.id(roleId))
  def findOrCreateAndSave(roleId: String, category: String, perms: Permission*): Role = {
    find(roleId).openOr {
      logger.info("Create Role %s for category %s".format(roleId, category))
      val r = create.id(roleId).category(category)
      r.permissions.appendAll(perms)
      r.saveMe
    }
  }

  def allRoles(cat: String) = Role.findAll(By(category, cat))

  lazy val TeamOwner   = Role.find(R_TEAM_OWNER).openOrThrowException("No Owner Role found")
  lazy val TeamMember  = Role.find(R_TEAM_MEMBER).openOrThrowException("No Member Role found")
  lazy val TeamWatcher = Role.find(R_TEAM_WATCHER).openOrThrowException("No Watcher Role found")

}

class Role extends KeyedMapper[String, Role] with OneToMany[String,Role] {
  def getSingleton = Role
  def primaryKeyField = id

  object id extends MappedStringIndex(this, 32) {
    override def writePermission_? = true
    override def dbAutogenerated_? = false
    override def dbNotNull_? = true
    override def dbIndexed_? = true
    override def displayName = "Name"
  }

  object category extends MappedString(this, 50)

  object permissions extends MappedOneToMany(Permission, Permission.roleId) {
    def allPerms: List[APermission] = all.map(Permission.toAPermission)
  }

  override def equals(other: Any): Boolean = other match {
    case r: Role => r.id.is == this.id.is
    case _ => false
  }

  def displayName() = S ? ("userClientConnection.role."+id.is)

  override def asHtml = {
    val cls = "label" + (id.is match {
      case Role.R_TEAM_OWNER => " label-important"
      case Role.R_TEAM_MEMBER => " label-info"
      case _ => ""
    })
    <span class={cls}>{displayName}</span>
  }

}
