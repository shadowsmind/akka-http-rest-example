package example.common

package object domain {

  type DateTime = java.time.LocalDateTime
  type EntityStatus = EntityStatus.EntityStatus
  type UserRole = UserRole.UserRole

  object EntityStatus extends Enumeration {
    type EntityStatus = Value
    val Active, Deleted, Banned = Value
  }

  object UserRole extends Enumeration {
    type UserRole = Value
    val Admin, Editor, User = Value
  }

  trait HasRole {
    val role: UserRole

    def isAdmin: Boolean = role == UserRole.Admin
    def isEditor: Boolean = role == UserRole.Editor
  }

  trait HasId {
    val id: Option[Long]
  }

  trait HasSlug {
    val slug: String
  }

  trait HasTimestamps {
    val createdAt: DateTime
    val updatedAt: Option[DateTime]
  }

  trait HasStatus {
    val status: EntityStatus.EntityStatus

    def isActive: Boolean = status == EntityStatus.Active
  }

  trait BaseEntity extends HasId with HasTimestamps

  trait BaseEntityWithStatus extends BaseEntity with HasStatus

}
