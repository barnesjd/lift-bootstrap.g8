package $package$.config

import net.liftmodules.mapperauth.model.Role
import net.liftmodules.mapperauth.APermission
import net.liftmodules.mapperauth.model.Permission
import $package$.model.User

object DBSetup {

  implicit def aPerm2perm(aPerm: APermission): Permission = Permission.fromAPermission(aPerm)

  def run() {
    try {
      setupStandardRoles
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  /**
   * Add system roles.
   * FIXME: Adjust permissions!
   */
  def setupStandardRoles {

    Role.findOrCreateAndSave(Role.R_SUPERUSER, Role.CAT_SYSTEM, APermission.all)
    Role.findOrCreateAndSave(Role.R_USER, Role.CAT_SYSTEM, APermission.all)
    Role.findOrCreateAndSave(Role.R_TEAM_OWNER, Role.CAT_TEAM, APermission.all)
    Role.findOrCreateAndSave(Role.R_TEAM_MEMBER, Role.CAT_TEAM, APermission.all)
    Role.findOrCreateAndSave(Role.R_TEAM_WATCHER, Role.CAT_TEAM, APermission.all)


//      for (user <- User.findByEmail("$admin_email$")) {
//        user.user_roles.addRole(roleId)
//        user.save
//      }

  }

}